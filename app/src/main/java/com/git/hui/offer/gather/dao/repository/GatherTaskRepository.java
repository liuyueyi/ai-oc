
package com.git.hui.offer.gather.dao.repository;

import com.git.hui.offer.gather.dao.entity.GatherTaskEntity;
import com.git.hui.offer.web.model.PageListVo;
import com.git.hui.offer.web.model.req.GatherTaskSearchReq;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.ArrayList;
import java.util.List;

public interface GatherTaskRepository extends JpaRepository<GatherTaskEntity, Long>, JpaSpecificationExecutor<GatherTaskEntity> {


    /**
     * 找到最早提交的待处理任务
     *
     * @param state
     * @return
     */
    GatherTaskEntity findFirstByStateOrderByCreateTimeAsc(Integer state);


    default PageListVo<GatherTaskEntity> findList(GatherTaskSearchReq req) {
        Specification<GatherTaskEntity> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (req.getType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("type"), req.getType()));
            }
            if (req.getModel() != null) {
                predicates.add(criteriaBuilder.equal(root.get("model"), req.getModel()));
            }
            if (req.getTaskId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("id"), req.getTaskId()));
            }
            if (req.getState() != null) {
                predicates.add(criteriaBuilder.equal(root.get("state"), req.getState()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        // 分页时，PageNumber 从 0开始
        Page<GatherTaskEntity> ans = findAll(spec
                // 分页查询
                , PageRequest.of(req.getPage() - 1, req.getSize())
                        // 根据时间倒排，时间相同的根据id进行倒排
                        .withSort(Sort.by(Sort.Order.desc("createTime"), Sort.Order.desc("id")))
        );
        return PageListVo.of(ans.getContent(), ans.getTotalElements(), req.getPage(), req.getSize());
    }
}
