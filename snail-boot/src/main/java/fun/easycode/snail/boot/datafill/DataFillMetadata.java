package fun.easycode.snail.boot.datafill;

import lombok.Data;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * 元数据填充类型
 * @author xuzhe
 */
@Data
public class DataFillMetadata {

    /**
     * 数据填充操作对象
     */
    private Object opObj;
    /**
     * 数据填充操作属性
     */
    private Field opField;

    /**
     * 数据填充目标对象
     * key = 主键
     * value = 仅有主键属性的空对象
     */
    private Map<Object,Object> targetObjMap;

    /**
     * 数据填充操作属性上标注的@DataFill注解
     */
    private DataFill dataFill;
}
