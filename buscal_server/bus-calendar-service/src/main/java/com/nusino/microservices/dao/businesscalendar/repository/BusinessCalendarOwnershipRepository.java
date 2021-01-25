/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.dao.businesscalendar.repository;

import com.nusino.microservices.model.buscalendar.BusinessCalendarOwnership;
import com.nusino.microservices.model.buscalendar.BusinessCalendarOwnershipId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusinessCalendarOwnershipRepository extends JpaRepository<BusinessCalendarOwnership, BusinessCalendarOwnershipId> {
    @Query(value = "SELECT * FROM CALENDAR_OWNERSHIP c WHERE c.CAL_ID = :calId and c.STATUS = 'ACTIVE' and c.VERSION = (SELECT max(VERSION)  FROM CALENDAR_OWNERSHIP WHERE CAL_ID = :calId)",
            nativeQuery = true)
    BusinessCalendarOwnership findLatestById(@Param("calId") String calId);

    @Query(value = "SELECT * FROM CALENDAR_OWNERSHIP c WHERE c.STATUS = 'ACTIVE' and c.OWNER_ID IN (:ownerIds) and c.VERSION = (SELECT max(VERSION)  FROM CALENDAR_OWNERSHIP WHERE CAL_ID = c.CAL_ID)",
            nativeQuery = true)
    List<BusinessCalendarOwnership> findByOrgIds(@Param("ownerIds") List<String> ownerIds);


    @Query(value = "SELECT * FROM CALENDAR_OWNERSHIP c WHERE c.CAL_ID = :calId and c.VERSION = (SELECT max(VERSION)  FROM CALENDAR_OWNERSHIP WHERE CAL_ID = :calId)",
            nativeQuery = true)
    BusinessCalendarOwnership findActiveLatestById(@Param("calId") String calId);

    @Query(value = "SELECT * FROM CALENDAR_OWNERSHIP c WHERE c.OWNER_ID IN (:ownerIds) and c.VERSION = (SELECT max(VERSION)  FROM CALENDAR_OWNERSHIP WHERE CAL_ID = c.CAL_ID)",
            nativeQuery = true)
    List<BusinessCalendarOwnership> findActiveByOrgIds(@Param("ownerIds") List<String> ownerIds);

    @Query(value = "SELECT * FROM CALENDAR_OWNERSHIP c WHERE c.OWNER_ID IN (SELECT u.ORG_ID  FROM USERS u WHERE u.USER_ID = :userId)",
            nativeQuery = true)
    List<BusinessCalendarOwnership> findByUserId(@Param("userId") String userId);


}