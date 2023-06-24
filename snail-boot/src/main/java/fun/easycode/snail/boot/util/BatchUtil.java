package fun.easycode.snail.boot.util;

import fun.easycode.snail.boot.core.SnailBootMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;

/**
 * 批处理工具类
 *  必须被spring管理才可以使用
 * @author xuzhen97
 */
@Slf4j
public class BatchUtil {
    /**
     * 批处理数量
     */
    private static final int BATCH = 1000;

    /**
     * MyBatis SqlSessionFactory
     */
    private static SqlSessionFactory SQL_SESSION_FACTORY;

    public BatchUtil(SqlSessionFactory sqlSessionFactory) {
        SQL_SESSION_FACTORY = sqlSessionFactory;
    }

    /**
     * 批量插入方法
     *
     * @param data     需要被处理的数据
     * @param mapper 实体类的mapper
     * @return int 影响的总行数
     */
    public static <T> int saveBatch(List<T> data, SnailBootMapper<T> mapper) {

        if(SQL_SESSION_FACTORY == null){
            throw new RuntimeException("SQL_SESSION_FACTORY is null, 请先使用spring管理BatchUtil");
        }

        int count = 0;
        SqlSession batchSqlSession = SQL_SESSION_FACTORY.openSession(ExecutorType.BATCH);
        try {
            for (int index = 0; index < data.size(); index++) {
                count += mapper.insert(data.get(index));
                if (index != 0 && index % BATCH == 0) {
                    batchSqlSession.flushStatements();
                }
            }
            batchSqlSession.commit();
        } catch (Exception e) {
            batchSqlSession.rollback();
            log.error(e.getMessage(), e);
        } finally {
            batchSqlSession.close();
        }
        return count;
    }
}
