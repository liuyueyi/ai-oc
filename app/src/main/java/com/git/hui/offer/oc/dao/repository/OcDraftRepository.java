package com.git.hui.offer.oc.dao.repository;

import com.git.hui.offer.oc.dao.entity.OcDraftEntity;
import com.git.hui.offer.web.model.PageListVo;
import com.git.hui.offer.web.model.req.DraftSearchReq;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;
import java.util.List;

public interface OcDraftRepository extends JpaRepository<OcDraftEntity, Long>, JpaSpecificationExecutor<OcDraftEntity> {

    @Query("""

            SELECT g FROM draft_oc g WHERE g.companyName = :companyName
            AND g.jobLocation = :jobLocation AND g.lastUpdatedTime = :lastUpdatedTime
            AND g.position = :position AND g.state != -1
            """)
    List<OcDraftEntity> findByUniqueKey(
            @Param("companyName") String companyName,
            @Param("jobLocation") String jobLocation,
            @Param("lastUpdatedTime") String lastUpdatedTime,
            @Param("position") String position
    );

    /**
     * 根据主键，更新 state 值
     *
     * 说明： @Modifying 需要和 @Transactional 注解一起使用
     *
     * @param id
     * @param state
     * @return
     */
    @Modifying(clearAutomatically = true)
    @Query(value = "update draft_oc g SET g.state = :state where g.id = :id")
    int updateStateById(@Param("id") Long id, @Param("state") Integer state);


    /**
     * 根据主键，更新 toProcess 值，同步清除一级缓存，避免脏数据
     *
     * @param ids
     * @param process
     * @return
     */
    @Modifying(clearAutomatically = true)
    @Query(value = "update draft_oc g SET g.toProcess = :process, g.state = :state where g.id in (:ids)")
    int updateProcessAndStateByIds(@Param("ids") List<Long> ids, @Param("process") Integer process, @Param("state") Integer state);

    /**
     * 条件查询
     *
     * @param req
     * @return
     */
    default PageListVo<OcDraftEntity> findList(DraftSearchReq req) {
        Specification<OcDraftEntity> spec = (root, query, criteriaBuilder) -> {
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
            if (req.getToProcess() != null) {
                predicates.add(criteriaBuilder.equal(root.get("toProcess"), req.getToProcess()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        // 分页时，PageNumber 从 0开始
        Page<OcDraftEntity> ans = findAll(spec
                // 分页查询
                , PageRequest.of(req.getPage() - 1, req.getSize())
                        // 根据时间倒排，时间相同的根据id进行倒排
                        .withSort(Sort.by(Sort.Order.desc("updateTime"), Sort.Order.desc("id")))
        );
        return PageListVo.of(ans.getContent(), ans.getTotalElements(), req.getPage(), req.getSize());
    }

}
