package fun.easycode.snail.boot.core;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.handlers.StrictFill;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * mybatis plus 审计信息
 * createdDate
 * createdBy
 * lastModifiedDate
 * lastModifiedBy
 * @author xuzhe
 */
public class AuditMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        this.strictInsertFill(metaObject, "createTime", ()-> now, LocalDateTime.class);
        this.strictInsertFill(metaObject, "createBy",UserHolder.getInstance().getLoginUser()::getId, String.class);
        this.strictInsertFill(metaObject, "updateTime", ()-> now, LocalDateTime.class);
        this.strictInsertFill(metaObject, "updateBy", UserHolder.getInstance().getLoginUser()::getId, String.class);

        // 兼容date
        Date nowDate = new Date();
        this.strictInsertFill(metaObject, "createTime", Date.class, nowDate);
        this.strictInsertFill(metaObject, "updateTime", Date.class, nowDate);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        this.strictUpdateFill(metaObject, "updateTime", () -> now, LocalDateTime.class);
        this.strictUpdateFill(metaObject, "updateBy", UserHolder.getInstance().getLoginUser()::getId, String.class);

        // 兼容date
        Date nowDate = new Date();
        this.strictUpdateFill(metaObject, "updateTime", Date.class, nowDate);
    }

    /**
     *  覆盖strictFill逻辑，为了实现修改的时候强制修改
     * @param insertFill  是否验证在 insert 时填充
     * @param tableInfo   cache 缓存
     * @param metaObject  metaObject meta object parameter
     * @param strictFills 填充信息
     * @return MetaObjectHandler
     */
    @Override
    public MetaObjectHandler strictFill(boolean insertFill, TableInfo tableInfo, MetaObject metaObject, List<StrictFill<?, ?>> strictFills) {
        if ((insertFill && tableInfo.isWithInsertFill()) || (!insertFill && tableInfo.isWithUpdateFill())) {
            strictFills.forEach(i -> {
                final String fieldName = i.getFieldName();
                final Class<?> fieldType = i.getFieldType();
                tableInfo.getFieldList().stream()
                        .filter(j -> j.getProperty().equals(fieldName) && fieldType.equals(j.getPropertyType()) &&
                                ((insertFill && j.isWithInsertFill()) || (!insertFill && j.isWithUpdateFill()))).findFirst()
                        .ifPresent(j -> {
                            // 插入走原先的逻辑
                            // 修改就走强制修改的逻辑
                            if(insertFill){
                                strictFillStrategy(metaObject, fieldName, i.getFieldVal());
                            }else{
                                strictFillStrategyForce(metaObject, fieldName, i.getFieldVal());
                            }
                        });
            });
        }
        return this;
    }

    /**
     * 不管实体有没有属性，都要以生成的为准
     *  主要用于修改的时候
     * @param metaObject MetaObject
     * @param fieldName String
     * @param fieldVal Supplier<?>
     * @return MetaObjectHandler
     */
    public MetaObjectHandler strictFillStrategyForce(MetaObject metaObject, String fieldName, Supplier<?> fieldVal) {
        Object obj = fieldVal.get();
        if (Objects.nonNull(obj)) {
            metaObject.setValue(fieldName, obj);
        }
        return this;
    }
}