package fun.easycode.snail.boot.core;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static fun.easycode.snail.boot.util.CamelUnderUtil.*;

/**
 * 动态查询工具类，用于MyBatis Plus
 * @author xuzhe
 */
@Slf4j
public final class DynamicOperate {
    /**
     * 跳过字段， queryWrapper中跳过
     */
    public static final List<String> SKIP_FIELDS = Arrays.asList("size", "current");

    /**
     * @param qry           查询指令
     * @param mapper        查询mapper
     * @param resultConvert 查询实体返回转换逻辑
     * @param <T>           数据库实体
     * @param <K>           查询指令
     * @param <R>           转换的返回结果
     * @return 要返回的实体
     */
    public static <T, K, R> List<R> list(K qry
            , BaseMapper<T> mapper
            , QueryResultConvert<T, R> resultConvert, String... skipFields) {
        QueryWrapper<T> queryWrapper = queryWrapper(qry, skipFields);
        List<T> result = mapper.selectList(queryWrapper);
        return result.stream().map(resultConvert::to)
                .collect(Collectors.toList());
    }

    /**
     * 分页查询工具方法
     * @param qry 查询指令
     * @param mapper 查询mapper
     * @param resultConvert 查询实体返回转换逻辑
     * @return 要返回的实体
     * @param <T> 数据库实体
     * @param <K> 查询指令
     * @param <R> 转换的返回结果
     */
    public static <T,K extends PageQry, R> PageDto<R> page(K qry
            , BaseMapper<T> mapper
            , QueryResultConvert<T,R> resultConvert){
        QueryWrapper<T> queryWrapper = queryWrapper(qry);
        Page<T> page = mapper.selectPage(new Page<>(qry.getCurrent(), qry.getSize()), queryWrapper);
        return PageDto.toPageDto(page, resultConvert::to);
    }

    /**
     * 根据实体命令生成QueryWrapper条件
     *
     * @param qry
     * @param <T>
     * @return
     */
    public static <T> QueryWrapper<T> queryWrapper(Object qry, String... skipFields) {

        Map<String, Field> fieldMap = ReflectUtil.getFieldMap(qry.getClass());

        QueryWrapper<T> queryWrapper = new QueryWrapper<>();

        List<String> skipFieldList = Arrays.asList(skipFields);

        for (Map.Entry<String, Field> entry : fieldMap.entrySet()) {

            // 如果字段要跳过则方法对这个字段不进行任何处理
            if (skipFieldList.stream().anyMatch(skipField -> ObjectUtil.equal(skipField, entry.getKey()))) {
                continue;
            }
            // 存在于跳过字段中则跳过，不处理条件
            if (SKIP_FIELDS.contains(entry.getKey())) {
                continue;
            }
            // 根据判断增加条件
            wrapper(qry, entry.getKey(), entry.getValue(), queryWrapper);
        }
        return queryWrapper;
    }

    /**
     * 根据实体命令生成QueryWrapper条件
     * @param qry
     * @param <T>
     * @return
     */
    public static <T> QueryWrapper<T> queryWrapper(Object qry){

        Map<String, Field> fieldMap = ReflectUtil.getFieldMap(qry.getClass());

        QueryWrapper<T> queryWrapper = new QueryWrapper<>();

        for (Map.Entry<String, Field> entry : fieldMap.entrySet()) {

            // 存在于跳过字段中则跳过，不处理条件
            if(SKIP_FIELDS.contains(entry.getKey())){
                continue;
            }
            // 根据判断增加条件
            wrapper(qry, entry.getKey(), entry.getValue(), queryWrapper);
        }
        return queryWrapper;
    }

