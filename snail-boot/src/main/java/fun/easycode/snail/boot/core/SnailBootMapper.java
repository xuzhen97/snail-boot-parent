package fun.easycode.snail.boot.core;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 通用mapper
 * 在原版的mapper上增加了批量操作和流式查询
 * @author xuzhen97
 * @param <T>
 */
public interface SnailBootMapper<T> extends BaseMapper<T>, StreamMapper<T>, BatchMapper<T>{

}
