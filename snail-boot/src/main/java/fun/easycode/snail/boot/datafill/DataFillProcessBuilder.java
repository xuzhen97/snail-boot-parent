package fun.easycode.snail.boot.datafill;

import cn.hutool.core.util.ReflectUtil;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 数据填充过程构建器
 * @param <T> 数据类型
 * @param <E> 填充逻辑远程返回的数据类型
 * @author xuzhen97
 */
public class DataFillProcessBuilder<T,E> {

    // key获取函数
    private Function<T, ?> keyGetFun;
    // 填充数据获取函数
    private Function<Collection<?>, Collection<E>> fillDataGetFun;
    // 填充数据key获取函数
    private Function<E,?> fillKeyGetFun;
    // 填充函数
    private BiConsumer<T, E> fillFun;

    /**
     * 设置key获取函数
     * @param keyGetFun key获取函数
     * @return this
     */
    public DataFillProcessBuilder<T,E> keyGetFun(Function<T, ?> keyGetFun) {
        this.keyGetFun = keyGetFun;
        return this;
    }

    /**
     * 设置填充数据获取函数
     * @param fillDataGetFun 填充数据获取函数
     * @return this
     */
    public DataFillProcessBuilder<T,E> fillDataGetFun(Function<Collection<?>, Collection<E>> fillDataGetFun) {
        this.fillDataGetFun = fillDataGetFun;
        return this;
    }

    /**
     * 设置填充数据key获取函数
     * @param fillKeyGetFun 填充数据key获取函数
     * @return this
     */
    public DataFillProcessBuilder<T,E> fillKeyGetFun(Function<E,?> fillKeyGetFun) {
        this.fillKeyGetFun = fillKeyGetFun;
        return this;
    }

    /**
     * 设置填充函数
     * @param fillFun 填充函数
     * @return this
     */
    public DataFillProcessBuilder<T,E> fillFun(BiConsumer<T, E> fillFun) {
        this.fillFun = fillFun;
        return this;
    }

    /**
     * 构建
     * @return 数据填充过程
     */
    public DataFillProcess<T,E> build() {
        if (keyGetFun == null) {
            throw new IllegalArgumentException("keyGetFun不能为空");
        }
        if (fillDataGetFun == null) {
            throw new IllegalArgumentException("fillDataGetFun不能为空");
        }
        if(fillKeyGetFun == null){
            // 默认使用id字段
            fillKeyGetFun = e -> ReflectUtil.getFieldValue(e, "id");
        }
        if (fillFun == null) {
            throw new IllegalArgumentException("fillFun不能为空");
        }
        return new DataFillProcess<>(keyGetFun, fillDataGetFun,fillKeyGetFun, fillFun);
    }

    /**
     * 获取构建器
     * @param <T> 数据类型
     * @param <E> 填充逻辑远程返回的数据类型
     * @return 构建器
     */
    public static <T,E> DataFillProcessBuilder<T,E> builder() {
        return new DataFillProcessBuilder<>();
    }

    /**
     * 请求, 简化填充写法
     * @param supplier 请求函数
     * @param <RE> 返回类型
     * @return 返回值
     */
    public static <RE> RE request(Supplier<RE> supplier){
        return supplier.get();
    }
}
