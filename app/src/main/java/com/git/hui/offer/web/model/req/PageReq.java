package com.git.hui.offer.web.model.req;

import lombok.Data;

/**
 * @author YiHui
 * @date 2025/7/15
 */
@Data
public class PageReq {
    private Integer page;
    private Integer size;

    public void autoInitPage() {
        if (page == null || page <= 0) {
            page = 1;
        }
        if (size == null) {
            size = 10;
        }
    }
}
