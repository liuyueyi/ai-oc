package com.git.hui.offer.user.service;

import com.git.hui.offer.components.bizexception.BizException;
import com.git.hui.offer.components.bizexception.StatusEnum;
import com.git.hui.offer.components.context.ReqInfoContext;
import com.git.hui.offer.components.context.UserBo;
import com.git.hui.offer.components.env.SpringUtil;
import com.git.hui.offer.components.id.IdUtil;
import com.git.hui.offer.components.permission.UserRole;
import com.git.hui.offer.constants.common.BaseStateEnum;
import com.git.hui.offer.user.convert.UserConvert;
import com.git.hui.offer.user.dao.entity.UserEntity;
import com.git.hui.offer.user.dao.repository.UserRepository;
import com.git.hui.offer.user.helper.UserRandomGenHelper;
import com.git.hui.offer.util.DateUtil;
import com.git.hui.offer.web.model.PageListVo;
import com.git.hui.offer.web.model.req.UserSaveReq;
import com.git.hui.offer.web.model.req.UserSearchReq;
import com.git.hui.offer.web.model.res.UserVo;
import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;

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

    /**
     * 查询用户列表
     *
     * @param req
     * @return
     */
    public PageListVo<UserVo> searchUserList(UserSearchReq req) {
        if (req.getRole() != null && req.getRole().equals(UserRole.ALL.getValue())) {
            req.setRole(null);
        }
        PageListVo<UserEntity> res = userRepository.findList(req);
        List<UserVo> list = UserConvert.toVo(res.getList());
        return PageListVo.of(list, res.getTotal(), req.getPage(), req.getSize());
    }

    public boolean updateUserRole(Long userId, Integer role, Long expireTime) {
        UserEntity user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new BizException(StatusEnum.RECORDS_NOT_EXISTS, "用户不存在");
        }
        if (Objects.equals(role, UserRole.VIP.getValue())) {
            // vip用户，要求过期时间存在
            if (expireTime == null) {
                expireTime = System.currentTimeMillis() + SpringUtil.getSiteConfig().getVipPeriods() * 86400_000L;
            }
            user.setExpireTime(new Date(expireTime / DateUtil.ONE_DAY_MILL * DateUtil.ONE_DAY_MILL));
        }
        user.setRole(role);
        user.setUpdateTime(new Date());
        userRepository.saveAndFlush(user);
        return true;
    }

    /**
     * 用户修改自己的信息
     *
     * @param req
     * @return
     */
    public UserVo updateUserInfo(UserSaveReq req) {
        if (Objects.equals(ReqInfoContext.getReqInfo().getUserId(), req.getUserId())) {
            throw new BizException(StatusEnum.FORBID_ERROR);
        }

        UserEntity user = userRepository.findById(req.getUserId()).orElse(null);
        if (user == null) {
            throw new BizException(StatusEnum.RECORDS_NOT_EXISTS, "用户不存在");
        }
        if (StringUtils.isNotBlank(req.getDisplayName())) {
            user.setDisplayName(req.getDisplayName());
        }
        if (StringUtils.isNotBlank(req.getAvatar())) {
            user.setAvatar(req.getAvatar());
        }
        user.setUpdateTime(new Date());
        userRepository.saveAndFlush(user);
        return UserConvert.toVo(user);
    }


    @Transactional(rollbackFor = Exception.class)
    public UserBo autoRegisterWxUserInfo(String uuid) {
        UserSaveReq req = new UserSaveReq().setWxId(uuid);
        UserEntity user = registerOrGetUserInfo(req);
        UserBo bo = UserConvert.toBo(user);
        ReqInfoContext.getReqInfo().setUserId(user.getId());
        ReqInfoContext.getReqInfo().setUser(bo);
        return bo;
    }

    /**
     * 没有注册时，先注册一个用户；若已经有，则登录
     *
     * @param req
     */
    private UserEntity registerOrGetUserInfo(UserSaveReq req) {
        UserEntity user = userRepository.findByWxId(req.getWxId());
        if (user == null) {
            return registerByWx(req.getWxId());
        }
        return user;
    }


    /**
     * 微信用户注册
     *
     * @param wxId 微信三方 id
     * @return 用户id
     */
    private UserEntity registerByWx(String wxId) {
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
        return user;
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
        return UserConvert.toBo(user);
    }
}
