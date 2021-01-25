/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.vo.buscalendar;

import com.nusino.microservices.model.buscalendar.Status;

public class CalendarOwnership {
    private String calId;
    private double version;
    private String token;
    private String description;
    private CalendarInst calendarInst;
    private String calendarInstUrl;
    private String ownerId;
    private Status status = Status.ACTIVE;
    private String note;

    public String getCalId() {
        return calId;
    }

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

    public CalendarInst getCalendarInst() {
        return calendarInst;
    }

    public void setCalendarInst(CalendarInst calendarInst) {
        this.calendarInst = calendarInst;
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
