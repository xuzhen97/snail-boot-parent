package fun.easycode.snail.boot.datafill;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

/**
 * 数据填充策略
 *
 * @author xuzhe
 */
public interface DataFillStrategy {

    /**
     * 填充逻辑
     *
     * @param metadataList 元数据集合
     */
    void fill(List<DataFillMetadata> metadataList);

    /**
     * 获取可以处理的类型Class
     *
     * @return Class
     */
    default Class<?> getType() {
        return void.class;
    }

    /**
     * 获取注解，数据源对象上增加注解也可以
     * @return
     */
    default Class<? extends Annotation> getAnnotation(){
        return null;
    }

    /**
     * 基本数据类型set逻辑
     * @param metadata
     * @param container
     * @param ofAnnoClass
     * @param <T>
     */
    default <T extends Annotation> void basicTypeSet(DataFillMetadata metadata, Object container , Class<T> ofAnnoClass){
        Object primaryKey = metadata.getTargetObjMap().keySet().stream().findFirst().get();
        Object databaseObj = DataCopy.getInstance().getObjById(primaryKey, container, ofAnnoClass);

        // 查找不到填充对象则本地处理结束
        if(databaseObj == null){
            return;
        }

        // 如果返回值是基本数据类型那么就直接就是值
        if(ClassUtil.isBasicType(databaseObj.getClass())
                || databaseObj.getClass().isAssignableFrom(String.class)){
            ReflectUtil.setFieldValue(metadata.getOpObj(), metadata.getOpField(), databaseObj);
        }else {

            Object value = null;

            // 如果用户自定义了spEl表达式，那么完全以el表达式的结果作为value
            // 如果没有定义spEl表达式则查找同名的属性进行填充
            if (!StringUtils.isEmpty(metadata.getDataFill().spEl())) {
                Optional<?> valueOptional = DataCopy.getInstance().parserSpEl(
                        databaseObj,
                        null,
                        metadata.getDataFill().spEl(),
                        metadata.getOpField().getType());
                value = valueOptional.orElse(null);
            } else {
                Field databaseObjField = ReflectUtil.getField(databaseObj.getClass()
                        , metadata.getOpField().getName());

                if (databaseObjField != null) {
                    value = ReflectUtil.getFieldValue(databaseObj, databaseObjField);
                }
            }
            ReflectUtil.setFieldValue(metadata.getOpObj(), metadata.getOpField(), value);
        }
    }

    /**
     * 处理返回结构
     * @param metadata 元数据
     * @param container 数据源查询后返回的容器，就是list或者map
     * @param idAnno 当返回的数据主键不是id的时候，用什么注解进行的标注
     */
    default void handlerResult(DataFillMetadata metadata, Object container, Class<? extends Annotation> idAnno){
        // 如果目标对象是基本类型，我们直接去找source中有没有重名的属性
        // 有就取值
        if (ClassUtil.isBasicType(metadata.getOpField().getType())
                || metadata.getOpField().getType().isAssignableFrom(String.class)) {
            basicTypeSet(metadata, container, idAnno);
        }else{
            metadata.getTargetObjMap().forEach((key, value) -> {
                Object databaseObj = DataCopy.getInstance().getObjById(key, container, idAnno);
                if (databaseObj != null) {
                    DataCopy.getInstance().copy(databaseObj
                            , value, metadata.getDataFill().spEl());
                }
            });
        }
    }
}
