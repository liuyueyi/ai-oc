package com.git.hui.offer.user.service;

import com.git.hui.offer.components.context.ReqInfoContext;
import com.git.hui.offer.components.context.UserBo;
import com.git.hui.offer.components.id.IdUtil;
import com.git.hui.offer.components.permission.UserRole;
import com.git.hui.offer.constants.common.BaseStateEnum;
import com.git.hui.offer.user.dao.entity.UserEntity;
import com.git.hui.offer.user.dao.repository.UserRepository;
import com.git.hui.offer.user.helper.UserRandomGenHelper;
import com.git.hui.offer.util.json.IntBaseEnum;
import com.git.hui.offer.web.model.req.UserSaveReq;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 用户服务
 *
 * @author YiHui
 * @date 2025/7/16
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(rollbackFor = Exception.class)
    public Long autoRegisterWxUserInfo(String uuid) {
        UserSaveReq req = new UserSaveReq().setWxId(uuid);
        Long userId = registerOrGetUserInfo(req);
        ReqInfoContext.getReqInfo().setUserId(userId);
        return userId;
    }

    /**
     * 没有注册时，先注册一个用户；若已经有，则登录
     *
     * @param req
     */
    private Long registerOrGetUserInfo(UserSaveReq req) {
        UserEntity user = userRepository.findByWxId(req.getWxId());
        if (user == null) {
            return registerByWx(req.getWxId());
        }
        return user.getId();
    }


    /**
     * 微信用户注册
     *
     * @param wxId 微信三方 id
     * @return 用户id
     */
    private Long registerByWx(String wxId) {
        UserEntity user = new UserEntity()
                .setId(IdUtil.genId())
                .setWxId(wxId)
                .setRole(UserRole.NORMAL.getValue())
                .setCreateTime(new Date())
                .setUpdateTime(new Date())
                .setState(BaseStateEnum.NORMAL_STATE.getValue())
                .setDisplayName(UserRandomGenHelper.genNickName())
                .setAvatar(UserRandomGenHelper.genAvatar());
        userRepository.saveAndFlush(user);
        return user.getId();
    }

    /**
     * 获取用户信息
     *
     * @param userId
     * @return
     */
    public UserBo getUserBo(Long userId) {
        UserEntity user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return null;
        }
        return new UserBo(user.getId(), user.getDisplayName(), IntBaseEnum.getEnumByCode(UserRole.class, user.getRole()));
    }
}
