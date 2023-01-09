package lox;

import lox.Expr.Binary;
import lox.Expr.Grouping;
import lox.Expr.Literal;
import lox.Expr.Unary;
import static lox.TokenType.*;

public class Interpreter implements Expr.Visitor<Object> 
{

    @Override
    public Object visitBinaryExpr(Binary expr) {
        var left = evaluate(expr.left);
        var right = evaluate(expr.right);
        switch (expr.operator.type)
        {
            case MINUS:
                return (double)left - (double)right;
            case SLASH:
                return (double)left / (double)right;
            case STAR:
                return (double)left * (double)right;
            case PLUS:
                if (left instanceof Double && right instanceof Double)
                {
                    return (double)left + (double)right;
                }
                else if (left instanceof String && right instanceof String)
                {
                    return (String)left + (String)right;
                }
                else if (left instanceof String && right instanceof Double)
                {
                    return (String)left + (Double)right;
                }
                break;
            case GREATER:
                return (double)left > (double)right;
            case GREATER_EQUAL:
                return (double)left >= (double)right;
            case LESS:
                return (double)left < (double)right;
            case LESS_EQUAL:
                return (double)left <= (double)right;
            case BANG_EQUAL:
                return !isEqual(left, right);
            case EQUAL_EQUAL:
                return isEqual(left, right);
        }
        return null;
    }

    @Override
    public Object visitGroupingExpr(Grouping expr) {
        return evaluate(expr.expression);
    }

    @Override
    public Object visitLiteralExpr(Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitUnaryExpr(Unary expr) {
        var right = evaluate(expr.right);
        switch (expr.operator.type)
        {
            case BANG:
                return !isTruthy(right);
            case MINUS:
                checkNumberOperand(expr.operator, right);
                return -(double)right;
        }
        return null;
    }

    private void checkNumberOperand(Token operator, Object operand)
    {
        if (operand instanceof Double) return;
        throw new RuntimeError(operator, "Operand must be a number.");
    }
    private boolean isTruthy(Object obj)
    {
        if (obj == null) return false;
        if (obj instanceof Boolean) return (boolean)obj;
        return true;
    }

    private boolean isEqual(Object a, Object b)
    {
        if (a == null && b == null) return true;
        if (a == null) return false;
        return a.equals(b);
    }

    private Object evaluate(Expr expr)
    {
        return expr.accept(this);
    }
    
}
