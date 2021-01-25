/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.service.buscalendar.translator.english;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LanguageExprTranslatorTest {

    @Test
    public void testDateEprTranslator() {
        DateExprTranslator translator = new DateExprTranslator();
        String text = "Sept 24th Monday";
        Assertions.assertEquals(true, translator.isLanguageMyElement(text));
        String result = translator.languageToFormula(text);
        Assertions.assertEquals("M9D24E1", result);
        //
        text = "2021 Sept 24th Mon";
        Assertions.assertEquals(true, translator.isLanguageMyElement(text));
        result = translator.languageToFormula(text);
        Assertions.assertEquals("Y2021M9D24E1", result);

        text = "Sept 2021  24th SUNDAY";
        Assertions.assertEquals(true, translator.isLanguageMyElement(text));
        result = translator.languageToFormula(text);
        Assertions.assertEquals("M9Y2021D24E7", result);

        text = "September/24th, 2021 SUN";
        Assertions.assertEquals(true, translator.isLanguageMyElement(text));
        result = translator.languageToFormula(text);
        Assertions.assertEquals("M9D24Y2021E7", result);
        //
        text = "Sept/24, 2021 SUNDAY";
        Assertions.assertEquals(true, translator.isLanguageMyElement(text));
        result = translator.languageToFormula(text);
        Assertions.assertEquals("M9D24Y2021E7", result);
    }

    @Test
    public void testLastDayOfMonthTranslator() {
        LastDayOfXMonthExprTranslator translator = new LastDayOfXMonthExprTranslator();
        String text = "The last day in every month";
        Assertions.assertEquals(true, translator.isLanguageMyElement(text));
        String result = translator.languageToFormula(text);
        Assertions.assertEquals("LDM", result);
        //
        text = "last day in every month";
        Assertions.assertEquals(true, translator.isLanguageMyElement(text));
        result = translator.languageToFormula(text);
        Assertions.assertEquals("LDM", result);
        //
        text = "last day in the month";
        Assertions.assertEquals(true, translator.isLanguageMyElement(text));
        result = translator.languageToFormula(text);
        Assertions.assertEquals("LDM", result);
        //
        text = "The last day of all months";
        Assertions.assertEquals(true, translator.isLanguageMyElement(text));
        result = translator.languageToFormula(text);
        Assertions.assertEquals("LDM", result);

        //
        text = "The last day of March";
        Assertions.assertEquals(true, translator.isLanguageMyElement(text));
        result = translator.languageToFormula(text);
        Assertions.assertEquals("LDM3", result);

        //
        text = "The last day of Septe";
        Assertions.assertEquals(true, translator.isLanguageMyElement(text));
        result = translator.languageToFormula(text);
        Assertions.assertEquals("LDM9", result);
    }

    @Test
    public void testXDayOfWeekXInMonthXExprTranslator() {
        XDayOfWeekXInMonthXExprTranslator translator = new XDayOfWeekXInMonthXExprTranslator();
        String text = "The 2nd Saturday in every month";
        Assertions.assertEquals(true, translator.isLanguageMyElement(text));
        String result = translator.languageToFormula(text);
        Assertions.assertEquals("W2E6M", result);
        //
        text = "2nd Saturday of every month";
        Assertions.assertEquals(true, translator.isLanguageMyElement(text));
        result = translator.languageToFormula(text);
        Assertions.assertEquals("W2E6M", result);
        //
        text = "2nd Saturday in month";
        Assertions.assertEquals(true, translator.isLanguageMyElement(text));
        result = translator.languageToFormula(text);
        Assertions.assertEquals("W2E6M", result);
        //
        text = "2nd Saturday at each month";
        Assertions.assertEquals(true, translator.isLanguageMyElement(text));
        result = translator.languageToFormula(text);
        Assertions.assertEquals("W2E6M", result);
        //
        text = "2nd Saturday in the month";
        Assertions.assertEquals(true, translator.isLanguageMyElement(text));
        result = translator.languageToFormula(text);
        Assertions.assertEquals("W2E6M", result);
        //
        text = "2nd Saturday in all months";
        Assertions.assertEquals(true, translator.isLanguageMyElement(text));
        result = translator.languageToFormula(text);
        Assertions.assertEquals("W2E6M", result);
        //
        text = "2nd Sat of all months";
        Assertions.assertEquals(true, translator.isLanguageMyElement(text));
        result = translator.languageToFormula(text);
        Assertions.assertEquals("W2E6M", result);
        //
        text = "The 3rd Wednesday at February";
        Assertions.assertEquals(true, translator.isLanguageMyElement(text));
        result = translator.languageToFormula(text);
        Assertions.assertEquals("W3E3M2", result);
        //
        text = "The 3rd Wednesday in the October";
        Assertions.assertEquals(true, translator.isLanguageMyElement(text));
        result = translator.languageToFormula(text);
        Assertions.assertEquals("W3E3M10", result);

        text = "3rd Wed at Feb";
        Assertions.assertEquals(true, translator.isLanguageMyElement(text));
        result = translator.languageToFormula(text);
        Assertions.assertEquals("W3E3M2", result);
    }


    @Test
    public void testLastXDayOfWeekXInMonthXExprTranslator() {
        LastXDayOfWeekXInMonthXExprTranslator translator = new LastXDayOfWeekXInMonthXExprTranslator();
        String text = "The    last     2nd   Saturday in every month";
        Assertions.assertEquals(true, translator.isLanguageMyElement(text));
        String result = translator.languageToFormula(text);
        Assertions.assertEquals("LW2E6M", result);
        //
        text = "Last 2nd Saturday of every month";
        Assertions.assertEquals(true, translator.isLanguageMyElement(text));
        result = translator.languageToFormula(text);
        Assertions.assertEquals("LW2E6M", result);
        //
        text = "2nd Last Saturday of every month";
        Assertions.assertEquals(true, translator.isLanguageMyElement(text));
        result = translator.languageToFormula(text);
        Assertions.assertEquals("LW2E6M", result);
        //
        text = "Last Saturday of every month";
        Assertions.assertEquals(true, translator.isLanguageMyElement(text));
        result = translator.languageToFormula(text);
        Assertions.assertEquals("LW1E6M", result);
        //

        text = "Last 2nd Saturday in month";
        Assertions.assertEquals(true, translator.isLanguageMyElement(text));
        result = translator.languageToFormula(text);
        Assertions.assertEquals("LW2E6M", result);
        //
        text = " 2nd  Last Saturday in month";
        Assertions.assertEquals(true, translator.isLanguageMyElement(text));
        result = translator.languageToFormula(text);
        Assertions.assertEquals("LW2E6M", result);
        //
        text = "Last 2nd Saturday at each month";
        Assertions.assertEquals(true, translator.isLanguageMyElement(text));
        result = translator.languageToFormula(text);
        Assertions.assertEquals("LW2E6M", result);
        //
        text = "Last 2nd Saturday in the month";
        Assertions.assertEquals(true, translator.isLanguageMyElement(text));
        result = translator.languageToFormula(text);
        Assertions.assertEquals("LW2E6M", result);
        //
        text = "2nd Last Saturday in all months";
        Assertions.assertEquals(true, translator.isLanguageMyElement(text));
        result = translator.languageToFormula(text);
        Assertions.assertEquals("LW2E6M", result);
        //
        text = "last 2nd Sat of all months";
        Assertions.assertEquals(true, translator.isLanguageMyElement(text));
        result = translator.languageToFormula(text);
        Assertions.assertEquals("LW2E6M", result);
        //
        text = "The Last 3rd Wednesday at February";
        Assertions.assertEquals(true, translator.isLanguageMyElement(text));
        result = translator.languageToFormula(text);
        Assertions.assertEquals("LW3E3M2", result);
        //
        text = "The Last  Wednesday at February";
        Assertions.assertEquals(true, translator.isLanguageMyElement(text));
        result = translator.languageToFormula(text);
        Assertions.assertEquals("LW1E3M2", result);
        //
        text = "The 3rd Last Wednesday in the October";
        Assertions.assertEquals(true, translator.isLanguageMyElement(text));
        result = translator.languageToFormula(text);
        Assertions.assertEquals("LW3E3M10", result);

        text = "Last 3rd Wed at Feb";
        Assertions.assertEquals(true, translator.isLanguageMyElement(text));
        result = translator.languageToFormula(text);
        Assertions.assertEquals("LW3E3M2", result);
    }


    @Test
    public void testXDayOfYearExprTranslator() {
        XDayOfWeekXInYearExprTranslator translator = new XDayOfWeekXInYearExprTranslator();
        String text = "The 2nd Saturday in every Year";
        Assertions.assertEquals(true, translator.isLanguageMyElement(text));
        String result = translator.languageToFormula(text);
        Assertions.assertEquals("W2E6Y", result);
        //
        text = "2nd Saturday of every Year";
        Assertions.assertEquals(true, translator.isLanguageMyElement(text));
        result = translator.languageToFormula(text);
        Assertions.assertEquals("W2E6Y", result);
        //
        text = "2nd Saturday in YEAR";
        Assertions.assertEquals(true, translator.isLanguageMyElement(text));
        result = translator.languageToFormula(text);
        Assertions.assertEquals("W2E6Y", result);
        //
        text = "2nd Saturday at each Year";
        Assertions.assertEquals(true, translator.isLanguageMyElement(text));
        result = translator.languageToFormula(text);
        Assertions.assertEquals("W2E6Y", result);
        //
        text = "2nd Saturday in the Year";
        Assertions.assertEquals(true, translator.isLanguageMyElement(text));
        result = translator.languageToFormula(text);
        Assertions.assertEquals("W2E6Y", result);
        //
        text = "2nd Saturday in all years";
        Assertions.assertEquals(true, translator.isLanguageMyElement(text));
        result = translator.languageToFormula(text);
        Assertions.assertEquals("W2E6Y", result);
        //
        text = "2nd Sat of all  Years";
        Assertions.assertEquals(true, translator.isLanguageMyElement(text));
        result = translator.languageToFormula(text);
        Assertions.assertEquals("W2E6Y", result);
    }

    @Test
    public void testYmdValueExprTranslator() {
        YmdValueExprTranslator translator = new YmdValueExprTranslator();
        String text = "From  Febr 12nd to March/8;";
        Assertions.assertEquals(true, translator.isLanguageMyElement(text));
        String result = translator.languageToFormula(text);
        Assertions.assertEquals("2-12,3-8;", result);
        //
        text = "From Febr 12nd to  Sept 5;";
        Assertions.assertEquals(true, translator.isLanguageMyElement(text));
        result = translator.languageToFormula(text);
        Assertions.assertEquals("2-12,9-5;", result);

        //
        text = "Between Febr/13rd and APril 11;";
        Assertions.assertEquals(true, translator.isLanguageMyElement(text));
        result = translator.languageToFormula(text);
        Assertions.assertEquals("2-13,4-11;", result);

        text = "In the range of Febr 13rd and APril 11;";
        Assertions.assertEquals(true, translator.isLanguageMyElement(text));
        result = translator.languageToFormula(text);
        Assertions.assertEquals("2-13,4-11;", result);

        text = "In the range  Febr 13rd and ApRil 11;";
        Assertions.assertEquals(true, translator.isLanguageMyElement(text));
        result = translator.languageToFormula(text);
        Assertions.assertEquals("2-13,4-11;", result);


        text = "In range of Febr 13rd and June 11;";
        Assertions.assertEquals(true, translator.isLanguageMyElement(text));
        result = translator.languageToFormula(text);
        Assertions.assertEquals("2-13,6-11;", result);

        text = "range of OCT/20 and JUly/11;";
        Assertions.assertEquals(true, translator.isLanguageMyElement(text));
        result = translator.languageToFormula(text);
        Assertions.assertEquals("10-20,7-11;", result);
    }

    @Test
    public void testJustFrom() {
        YmdValueExprTranslator translator = new YmdValueExprTranslator();
        String text = "From Febr 12;";
        text = text.trim().toUpperCase();
        Boolean yes = YmdValueExprTranslator.JUST_FROM.matcher(text).find();
        System.out.println("------- " + yes);
    }
}
