package fun.easycode.snail.boot.core;

import org.apache.ibatis.annotations.Param;

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
    int insertBatchSomeColumn(List<T> list);

    /**
     * 批量replace into
     * @param list
     * @return
     */
    int replaceBatchSomeColumn(List<T> list);

    /**
     * 批量插入或更新
     *  insert into table (id, name) values (1, 'a'), (2, 'b') on duplicate key update name = values(name)
     * @param list 数据集
     * @return 影响行数
     */
    int insertOrUpdateBatch(@Param("list") List<T> list);
}
