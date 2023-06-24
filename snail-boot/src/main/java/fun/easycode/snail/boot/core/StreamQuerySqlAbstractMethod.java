package fun.easycode.snail.boot.core;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.mapping.*;

public class StreamQuerySqlAbstractMethod extends AbstractMethod {
    private static final String METHOD = "streamQuerySql";
    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        String sqlFormat = "<script>\nSELECT %s FROM %s %s \n</script>";

        String sql = String.format(sqlFormat, tableInfo.getAllSqlSelect(),
                tableInfo.getTableName(), "${customSql}");

        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        String statementName = mapperClass.getName() + DOT + METHOD;
        if (configuration.hasStatement(statementName, false)) {
            logger.warn(LEFT_SQ_BRACKET + statementName + "] Has been loaded by XML or SqlProvider or Mybatis's Annotation, so ignoring this injection for [" + getClass() + RIGHT_SQ_BRACKET);
            return null;
        }
        /* 缓存逻辑处理 */
        return builderAssistant.addMappedStatement(METHOD, sqlSource, StatementType.PREPARED, SqlCommandType.SELECT,
                Integer.MIN_VALUE, null, null, null, tableInfo.getResultMap() , modelClass,
                ResultSetType.FORWARD_ONLY, true, true, false, null, null, null,
                configuration.getDatabaseId(), languageDriver, null);
    }
}
