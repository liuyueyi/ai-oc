package com.git.hui.offer.web.model;

import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 统一返回结果分页信息
 *
 * @author YiHui
 * @date 2025/7/15
 */
@Data
public class PageListVo<T> {
    /**
     * 用户列表
     */
    List<T> list;
    /**
     * 是否有更多
     */
    private Boolean hasMore;
    /**
     * 当前页码
     */
    private Integer page;
    /**
     * 分页页大小
     */
    private Integer size;
    /**
     * 总数
     */
    private Long total;

    private Boolean locked;

    public static <T> PageListVo<T> emptyVo() {
        PageListVo<T> vo = new PageListVo<>();
        vo.setList(Collections.emptyList());
        vo.setHasMore(false);
        return vo;
    }

    public static <T> PageListVo<T> of(List<T> list, long total, int page, int size) {
        PageListVo<T> vo = new PageListVo<>();
        vo.setList(Optional.ofNullable(list).orElse(Collections.emptyList()));
        vo.setHasMore(vo.getList().size() == size);
        vo.setTotal(total);
        vo.setPage(page);
        vo.setSize(size);
        return vo;
    }
}
