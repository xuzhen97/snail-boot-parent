package fun.easycode.snail.boot.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 查询表达式解析器
 * @author xuzhen97
 */
public class QueryDslParser {

    private static final Pattern QUERY_PATTERN = Pattern.compile("(\\w+?)(\\[.+?\\]=)([a-zA-Z0-9\\u4e00-\\u9fa5()|\\s\\-:]+?),");
    private static final Pattern OPERATOR_PATTERN = Pattern.compile("(?<=\\[).*(?=\\])");
    /**
     * 解析查询表达式
     * @param value 查询表达式
     * @param tClass 实体类
     * @param <T> 实体类
     * @return 查询构造器
     */
    public static <T> QueryWrapperBuilder<T> parser(String value, Class<T> tClass) {
        QueryWrapperBuilder<T> builder;
        if(tClass == null){
            builder = new QueryWrapperBuilder<>();
        }else{
            builder = new QueryWrapperBuilder<>(tClass);
        }
        // 整体表达式正则
        Matcher matcher = QUERY_PATTERN.matcher(value + ",");
        // 操作符匹配正则
        while (matcher.find()) {
            Matcher operatorMatcher = OPERATOR_PATTERN.matcher( matcher.group(2));
            builder.with(matcher.group(1), operatorMatcher.find() ? operatorMatcher.group() : "", matcher.group(3));
        }
        return builder;
    }

    /**
     * 解析查询表达式
     * @param value 查询表达式
     * @param <T> 实体类
     * @return 查询构造器
     */
    public static <T> QueryWrapperBuilder<T> parser(String value) {
        return parser(value, null);
    }
}
