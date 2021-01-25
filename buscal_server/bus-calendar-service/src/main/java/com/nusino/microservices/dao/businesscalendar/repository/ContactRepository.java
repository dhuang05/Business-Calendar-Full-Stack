/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.dao.businesscalendar.repository;

import com.nusino.microservices.model.buscalendar.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ContactRepository extends JpaRepository<Contact, String> {
    @Query(value = "select count(*) from  CONTACT where upper(EMAIL) = upper(:email)",
            nativeQuery = true)
    Integer countByEmail(@Param("email") String email);
}
