package com.git.hui.offer.oc.dao.repository;

import com.git.hui.offer.oc.dao.entity.GatherDraftOcEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GatherOcDraftRepository extends JpaRepository<GatherDraftOcEntity, Long> {

    @Query("""

            SELECT g FROM gather_draft_oc g WHERE g.companyName = :companyName
            AND g.jobLocation = :jobLocation AND g.lastUpdatedTime = :lastUpdatedTime
            AND g.position = :position AND g.state != -1
            """)
    List<GatherDraftOcEntity> findByUniqueKey(
            @Param("companyName") String companyName,
            @Param("jobLocation") String jobLocation,
            @Param("lastUpdatedTime") String lastUpdatedTime,
            @Param("position") String position
    );
}
