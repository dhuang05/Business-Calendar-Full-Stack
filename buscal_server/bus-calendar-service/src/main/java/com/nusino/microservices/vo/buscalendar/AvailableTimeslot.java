/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.vo.buscalendar;

/**
 * calendarTimeslot and requestTimeslot are equals but different timezone.
 */
public class AvailableTimeslot {
    private Timeslot calendarTimeslot;
    private Timeslot requestTimeslot;

    public Timeslot getCalendarTimeslot() {
        return calendarTimeslot;
    }

    public void setCalendarTimeslot(Timeslot calendarTimeslot) {
        this.calendarTimeslot = calendarTimeslot;
    }

    public Timeslot getRequestTimeslot() {
        return requestTimeslot;
    }

    public void setRequestTimeslot(Timeslot requestTimeslot) {
        this.requestTimeslot = requestTimeslot;
    }

    @Override
    public AvailableTimeslot clone() {
        AvailableTimeslot cloned = new AvailableTimeslot();
        if (calendarTimeslot != null) {
            cloned.calendarTimeslot = calendarTimeslot.clone();
        }
        if (requestTimeslot != null) {
            cloned.requestTimeslot = requestTimeslot.clone();
        }
        return cloned;
    }
}
