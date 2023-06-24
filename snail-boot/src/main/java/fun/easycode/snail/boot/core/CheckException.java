package fun.easycode.snail.boot.core;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 通用逻辑异常
 * @author xuzhen97
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CheckException extends RuntimeException{

    /**
     * 通用逻辑错误码
     * @Author: xuzhen97
     * @Date:  2023/03/22
     */
    public final static Integer DEFAULT_CODE = 40001;

    /**
     * 错误码
     * @Author: xuzhen97
     * @Date:  2023/03/22
     */
    private final Integer code;
    private final String message;

    /**
     * 通用逻辑异常
     * @param message 错误信息
     */
    public CheckException(String message){
        super(message);
        this.code = DEFAULT_CODE;
        this.message = message;
    }

    /**
     * 通用逻辑异常
     * @param code 错误码
     * @param message 错误信息
     */
    public CheckException(Integer code, String message){
        super(message);
        this.code = code;
        this.message = message;
    }

    /**
     * 返回错误信息
     * @return 错误信息
     */
    public ErrorDto error(){
        return new ErrorDto(code, message);
    }

    /**
     * 返回错误信息
     * @param code 错误码
     * @param message 错误信息
     * @return 错误信息
     */
    public static ErrorDto error(Integer code, String message){
        return new ErrorDto(code, message);
    }

    /**
     * 返回错误信息
     * @param msg 错误信息
     * @return 错误信息
     */
    public static ErrorDto error(String msg){
        return new ErrorDto(DEFAULT_CODE, msg);
    }
}
