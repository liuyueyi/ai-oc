package com.git.hui.offer.oc.mcp;

import com.git.hui.offer.components.context.ReqInfoContext;
import com.git.hui.offer.oc.mcp.model.McpReqDto;
import com.git.hui.offer.oc.service.OcService;
import com.git.hui.offer.util.json.JsonUtil;
import com.git.hui.offer.web.model.PageListVo;
import com.git.hui.offer.web.model.req.OcSearchReq;
import com.git.hui.offer.web.model.res.OcVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.List;

/**
 * @author YiHui
 * @date 2025/7/28
 */
@Slf4j
@Service
public class OcMcpService {
    private final OcService ocService;

    @Autowired
    public OcMcpService(OcService ocService) {
        this.ocService = ocService;
    }

    @Tool(description = "根据输入的用户求职意愿信息，返回满足条件的职位列表给用户")
    public List<OcVo> queryRecommendOcListForUser(McpReqDto req) {
        log.info("用户：{} 使用了MCP Server进行校招信息推荐：{}", ReqInfoContext.getReqInfo().getUser().nickName(), JsonUtil.toStr(req));
        OcSearchReq search = new OcSearchReq();
        if (StringUtils.isNotBlank(req.getCompanyType())) {
            search.setCompanyType(req.getCompanyType());
        }
        if (StringUtils.isNotBlank(req.getRecruitmentType())) {
            search.setRecruitmentType(req.getRecruitmentType());
        }
        if (StringUtils.isNotBlank(req.getRecruitmentTarget())) {
            search.setRecruitmentTarget(req.getRecruitmentTarget());
        }
        if (StringUtils.isNotBlank(req.getPosition())) {
            search.setPosition(req.getPosition());
        }
        if (StringUtils.isNotBlank(req.getJobLocation())) {
            search.setJobLocation(req.getJobLocation());
        }
        // 定义最多只返回20条数据
        search.setPage(1);
        search.setSize(20);
        PageListVo<OcVo> vo = ocService.searchOcList(search);
        return vo.getList();
    }
}
