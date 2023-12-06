package fun.easycode.snail.boot.datafill;

/**
 * 需要填充的id字段要做的处理
 * 因为有些是,分割的所以就需要处理，正常的1对多就没有问题
 * @author xuzhe
 */
public enum IDGenerateMode {
    /**
     * 默认不做任何处理
     */
    DEFAULT,
    /**
     * 多个，认为一个字段中有多个Id是用,号分割
     * 排序也会默认根据id的排序进行排序
     */
    MULTIPLE
}
