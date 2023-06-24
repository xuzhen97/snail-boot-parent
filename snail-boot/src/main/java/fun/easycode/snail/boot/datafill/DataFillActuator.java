package fun.easycode.snail.boot.datafill;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据填充执行器
 * @param <T> 数据类型
 */
public class DataFillActuator<T> {
    // 数据
    private final Collection<T> data;
    // 填充过程
    private final List<DataFillProcess<T,?>> processes = new ArrayList<>();

    /**
     * 构造函数
     * @param data 数据
     */
    public DataFillActuator(Collection<T> data) {
        this.data = data;
    }

    /**
     * 增加填充过程
     * @param process 填充过程
     */
    public DataFillActuator<T> addProcess(DataFillProcess process) {
        processes.add(process);
        return this;
    }

    /**
     * 执行
     */
    public void execute() {
        List<? extends DataFillTask<T, ?>> tasks = processes.stream()
                .map(process -> new DataFillTask<>(process, data))
                .collect(Collectors.toList());
        try {
            // 执行任务
            DataFillThreadExecutor.execParallelTasks(tasks);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
