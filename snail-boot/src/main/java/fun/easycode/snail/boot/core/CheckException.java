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
     * 错误码
     */
    private final Integer code;
    /**
     * 错误信息
     */
    private final String message;

    /**
     * 通用逻辑异常
     * @param message 错误信息
     */
    public CheckException(String message){
        super(message);
        this.code = R.DEFAULT_CODE;
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
}
