/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.service.buscalendar.expr.stdexpr.func;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public interface AddOnExprHandler {

    public boolean isMyExpr(String expr);

    List<LocalDate> calculate(int year, String expr);

    List<String> exprDescs();
}
