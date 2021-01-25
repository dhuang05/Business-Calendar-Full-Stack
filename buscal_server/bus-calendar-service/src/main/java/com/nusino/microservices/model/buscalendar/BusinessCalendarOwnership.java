/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.model.buscalendar;

import javax.persistence.*;

@Entity
@IdClass(BusinessCalendarOwnershipId.class)
@Table(name = "CALENDAR_OWNERSHIP")
public class BusinessCalendarOwnership extends BusinessCalendarOwnershipId {
    @Id
    @Basic(optional = false)
    @Column(name = "CAL_ID", nullable = false)
    private String calId;

    @Id
    @Column(name = "VERSION")
    private double version;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "TOKEN")
    private String token;

    @Column(name = "CALENDAR_INST_JSON")
    private String calendarInstJson;

    @Column(name = "CALENDAR_INST_URL")
    private String calendarInstUrl;

    @Column(name = "OWNER_ID")
    private String ownerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private Status status = Status.ACTIVE;

    @Column(name = "NOTE")
    private String note;

    @Override
    public String getCalId() {
        return calId;
    }

    @Override
    public void setCalId(String calId) {
        this.calId = calId;
    }

    public double getVersion() {
        return version;
    }

    public void setVersion(double version) {
        this.version = version;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCalendarInstJson() {
        return calendarInstJson;
    }

    public void setCalendarInstJson(String calendarInstJson) {
        this.calendarInstJson = calendarInstJson;
    }

    public String getCalendarInstUrl() {
        return calendarInstUrl;
    }

    public void setCalendarInstUrl(String calendarInstUrl) {
        this.calendarInstUrl = calendarInstUrl;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}