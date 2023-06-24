package fun.easycode.snail.boot.datafill;

import lombok.Getter;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * 数据填充过程
 * @author xuzhen97
 */
@Getter
public class DataFillProcess<T,E> {

    // key获取函数
    private final Function<T, ?> keyGetFun;
    // 填充数据获取函数
    private final Function<Collection<?>, Collection<E>> fillDataGetFun;
    // 填充数据key获取函数
    private final Function<E,?> fillKeyGetFun;
    // 填充函数
    private final BiConsumer<T, E> fillFun;

    /**
     * 构造函数
     * @param keyGetFun key获取函数
     * @param fillDataGetFun 填充数据获取函数
     * @param fillKeyGetFun 填充数据key获取函数
     * @param fillFun 填充函数
     */
    protected DataFillProcess(Function<T, ?> keyGetFun, Function<Collection<?>, Collection<E>> fillDataGetFun, Function<E, ?> fillKeyGetFun, BiConsumer<T, E> fillFun) {
        this.keyGetFun = keyGetFun;
        this.fillDataGetFun = fillDataGetFun;
        this.fillKeyGetFun = fillKeyGetFun;
        this.fillFun = fillFun;
    }
}
