/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.vo.buscalendar;

import com.nusino.microservices.model.buscalendar.Organization;
import com.nusino.microservices.model.buscalendar.User;

public class RegistrationForm {
    private User user;
    private Organization organization;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }
}
