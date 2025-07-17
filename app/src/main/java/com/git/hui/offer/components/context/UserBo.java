package com.git.hui.offer.components.context;

import com.git.hui.offer.components.permission.UserRole;

/**
 * 用户业务对象
 *
 * @author YiHui
 * @date 2025/7/15
 */
public record UserBo(Long userId, String nickName, String avatar, UserRole role) {
}
