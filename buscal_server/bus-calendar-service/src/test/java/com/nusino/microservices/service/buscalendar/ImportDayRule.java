/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.service.buscalendar;

import com.nusino.microservices.service.buscalendar.expr.DayRuleExprInterpretor;
import com.nusino.microservices.service.buscalendar.expr.model.DayRuleExpr;
import com.nusino.microservices.service.buscalendar.util.JsonUtil;
import com.nusino.microservices.vo.buscalendar.CalendarInst;
import com.nusino.microservices.vo.buscalendar.DayRule;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ImportDayRule {
    private final ExprEngine exprEngine = new ExprEngine(true);

    private final SimpleXlsxReader simpleXlsxReader = new SimpleXlsxReader();

    public static void main(String[] args) throws Exception {
        ImportDayRule importDayRule = new ImportDayRule();
        importDayRule.readImport("n_america_holiday_rule.xlsx");
    }

    public void readImport(String url) throws IOException {
        String sheetName = "CA";
        Map<String, String> abrevStateMap = readStateMap(sheetName + ".txt");
        Map<String, String>  stateTZ = readStateTimeZoneMap(sheetName + "_ZID.txt");
        Map<Integer, CalRule> calendars = readImport(url, sheetName);
        List<CalendarInst> calendarInsts = toCalendars (calendars, abrevStateMap, stateTZ);

        String sqlTemplate = "INSERT INTO CALENDAR_OWNERSHIP (CAL_ID, OWNER_ID, DESCRIPTION, CALENDAR_INST_JSON) VALUES ('{ID}','SUPER_ORG', '{DESC}', '{JSON}'); \n";
        StringBuilder sb = new StringBuilder();
        for(CalendarInst inst : calendarInsts) {
            String sql = sqlTemplate;
            sql = sql.replace("{ID}", inst.getCalId());
            sql = sql.replace("{DESC}", inst.getDesc().replaceAll("[']{1}", "''"));
            sql = sql.replace("{JSON}", JsonUtil.toJson(inst).replaceAll("[']{1}", "''"));
            sb.append(sql);
        }

        System.out.print("======= result:\n" + JsonUtil.toJson(calendarInsts));
        System.out.print("======= sql:\n" +sb.toString());
    }

    public List<CalendarInst> toCalendars (Map<Integer, CalRule> calendars, Map<String, String> abrevStateMap, Map<String, String>  stateTZ) {
        CalendarInst template = loadTemplate();
        List<CalendarInst> calendarInsts = new ArrayList<>();
        for(CalRule calRule : calendars.values()) {
            String name = calRule.name;
            String[] industStateCountry = splitCalendarName(name);
            String indust = industStateCountry[0].trim();
            if(indust.equals("GOV")) {
                indust = "Government".toUpperCase();
                name = name.replaceAll("GOV", indust);
            }
            indust = indust.toLowerCase();
            indust = indust.substring(0,1).toUpperCase() + indust.substring(1);
            String stateAbrev = industStateCountry[1].trim();
            String country = industStateCountry[2].trim();
            String state = abrevStateMap.get(stateAbrev.toUpperCase());
            if(stateAbrev.toUpperCase().equals("ALL")) {
                continue;
            }
            //System.out.println("STATE for timezone: " + stateAbrev.toUpperCase());
            String timeZoneId = stateTZ.get(state.toUpperCase());

            // GET TOGETHER
            CalendarInst calInst = JsonUtil.clone(template);
            calInst.getHolidayRules().clear();
            //
            calendarInsts.add(calInst);
            calInst.setCalId(name);
            calInst.setDesc(indust + " in " + state + ", " + (country.equals("CA") ? "Canada" : "America") );
            calInst.setTimeZone(timeZoneId);

            for(ImportDayRule.Rule rule : calRule.rules) {
                DayRule dayRUle = new DayRule();
                dayRUle.setDesc(normalize(rule.name));
                dayRUle.setExpr(normalize(rule.expr));
                calInst.getHolidayRules().add(dayRUle);
            }
        }
        return calendarInsts;
    }

    public String[] splitCalendarName(String text) {
        String[] ret = new String[3];
        //"BANK@AK_US"
        text = text.trim();
        String[] parks = text.split("[@]");
        ret[0] = parks[0];
        parks = parks[1].split("[_]");
        ret[1] = parks[0];
        ret[2] = parks[1];

        return ret;
    }

    private String normalize(String text) {
        return text.replaceAll("[\\s]{1,}", " ").trim();
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

    public Map<String, String> readStateMap(String url) throws IOException {
        Map<String, String> map = new HashMap<>();
        InputStream in = null;
        try {
            try {
                in = new FileInputStream(url);
            } catch (Exception ex) {
                if (in == null) {
                    in = ImportDayRule.class.getClassLoader().getResourceAsStream(url);
                }
            }
            StringBuilder contentBuilder = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {

                String sCurrentLine;
                while ((sCurrentLine = br.readLine()) != null) {
                    if (sCurrentLine != null && !sCurrentLine.trim().isEmpty()) {
                        sCurrentLine = sCurrentLine.trim().replaceAll("[\\s]{1,}", " ");
                        int lastIndex = sCurrentLine.lastIndexOf(" ");
                        String state = sCurrentLine.substring(0, lastIndex);
                        String abrev = sCurrentLine.substring(lastIndex);

                        map.put(abrev.trim().toUpperCase(), state.trim());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            // report error
        } finally {
            try {
                in.close();
            } catch (Exception ex) {
                //
            }
        }
        return map;
    }

    public Map<String, String> readStateTimeZoneMap(String url) throws IOException {
        Map<String, String> map = new HashMap<>();
        InputStream in = null;
        try {
            try {
                in = new FileInputStream(url);
            } catch (Exception ex) {
                if (in == null) {
                    in = ImportDayRule.class.getClassLoader().getResourceAsStream(url);
                }
            }
            StringBuilder contentBuilder = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {

                String sCurrentLine;
                while ((sCurrentLine = br.readLine()) != null) {
                    if (sCurrentLine != null && !sCurrentLine.trim().isEmpty()) {
                        sCurrentLine = sCurrentLine.trim().replaceAll("[\\s]{1,}", " ");
                        int lastIndex = sCurrentLine.lastIndexOf(" ");
                        String state = sCurrentLine.substring(0, lastIndex);
                        String timezone = sCurrentLine.substring(lastIndex);

                        map.put(state.trim().toUpperCase(), timezone.trim());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            // report error
        } finally {
            try {
                in.close();
            } catch (Exception ex) {
                //
            }
        }
        return map;
    }



    public  Map<Integer, CalRule> readImport(String url, String sheetName) throws IOException {
        InputStream in = null;
        try {
            try {
                in = new FileInputStream(url);
            } catch (Exception ex) {
                if (in == null) {
                    in = ImportDayRule.class.getClassLoader().getResourceAsStream(url);
                }
            }
            List<Map<Integer, Object>> calData = readImport(in, sheetName);
            int rowNum = 0;
            //order already as sequence
            Map<Integer, CalRule> calendars = new HashMap<>();
            for (Map<Integer, Object> row : calData) {

                if (rowNum == 0) {
                    calendars.putAll(readHearAsCalendar(row));
                } else {
                    buildCalendar(row, calendars);
                }
                rowNum++;
            }
            return calendars;
        } catch (Exception ex) {
            ex.printStackTrace();
            // report error
        } finally {
            try {
                in.close();
            } catch (Exception ex) {
                //
            }
        }
        return null;
    }


    public List<Map<Integer, Object>> readImport(InputStream in, String sheetName) throws IOException {
        simpleXlsxReader.importWorkbook(in);
        simpleXlsxReader.requestSheet(sheetName);
        int maxCol = 300;
        return simpleXlsxReader.readAllRows(0, maxCol);
    }

    private void buildCalendar(Map<Integer, Object> row, Map<Integer, CalRule> calendars) {
        Rule rule = null;
        for (Integer col : row.keySet()) {
            if (col == 0) {
                rule = new Rule();
                Object name = row.get(col);
                if (name == null || name.toString().trim().isEmpty()) {
                    break;
                }
                rule.name = name.toString().trim();
            }
            if (col == 3) {
                Object data = row.get(col);
                if (data == null || data.toString().trim().isEmpty()) {
                    break;
                }
                rule.expr = data.toString().trim();
                evaluateWithError(rule.name, rule.expr, null);
            }
            if (col >= 4) {
                Object data = row.get(col);
                if (data == null || data.toString().trim().isEmpty()) {
                    continue;
                }
                if (data.toString().trim().toUpperCase().equalsIgnoreCase("Y") && calendars.get(col) != null) {
                    calendars.get(col).rules.add(rule);
                }
            }
        }
    }

    private Map<Integer, CalRule> readHearAsCalendar(Map<Integer, Object> row) {
        Map<Integer, CalRule> calendars = new HashMap<>();
        for (Integer col : row.keySet()) {
            if (col >= 4) {
                Object name = row.get(col);
                if (name != null && !name.toString().trim().isEmpty()) {
                    CalRule calRule = new CalRule();
                    calRule.name = name.toString().trim();
                    calendars.put(col, calRule);
                }
            }
        }
        return calendars;
    }


    private void evaluateWithError(String name, String exprText, LocalDate day) {
        if (day == null) {
            day = LocalDate.now();
        }
        try {
            DayRuleExprInterpretor interpretor = exprEngine.getDayRuleExprInterpretor();
            System.out.printf("////%s data, input date to test %s \n input/out:\n%s\n", name, day.toString(), exprText);
            DayRuleExpr dayRuleExpr = interpretor.parse(exprText);
            String formula = interpretor.calculateExpr(dayRuleExpr, day);
            System.out.printf("%s \nrslt %s test using %s\n", interpretor.toExpr(dayRuleExpr), formula, exprEngine.evalBooleanFormula(formula, exprText));
        } catch (Exception ex) {
            System.out.printf("Error: \n" + exprText + "\n" + ex.getMessage() + "\n");

        }
    }

    static class CalRule {
        String name;
        List<Rule> rules = new ArrayList<>();

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Rule> getRules() {
            return rules;
        }

        public void setRules(List<Rule> rules) {
            this.rules = rules;
        }
    }


    static class Rule {
        String name;
        String expr;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getExpr() {
            return expr;
        }

        public void setExpr(String expr) {
            this.expr = expr;
        }
    }
}

