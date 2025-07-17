package com.git.hui.offer.web.controller.front;

import com.git.hui.offer.components.context.ReqInfoContext;
import com.git.hui.offer.components.permission.Permission;
import com.git.hui.offer.components.permission.UserRole;
import com.git.hui.offer.user.service.UserService;
import com.git.hui.offer.web.model.req.UserSaveReq;
import com.git.hui.offer.web.model.res.UserVo;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 后台用户管理
 *
 * @author YiHui
 * @date 2025/7/17
 */
@Permission(role = UserRole.NORMAL)
@RestController
@RequestMapping(path = "/api/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    /**
     * 更新用户信息
     *
     * @param displayName 用户id
     * @param avatar      角色
     * @return
     */
    @RequestMapping(path = "update")
    public UserVo updateUserRole(String displayName, String avatar) {
        Assert.notNull(displayName, "用户id不能为空");
        Assert.notNull(avatar, "角色不能为空");
        UserSaveReq req = new UserSaveReq().setUserId(ReqInfoContext.getReqInfo().getUserId())
                .setDisplayName(displayName)
                .setAvatar(avatar);
        return userService.updateUserInfo(req);
    }
}
