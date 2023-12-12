package fun.easycode.snail.boot.generator;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.builder.Entity;
import com.baomidou.mybatisplus.generator.fill.Column;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.Collections;

/**
 * 定义的统一格式的代码生成器
 * @author xuzhen97
 */
public final class SnailBootGenerator {

    /**
     * 根据GeneratorConfig生成统一的代码
     * @param generatorConfig
     */
    public static void generator(GeneratorConfig generatorConfig){
        assert generatorConfig != null;

        String projectPath = System.getProperty("user.dir");

        String mavenJavaFolder = String.join(File.separator,"src","main","java");
        String mavenResourceFolder = String.join(File.separator,"src","main","resources");

        // pack 要根据moduleName是否存在来生成
        String pack;
        String mapperXmlFolder;

        if(StringUtils.isEmpty(generatorConfig.getModuleName())){
            pack = generatorConfig.getParentPackage() + ".dao";
            mapperXmlFolder = mavenResourceFolder + File.separator + "mapper";
        }else{
            pack = generatorConfig.getParentPackage() + "." + generatorConfig.getModuleName() + ".dao";
            mapperXmlFolder = mavenResourceFolder + File.separator + "mapper" + File.separator + generatorConfig.getModuleName();
        }

        FastAutoGenerator.create(generatorConfig.getUrl() , generatorConfig.getUsername(), generatorConfig.getPassword())
                .globalConfig(builder -> {
                    builder.author(generatorConfig.getAuthor()) // 设置作者
                            .outputDir(projectPath + File.separator + mavenJavaFolder); // 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent(generatorConfig.getParentPackage()) // 设置父包名
                            .moduleName(generatorConfig.getModuleName()) // 设置父包模块名
                            .entity("dao")
                            .mapper("dao")
                            .pathInfo(Collections.singletonMap(OutputFile.xml, projectPath + File.separator + mapperXmlFolder)); // 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    Entity.Builder tempBuilder = builder.addInclude(generatorConfig.getTables()) // 设置需要生成的表名
//                            .addTablePrefix(generatorConfig.getTablePrefix())// 设置过滤表前缀
                            .entityBuilder().enableLombok()  // lombok
                            .enableRemoveIsPrefix() // 去除boolean类型数据的is_前缀
                            .enableTableFieldAnnotation()
                            .logicDeleteColumnName("is_deleted")
                            .addTableFills(new Column("created_by", FieldFill.INSERT)
                                    , new Column("updated_by", FieldFill.INSERT_UPDATE)
                                    , new Column("created_time", FieldFill.INSERT)
                                    , new Column("updated_time", FieldFill.INSERT_UPDATE));

                    // 只有表的前缀有值的时候才进行设置
                    if(!StringUtils.isEmpty(generatorConfig.getTablePrefix())){
                        builder.addTablePrefix(generatorConfig.getTablePrefix());
                    }

                    // 默认主键策略统统设置成为雪花算法，但是可以设置忽略主键设置，那么采用mybatis plus代码生成器默认行为
                    if(!generatorConfig.getIgnoreIdType()){
                        tempBuilder.idType(IdType.ASSIGN_ID);
                    }

                })
                .templateEngine(new SnailBootFreemarkerTemplateEngine())
                .execute();
    }
}
