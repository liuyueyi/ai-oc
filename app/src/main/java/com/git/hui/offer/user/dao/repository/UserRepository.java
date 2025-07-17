
package com.git.hui.offer.user.dao.repository;

import com.git.hui.offer.user.dao.entity.UserEntity;
import com.git.hui.offer.web.model.PageListVo;
import com.git.hui.offer.web.model.req.UserSearchReq;
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

public interface UserRepository extends JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {

    UserEntity findByWxId(String wxId);

    /**
     * 更新用户权限
     *
     * @param userId
     * @param role
     * @param expire
     * @return
     */
    @Modifying(clearAutomatically = true)
    @Query(value = "update user_info u SET u.role = :role, u.expireTime = :expire, u.updateTime=now() where u.id = :id")
    int updateRoleById(@Param("userId") Long userId, @Param("role") Integer role, @Param("expireTime") String expire);


    /**
     * 条件查询
     *
     * @param req
     * @return
     */
    default PageListVo<UserEntity> findList(UserSearchReq req) {
        Specification<UserEntity> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (req.getUserId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("id"), req.getUserId()));
            }
            if (req.getDisplayName() != null && !req.getDisplayName().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("displayName"), "%" + req.getDisplayName() + "%"));
            }
            if (req.getRole() != null) {
                predicates.add(criteriaBuilder.equal(root.get("role"), req.getRole()));
            }
            // 过滤掉已经删除的数据
            predicates.add(criteriaBuilder.notEqual(root.get("state"), -1));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        // 分页时，PageNumber 从 0开始
        Page<UserEntity> ans = findAll(spec
                // 分页查询
                , PageRequest.of(req.getPage() - 1, req.getSize())
                        // 根据时间倒排，时间相同的根据id进行倒排
                        .withSort(Sort.by(Sort.Order.desc("updateTime"), Sort.Order.desc("id")))
        );
        return PageListVo.of(ans.getContent(), ans.getTotalElements(), req.getPage(), req.getSize());
    }
}
