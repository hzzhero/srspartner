package com.he.srs.util.srsInterpreter;

public class NonterminalExpression extends Expression {

    /**
     * 每个非终结符表达式都会对其他表达式产生依赖
     *
     * @param expressions
     */
    public NonterminalExpression(Expression... expressions) {

    }

        @Override
    public String interpret(Context ctx) {
        return null;
    }
}
