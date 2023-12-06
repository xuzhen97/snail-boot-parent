package fun.easycode.snail.boot.core;

import fun.easycode.snail.boot.datafill.DataFillExecutor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import javax.annotation.Resource;

/**
 * interceptor 用于数据填充
 * @author xuzhe
 */
public class ExecutorDataFillInterceptor implements MethodInterceptor {

    @Resource
    private DataFillExecutor dataFillExecutor;

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Object result = methodInvocation.proceed();
        dataFillExecutor.execute(result);
        return result;
    }
}
