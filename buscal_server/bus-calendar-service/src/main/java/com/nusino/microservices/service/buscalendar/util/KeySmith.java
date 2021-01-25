/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.service.buscalendar.util;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

public class KeySmith {
    private final static Pattern SPLITTER = Pattern.compile("[" + ((char) 03) + "]{1,}");

    public static String makeKey(String... codes) {
        int i = 0;
        StringBuilder key = new StringBuilder();
        for (String code : codes) {
            code = StringUtils.normalizeSpace(code);
            if (i > 0) {
                key.append(((char) 03));
            }
            key.append(code.toUpperCase().trim());
            i++;
        }
        return key.toString();
    }

    public static String makeKey(Object... codes) {
        int i = 0;
        StringBuilder key = new StringBuilder();
        for (Object codeObj : codes) {
            String code = StringUtils.normalizeSpace(codeObj.toString());
            if (i > 0) {
                key.append(((char) 03));
            }
            key.append(code.toUpperCase().trim());
            i++;
        }
        return key.toString();
    }

    public static String[] decomposeKey(String key) {
        if (key == null) {
            return null;
        }
        return SPLITTER.split(key);
    }


}
