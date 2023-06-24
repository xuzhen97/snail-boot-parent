package fun.easycode.snail.boot.core;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.ResultHandler;

/**
 * 流式查询mapper
 * @param <T>
 * @author xuzhen97
 */
public interface StreamMapper<T>{
    /**
     * 查询结果流式处理
     * @param wrapper 条件构造器
     * @param handler 结果处理器
     */
    void streamQuery(@Param(Constants.WRAPPER) Wrapper<T> wrapper, ResultHandler<T> handler);
    /**
     * 查询结果流式处理
     * @param customSql 自定义sql
     * @param handler 结果处理器
     */
    void streamQuerySql(@Param("customSql") String customSql, ResultHandler<T> handler);
}
