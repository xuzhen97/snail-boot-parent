package fun.easycode.snail.boot.core;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 分页查询qry
 * @author xuzhe
 */
@Data
public class PageQry {
    @NotNull
    private Integer size = 10;
    @NotNull
    private Integer current = 1;
}