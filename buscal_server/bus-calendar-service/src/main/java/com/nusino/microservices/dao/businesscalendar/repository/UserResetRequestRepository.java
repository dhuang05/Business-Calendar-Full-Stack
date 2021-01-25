/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.dao.businesscalendar.repository;

import com.nusino.microservices.model.buscalendar.Person;
import com.nusino.microservices.model.buscalendar.UserResetRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserResetRequestRepository extends JpaRepository<UserResetRequest, String> {
}
