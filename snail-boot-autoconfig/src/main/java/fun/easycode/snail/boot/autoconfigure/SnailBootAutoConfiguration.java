package fun.easycode.snail.boot.autoconfigure;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import fun.easycode.snail.boot.core.AuditMetaObjectHandler;
import fun.easycode.snail.boot.core.ExecutorContext;
import fun.easycode.snail.boot.core.SnailBootSqlInjector;
import fun.easycode.snail.boot.core.UserHolder;
import fun.easycode.snail.boot.util.BatchUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
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
public class SnailBootAutoConfiguration {
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

}
