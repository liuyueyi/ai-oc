package com.git.hui.offer.user.convert;

import com.git.hui.offer.components.context.UserBo;
import com.git.hui.offer.components.permission.UserRole;
import com.git.hui.offer.user.dao.entity.UserEntity;
import com.git.hui.offer.util.json.IntBaseEnum;
import com.git.hui.offer.web.model.res.UserVo;

import java.util.Collections;
import java.util.List;

/**
 * @author YiHui
 * @date 2025/7/17
 */
public class UserConvert {

    public static UserBo toBo(UserEntity user) {
        return new UserBo(user.getId(), user.getDisplayName(), user.getAvatar(), IntBaseEnum.getEnumByCode(UserRole.class, user.getRole()));
    }

    public static UserVo toVo(UserEntity user) {
        long now = System.currentTimeMillis();
        if (user.getRole().equals(UserRole.VIP.getValue())) {
            if (user.getExpireTime() == null || user.getExpireTime().getTime() < now) {
                // vip失效
                user.setRole(UserRole.NORMAL.getValue());
            }
        }
        return new UserVo().setUserId(user.getId())
                .setRole(user.getRole())
                .setState(user.getState())
                .setAvatar(user.getAvatar())
                .setDisplayName(user.getDisplayName())
                .setExpireTime(user.getExpireTime() == null ? null : user.getExpireTime().getTime())
                .setCreateTime(user.getCreateTime().getTime())
                .setUpdateTime(user.getUpdateTime().getTime())
                .setEmail(user.getEmail())
                .setIntro(user.getIntro())
                .setWxId(user.getWxId());
    }

    public static List<UserVo> toVo(List<UserEntity> list) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        return list.stream().map(UserConvert::toVo).toList();
    }
}
