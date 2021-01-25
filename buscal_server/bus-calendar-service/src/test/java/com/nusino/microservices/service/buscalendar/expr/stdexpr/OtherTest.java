package com.nusino.microservices.service.buscalendar.expr.stdexpr;

import com.nusino.microservices.service.buscalendar.util.JsonUtil;
import com.nusino.microservices.vo.buscalendar.BusinessHour;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.regex.Pattern;

public class OtherTest {
    Pattern LOGIC_REGEX = Pattern.compile("^([\\||&|!|>|<|=|(|)]{1,})");

    @Test
    public void TimeZoneConvertingTest() {
        LocalDateTime ldt = LocalDateTime.of(2020, 12, 1, 14, 10, 50);
        ZonedDateTime zt = ldt.atZone(ZoneId.of("America/Toronto"));

        System.out.println("1 /// " + ldt);
        System.out.println("2 /// " + zt);


        ZoneId zone = ZoneId.of("Europe/Berlin");
        zt = zt.withZoneSameInstant(zone);
        System.out.println("3 /// " + zt);

    }

    @Test
    public void LocalTimeParsingTest() {
        BusinessHour businessHour = new BusinessHour();
        businessHour.setBusinessHourFrom(LocalTime.of(8, 10));
        businessHour.setBusinessHourTo(LocalTime.of(14, 10, 20));

        String json = JsonUtil.toJson(businessHour);

        System.out.println(json);

        businessHour = JsonUtil.fromJson(json, BusinessHour.class);
    }

}
