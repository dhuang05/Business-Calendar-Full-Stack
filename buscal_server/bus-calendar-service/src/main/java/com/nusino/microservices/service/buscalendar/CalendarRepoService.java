/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.service.buscalendar;

import com.nusino.microservices.dao.businesscalendar.dir.CalendarInstDir;
import com.nusino.microservices.dao.businesscalendar.repository.BusinessCalendarOwnershipRepository;
import com.nusino.microservices.model.buscalendar.BusinessCalendarOwnership;
import com.nusino.microservices.service.buscalendar.util.JsonUtil;
import com.nusino.microservices.vo.buscalendar.CalendarInst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

@Service
public class CalendarRepoService {
    @Autowired
    private BusinessCalendarOwnershipRepository calendarOwnershipRepository;
    private CalendarInstDir calendarDir;
    @Autowired
    private FullCalendarKeeper fullCalendarKeeper;

    public CalendarInst findLatestById(String calId) {
        CalendarInst calendarInst = null;
        BusinessCalendarOwnership calendarInstOwnership = calendarOwnershipRepository.findLatestById(calId);
        if (calendarInstOwnership != null) {
            String json = calendarInstOwnership.getCalendarInstJson();
            if (json != null && !json.trim().isEmpty()) {
                calendarInst = JsonUtil.fromJson(json, CalendarInst.class);
            } else {
                String url = calendarInstOwnership.getCalendarInstUrl();
                if (url != null) {
                    //no implemented yet
                } else {
                    calendarInst = calendarDir.findLatestById(calId);
                }
            }
        }
        return calendarInst;
    }

    public CalendarInst loadTemplate() {
        CalendarInst calendarInst = null;
        InputStream in = CalendarRepoService.class.getClassLoader().getResourceAsStream("template/america.json");
        try {
            byte[] bytes = in.readAllBytes();
            calendarInst = JsonUtil.fromJson(new String(bytes), CalendarInst.class);

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (Exception ex2) {
                //
            }
        }
        return calendarInst;
    }

    public BusinessCalendarOwnership saveCalendarInst(BusinessCalendarOwnership businessCalendarOwnership) {
        /*
        // if save to db, otherwise, save to
        if( save to othe place) {
            //TODO: save
            //then
            businessCalendarOwnership.setCalendarInstJson(null);
            //if to remote, not local
            businessCalendarOwnership.setCalendarInstUrl();
        }
        */
        calendarOwnershipRepository.saveAndFlush(businessCalendarOwnership);
        return businessCalendarOwnership;
    }

    public List<BusinessCalendarOwnership> findCalendarInstOwnershipsByUserId(String userId) {
        return calendarOwnershipRepository.findByUserId(userId);
    }

}
