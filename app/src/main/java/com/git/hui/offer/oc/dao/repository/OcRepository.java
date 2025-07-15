package com.git.hui.offer.oc.dao.repository;

import com.git.hui.offer.oc.dao.entity.OcEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OcRepository extends JpaRepository<OcEntity, Long> {


    List<OcEntity> findByDraftIdInAndStateNot(
            List<Long> draftIds, Integer state
    );
}
