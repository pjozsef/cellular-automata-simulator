/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.elte.inf.people.pojsaai.evaluator.common.token;

import hu.elte.inf.people.pojsaai.evaluator.exception.EvaluationException;
import static jdk.nashorn.internal.runtime.JSType.isNumber;

/**
 * A factory class, creating instances of classes that implement the Token interface.
 * @author József Pollák
 */
public class TokenFactory{

    /**
     * Creates Token instances based on the parameter string.
     * @param s input String
     * @return the corresponding Token instance
     * @throws EvaluationException if the parameter doesn't map to any known token
     */
    public static Token create(String s){
        switch(s){
            case "(":
                return LeftParenthesisToken.getInstance();
            case ")":
                return RightParenthesisToken.getInstance();
            case "+":
                return new NumFunctionToken(s, TokenFactory::plus, 20);
            case "-":
                return new NumFunctionToken(s, TokenFactory::minus, 20);
            case "*":
                return new NumFunctionToken(s, TokenFactory::mul, 40);
            case "/":
                return new NumFunctionToken(s, TokenFactory::div, 40);
            case "%":
                return new NumFunctionToken(s, TokenFactory::mod, 40);
            case "==":
                return new RelationToken(s, TokenFactory::equal, 40);
            case "!=":
                return new RelationToken(s, TokenFactory::nequal, 40);
            case "<":
                return new RelationToken(s, TokenFactory::lesser, 0);
            case ">":
                return new RelationToken(s, TokenFactory::greater, 0);
            case "<=":
                return new RelationToken(s, TokenFactory::lesserEqual, 0);
            case ">=":
                return new RelationToken(s, TokenFactory::greaterEqual, 0);
            default:
                if(isNumber(s)){
                    return new NumberToken(Double.parseDouble(s));
                }else{
                    throw new EvaluationException("\""+s+"\" cannot be parsed into token!");
                }
        }
    }

    private TokenFactory(){
        throw new IllegalStateException();
    }
    
    private static Double plus(Double a, Double b){
        return a + b;
    }

    private static Double minus(Double a, Double b){
        return a - b;
    }

    private static Double mul(Double a, Double b){
        return a * b;
    }

    private static Double div(Double a, Double b){
        if(b==0){
            throw new EvaluationException("Division by zero in: "+a+"/"+b);
        }
        return a / b;
    }

    private static Double mod(Double a, Double b){
        return a % b;
    }

    private static Boolean equal(Double a, Double b){
        return a == b.doubleValue();
    }
    
    private static Boolean nequal(Double a, Double b){
        return a != b.doubleValue();
    }
    
    private static Boolean lesser(Double a, Double b){
        return a < b;
    }
    
    private static Boolean greater(Double a, Double b){
        return a > b;
    }
    
    private static Boolean lesserEqual(Double a, Double b){
        return a <= b;
    }
    
    private static Boolean greaterEqual(Double a, Double b){
        return a >= b;
    }
}
