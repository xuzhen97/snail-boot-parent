package fun.easycode.snail.boot.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.logging.Log;

/**
 * @Description: mybatis-plus sql日志打印
 * @Author: xuzhen97
 */
@Slf4j
public class MyBatisPlusSQLLog implements Log {

    private static Boolean isSQLLog = true;

    public MyBatisPlusSQLLog(String clazz) {
    }

    /**
     * @Description: 是否打印sql日志
     * @Author: xuzhen97
     * @Date:  2023/03/20
     */
    public static void setIsSQLLog(Boolean isSQLLog) {
        MyBatisPlusSQLLog.isSQLLog = isSQLLog;
    }

    @Override
    public boolean isDebugEnabled() {
        return true;
    }

    @Override
    public boolean isTraceEnabled() {
        return true;
    }

    @Override
    public void error(String s, Throwable e) {
        log.error(s, e);
    }

    @Override
    public void error(String s) {
        log.error(s);
    }

    @Override
    public void debug(String s) {
        if(isSQLLog) {
            log.info(s);
        }
    }

    @Override
    public void trace(String s) {
        if(isSQLLog) {
            log.trace(s);
        }
    }

    @Override
    public void warn(String s) {
        log.warn(s);
    }
}