    /**
     * 给wrapper增加条件
     * @param o 对象例如qry cmd
     * @param key 字段的名称，反射出来的
     * @param field 反射出来的字段Field对象
     * @param wrapper wrapper对象 可以是Query也可以是Update
     */
    private static void wrapper(Object o, String key, Field field, AbstractWrapper wrapper) {

        if (isConditional(key, OperateSymbol.EQ.getValue())) {
            int index = lastIndexOf(key, OperateSymbol.EQ.getValue());
            String fieldName = getDatabaseFieldName(index, key, field);
            Object value = ReflectUtil.getFieldValue(o, field);
            if (ObjectUtil.isNotEmpty(value)) {
                wrapper.eq(fieldName, transformValue(value));
            }
        } else if (isConditional(key, OperateSymbol.LIKE.getValue())) {
            int index = lastIndexOf(key, OperateSymbol.LIKE.getValue());
            String fieldName = getDatabaseFieldName(index, key, field);
            Object value = ReflectUtil.getFieldValue(o, key);
            if (ObjectUtil.isNotEmpty(value)) {
                wrapper.like(fieldName, transformValue(value));
            }
        } else if (isConditional(key, OperateSymbol.LIKE_LEFT.getValue())) {
            int index = lastIndexOf(key, OperateSymbol.LIKE_LEFT.getValue());
            String fieldName = getDatabaseFieldName(index, key, field);
            Object value = ReflectUtil.getFieldValue(o, field);
            if (ObjectUtil.isNotEmpty(value)) {
                wrapper.likeLeft(fieldName, transformValue(value));
            }
        } else if (isConditional(key, OperateSymbol.LIKE_RIGHT.getValue())) {
            int index = lastIndexOf(key, OperateSymbol.LIKE_RIGHT.getValue());
            String fieldName = getDatabaseFieldName(index, key, field);
            Object value = ReflectUtil.getFieldValue(o, field);
            if (ObjectUtil.isNotEmpty(value)) {
                wrapper.likeRight(fieldName, transformValue(value));
            }
        } else if (isConditional(key, OperateSymbol.GT.getValue())) {
            int index = lastIndexOf(key, OperateSymbol.GT.getValue());
            String fieldName = getDatabaseFieldName(index, key, field);
            Object value = ReflectUtil.getFieldValue(o, field);
            if (ObjectUtil.isNotEmpty(value)) {
                wrapper.gt(fieldName, transformValue(value));
            }
        } else if (isConditional(key, OperateSymbol.LT.getValue())) {
            int index = lastIndexOf(key, OperateSymbol.LT.getValue());
            String fieldName = getDatabaseFieldName(index, key, field);
            Object value = ReflectUtil.getFieldValue(o, field);
            if (ObjectUtil.isNotEmpty(value)) {
                wrapper.lt(fieldName, transformValue(value));
            }
        } else if (isConditional(key, OperateSymbol.GE.getValue())) {
            int index = lastIndexOf(key, OperateSymbol.GE.getValue());
            String fieldName = getDatabaseFieldName(index, key, field);
            Object value = ReflectUtil.getFieldValue(o, field);
            if (ObjectUtil.isNotEmpty(value)) {
                wrapper.ge(fieldName, transformValue(value));
            }
        } else if (isConditional(key, OperateSymbol.LE.getValue())) {
            int index = lastIndexOf(key, OperateSymbol.LE.getValue());
            String fieldName = getDatabaseFieldName(index, key, field);
            Object value = ReflectUtil.getFieldValue(o, field);
            if (ObjectUtil.isNotEmpty(value)) {
                wrapper.le(fieldName, transformValue(value));
            }
        } else if (isConditional(key, OperateSymbol.IS_NULL.getValue())) {
            int index = lastIndexOf(key, OperateSymbol.IS_NULL.getValue());
            String fieldName = getDatabaseFieldName(index, key, field);
            Object value = ReflectUtil.getFieldValue(o, field);
            if (value != null && !(value instanceof Boolean)) {
                throw new CheckException("is_null条件必须是Boolean类型");
            }
            if (value != null) {
                if ((Boolean) value) {
                    wrapper.isNull(fieldName);
                } else {
                    wrapper.isNotNull(fieldName);
                }
            }
        } else if (isConditional(key, OperateSymbol.NOT_IN.getValue())) {
            int index = lastIndexOf(key, OperateSymbol.NOT_IN.getValue());
            String fieldName = getDatabaseFieldName(index, key, field);
            Object value = ReflectUtil.getFieldValue(o, field);
            if (ObjectUtil.isNotEmpty(value)) {
                wrapper.notIn(fieldName, (Collection<?>) value);
            }
        } else if (isConditional(key, OperateSymbol.IN.getValue())) {
            int index = lastIndexOf(key, OperateSymbol.IN.getValue());
            String fieldName = getDatabaseFieldName(index, key, field);
            Object value = ReflectUtil.getFieldValue(o, field);
            if (ObjectUtil.isNotEmpty(value)) {
                wrapper.in(fieldName, (Collection<?>) value);
            }
        } else if (isConditional(key, OperateSymbol.SORT.getValue())) {
            int index = lastIndexOf(key, OperateSymbol.SORT.getValue());
            String fieldName = getDatabaseFieldName(index, key, field);
            Object value = ReflectUtil.getFieldValue(o, field);
            // 必须传入值，不是DESC其他值均为ASC
            if (ObjectUtil.isNotEmpty(value)) {
                if (ObjectUtil.equal(String.valueOf(transformValue(value)).toUpperCase(), "DESC")) {
                    wrapper.orderByDesc(fieldName);
                } else {
                    wrapper.orderByAsc(fieldName);
                }
            }
        } else {
            // 如果字段后面什么条件都没有加就按照等于处理
            String fieldName = getDatabaseFieldName(-1, key, field);
            Object value = ReflectUtil.getFieldValue(o, field);
            if (ObjectUtil.isNotEmpty(value)) {
                wrapper.eq(fieldName, transformValue(value));
            }
        }
    }

    /**
     * 转换value
     *
     * @param value
     * @return
     */
    private static Object transformValue(Object value) {
        // TODO 暂无作用
        return value;
    }

    /**
     *  从后面查看字符串存不存在，严格判断大小写
     *  避免某些单词误判，例如enable
     * @param original 原字符串
     * @param condition 条件字符串，例如本类的静态变量LIKE
     * @return int index
     */
    private static int lastIndexOf(String original, String condition){
        return original.lastIndexOf(condition);
    }

    /**
     * 判断原字符串中有没有条件字符
     *
     * @param original  原字符
     * @param condition 条件字符
     * @return
     */
    private static boolean isConditional(String original, String condition) {
        return original.endsWith(condition);
    }

    /**
     * 根据前面查询的条件index以及原始字符串转换成数据库字段名
     * @param index lastIndexOfLower 返回index
     * @param original 原始字符串例如nameLike
     * @return 数据库字段名
     */
    private static String getDatabaseFieldName(int index, String original, Field field){
        // 如果字段存在别名，直接取别名中的值
        if(AnnotationUtil.hasAnnotation(field, Alias.class)) {
            Alias alias = field.getAnnotation(Alias.class);
            return alias.value();
        }
        if(index != -1){
            original = original.substring(0, index);
        }
        return camel2under(original);
    }

}
