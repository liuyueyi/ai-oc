
package com.git.hui.offer.configs.dao.repository;

import com.git.hui.offer.configs.dao.entity.CommonDictEntity;
import com.git.hui.offer.constants.common.BaseStateEnum;
import com.git.hui.offer.web.model.PageListVo;
import com.git.hui.offer.web.model.req.DictSearchReq;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.ArrayList;
import java.util.List;

public interface CommonDictRepository extends JpaRepository<CommonDictEntity, Long>, JpaSpecificationExecutor<CommonDictEntity> {

    /**
     * 查询有效的字典
     *
     * @param state 状态
     * @return
     */
    List<CommonDictEntity> findAllByState(Integer state);

    CommonDictEntity findFirstByAppAndKeyAndValue(String app, String key, String value);

    default PageListVo<CommonDictEntity> findList(DictSearchReq req) {
        Specification<CommonDictEntity> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (req.getScope() != null) {
                predicates.add(criteriaBuilder.equal(root.get("scope"), req.getScope()));
            }
            if (req.getKey() != null) {
                predicates.add(criteriaBuilder.like(root.get("key"), "%" + req.getKey() + "%"));
            }
            if (req.getState() != null) {
                predicates.add(criteriaBuilder.equal(root.get("state"), req.getState()));
            }
            // 不查询已删除的值
            predicates.add(criteriaBuilder.notEqual(root.get("state"), BaseStateEnum.DELETED_STATE.getValue()));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        // 分页时，PageNumber 从 0开始
        Page<CommonDictEntity> ans = findAll(spec
                // 分页查询
                , PageRequest.of(req.getPage() - 1, req.getSize())
                        // 根据时间倒排，时间相同的根据id进行倒排
                        .withSort(Sort.by(Sort.Order.desc("createTime"), Sort.Order.desc("id")))
        );
        return PageListVo.of(ans.getContent(), ans.getTotalElements(), req.getPage(), req.getSize());
    }

}
