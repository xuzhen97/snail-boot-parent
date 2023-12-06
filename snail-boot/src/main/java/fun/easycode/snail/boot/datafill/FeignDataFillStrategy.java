package fun.easycode.snail.boot.datafill;

import cn.hutool.core.util.ReflectUtil;
import fun.easycode.snail.boot.core.CheckException;
import fun.easycode.snail.boot.validator.IValidate;
import org.springframework.beans.BeansException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * feign填充策略
 *
 * @author xuzhe
 */
public class FeignDataFillStrategy implements DataFillStrategy, ApplicationContextAware {

    private ApplicationContext context;

    @Override
    public void fill(List<DataFillMetadata> metadataList) {

        Map<Class<?>, List<DataFillMetadata>> sourceMap = metadataList.stream()
                .collect(Collectors.groupingBy(metadata -> metadata.getDataFill().source()));


        sourceMap.forEach((feignClass, feignClassMetadataList) -> {

            FeignClient feignClient = feignClass.getAnnotation(FeignClient.class);
            Object clientObj = getFeignBean(feignClient.name(), feignClass);
            // 获取DataFill ， 只要被处理器处理过传过来的，元数据集合注解的信息是一致的
            DataFill dataFill = metadataList.stream().findFirst().get().getDataFill();

            Method method;

            if(StringUtils.isEmpty(dataFill.methodName())){
                Method[] methods = ReflectUtil.getMethods(feignClass);

                if (methods.length != 1) {
                    throw new CheckException(feignClass.getName() + "用于@DataFill时，如果不指定methodName, 则必须有且仅能有一个方法！");
                }

                method = methods[0];
            }else{
                method = ReflectUtil.getMethodByName(feignClass, dataFill.methodName());
                if(method == null){
                    throw new CheckException("@DataFill 指定methodName找不到对应的方法!");
                }
            }

            // 获取到client请求的主键信息
            List<Object> ids = feignClassMetadataList.stream()
                    .flatMap(metadata -> metadata.getTargetObjMap().keySet().stream())
                    .collect(Collectors.toList());

            // el表达式可以使用的变量
            Map<String, Object> contextProps = new HashMap<>();
            contextProps.put("ids", ids);

            Map<String, Optional<Object>> paramMap = Arrays.stream(dataFill.params())
                    .collect(Collectors.toMap(DataParam::name
                            , param -> DataCopy.getInstance()
                                    .parserSpEl(param.value(), Object.class, contextProps)));

            Object invoke;
            // 如果标注@DataParam注解，我们将完全按照DataParam标记传参
            // 如果没有注解标记，我们认为方法有且只有一个参数，并传入ids
            if (paramMap.size() == 0) {
                invoke = ReflectUtil.invoke(clientObj, method, ids);
            } else {
                // 处理参数列表
                Parameter[] parameters = method.getParameters();
                Object[] args = new Object[parameters.length];

                // 如果发现feign调用方法的第一个参数实现了IValidate，说明是类传参
                if(IValidate.class.isAssignableFrom(parameters[0].getType())){
                    try {
                        Object cmd = parameters[0].getType().newInstance();
                        paramMap.forEach((key, value) -> {
                            value.ifPresent(o -> ReflectUtil.setFieldValue(cmd, key, o));
                        });
                        args[0] = cmd;
                    } catch (Exception e) {
                        throw new CheckException(parameters[0].getType().getName() + "缺少无参构造!");
                    }
                }else{
                    // 不是非类传参就是直接传参
                    for (int i = 0; i < parameters.length; i++) {
                        Parameter parameter = parameters[i];
                        args[i] = paramMap.get(parameter.getName()) == null
                                ? null : paramMap.get(parameter.getName()).orElse(null);
                    }
                }
                // 调用方法
                invoke = ReflectUtil.invoke(clientObj, method, args);
            }

            if(invoke != null){
                feignClassMetadataList.forEach(metadata -> {
                    handlerResult(metadata, invoke, FeignId.class);
                });
            }
        });

    }

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return FeignClient.class;
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }

    public <T> T getFeignBean(String beanName, Class<T> tClass) {
        FeignContext feignContext = context.getBean("feignContext", FeignContext.class);
        System.out.println(feignContext.getContextNames());
        return feignContext.getInstance(beanName, tClass);
    }
}
