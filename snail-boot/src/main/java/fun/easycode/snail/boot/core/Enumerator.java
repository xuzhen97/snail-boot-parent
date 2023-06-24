package fun.easycode.snail.boot.core;

import com.baomidou.mybatisplus.annotation.IEnum;

import java.io.Serializable;

/**
 * 自定义枚举器
 *  主要用于数据返回的时候自定义枚举实现
 *  还有就是jackson的配置，json序列化返回对象，属性存在value和desc
 * @author xuzhe
 * @param <T>
 */
public interface Enumerator<T extends Serializable> extends IEnum<T> {

    /**
     * 获取枚举说明
     * @return String
     */
    String getDesc();
}
