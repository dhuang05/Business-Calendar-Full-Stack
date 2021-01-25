/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.service.buscalendar.expr.exprif;

import com.nusino.microservices.exception.HandlableException;
import com.nusino.microservices.service.buscalendar.expr.model.Expr;
import com.nusino.microservices.service.buscalendar.expr.model.Pair;

import java.time.LocalDate;
import java.util.regex.Pattern;

public interface ExprInterpretor<T extends Expr> {
    String SPACE = " ";
    Pattern S_REGEX = Pattern.compile("\\s+");
    Pattern Y_NUM_REGEX = Pattern.compile("([Y|y]{1,}[0-9]{1,})");
    Pattern M_NUM_REGEX = Pattern.compile("([M|m]{1,}[0-9]{1,})");
    Pattern D_NUM_REGEX = Pattern.compile("([D|d]{1,}[0-9]{1,})");
    Pattern E_NUM_REGEX = Pattern.compile("([E|e]{1,}[0-9]{1,})");
    Pattern W_NUM_REGEX = Pattern.compile("([W|w]{1,}[0-9]{1,})");
    //
    Pattern Y_REGEX = Pattern.compile("([Y|y]{1,})");
    Pattern M_0_REGEX = Pattern.compile("([M|m]{1,}[0-9]{0,2})");
    //
    Pattern Y_NORMALIZED_REGEX = Pattern.compile("[Y|y]{1,}");
    Pattern M_NORMALIZED_REGEX = Pattern.compile("[M|m]{1,}");
    Pattern D_NORMALIZED_REGEX = Pattern.compile("[D|d]{1,}");
    Pattern E_NORMALIZED_REGEX = Pattern.compile("[E|e]{1,}");
    Pattern W_NORMALIZED_REGEX = Pattern.compile("[W|w]{1,}");

    boolean isMyExpr(String exprssionText);

    T parse(String exprssionText);

    String parseParams(T expr, String expressions);

    String toExpr(T expr);

    String calculateExpr(T expr, LocalDate date);

    void validate(T expr) throws HandlableException;

    //
    default String toExprString(Expr expr) {
        return toExpr((T) expr);
    }

    default String calculateExprString(Expr expr, LocalDate date) {
        return calculateExpr((T) expr, date);
    }
}
