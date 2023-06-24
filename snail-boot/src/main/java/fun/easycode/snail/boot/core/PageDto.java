package fun.easycode.snail.boot.core;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 分页结果返回dto封装
 * @author xuzhe
 */
@Data
@Builder
public class PageDto<T> {
    private Long current;
    private Long size;
    private Long totalSize;
    private Long totalPage;
    private List<T> data;

    /**
     * mybatis-plus page转换PageDto
     * @param page mybatis plus page
     * @param convert 转换关系
     * @param <T> dto类型
     * @param <K> 实体类型
     * @return PageDto
     */
    public static <T,K> PageDto<T> toPageDto(Page<K> page, QueryResultConvert<K,T> convert){

        List<T> data = page.getRecords().stream().map(convert::to)
                .collect(Collectors.toList());
        return PageDto.<T>builder()
                .current(page.getCurrent())
                .size(page.getSize())
                .totalSize(page.getTotal())
                .totalPage(page.getPages())
                .data(data)
                .build();
    }

    /**
     * 转换，这是mybatis plus page类型和dto类型转换的声明
     * @param <T>
     * @param <K>
     */
    @FunctionalInterface
    public interface Convert<T,K>{
        K convert(T t);
    }
}