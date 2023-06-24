package fun.easycode.snail.boot.test.adapter.api;

import fun.easycode.snail.boot.core.Enumerator;

/**
 * @author xuzhen97
 */

public enum UserEnum implements Enumerator<Integer> {
    STATE1(1, "状态1"),
    STATE2(2, "状态2"),
    ;

    private Integer value;
    private String desc;

    UserEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @Override
    public String getDesc() {
        return desc;
    }

    @Override
    public Integer getValue() {
        return value;
    }
}
