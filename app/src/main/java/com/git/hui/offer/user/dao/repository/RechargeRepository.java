
package com.git.hui.offer.user.dao.repository;

import com.git.hui.offer.user.dao.entity.RechargeEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RechargeRepository extends JpaRepository<RechargeEntity, Long>, JpaSpecificationExecutor<RechargeEntity> {

    List<RechargeEntity> findByUserIdAndVipLevelAndStatusInOrderByIdDesc(Long userId, Integer vipLevel, List<Integer> status);


    /**
     * 查询用户的充值记录
     *
     * @param userId
     * @param status
     * @return
     */
    List<RechargeEntity> findByUserIdAndStatusInOrderByIdDesc(Long userId, List<Integer> status);


    /**
     * 使用悲观锁获取充值记录
     *
     * @param id
     * @return
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from user_recharge r where r.id = ?1")
    RechargeEntity selectByIdForUpdate(Long id);
}
