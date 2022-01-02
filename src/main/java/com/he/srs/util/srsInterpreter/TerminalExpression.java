package com.he.srs.util.srsInterpreter;

import lombok.Data;

@Data
public class TerminalExpression extends Expression {

    private String pid;

    @Override
    public String interpret(Context ctx) {
        return null;
    }
}
