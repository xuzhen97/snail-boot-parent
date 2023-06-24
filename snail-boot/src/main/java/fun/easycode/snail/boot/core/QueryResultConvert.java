package fun.easycode.snail.boot.core;

/**
 * 查询结果转换
 * @author xuzhe
 */
@FunctionalInterface
public interface QueryResultConvert<T,R> {
    /**
     * 数据库实体T转换成返回结果R
     * @param entity 数据库实体
     * @return R
     */
    R to(T entity);
}
