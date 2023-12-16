package fun.easycode.snail.boot.core;

import lombok.Getter;

/**
 * 统一返回值
 * @author xuzhe
 */
@Getter
public class R<T> {

    /**
     * 通用逻辑错误码
     */
    public final static Integer DEFAULT_CODE = 10001;

    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 消息提示，code=0是成功提示，其它都是错误提示
     */
    private String message;

    /**
     * 返回数据
     */
    private T data;

    private R(Integer code, String message){
        this.code = code;
        this.message = message;
    }

    private R(Integer code, String message, T data){
        this.code = code;
        this.message = message;
        this.data = data;
    }

    private R(Integer code, T data){
        this.code = code;
        this.data = data;
    }

    /**
     * 成功返回信息
     * @param data 数据对象
     * @return R
     * @param <T> 数据对象类型
     */
    public static <T> R<T> success(T data){
        return new R<>(0, data);
    }

    /**
     * 成功返回信息，只返回一个中文提示
     * @param message 成功信息
     * @return R
     */
    public static R<?> message(String message){
        return new R<>(0, message);
    }

    /**
     * 成功返回信息，提示 + 数据
     * @param message 成功信息
     * @param data 数据对象
     * @return R
     * @param <T> 数据对象类型
     */
    public static <T> R<T> success(String message, T data){
        return new R<T>(0, message, data);
    }

    /**
     * 错误返回结果，自定义错误码，错误信息，不需要返回数据对象
     * @param code 错误码
     * @param message 错误五信息
     * @return R
     */
    public static  R<?> error(Integer code, String message){
        return new R<>(code, message);
    }

    /**
     * 错误返回结果，自定义错误码，自定义错误信息，自定义返回值
     * @param code 错误码
     * @param message 错误信息
     * @param data 错误返回值
     * @return R
     * @param <T> 数据对象类型
     */
    public static <T>  R<T> error(Integer code, String message, T data){
        return new R<>(code, message, data);
    }

    /**
     * 错误返回结果，只返回错误信息
     * 使用默认错误码00001
     * @param message 错误信息
     * @return R
     */
    public static R<?> error(String message){
        return new R<>(DEFAULT_CODE, message);
    }

    /**
     * 错误返回结果，带错误信息，附带其它返回值
     * 使用默认错误码 00001
     * @param message 错误信息
     * @param data 错误想要返回的对象信息
     * @return R
     * @param <T> 数据对象类型
     */
    public static <T> R<T> error(String message, T data){
        return new R<>(DEFAULT_CODE, message, data);
    }

}