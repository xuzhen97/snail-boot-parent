package fun.easycode.snail.boot.datafill;

import cn.hutool.core.util.ReflectUtil;
import fun.easycode.snail.boot.core.CheckException;
import fun.easycode.snail.boot.core.PageDto;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @author xuzhe
 */
public class DataCopy implements ApplicationContextAware {

    private static ApplicationContext context;
    private ExpressionParser expressionParser;

    /**
     * 解析spEl表达式， 传输两个对象source 和 target 到上下文
     * @param source
     * @param target
     * @param spEl
     * @param tClass
     * @return
     * @param <T>
     */
    public <T> Optional<T> parserSpEl(Object source, Object target, String spEl, Class<T> tClass){
        if(StringUtils.isEmpty(spEl)){
            return Optional.empty();
        }
        EvaluationContext evaluationContext = new StandardEvaluationContext(context);
        evaluationContext.setVariable("source", source);
        evaluationContext.setVariable("target", target);
        Expression expression = expressionParser.parseExpression(spEl);
        return Optional.ofNullable(expression.getValue(evaluationContext, tClass));
    }

    /**
     * 解析spEl表达式
     * @param spEl
     * @param tClass
     * @return
     * @param <T>
     */
    public <T> Optional<T> parserSpEl(String spEl, Class<T> tClass){
        if(StringUtils.isEmpty(spEl)){
            return Optional.empty();
        }
        EvaluationContext evaluationContext = new StandardEvaluationContext(context);
        Expression expression = expressionParser.parseExpression(spEl);
        return Optional.ofNullable(expression.getValue(evaluationContext, tClass));
    }

    /**
     * 解析spEl表达式，允许往上下文传输contextProps变量列表，供用户使用
     * @param spEl
     * @param tClass
     * @param contextProps
     * @return
     * @param <T>
     */
    public <T> Optional<T> parserSpEl(String spEl, Class<T> tClass,final Map<String,Object> contextProps){
        if(StringUtils.isEmpty(spEl)){
            return Optional.empty();
        }
        EvaluationContext evaluationContext = new StandardEvaluationContext(context);
        if(contextProps != null){
            contextProps.forEach((key,value)->{
                evaluationContext.setVariable(key, value);
            });
        }
        Expression expression = expressionParser.parseExpression(spEl);
        return Optional.ofNullable(expression.getValue(evaluationContext, tClass));
    }

