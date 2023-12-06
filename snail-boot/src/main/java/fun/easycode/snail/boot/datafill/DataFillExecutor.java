package fun.easycode.snail.boot.datafill;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.HashUtil;
import cn.hutool.core.util.ReflectUtil;
import fun.easycode.snail.boot.core.PageDto;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据填充工厂
 *
 * @author xuzhe
 */
@Slf4j
public class DataFillExecutor {

    /**
     * 填充框架执行
     *
     * @param o 填充对象
     */
    public void execute(Object o) {
        List<DataFillMetadata> metadataList = new ArrayList<>();
        fetch(o, metadataList);
        executeMetaDataList(metadataList);
    }

    private void executeMetaDataList(List<DataFillMetadata> metadataList) {

        executeDataFill(metadataList);
        List<DataFillMetadata> metadataListSub = new ArrayList<>();

        for (DataFillMetadata metadata : metadataList) {
            fetch(metadata.getTargetObjMap().values(), metadataListSub);
        }
        if (metadataListSub.size() > 0) {
            executeMetaDataList(metadataListSub);
        }
    }

    private void executeDataFill(List<DataFillMetadata> metadataList) {
        //异步任务
        List<DataFillTask<List<Object>>> tasks = new ArrayList<>();

        // 现根据sourceClass进行分组
        // 实际上就是BaseMapper或者FeignContract的实现
        // 其它实现不支持，默认不处理
        Map<Class<?>, List<DataFillMetadata>> anClassMetadatas = metadataList.stream()
                .collect(Collectors.groupingBy(dataFillMetadata -> dataFillMetadata.getDataFill().source()));

        // 根据params hash 进行group分组生成task
        anClassMetadatas.forEach((sourceClass, sourceClassMetadataList) -> {
            sourceClassMetadataList.stream().collect(Collectors.groupingBy(this::hashParams)).values()
                    .forEach(hashMetadataList -> {
                        DataFillStrategyContext.getStrategy(sourceClass).ifPresent(strategy -> {
                            tasks.add(new DataFillTask<>(strategy, hashMetadataList));
                        });
                    });
        });

        try {
            DataFillThreadPoolManager.execParallelTasks(tasks);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 填充信息拉取
     *
     * @param o            处理对象
     * @param metadataList 元数据集合
     */
    @SuppressWarnings("all")
    private void fetch(Object o, List<DataFillMetadata> metadataList) {
        if (o == null || metadataList == null) {
            return;
        }
        if (o instanceof Collection) {
            for (Object o1 : ((Collection) o)) {
                fetch(o1, metadataList);
            }
        } else if (o instanceof Map) {
            for (Object value : ((Map) o).values()) {
                fetch(value, metadataList);
            }
        } else if (o instanceof PageDto) {
            PageDto pageDto = (PageDto) o;
            fetch(pageDto.getData(), metadataList);
        } else {
            Class oClass = o.getClass();
            // 这里抛出的异常其最终目的就是中断当前集合的遍历,
            // 如果集合容器中第一个节点到达这里并判断没有填充标识，则当前容器就不需要在继续遍历下去了
            if (oClass.getAnnotation(EnableDataFill.class) == null) {
                return;
            }

            // 如果传入操作对象是基本数据类型或者String不进行下面的逻辑
            if (ClassUtil.isBasicType(oClass)
                    || oClass.isAssignableFrom(String.class)) {
                return;
            }

            Map<String, Field> fieldMap = ReflectUtil.getFieldMap(oClass);

            for (Map.Entry<String, Field> entry : fieldMap.entrySet()) {

                Field field = entry.getValue();

                // 非基本类型而且拥有DataFill的注解才代表需要处理
                DataFill dataFill = field.getAnnotation(DataFill.class);

                // 字段上未增加DataFill注解，代表字段不需要处理
                if (dataFill == null) {
                    continue;
                }

                Object fieldValue = ReflectUtil.getFieldValue(o, field);

                if (ClassUtil.isBasicType(field.getType())
                        || field.getType().isAssignableFrom(String.class)) {
                    // 基本类型或者String 给fieldValue复制null, 方便下边的判断
                    fieldValue = null;
                } else {
                    if (fieldValue == null) {
                        continue;
                    }
                }

                DataFillMetadata dataFillMetadata = new DataFillMetadata();
                dataFillMetadata.setDataFill(dataFill);
                dataFillMetadata.setOpObj(o);
                dataFillMetadata.setOpField(field);

                Map<Object, Object> targetObjMap = new HashMap<>();

                // DataFill可以依附的对象
                // - Collection
                // - 普通对象
                // - 基本类型
                if (fieldValue == null) {
                    // 如果要操作的field是基本数据类型或者String
                    // 对象或者List都是去对象、List内对象里面的主键属性
                    // 但是基本数据类型取的是opObj操作对象的属性
                    // 这里是结合上面的判断得出的fieldValue == null 是基本数据类型或者String
                    Field primaryKeyField = fieldMap.get(dataFill.value());
                    Object primaryKeyValue = ReflectUtil.getFieldValue(o, primaryKeyField);
                    targetObjMap.put(primaryKeyValue, null);
                } else if (fieldValue instanceof Collection) {
                    for (Object targetObj : (Collection) fieldValue) {
                        Field primaryKeyField = ReflectUtil.getField(targetObj.getClass(), dataFill.value());
                        Object primaryKeyValue = ReflectUtil.getFieldValue(targetObj, primaryKeyField);
                        targetObjMap.put(primaryKeyValue, targetObj);
                    }
                } else {
                    // 如果是普通对象
                    Field primaryKeyField = ReflectUtil.getField(fieldValue.getClass(), dataFill.value());
                    Object primaryKeyValue = ReflectUtil.getFieldValue(fieldValue, primaryKeyField);

                    targetObjMap.put(primaryKeyValue, fieldValue);
                }

                dataFillMetadata.setTargetObjMap(targetObjMap);

                metadataList.add(dataFillMetadata);
            }
        }
    }

    /**
     * 根据DataParam[] 参数列表计算一个hash
     * 用于分组，不同的条件需要不同的查询
     *
     * @param metadata DataFillMetadata
     * @return long hash
     */
    public long hashParams(DataFillMetadata metadata) {
        DataParam[] params = metadata.getDataFill().params();
        Map<String, DataParam> paramMap = Arrays.stream(params)
                .collect(Collectors.toMap(DataParam::name, p -> p));

        List<String> paramNameList = Arrays.stream(params).map(DataParam::name)
                .sorted().collect(Collectors.toList());
        String grouping = paramNameList.stream().map(paramName -> paramName + paramMap.get(paramName))
                .collect(Collectors.joining());
        return HashUtil.mixHash(grouping);
    }
}
