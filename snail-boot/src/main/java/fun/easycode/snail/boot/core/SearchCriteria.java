package fun.easycode.snail.boot.core;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 查询条件
 * @author xuzhen97
 */
@Data
@AllArgsConstructor
public class SearchCriteria {
    /**
     * 查询字段
     */
    private String key;
    /**
     * 查询条件
     */
    private String operation;
    /**
     * 查询值
     */
    private Object value;
}