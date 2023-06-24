package fun.easycode.snail.boot.generator;

import lombok.Getter;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

import java.util.Arrays;
import java.util.List;

/**
 * 代码生成可选配置
 * @author xuzhe
 */
@Getter
public class GeneratorConfig {
    /**
     * 数据库url
     */
    private String url;
    /**
     * 帐号名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 作者
     */
    private String author;

    /**
     * 父级包名
     */
    private String parentPackage;

    /**
     * 要生成的表名
     */
    private List<String> tables;

    /**
     * 生成的表名同意过滤的前缀
     */
    private String tablePrefix;

    /**
     * 模块名字
     */
    private String moduleName;

    /**
     * 忽略设置主键策略
     */
    private Boolean ignoreIdType;

    public GeneratorConfig(String url, String username, String password
            ,String author, String parentPackage, List<String> tables, String tablePrefix, String moduleName, Boolean ignoreIdType){
        this.url = url;
        this.username = username;
        this.password = password;
        this.author = author;
        this.parentPackage = parentPackage;
        this.tables = tables;
        this.tablePrefix = tablePrefix;
        this.moduleName = moduleName;
        this.ignoreIdType = ignoreIdType;
    }

    public static GeneratorConfigBuilder builder(){
        return new GeneratorConfigBuilder();
    }

    /**
     * GeneratorConfigBuilder
     * @author xuzhe
     */
    public static class GeneratorConfigBuilder{


        /**
         * 数据库url
         */
        private String url;
        /**
         * 帐号名
         */
        private String username;
        /**
         * 密码
         */
        private String password;
        /**
         * 作者
         */
        private String author;

        /**
         * 父级包名
         */
        private String parentPackage;

        /**
         * 要生成的表名
         */
        private List<String> tables;

        /**
         * 生成的表名同意过滤的前缀
         */
        private String tablePrefix;
        /**
         * 模块名字
         */
        private String moduleName;

        /**
         * 忽略设置主键策略
         */
        private Boolean ignoreIdType;

        public GeneratorConfigBuilder(){
            // 给定默认值
            this.ignoreIdType = false;
        }

        /**
         * 根据DataSourceProperties 设置url、username、password
         * @param properties
         * @return
         */
        public GeneratorConfigBuilder datasourceProperties(DataSourceProperties properties){
            this.url = properties.getUrl();
            this.username = properties.getUsername();
            this.password = properties.getPassword();
            return this;
        }

        /**
         * 设置url
         * @param url
         * @return
         */
        public GeneratorConfigBuilder url(String url){
            this.url = url;
            return this;
        }

        /**
         * 设置username
         * @param username
         * @return
         */
        public GeneratorConfigBuilder username(String username){
            this.username = username;
            return this;
        }

        /**
         * 设置password
         * @param password
         * @return
         */
        public GeneratorConfigBuilder password(String password){
            this.password = password;
            return this;
        }

        /**
         * 设置作者
         * @param author
         * @return
         */
        public GeneratorConfigBuilder author(String author){
            this.author = author;
            return this;
        }

        /**
         * 设置代码所在父级包
         * @param parentPackage
         * @return
         */
        public GeneratorConfigBuilder parentPackage(String parentPackage){
            this.parentPackage = parentPackage;
            return this;
        }

        /**
         * 设置要生成表
         * @param tables
         * @return
         */
        public GeneratorConfigBuilder tables(List<String> tables){
            this.tables = tables;
            return this;
        }

        /**
         * 设置要生成的表
         * @param tables
         * @return
         */
        public GeneratorConfigBuilder tables(String... tables){
            assert tables.length > 0;
            this.tables = Arrays.asList(tables);
            return this;
        }

        /**
         * 设置表统一要过滤的前缀
         * @param tablePrefix
         * @return
         */
        public GeneratorConfigBuilder tablePrefix(String tablePrefix){
            this.tablePrefix = tablePrefix;
            return this;
        }

        /**
         * 设置模块名
         * @param moduleName
         * @return
         */
        public GeneratorConfigBuilder moduleName(String moduleName){
            this.moduleName = moduleName;
            return this;
        }

        public GeneratorConfigBuilder ignoreIdType(Boolean ignoreIdType){
            this.ignoreIdType = ignoreIdType;
            return this;
        }

        /**
         * build config对象
         * @return
         */
        public GeneratorConfig build(){
            return new GeneratorConfig(this.url, this.username, this.password
                    , this.author, this.parentPackage,this.tables,this.tablePrefix, this.moduleName, this.ignoreIdType);
        }
    }
}
