package fun.easycode.snail.boot.autoconfigure;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import fun.easycode.snail.boot.core.*;
import fun.easycode.snail.boot.datafill.*;
import fun.easycode.snail.boot.util.BatchUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.aop.Advisor;
import org.springframework.aop.support.DefaultBeanFactoryPointcutAdvisor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 通用的自动配置，命令行应用依旧生效
 * @author xuzhen97
 */
@EnableConfigurationProperties(SnailBootProperties.class)
@Configuration
@Slf4j
public class SnailBootAutoConfiguration implements BeanFactoryAware {

    private BeanFactory beanFactory;

    @Bean
    @ConditionalOnBean(SqlSessionFactory.class)
    public BatchUtil batchUtil(SqlSessionFactory factory){
        return new BatchUtil(factory);
    }

    /**
     * 配置用户信息获取器，如果用户自己配置则默认配置失效
     * @return UserHolder
     */
    @Bean
    @ConditionalOnMissingBean(UserHolder.class)
    public UserHolder userHolder(){
        return new UserHolder.DefaultUserHolder();
    }

    /**
     * 配置mybatis plus插件, 如果用户自己配置则默认配置失效
     * @return MybatisPlusInterceptor
     */
    @Bean
    @ConditionalOnMissingBean(MybatisPlusInterceptor.class)
    public MybatisPlusInterceptor interceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        // 防全表更新与删除插件
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
        if(log.isDebugEnabled()){
            log.debug("配置MybatisPlusInterceptor，配置分页插件和防全表更新删除插件。。。");
        }
        return interceptor;
    }

    /**
     * mybatis plus sql注入器
     * @return
     */
    @Bean
    @ConditionalOnClass(ISqlInjector.class)
    public SnailBootSqlInjector jointBlockSqlInjector(){
        return new SnailBootSqlInjector();
    }

    /**
     * mybatis plus审计字段处理
     * @return AuditMetaObjectHandler
     */
    @Bean
    @ConditionalOnClass(MetaObjectHandler.class)
    public AuditMetaObjectHandler auditMetaObjectHandler(){
        return new AuditMetaObjectHandler();
    }

    /**
     * 执行器上下文，用于直接获取执行器进行调用
     * @return ExecutorContext
     */
    @Bean
    public ExecutorContext executorContext(){
        return new ExecutorContext();
    }

    @Bean
    public DataCopy dataCopy(){
        return new DataCopy();
    }


    /**
     * 代码填充织入执行器
     * @return DefaultBeanFactoryPointcutAdvisor
     */
    @ConditionalOnClass(Advisor.class)
    @Bean
    public DefaultBeanFactoryPointcutAdvisor executorDataFillAdvisor() {
        // 自动填充织入到Executor.execute方法
        DefaultBeanFactoryPointcutAdvisor advisor = new DefaultBeanFactoryPointcutAdvisor();
        advisor.setPointcut(executorDataFillPointcut());
        advisor.setAdvice(executorDataFillInterceptor());
        advisor.setBeanFactory(beanFactory);
        return advisor;
    }

    /**
     * 执行器识别定义的切点
     * @return ExecutorDataFillPointcut
     */
    @Bean
    @ConditionalOnClass(Advisor.class)
    public ExecutorDataFillPointcut executorDataFillPointcut(){
        return new ExecutorDataFillPointcut();
    }

    /**
     * 执行器数据填充具体的切入逻辑
     * @return ExecutorDataFillInterceptor
     */
    @Bean
    @ConditionalOnClass(Advisor.class)
    public ExecutorDataFillInterceptor executorDataFillInterceptor(){
        return new ExecutorDataFillInterceptor();
    }

    // 下方是DataFill的配置

    /**
     * mybatis plus填充策略支持
     * @return MyBatisPlusDataFillStrategy
     */
    @Bean
    @ConditionalOnClass({BaseMapper.class})
    public MyBatisPlusDataFillStrategy myBatisPlusDataFillStrategy(){
        return new MyBatisPlusDataFillStrategy();
    }

    /**
     * feign远程调用策略支持
     * @return FeignDataFillStrategy
     */
    @Bean
    @ConditionalOnClass(name = "org.springframework.cloud.openfeign.FeignContext")
    public FeignDataFillStrategy feignDataFillStrategy(){
        return new FeignDataFillStrategy();
    }

    /**
     * 数据填充具体执行器, 这里与业务执行器没有联系
     * @return DataFillExecutor
     */
    @Bean
    public DataFillExecutor dataFillExecutor(){
        return new DataFillExecutor();
    }

    /**
     * 数据填充策略Context, 用于获取填充策略
     * @return DataFillStrategyContext
     */
    @Bean
    public DataFillStrategyContext dataFillStrategyContext(){
        return new DataFillStrategyContext();
    }

    /**
     * 数据填充线程池管理
     * @return DataFillThreadPoolManager
     */
    @Bean
    public DataFillThreadPoolManager dataFillThreadPoolManager(){
        return new DataFillThreadPoolManager();
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
