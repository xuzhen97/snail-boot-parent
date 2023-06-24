package fun.easycode.snail.boot.core;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 当产生异常的时候需要用到的dto
 * @author xuzhe
 */
@AllArgsConstructor
@Getter
public class ErrorDto{
    private Integer code;
    private String message;
}