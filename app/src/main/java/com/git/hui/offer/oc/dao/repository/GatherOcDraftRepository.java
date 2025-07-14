package com.git.hui.offer.oc.dao.repository;

import com.git.hui.offer.oc.dao.entity.GatherOcDraftEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GatherOcDraftRepository extends JpaRepository<GatherOcDraftEntity, Long> {

    @Query("""

            SELECT g FROM GatherOcDraftEntity g WHERE g.companyName = :companyName
            AND g.jobLocation = :jobLocation AND g.lastUpdatedTime = :lastUpdatedTime
            AND g.position = :position AND g.state != -1
            """)
    List<GatherOcDraftEntity> findByUniqueKey(
            @Param("companyName") String companyName,
            @Param("jobLocation") String jobLocation,
            @Param("lastUpdatedTime") String lastUpdatedTime,
            @Param("position") String position
    );
}
