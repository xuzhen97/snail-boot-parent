package fun.easycode.snail.boot.core;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.extension.injector.methods.InsertBatchSomeColumn;

import java.util.List;

/**
 * 自定义sql注入器
 * @author xuzhen97
 */
public class SnailBootSqlInjector extends DefaultSqlInjector {
    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass, TableInfo tableInfo) {
        List<AbstractMethod> methodList = super.getMethodList(mapperClass, tableInfo);
        methodList.add(new InsertBatchSomeColumn());
        methodList.add(new ReplaceBatchSomeColumn());
        methodList.add(new StreamQuerySqlAbstractMethod());
        methodList.add(new StreamQueryAbstractMethod());
        methodList.add(new InsertOrUpdateBatchMethod());
        return methodList;
    }
}
