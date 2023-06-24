package fun.easycode.snail.boot.core;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 通用mapper
 * 在原版的mapper上增加了批量操作和流式查询
 * @param <T>
 */
public interface SnailBootMapper<T> extends BaseMapper<T>, StreamMapper<T>, BatchMapper<T>{

    /**
     * 批量插入或更新
     *  insert into table (id, name) values (1, 'a'), (2, 'b') on duplicate key update name = values(name)
     * @param list 数据集
     * @return 影响行数
     */
    int insertOrUpdateBatch(@Param("list") List<T> list);
}