    /**
     * 对象属性copy
     * 方法支持使用@DataAlias注解进行转义
     * 例如目标中叫做id ,但是在source中叫做userId
     * 那么我们可以在目标中增加注解@DataAlias("userId")来进行对接
     *
     * @param source 元对象
     * @param target 目标对象
     */
    @SuppressWarnings("all")
    public void copy(Object source, Object target, String spEl) {

        Map<String, Field> sourceFieldMap = ReflectUtil.getFieldMap(source.getClass());
        Map<String, Field> targetFieldMap = ReflectUtil.getFieldMap(target.getClass());

        for (Map.Entry<String, Field> targetEntry : targetFieldMap.entrySet()) {
            Field targetField = targetEntry.getValue();
            String targetFieldName;
            if (targetField.isAnnotationPresent(DataAlias.class)) {
                DataAlias dataAlias = targetField.getAnnotation(DataAlias.class);
                targetFieldName = dataAlias.value();
            } else {
                targetFieldName = targetEntry.getKey();
            }

            Field sourceField = sourceFieldMap.get(targetFieldName);

            // 如果source和target对象，属性值能直接匹配的话，就直接copy
            if (sourceField != null) {
                Object value = ReflectUtil.getFieldValue(source, sourceField);
                ReflectUtil.setFieldValue(target, targetField, value);
            }
            // 如果target对象中的属性是一个完整的对象，或者list, 就需要增加DataGenerate处理
            // 要知道如果我们对象很简单数据填充的时候只需要mapstruct将id转换成空对象即可
            // 但是超过两层结构，我们的数据其实还没有加载，那么就需要DataGenerate来进行解决了
            else if (targetField.isAnnotationPresent(DataGenerate.class)) {
                DataGenerate dataGenerate = targetField.getAnnotation(DataGenerate.class);

                sourceField = sourceFieldMap.get(dataGenerate.value());
                Object sourceFieldValue = ReflectUtil.getFieldValue(source, sourceField);

                // 如果是id生成的默认形式，会根据我们声明的目标对象类型，生成空对象
                // 然后去source对象中去取指定的属性值填充到空对象内
                // 通俗的讲 就是id属性转换成一个对象，对象中仅仅id属性有值
                if (dataGenerate.mode() == IDGenerateMode.DEFAULT) {
                    // 根据要生成空对象的Field生成实例
                    Object targetFieldValue = ReflectUtil.newInstance(targetField.getType());

                    // 生成实例后将主键信息填写进去
                    Field subTargetField = ReflectUtil.getField(targetFieldValue.getClass()
                            , dataGenerate.targetField());

                    if (targetField != null) {
                        ReflectUtil.setFieldValue(targetFieldValue, subTargetField, sourceFieldValue);
                    }
                    ReflectUtil.setFieldValue(target, targetField, targetFieldValue);

                } else if (dataGenerate.mode() == IDGenerateMode.MULTIPLE
                        && dataGenerate.listClass() != void.class) {
                    // 这里主要是处理数据库中一个字段存了多个id类似 aa,bb,cc这种
                    // 而我们需要将多个id生成为list集合， 方便填充框架下一步工作
                    String idsStr = (String) sourceFieldValue;
                    // 数据库的主键分割
                    List<String> ids = Arrays.asList(idsStr.split(dataGenerate.listSeparator()));
                    List list = new ArrayList();
                    for (String id : ids) {
                        Object targetFieldValue = ReflectUtil.newInstance(dataGenerate.listClass());
                        // 生成实例后将主键信息填写进去
                        Field subTargetField = ReflectUtil.getField(targetFieldValue.getClass()
                                , dataGenerate.targetField());

                        if (subTargetField != null) {
                            ReflectUtil.setFieldValue(targetFieldValue, targetField, id);
                        }
                        list.add(targetFieldValue);
                    }
                    ReflectUtil.setFieldValue(target, targetField, list);
                }
            }
        }
        // 如果非基本类型定义了el表达式，不会取返回参数，会执行用户自定义的逻辑
        if(!StringUtils.isEmpty(spEl)){
            parserSpEl(source, target, spEl, Void.class);
        }
    }

    public Object getMapObjById(Object id, Map map){
        return map.get(id);
    }

    public Object getPageDtoById(Object id , PageDto result
            , Class<? extends Annotation> idAnnotation){
        return getCollectionObjById(id, result.getData(), idAnnotation);
    }

    public Object getObjById(Object id, Object container, Class<? extends Annotation> idAnnotation){
        if(container instanceof Map){
            return getMapObjById(id, (Map) container);
        }else if (container instanceof Collection){
            return getCollectionObjById(id, (Collection) container, idAnnotation);
        }else if(container instanceof PageDto){
            return getPageDtoById(id, (PageDto) container, idAnnotation);
        }else{
            throw new CheckException("不支持的容器!");
        }
    }

    /**
     * 根据id获取List中符合的元素
     *  默认取id的列对比，如果没有叫做id的列则取@TableId的列对比
     * @param id
     * @param collection
     * @param idAnnotation 如果id不是主键默认名称，应该以什么注解进行判断
     * @return
     */
    @SuppressWarnings("all")
    public Object getCollectionObjById(Object id, Collection collection, Class<? extends Annotation> idAnnotation){

        if(collection == null || collection.size() == 0){
            return null;
        }

        Class<?> elementClass =  collection.stream().findFirst().get().getClass();
        Map<String, Field> fieldMap = ReflectUtil.getFieldMap(elementClass);

        Field field = fieldMap.get("id");

        if(field == null){

            Optional<Field> first = fieldMap.values()
                    .stream()
                    .filter(f -> f.isAnnotationPresent(idAnnotation))
                    .findFirst();

            field = first.isPresent()?first.get():null;

            if(field == null){
                throw new CheckException(elementClass.getName() + "请增加注解" + idAnnotation.getName()+"标记主键！");
            }
        }

        final Field conditionField = field;

        Optional first = collection.stream().filter(obj -> Objects.equals(id, ReflectUtil.getFieldValue(obj, conditionField)))
                .findFirst();

        return first.isPresent()?first.get():null;
    }

    public static DataCopy getInstance(){
        return context.getBean(DataCopy.class);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
        expressionParser = new SpelExpressionParser();
    }
}
