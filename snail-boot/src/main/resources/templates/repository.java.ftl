package ${repoPack};

import ${package.Entity}.${entity};
import ${package.Mapper}.${table.mapperName};
import ${superServiceImplClassPackage};
import org.springframework.stereotype.Repository;

/**
* <p>
* ${table.comment!} Repository
* </p>
*
* @author ${author}
* @since ${date}
*/
@Repository
<#if kotlin>
open class ${repoName} : ${superServiceImplClass}<${table.mapperName}, ${entity}>(){

}
<#else>
public class ${repoName} extends ${superServiceImplClass}<${table.mapperName}, ${entity}> {

}
</#if>
