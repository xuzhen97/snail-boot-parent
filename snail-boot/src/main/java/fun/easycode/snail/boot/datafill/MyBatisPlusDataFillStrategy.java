package fun.easycode.snail.boot.datafill;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.*;
import java.util.stream.Collectors;

/**
 * mybatis-plus填充策略
 * @author xuzhe
 */
public class MyBatisPlusDataFillStrategy implements DataFillStrategy, ApplicationContextAware {

    private ApplicationContext context;

    @SuppressWarnings("all")
    @Override
    public void fill(List<DataFillMetadata> metadataList) {

        Map<Class<?>, List<DataFillMetadata>> collect = metadataList.stream()
                .collect(Collectors.groupingBy(metadata -> metadata.getDataFill().source()));

        collect.forEach((sourceClass, sourceClassMetadataList) -> {
            BaseMapper baseMapper = (BaseMapper) context.getBean(sourceClass);

            List<Object> ids = sourceClassMetadataList.stream()
                    .flatMap(metadata-> metadata.getTargetObjMap().keySet().stream())
                    .collect(Collectors.toList());

            // el表达式可以使用的变量
            Map<String, Object> contextProps = new HashMap<>();
            contextProps.put("ids", ids);

            Map<String, Optional<Object>> paramMap = Arrays.stream(sourceClassMetadataList.stream()
                            .findFirst().get()
                            .getDataFill().params())
                    .collect(Collectors.toMap(DataParam::name
                            , param -> DataCopy.getInstance()
                                    .parserSpEl(param.value(), Object.class, contextProps)));

            Object invoke;
            // 如果标注@DataParam注解，我们将完全按照DataParam标记传参
            // 如果没有注解标记，直接调用BaseMapper的selectBatchIds方法
            if (paramMap.size() == 0) {
                invoke = baseMapper.selectBatchIds(ids);
            } else {
                // 如果传入DataParam列表，将按照参数列表插叙
                // 不要忘记自行塞入 ids, 参考el表达式
                QueryWrapper<?> wrapper = new QueryWrapper<>();
                paramMap.forEach((paramName,paramValue)->{
                    if(paramValue.isPresent()){
                        // 这里支持Collection参数和单个参数， 用In或者eq
                        if(paramValue.get() instanceof Collection){
                            wrapper.in(paramName, (Collection<?>) paramValue.get());
                        }else{
                            wrapper.eq(paramName, paramValue.get());
                        }
                    }
                });
                invoke = baseMapper.selectList(wrapper);
            }

            sourceClassMetadataList.forEach(metadata -> {
                // 如果是指定使用自定义注解，我们将使用自定义注解
                if(metadata.getDataFill().isCustomMyBatisPlusAnno()) {
                    handlerResult(metadata, invoke, DataFillId.class);
                }else {
                    handlerResult(metadata, invoke, TableId.class);
                }
            });
        });
    }

    @Override
    public Class<?> getType() {
        return BaseMapper.class;
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }


}
