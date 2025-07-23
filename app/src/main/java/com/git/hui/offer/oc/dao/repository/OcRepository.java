package com.git.hui.offer.oc.dao.repository;

import com.git.hui.offer.oc.dao.entity.OcInfoEntity;
import com.git.hui.offer.web.model.PageListVo;
import com.git.hui.offer.web.model.req.OcSearchReq;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.ArrayList;
import java.util.List;

public interface OcRepository extends JpaRepository<OcInfoEntity, Long>, JpaSpecificationExecutor<OcInfoEntity> {


    List<OcInfoEntity> findByDraftIdInAndStateNot(
            List<Long> draftIds, Integer state
    );


    /**
     * 条件查询
     *
     * @param req
     * @return
     */
    default PageListVo<OcInfoEntity> findList(OcSearchReq req) {
        Specification<OcInfoEntity> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (req.getId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("id"), req.getId()));
            }
            if (req.getCompanyName() != null && !req.getCompanyName().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("companyName"), "%" + req.getCompanyName() + "%"));
            }
            if (req.getCompanyType() != null && !req.getCompanyType().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("companyType"), req.getCompanyType()));
            }
            if (req.getJobLocation() != null && !req.getJobLocation().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("jobLocation"), "%" + req.getJobLocation() + "%"));
            }
            if (req.getRecruitmentType() != null && !req.getRecruitmentType().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("recruitmentType"), req.getRecruitmentType()));
            }
            if (req.getRecruitmentTarget() != null && !req.getRecruitmentTarget().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("recruitmentTarget"), "%" + req.getRecruitmentTarget() + "%"));
            }
            if (req.getPosition() != null && !req.getPosition().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("position"), "%" + req.getPosition() + "%"));
            }
            if (req.getDeliveryProgress() != null && !req.getDeliveryProgress().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("deliveryProgress"), req.getDeliveryProgress()));
            }
            if (req.getLastUpdatedTimeAfter() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("lastUpdatedTime"), req.getLastUpdatedTimeAfter()));
            }
            if (req.getLastUpdatedTimeBefore() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("lastUpdatedTime"), req.getLastUpdatedTimeBefore()));
            }
            if (req.getState() != null) {
                predicates.add(criteriaBuilder.equal(root.get("state"), req.getState()));
            }
            if (req.getNotState() != null) {
                predicates.add(criteriaBuilder.notEqual(root.get("state"), req.getNotState()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        // 分页时，PageNumber 从 0开始
        Page<OcInfoEntity> ans = findAll(spec
                // 分页查询
                , PageRequest.of(req.getPage() - 1, req.getSize())
                        // 根据id进行倒排
                        .withSort(Sort.by(Sort.Order.desc("id")))
        );
        return PageListVo.of(ans.getContent(), ans.getTotalElements(), req.getPage(), req.getSize());
    }

}
