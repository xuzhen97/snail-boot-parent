package fun.easycode.snail.boot.core;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author xuzhe
 */
public class ExecutorContext implements ApplicationContextAware {

    private static ApplicationContext context;

    /**
     * 根据执行器class获取执行器实例
     * @param executorClass 执行器class
     * @return 执行器实例
     * @param <T> 执行器class
     */
    public static <T extends Executor<K,R>,K,R> T get(Class<T> executorClass){
        return context.getBean(executorClass);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
}
