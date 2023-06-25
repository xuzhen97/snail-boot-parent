package fun.easycode.snail.boot.generator;

import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.engine.AbstractTemplateEngine;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.util.ClassUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;

/**
 * 自定义freemarker代码生成引擎
 *  仅生成dao层，附带生成仓储文件，就是mybatis plus封装的service
 * @author xuzhe
 */
public class SnailBootFreemarkerTemplateEngine extends FreemarkerTemplateEngine {

    private static final String SUPER_MAPPER_CLASS_PACKAGE = "fun.easycode.snail.boot.core.SnailBootMapper";

    @Override
    public @NotNull AbstractTemplateEngine batchOutput() {
        try {
            ConfigBuilder config = this.getConfigBuilder();
            List<TableInfo> tableInfoList = config.getTableInfoList();
            tableInfoList.forEach(tableInfo -> {
                Map<String, Object> objectMap = this.getObjectMap(config, tableInfo);
                Optional.ofNullable(config.getInjectionConfig()).ifPresent(t -> {
                    t.beforeOutputFile(tableInfo, objectMap);
                    // 输出自定义文件
                    outputCustomFile(t.getCustomFile(), tableInfo, objectMap);
                });
                // entity
                outputEntity(tableInfo, objectMap);

                // overwrite BaseMapper
                objectMap.put("superMapperClassPackage", SUPER_MAPPER_CLASS_PACKAGE);
                objectMap.put("superMapperClass", ClassUtils.getSimpleName(SUPER_MAPPER_CLASS_PACKAGE));

                // mapper and xml
                outputMapper(tableInfo, objectMap);
                // repository
                outputRepository(tableInfo, objectMap);

            });
        } catch (Exception e) {
            throw new RuntimeException("无法创建文件，请检查配置信息！", e);
        }
        return this;
    }

    protected void outputRepository(TableInfo tableInfo,  Map<String, Object> objectMap){
        // 全局输出路径
        String globalDir = getConfigBuilder().getGlobalConfig().getOutputDir();
        // 仓储所在package
        String repoPack = getConfigBuilder().getPackageConfig().getParent()
                + "." + getConfigBuilder().getPackageConfig().getEntity();
        // objectMap put repoPack ftl模板使用
        objectMap.put("repoPack", repoPack);

        // 仓储生成java源文件路径
        String repositoryPath = globalDir + File.separator
                + repoPack.replaceAll("\\.", Matcher.quoteReplacement(File.separator));
        // 仓储生成名称
        String repoName = tableInfo.getEntityName() + "Repository";
        // objectMap put repoName ftl模板使用
        objectMap.put("repoName", repoName);
        // ftl模板路径
        String repositoryFile = repositoryPath + File.separator + repoName + suffixJavaOrKt();
        // 输出文件
        outputFile(new File(repositoryFile), objectMap, "/templates/repository.java.ftl", true);
    }

}
