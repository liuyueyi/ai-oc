package com.git.hui.offer.web.model.req;

import lombok.Data;

/**
 * @author YiHui
 * @date 2025/7/21
 */
@Data
public class DictSearchReq extends PageReq {
    private String scope;
    private String key;
    private Integer state;
}
