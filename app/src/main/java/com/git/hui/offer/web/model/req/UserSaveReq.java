package com.git.hui.offer.web.model.req;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 用户入参
 *
 * @author louzai
 * @date 2022-07-24
 */
@Data
@Accessors(chain = true)
public class UserSaveReq {
    /**
     * 主键ID
     */
    private Long userId;

    /**
     * 第三方用户ID
     */
    private String wxId;
}
