/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.service.buscalendar.expr.model.standard.func;

import com.nusino.microservices.service.buscalendar.expr.model.FuncExpr;
import com.nusino.microservices.service.buscalendar.util.JsonUtil;

public class AddOnFuncExpr extends FuncExpr {

    @Override
    public AddOnFuncExpr clone() {
        return JsonUtil.clone(this);
    }
}
