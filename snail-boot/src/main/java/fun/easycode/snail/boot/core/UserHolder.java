package fun.easycode.snail.boot.core;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * UserHolder
 *  用户持有者，用来操作当前用户信息
 * @author xuzhe
 */
public abstract class UserHolder<T extends UserInfo> implements ApplicationContextAware {

    private static ApplicationContext context;

    /**
     * 获取UserHolder实例
     * @return UserHolder
     */
    public static <T extends UserInfo> UserHolder<T> getInstance(){
        return context.getBean(UserHolder.class);
    }

    /**
     * 获取当前登录用户
     * @return 登录用户
     */
    public T getLoginUser(){
        throw new CheckException("先重写getUser方法!");
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    /**
     * 子类可以获取context
     * @return ApplicationContext
     */
    protected static ApplicationContext getContext(){
        return context;
    }

    /**
     * 默认的UserHolder
     */
    public static class DefaultUserHolder extends UserHolder{
        @Override
        public UserInfo getLoginUser() {
            return () -> "snail-boot";
        }
    }
}
