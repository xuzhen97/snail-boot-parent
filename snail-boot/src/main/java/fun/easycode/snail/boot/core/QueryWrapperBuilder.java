package fun.easycode.snail.boot.core;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.ColumnCache;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryWrapperBuilder<T> {

    private final List<SearchCriteria> params;
    private final Class<T> tClass;
    private final Map<String, ColumnCache> columnMap;

    public QueryWrapperBuilder(Class<T> tClass){
        params = new ArrayList<>();
        this.tClass = tClass;
        columnMap = LambdaUtils.getColumnMap(tClass);
    }

    public QueryWrapperBuilder(){
        params = new ArrayList<>();
        this.tClass = null;
        this.columnMap = null;
    }

    public QueryWrapperBuilder<T> with(String key, String operation, Object value){
        params.add(new SearchCriteria(key, operation, value));
        return this;
    }

    public QueryWrapper<T> build(){
        if(params.size() == 0){
            return null;
        }
        QueryWrapper<T> queryWrapper = Wrappers.query();

        for(SearchCriteria criteria : params){
            String tableField = getTableField(criteria.getKey());
            // 解析查询条件
            analysis(tableField, criteria.getOperation(), criteria.getValue(), queryWrapper);
        }
        return queryWrapper;
    }

    private String getTableField(String key){
        if(tClass != null) {
            String columnKey = LambdaUtils.formatKey(key);
            return columnMap.get(columnKey).getColumnSelect();
        }else{
            return key;
        }
    }

    /**
     * 解析查询条件
     * @param tableField 字段名
     * @param operation 操作符
     * @param value 值
     * @param wrapper 条件构造器
     * @param <T> 实体类
     */
    private static <T> void analysis(String tableField, String operation, Object value, QueryWrapper<T> wrapper) {

        // 如果是空值，不处理
        if (!ObjectUtil.isNotEmpty(value)) {
            return;
        }

        if (Objects.equals(OperateSymbol.EQ.getValue(), operation)) {
            wrapper.eq(tableField, value);
        } else if (Objects.equals(OperateSymbol.LIKE.getValue(), operation)) {
            wrapper.like(tableField, value);
        } else if (Objects.equals(OperateSymbol.LIKE_LEFT.getValue(), operation)) {
            wrapper.likeLeft(tableField, value);
        } else if (Objects.equals(OperateSymbol.LIKE_RIGHT.getValue(), operation)) {
            wrapper.likeRight(tableField, value);
        } else if (Objects.equals(OperateSymbol.GT.getValue(), operation)) {
            wrapper.gt(tableField, value);
        } else if (Objects.equals(OperateSymbol.LT.getValue(), operation)) {
            wrapper.lt(tableField, value);
        } else if (Objects.equals(OperateSymbol.GE.getValue(), operation)) {
            wrapper.ge(tableField, value);
        } else if (Objects.equals(OperateSymbol.LE.getValue(), operation)) {
            wrapper.le(tableField, value);
        } else if (Objects.equals(OperateSymbol.IS_NULL.getValue(), operation)) {
            if ((Boolean) value) {
                wrapper.isNull(tableField);
            } else {
                wrapper.isNotNull(tableField);
            }
        } else if (Objects.equals(OperateSymbol.NOT_IN.getValue(), operation)) {
            wrapper.notIn(tableField, getValueCollection(value));
        } else if (Objects.equals(OperateSymbol.IN.getValue(), operation)) {
            wrapper.in(tableField, getValueCollection(value));
        } else if (Objects.equals(OperateSymbol.SORT.getValue(), operation)) {
            if (ObjectUtil.equal(String.valueOf(value).toUpperCase(), "DESC")) {
                wrapper.orderByDesc(tableField);
            } else {
                wrapper.orderByAsc(tableField);
            }
        } else {
            throw new CheckException("不支持的查询条件！");
        }
    }

    private static List<Object> getValueCollection(Object value) {
        Pattern valuePattern = Pattern.compile("(?<=\\().*(?=\\))");
        Matcher matcher = valuePattern.matcher(value.toString());
        if (!matcher.find()) {
           throw new CheckException("集合条件value表达式不正确！");
        }
        String[] values = matcher.group().split("\\|");
        return Arrays.asList(values);
    }
}