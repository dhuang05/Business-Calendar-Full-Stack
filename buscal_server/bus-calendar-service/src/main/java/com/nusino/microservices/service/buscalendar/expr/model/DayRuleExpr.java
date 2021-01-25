/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.service.buscalendar.expr.model;

import com.nusino.microservices.service.buscalendar.expr.model.standard.YmdValueExpr;

import java.util.ArrayList;
import java.util.List;


public class DayRuleExpr implements Expr {
    private String ruleId;
    private String expr;
    private YmdValueExpr from;
    private YmdValueExpr to;

    private List<Expr> exprs = new ArrayList<>();

    @Override
    public DayRuleExpr clone() {
        DayRuleExpr cloned = new DayRuleExpr();
        cloned.setFrom(from.clone());
        cloned.setTo(to.clone());
        cloned.setExpr(expr);
        for (Expr expr : exprs) {
            cloned.getExprs().add(expr.clone());
        }
        cloned.setRuleId(ruleId);
        return cloned;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getExpr() {
        return expr;
    }

    public void setExpr(String expr) {
        this.expr = expr;
    }

    public YmdValueExpr getFrom() {
        return from;
    }

    public void setFrom(YmdValueExpr from) {
        this.from = from;
    }

    public YmdValueExpr getTo() {
        return to;
    }

    public void setTo(YmdValueExpr to) {
        this.to = to;
    }

    public List<Expr> getExprs() {
        return exprs;
    }

    public void setExprs(List<Expr> exprs) {
        this.exprs = exprs;
    }

}
