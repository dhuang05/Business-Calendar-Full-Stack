/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.model.buscalendar;

import java.io.Serializable;

public class BusinessCalendarOwnershipId implements Serializable {
    private String calId;
    private double version;

    public String getCalId() {
        return calId;
    }

    public void setCalId(String calId) {
        this.calId = calId;
    }
}
