package org.example;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Calculator {

    public String calculate(String equation) {
        checkEquation(equation);
        if (equation.contains("(")) {
            equation =
                    equation.substring(0, equation.indexOf('(')) +
                    calculate(equation.substring(equation.indexOf('(') + 1, equation.indexOf(')'))) +
                    equation.substring(equation.indexOf(')') + 1);
        }
        String[] expressionTokens = equation.splitWithDelimiters("[+\\-*/]", equation.length());
        List<String> listTokens  = new ArrayList<>(Arrays.asList(expressionTokens));
        while (listTokens.contains("")) {
            listTokens.remove("");
        }
        if (listTokens.getFirst().equals("-")) {
            listTokens.removeFirst();
            listTokens.set(0, "-" + listTokens.getFirst());
        }
        while (listTokens.size() > 1) {
            int indexMultiplication = listTokens.indexOf("*");
            int indexDivision = listTokens.indexOf("/");
            int indexSum = listTokens.indexOf("+");
            int indexSubtraction = listTokens.indexOf("-");

            if (indexMultiplication != -1 || indexDivision != -1) {
                if (indexMultiplication == -1) {
                    divide(listTokens, indexDivision);
                } else if (indexDivision == -1) {
                    multiply(listTokens, indexMultiplication);
                } else {
                    if (indexMultiplication > indexDivision) {
                        divide(listTokens, indexDivision);
                    } else {
                        multiply(listTokens, indexMultiplication);
                    }
                }
            } else if (indexSum != -1 || indexSubtraction != -1) {
                if (indexSum == -1) {
                    subtract(listTokens, indexSubtraction);
                } else if (indexSubtraction == -1) {
                    add(listTokens, indexSum);
                } else {
                    if (indexSum > indexSubtraction) {
                        subtract(listTokens, indexSubtraction);
                    } else {
                        add(listTokens, indexSum);
                    }
                }
            }
        }
        return listTokens.getFirst();
    }

    private void checkEquation(String equation) {
        if (equation == null || equation.isEmpty()) {
            throw new IllegalArgumentException("A equação não pode ser nula ou vazia.");
        }
        if ((equation.contains("(") && !equation.contains(")")) ||
                (equation.contains(")") && !equation.contains("("))) {
            throw new IllegalArgumentException("Todos os parênteses precisam ser fechados na equação.");
        }
    }

    private void divide(List<String> listTokens, int operationIndex) {
        if (numberSignIsPresent(listTokens, operationIndex)) {
            addSign(listTokens, operationIndex);
        }
        BigDecimal dividing = new BigDecimal(listTokens.get(operationIndex-1));
        BigDecimal divisor = new BigDecimal(listTokens.get(operationIndex+1));
        if (divisor.equals(BigDecimal.ZERO)) {
            throw new IllegalArgumentException("Divisão por zero não é permitida.");
        }
        listTokens.set(operationIndex, dividing.divide(divisor, RoundingMode.DOWN).toString());
        listTokens.remove(operationIndex+1);
        listTokens.remove(operationIndex-1);
    }

    private void multiply(List<String> listTokens, int operationIndex) {
        if (numberSignIsPresent(listTokens, operationIndex)) {
            addSign(listTokens, operationIndex);
        }
        BigDecimal factor1 = new BigDecimal(listTokens.get(operationIndex-1));
        BigDecimal factor2 = new BigDecimal(listTokens.get(operationIndex+1));
        listTokens.set(operationIndex, factor1.multiply(factor2).toString());
        listTokens.remove(operationIndex+1);
        listTokens.remove(operationIndex-1);
    }



    private void add(List<String> listTokens, int operationIndex) {
        if (numberSignIsPresent(listTokens, operationIndex)) {
            addSign(listTokens, operationIndex);
        }
        BigDecimal number1 = new BigDecimal(listTokens.get(operationIndex-1));
        BigDecimal number2 = new BigDecimal(listTokens.get(operationIndex+1));
        listTokens.set(operationIndex, number1.add(number2).toString());
        listTokens.remove(operationIndex+1);
        listTokens.remove(operationIndex-1);
    }

    private void subtract(List<String> listTokens, int operationIndex) {
        if (numberSignIsPresent(listTokens, operationIndex)) {
            addSign(listTokens, operationIndex);
        }
        BigDecimal number1 = new BigDecimal(listTokens.get(operationIndex-1));
        BigDecimal number2 = new BigDecimal(listTokens.get(operationIndex+1));
        listTokens.set(operationIndex, number1.subtract(number2).toString());
        listTokens.remove(operationIndex+1);
        listTokens.remove(operationIndex-1);
    }

    private boolean numberSignIsPresent(List<String> listTokens, int operationIndex) {
        return !listTokens.get(operationIndex+1).matches("\\d+");
    }

    private void addSign(List<String> listTokens, int operationIndex) {
        String sign = listTokens.get(operationIndex+1);
        String operand = listTokens.get(operationIndex+2);
        listTokens.set(operationIndex+1, sign + operand);
        listTokens.remove(operationIndex+2);
    }
}
