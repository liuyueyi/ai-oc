package com.git.hui.offer.web.model.res;

import com.git.hui.offer.oc.dao.entity.OcDraftEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author YiHui
 * @date 2025/7/18
 */
@Data
@Accessors(chain = true)
public class GatherVo {
    private List<OcDraftEntity> insertList;
    private List<OcDraftEntity> updateList;
}
