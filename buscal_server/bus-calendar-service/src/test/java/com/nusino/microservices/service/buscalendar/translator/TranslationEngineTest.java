/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.service.buscalendar.translator;

import com.nusino.microservices.service.buscalendar.ExprEngine;
import com.nusino.microservices.service.buscalendar.expr.model.DayRuleExpr;
import com.nusino.microservices.service.buscalendar.translator.english.EnglishDayRuleTranslator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;


public class TranslationEngineTest {
    protected ExprEngine exprEngine = new ExprEngine(true);
    protected EnglishDayRuleTranslator englishDayRuleTranslator = new EnglishDayRuleTranslator();

    @Test
    public void testTranslatEngine() {
        long t0 = System.currentTimeMillis();
        String exprText = null;
        LocalDate date = null;

        exprText = "03-1,03-31; LW1E2M3";
        analyze(1, exprText);
        //
        exprText = "03-1,03-31; LW1E3M3";
        analyze(4, exprText);

        exprText = "03-1,03-31; LW2E3M3";
        analyze(6, exprText);

        exprText = "(M01d01 && !E6 && !E7) && true || (M01d01(1, 2) && E1)";
        analyze(10, exprText);

        exprText = "1-2,3-4; (M01d01 && !E6 && !E7) || (M01d01(1, 2) && E1)";
        analyze(20, exprText);

        exprText = "3-20,4-28; GoodFriday";
        analyze(30, exprText);

        exprText = "2-14,2-29; W3E1M";
        analyze(40, exprText);

        exprText = "5-18; MM05dd24(0,-1,-2,-3,-4,-5,-6) && E1";
        analyze(50, exprText);

        exprText = "M05dd01(-4,-5,-6) && E2";
        analyze(60, exprText);

        exprText = "7-1, 7-3; (mm07dd01 && !E6 && !E7) || (Mm07dd01(1, 2) && E1)";
        analyze(70, exprText);

        exprText = "8-1,8-7; W1E1M";
        analyze(80, exprText);

        exprText = "9-1,9-7; W1E1M(-12,-4)";
        analyze(90, exprText);

        exprText = "10-7,10-18; W2E1M10";
        analyze(100, exprText);

        exprText = "10-7,10-18; W2E1M10(10)";
        analyze(110, exprText);

        exprText = "10-7,10-18; GoodFriday(-1)";
        analyze(120, exprText);

        exprText = "11-11,11-11; m11d11";
        analyze(130, exprText);

        exprText = "W3E1Y";
        analyze(140, exprText);

        exprText = "W3E1Y (1,2)";
        analyze(150, exprText);

        exprText = "LDM4";
        analyze(160, exprText);

        exprText = "LDM4(4)";
        analyze(170, exprText);

        exprText = "12-25,12-28; (m12d25 && !e6 & !e7) | (m12d26 && !e6 && !e7) & GoodFriday(-1,-2,-4) & W2E1M10(-1,-2,-4) | M12D26E6(2) |  W3E1Y(-1,-2,-4) & m12d25e6(2) | m12d26e7(2) | m12d25e7(2,-4,-5,-6,-7,-8,-10)";
        analyze(180, exprText);

        exprText = "3-19,4-28; function goodFriday(Y) {\n" +
                "    var C = Math.floor(Y/100);\n" +
                "    var N = Y - 19*Math.floor(Y/19);\n" +
                "    var K = Math.floor((C - 17)/25);\n" +
                "    var I = C - Math.floor(C/4) - Math.floor((C - K)/3) + 19*N + 15;\n" +
                "    I = I - 30*Math.floor((I/30));\n" +
                "    I = I - Math.floor(I/28)*(1 - Math.floor(I/28)*Math.floor(29/(I + 1))*Math.floor((21 - N)/11));\n" +
                "    var J = Y + Math.floor(Y/4) + I + 2 - C + Math.floor(C/4);\n" +
                "    J = J - 7*Math.floor(J/7);\n" +
                "    var L = I - J;\n" +
                "    var M = 3 + Math.floor((L + 40)/44);\n" +
                "    var D = L + 28 - 31*Math.floor(M/4);\n" +
                "    D = D-2;\n" +
                "    if (D <= 0){\n" +
                "    D = D + 31;\n" +
                "    M = 3;\n" +
                "    }\n" +
                "    return Y + '-' + parseInt(M, 10) + '-' + parseInt(D, 10);\n" +
                "}(1,2,3)";
        analyze(400, exprText);
        long t1 = System.currentTimeMillis();
        System.out.printf("time spend %s sec \n", (t1 - t0) / 1000000000);
    }

    @Test
    public void testLanguageTranslatEngine() {
        String exprText = null;
        exprText = "From December 24th To December 24th;\n" +
                " December 24th and not Saturday not Sunday";

        exprText = "September/24th, 2021 SUN";

        analyzeLanguage(10, exprText);
    }

    private void analyze(int index, String exprText) {
        System.out.println("///" + index + "\nORIGINAL: " + exprText);
        DayRuleExpr dayRuleExpr = exprEngine.getDayRuleExprInterpretor().parse(exprText);
        String languageExr = englishDayRuleTranslator.exprToLanguage(dayRuleExpr);
        System.out.println("LANGUAGE: " + languageExr);
        String formula = englishDayRuleTranslator.languageToFormula(languageExr);
        if (formula == null || formula.trim().length() == 0) {
            throw new RuntimeException("Failure in  " + index);
        }
        System.out.println("  OUTPUT: " + formula);
    }


    private void analyzeLanguage(int index, String languageExr) {
        System.out.println("LANGUAGE: " + languageExr);
        String formula = englishDayRuleTranslator.languageToFormula(languageExr);
        if (formula == null || formula.trim().length() == 0) {
            throw new RuntimeException("Failure in  " + index);
        }
        System.out.println("  OUTPUT: " + formula);
    }
}
