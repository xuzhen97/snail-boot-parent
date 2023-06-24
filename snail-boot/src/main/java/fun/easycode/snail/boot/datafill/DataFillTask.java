package fun.easycode.snail.boot.datafill;

import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 数据框架填充任务
 * @author xuzhen97
 */
@Slf4j
public class DataFillTask<T,E> implements Callable<Void> {

    private final DataFillProcess<T,E> process;
    private final Collection<T> data;

    public DataFillTask(DataFillProcess<T,E> process, Collection<T> data) {
        this.process = process;
        this.data = data;
    }
    @Override
    public Void call() throws Exception {
        Function<T, ?> keyGetFun = process.getKeyGetFun();
        Function<Collection<?>, Collection<E>> fillDataGetFun = process.getFillDataGetFun();
        Function<E, ?> fillKeyGetFun = process.getFillKeyGetFun();
        BiConsumer<T, E> fillFun = process.getFillFun();
        // 获取keys
        List<?> keys = data.stream().map(keyGetFun).collect(Collectors.toList());
        // 获取填充数据
        Collection<E> fillData = fillDataGetFun.apply(keys);
        // 处理填充数据成key map 方便查找
        Map<Object, E> idFillDataMap = fillData.stream().collect(Collectors.toMap(fillKeyGetFun, e -> e));

        // 填充数据
        data.stream().filter(d-> idFillDataMap.containsKey(keyGetFun.apply(d))).forEach(d -> {
            E e = idFillDataMap.get(keyGetFun.apply(d));
            fillFun.accept(d, e);
        });
        return null;
    }
}
