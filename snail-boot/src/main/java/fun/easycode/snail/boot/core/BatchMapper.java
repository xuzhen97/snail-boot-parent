package fun.easycode.snail.boot.core;

import java.util.List;

/**
 * 批量操作mapper
 * @param <T>
 * @author xuzhen97
 */
public interface BatchMapper<T> {
    /**
     * 批量插入insert
     *
     * @param list 实体列表
     * @return 影响行数
     */
    Integer insertBatchSomeColumn(List<T> list);

    /**
     * 批量replace into
     * @param list
     * @return
     */
    Integer replaceBatchSomeColumn(List<T> list);
}
