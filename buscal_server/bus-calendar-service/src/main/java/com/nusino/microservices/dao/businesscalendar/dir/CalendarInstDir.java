/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.dao.businesscalendar.dir;

import com.nusino.microservices.service.buscalendar.util.JsonUtil;
import com.nusino.microservices.service.buscalendar.util.KeySmith;
import com.nusino.microservices.vo.buscalendar.CalendarInst;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

@Service
public class CalendarInstDir {
    @Value("${data-repo-dir:./business-calendar-repo}")
    private String dataRepoDir;
    private static final Map<String, CalendarInst> CALENDAR_INST_REPO = new HashMap<>();

    public CalendarInst findLatestById(String calId) {
        if (CALENDAR_INST_REPO.isEmpty()) {
            loadAllFiles();
        }
        return CALENDAR_INST_REPO.get(KeySmith.makeKey(calId));
    }

    private void loadAllFiles() {
        File root = new File(dataRepoDir);
        if (!root.exists()) {
            root.mkdirs();
        }
        loadAllFiles(root);
    }

    private void loadAllFiles(File folder) {
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                loadAllFiles(file);
            } else if (file.getName().toUpperCase().endsWith(".JSON")) {
                try {
                    byte[] bytes = Files.readAllBytes(file.toPath());
                    CalendarInst calendarInst = JsonUtil.fromJson(new String(bytes), CalendarInst.class);
                    CALENDAR_INST_REPO.put(KeySmith.makeKey(calendarInst.getCalId()), calendarInst);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public void saveCalendarInst(CalendarInst calendarInst) {
        File root = new File(dataRepoDir);
        if (!root.exists()) {
            root.mkdirs();
        }
        File file = new File(dataRepoDir + File.separatorChar + calendarInst.getCalId() + ".json");
        FileOutputStream fileOut = null;
        try {
            String json = JsonUtil.toJson(calendarInst);
            fileOut = new FileOutputStream(file);
            fileOut.write(json.getBytes());
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (fileOut != null) {
                try {
                    fileOut.close();
                } catch (Exception ex2) {
                    //
                }
            }
        }

    }
}
