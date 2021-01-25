/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.dao.businesscalendar.repository;

import com.nusino.microservices.model.buscalendar.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsersRepository extends JpaRepository<User, String> {
    @Modifying
    @Query(value = "UPDATE USERS set TOKEN = :token where USER_ID = :userId",
            nativeQuery = true)
    void updateToken(@Param("userId") String userId, @Param("token") String token);

    @Query(value = "Select * from USERS u left join PERSON p on p.PERSON_ID = u.PERSON_ID where (upper(u.USER_ID) like :keyword or upper(p.FIRST_NAME) like :keyword or upper(p.LAST_NAME) like :keyword) and u.ORG_ID = :orgId",
            nativeQuery = true)
    List<User> findByKeywordAndOrgId(@Param("keyword") String keyword, @Param("orgId") String orgId);

    @Query(value = "Select * from USERS u left join PERSON p on p.PERSON_ID = u.PERSON_ID where upper(u.USER_ID) like :keyword or upper(p.FIRST_NAME) like :keyword or upper(p.LAST_NAME) like :keyword",
            nativeQuery = true)
    List<User> findByKeyword(@Param("keyword") String keyword);

    @Query(value = "Select * from USERS u left join PERSON p on p.PERSON_ID = u.PERSON_ID where u.ORG_ID = :orgId",
            nativeQuery = true)
    List<User> findByOrgId(@Param("orgId") String orgId);

    @Query(value = "Select * from USERS u where upper(u.USER_ID) = upper(:userId)",
            nativeQuery = true)
    User findUserById(@Param("userId") String userId);

    @Query(value = "Select * from USERS u where TOKEN = :token",
            nativeQuery = true)
    User findUserByToken(@Param("token") String userId);
}
