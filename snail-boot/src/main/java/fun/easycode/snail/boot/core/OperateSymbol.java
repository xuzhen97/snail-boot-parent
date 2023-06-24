package fun.easycode.snail.boot.core;

/**
 * 操作符枚举
 * @author xuzhen97
 */
public enum OperateSymbol implements Enumerator<String>{
    /**
     * 等于
     */
    EQ("Eq", "等于"),
    /**
     * 模糊查询
     */
    LIKE("Like", "模糊查询"),
    /**
     * 模糊查询左
     */
    LIKE_LEFT("LikeLeft", "模糊查询左"),
    /**
     * 模糊查询右
     */
    LIKE_RIGHT("LikeRight", "模糊查询右"),
    /**
     * 大于
     */
    GT("Gt", "大于"),
    /**
     * 小于
     */
    LT("Lt", "小于"),
    /**
     * 大于等于
     */
    GE("Ge", "大于等于"),
    /**
     * 小于等于
     */
    LE("Le", "小于等于"),
    /**
     * in 集合查询
     */
    IN("In", "in 集合查询"),
    /**
     * not in 集合查询
     */
    NOT_IN("NotIn", "not in 集合查询"),
    /**
     * is null 查询
     */
    IS_NULL("IsNull", "is null 查询"),
    /**
     * 排序
     */
    SORT("Sort", "排序");
    private final String value;
    private final String desc;

    OperateSymbol(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getDesc() {
        return desc;
    }

}
