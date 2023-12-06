package fun.easycode.snail.boot.datafill;

import cn.hutool.core.util.ClassUtil;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;
import java.util.Optional;

/**
 * 填充策略Context
 * @author xuzhe
 */
public class DataFillStrategyContext implements ApplicationContextAware {

    // 实际上这里的key无用，因为判断条件需要ClassUtil.isAssignable判断
    // 并不是一个简单的key
    private static Map<String, DataFillStrategy> strategies;

    /**
     * 获取策略，就是根据DataFill上面的class
     * @param type 类型
     * @return 填充策略
     */
    public static Optional<DataFillStrategy> getStrategy(Class<?> type){
        // 填充策略寻找规则，注解和type的方式只要有一种即可
        return strategies.values().stream()
                .filter(sgy -> (sgy.getAnnotation() != null && type.isAnnotationPresent(sgy.getAnnotation()))
                        || (sgy.getType() != void.class && ClassUtil.isAssignable(sgy.getType(), type)))
                .findFirst();
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        DataFillStrategyContext.strategies = context.getBeansOfType(DataFillStrategy.class);
    }
}
