package fun.easycode.snail.boot.core;

import cn.hutool.core.util.ClassUtil;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 自定义aop切点
 *  当是Executor时切入，execute时执行逻辑
 * @author xuzhe
 */
public class ExecutorDataFillPointcut implements Pointcut {
    @Override
    public ClassFilter getClassFilter() {
        return new ClassFilter() {
            @Override
            public boolean matches(Class<?> aClass) {

                return ClassUtil.isAssignable(Executor.class, aClass);
            }
        };
    }

    @Override
    public MethodMatcher getMethodMatcher() {
        return new MethodMatcher() {
            @Override
            public boolean matches(Method method, Class<?> aClass) {
                return Objects.equals("execute", method.getName());
            }

            @Override
            public boolean isRuntime() {
                return false;
            }

            @Override
            public boolean matches(Method method, Class<?> aClass, Object... objects) {
                return false;
            }
        };
    }
}
