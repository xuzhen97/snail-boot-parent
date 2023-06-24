package fun.easycode.snail.boot.core;

/**
 * 执行器接口
 * @author xuzhe
 */
public interface Executor<K,R> {

    /**
     * 执行器执行，输入指令cmd返回结果R
     * @param cmd 指令
     * @return 执行结果
     */
    R execute(K cmd);

}
