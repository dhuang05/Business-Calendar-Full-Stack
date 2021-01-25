/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.dao.businesscalendar.repository;

import com.nusino.microservices.model.buscalendar.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrganizationRepository extends JpaRepository<Organization, String> {

    @Query(value = "SELECT * FROM ORGANIZATION o WHERE o.PARENT_ORG_ID = :parentOrgId",
            nativeQuery = true)
    List<Organization> findAllChildren(@Param("parentOrgId") String parentOrgId);

    @Query(value = "SELECT * FROM ORGANIZATION o left join USERS u on u.ORG_ID = o.ORG_ID WHERE u.USER_ID = :userId and o.ORG_NAME like :keyword",
            nativeQuery = true)
    List<Organization> findByKeywordAndUserId(@Param("keyword") String keyword, @Param("userId") String userId);


    @Query(value = "SELECT * FROM ORGANIZATION o left join USERS u on u.ORG_ID = o.ORG_ID WHERE u.USER_ID = :userId",
            nativeQuery = true)
    List<Organization> findByUserId(@Param("userId") String userId);

    @Query(value = "SELECT * FROM ORGANIZATION o WHERE o.ORG_NAME like :keyword",
            nativeQuery = true)
    List<Organization> findByKeyword(@Param("keyword") String keyword);

}
