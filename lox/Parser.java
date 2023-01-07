package lox;

import static lox.TokenType.*;

import java.lang.reflect.AnnotatedTypeVariable;
import java.util.List;

import javax.management.modelmbean.ModelMBeanInfoSupport;
import javax.print.event.PrintEvent;
import javax.swing.plaf.basic.BasicLookAndFeel;

import org.w3c.dom.ls.LSResourceResolver;

public class Parser {
    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens)
    {
        this.tokens = tokens;
    }

    private Expr expression()
    {
        return equality();
    }

    private Expr equality()
    {
        Expr expr = comparison();
        while (match(BANG_EQUAL, EQUAL_EQUAL)) 
        {
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr comparison()
    {
        var expr = term();
        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL))
        {
            var operator = previous();
            var right = term();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr term()
    {
        var expr = factor();
        while (match(MINUS, PLUS)) 
        {
            var operator = previous();
            var right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr factor()
    {
        var expr = unary();
        while (match(SLASH, STAR)) 
        {
            var operator = previous();
            var right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr unary()
    {
        if (match(BANG, MINUS))
        {
            var operator = previous();
            var right = unary();
            return new Expr.Unary(operator, right);
        }
        return primary();
    }

    private Expr primary()
    {
        if (match(FALSE)) return new Expr.Literal(false);
        if (match(TRUE)) return new Expr.Literal(true);
        if (match(NIL)) return new Expr.Literal(null);
        if (match(NUMBER, STRING)) 
        {
            return new Expr.Literal(previous().literal);
        }
        if (match(LEFT_PAREN))
        {
            var expr = expression();
            consume(RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }
    }

    private boolean match(TokenType... types)
    {
        for (var type: types)
        {
            if (check(type))
            {
                advance();
                return true;
            }
        }
        return false;
    }

    private boolean check(TokenType type)
    {

        if (isAtEnd()) return false;
        return peek().type == type;
    }

    private Token advance()
    {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd()
    {
        return peek().type == EOF;
    }

    private Token peek()
    {
        return tokens.get(current);
    }

    private Token previous()
    {
        return tokens.get(current -1);
    }

    private Token consume(TokenType type, String message)
    {
        if (check(type)) return advance();
        throw error(peek(), message);
    }

    private ParseError error(Token token, String message)
    {
        Lox.error(token, message);
        return new ParseError();
    }
}
