package fun.easycode.snail.boot.datafill;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * 数据框架填充任务
 * @author xuzhe
 */
@Slf4j
public class DataFillTask<T> implements Callable<List<T>> {

  private final DataFillStrategy strategy;
  private final List<DataFillMetadata> metadataList;

  public DataFillTask(DataFillStrategy handler, List<DataFillMetadata> metadataList){
    this.strategy = handler;
    this.metadataList = metadataList;
  }

  @Override
  public List<T> call() {
    try {
      strategy.fill(metadataList);
    }catch (Exception e){
      if(log.isDebugEnabled()) {
        e.printStackTrace();
      }
      log.error(e.getMessage());
    }
    return null;
  }

}
