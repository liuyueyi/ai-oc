package com.git.hui.offer.web.model.res;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author YiHui
 * @date 2025/7/17
 */
@Data
@Accessors(chain = true)
public class UserVo {
    private Long userId;
    private String displayName;
    private String avatar;
    private String wxId;
    private Integer role;
    private Integer state;
    private String email;
    private String intro;
    // vip用户的有效期
    private Long expireTime;
    private Long createTime;
    private Long updateTime;
}
