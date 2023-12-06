package fun.easycode.snail.boot.datafill;

import com.alibaba.ttl.TtlCallable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author xuzhe
 */
@Slf4j
public class DataFillThreadPoolManager {

    /**
     * 异步任务线程池,最大队列数,无需即使性
     */
    public static final ThreadPoolExecutor executor =
            new ThreadPoolExecutor(50
                    , 200
                    , 2L
                    , TimeUnit.SECONDS
                    , new LinkedBlockingQueue<>()
                    , new ThreadPoolExecutor.AbortPolicy());

    @ManagedAttribute
    public int getCorePoolSize() {
        return executor.getCorePoolSize();
    }

    @ManagedAttribute
    public int getMaximumPoolSize(){
        return executor.getMaximumPoolSize();
    }

    /**
     * 设置线程池初始数量
     * @param corePoolSize 初始数量
     */
    @ManagedAttribute
    public void setCorePoolSize(int corePoolSize){
        executor.setCorePoolSize(corePoolSize);
    }

    /**
     * 设置线程池最大数量
     * @param maximumPoolSize 最大数量
     */
    @ManagedAttribute
    public void setMaximumPoolSize(int maximumPoolSize){
        executor.setMaximumPoolSize(maximumPoolSize);
    }

    /**
     * 打印填充线程池基本信息
     */
    @ManagedOperation
    public void printPoolExecutorInfo() {
        log.info("填充线程池corePoolSize:{}、maximumPoolSize:{}.", executor.getCorePoolSize(), executor.getMaximumPoolSize());
    }

    /**
     * 执行异步任务，会等待全部执行完成
     * @param tasks 任务集合
     * @param <T> 返回值
     * @throws Exception 异常
     */
    public static <T> void execParallelTasks(Collection<? extends Callable<T>> tasks)
            throws Exception {
        // 使用Ttl标记，为了TransmittableThreadLocal的变量能够传递
        // 主要是为了中转用户信息
        List<TtlCallable<T>> ttlCallables = tasks.stream().map(TtlCallable::get)
                .collect(Collectors.toList());
        List<Future<T>> futures = executor.invokeAll(ttlCallables);

        for (Future<T> future : futures) {
            future.get();
        }
    }

}
