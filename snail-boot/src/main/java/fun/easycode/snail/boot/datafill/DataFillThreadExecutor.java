package fun.easycode.snail.boot.datafill;

import com.alibaba.ttl.TtlCallable;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 数据填充线程池
 * @author xuzhen97
 */
public final class DataFillThreadExecutor {

    /**
     * 异步任务线程池,最大队列数,无需及时返回的任务,请使用此线程池
     */
    public static final ThreadPoolExecutor executor =
            new ThreadPoolExecutor(50
                    , 200
                    , 2L
                    , TimeUnit.SECONDS
                    , new LinkedBlockingQueue<>()
                    , new ThreadPoolExecutor.AbortPolicy());

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
