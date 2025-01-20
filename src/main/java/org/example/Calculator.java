package org.example;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Calculator {

    /**
     * Resolve a equação matemática fornecida, levando em consideração a ordem de operações.
     * <p>
     *     O método resolve expressões em parênteses de forma recursiva e, em seguida, calcula as operações de acordo com a prioridade de operadores.
     * </p>
     * @param equation A equação a ser solucionada, do tipo String, não podendo ser vazia ou nula.
     * @return O resultado da equação como uma String
     * @throws IllegalArgumentException caso a equação passada seja nula, vazia, possua parênteses incompletos ou possua um erro nas operações.
     * @throws ArithmeticException caso haja uma divisão por zero na equação
     * */
    public String calculate(String equation) throws ArithmeticException, IllegalArgumentException {
        checkEquation(equation);
        equation = equation.replaceAll(" ", "");
        while (equation.contains("(")) {
            int firstClosedParenthesisIndex = getFirstClosedParenthesisIndex(equation);
            String innerExpression = equation.substring(firstClosedParenthesisIndex + 1, equation.indexOf(')'));
            String solvedParentheses = calculate(innerExpression);
            equation =  equation.replace("("+innerExpression+")", solvedParentheses);
        }

        List<String> listTokens = getEquationTokens(equation);
        List<String> operations = getOperations(listTokens);

        for (String operation : operations) {
            calculateExpression(listTokens, listTokens.indexOf(operation));
        }
        return listTokens.get(0);
    }

    /**
     * Verifica se a equação passada está vazia, nula ou possui parênteses incompletos
     * @param equation A equação a ser avaliada
     * @throws IllegalArgumentException quando a equação passada é vazia, nula ou possui parênteses incompletos
     */
    private void checkEquation(String equation) throws IllegalArgumentException {
        if (equation == null || equation.isEmpty()) {
            throw new IllegalArgumentException("A equação não pode ser nula ou vazia.");
        }
        if (equation.matches("^[*/].*")) {
            throw new IllegalArgumentException("A equação não pode iniciar com os operadores * ou /.");
        }
        if (equation.matches(".*[*/+-]$")) {
            throw new IllegalArgumentException("A equação não pode terminar com um operador matemático.");
        }
        if(equation.matches(".*[^\\d+*/()\\s-].*")) {
            throw new IllegalArgumentException("A equação não deve conter letras ou caracteres especiais.");
        }
        if (equation.contains("()")) {
            throw new IllegalArgumentException("Não podem haver parênteses vazios na equação.");
        }
        Stack<Boolean> parentheses = new Stack<>();
        char[] digits = equation.toCharArray();
        for (char character : digits) {
            if (character == '(') {
                parentheses.push(true);
            } else if (character == ')') {
                if (parentheses.isEmpty()) {
                    throw new IllegalArgumentException("Os parênteses na equação devem estar equilibrados.");
                }
                parentheses.pop();
            }
        }
        if (!parentheses.isEmpty()) {
            throw new IllegalArgumentException("Os parênteses na equação devem estar equilibrados.");
        }
    }

    /**
     * Retorna a posição de início da expressão do primeiro parênteses completo na equação
     * @param equation A equação a ser analisada
     * @return A posição de início da expressão que inicia com parênteses
     */
    private int getFirstClosedParenthesisIndex(String equation) throws IllegalArgumentException{
        int firstClosedParenthesesIndex = equation.indexOf(')');
        char[] digits = equation.toCharArray();
        for (int i = firstClosedParenthesesIndex - 1; i >= 0; i--) {
            if (digits[i] == '(') {
                return i;
            }
        }
        throw new IllegalArgumentException("A equação possui um erro na organização dos parênteses.");
    }

    /**
     * Retorna uma List<String> onde cada posição armazena um número ou uma operação a ser realizada na equação
     * utilizando a expressão regular "((?<=^|[*\/+-])[-+])?\\d+|[+\\-*\/]" para identificar números e operadores na equação:
     * - ((?<=^|[*\/+-])[-+])?\\d+
     * Captura um número com um sinal de + ou - antes dele, desde que o sinal seja precedido pelo início da equação ou um sinal de operação.
     * - [+\\-*\/]
     * Captura um sinal de operação
     * @param equation A equação que será usada transformada em Lista
     * @return Uma List<String> com os tokens presentes na equação
     */
    private List<String> getEquationTokens(String equation) {
        String regex = "((?<=^|[*/+-])[-+])?\\d+|[+\\-*/]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(equation);
        List<String> listTokens = new LinkedList<>();
        while (matcher.find()) {
            listTokens.add(matcher.group());
        }
        return listTokens;
    }

    /**
     * Retorna uma lista com as operações organizadas por prioridade
     * @param listTokens A lista contendo os tokens
     * @return Uma lista contendo os operadores presentes na equação por ordem de prioridade de resolução
     */
    private List<String> getOperations(List<String> listTokens) {
        List<String> operationsIndex = new LinkedList<>();
        for (String token : listTokens) {
            if (!token.matches("[+\\-*/]")) {
                continue;
            }
            switch (token) {
                case "+":
                case "-":
                    operationsIndex.add(operationsIndex.size(), token);
                    break;
                case "/":
                case "*":
                    int lastPriority = Math.max(operationsIndex.lastIndexOf("*"), operationsIndex.lastIndexOf("/"));
                    if (lastPriority == -1) {
                        operationsIndex.add(0, token);
                    } else {
                        operationsIndex.add(lastPriority + 1, token);
                    }
            }
        }
        return operationsIndex;
    }

    /**
     * Calcula a operação entre os operandos à esquerda e à direita do operador na lista de tokens.
     * <p>
     *     O operador é substituído pelo resultado da operação e os operandos são removidos da lista.
     * </p>
     * <p>
     *     Divisões não exatas São arredondadas para baixo.
     * </p>
     * @param listTokens A lista contendo os números e as operações a serem realizadas
     * @param operationIndex O Index do operador a ser processado na lista
     * @throws ArithmeticException caso a operação a ser realizada seja uma divisão por zero
     * @throws IllegalArgumentException Se os operandos ao redor do operador sejam inválidos
     */
    private void calculateExpression(List<String> listTokens, int operationIndex) {
        BigDecimal term1;
        try {
            term1 = new BigDecimal(listTokens.get(operationIndex - 1));
        } catch (NumberFormatException exception) {
            throw new NumberFormatException(listTokens.get(operationIndex-1) + " não é um número válido.");
        }

        BigDecimal term2;
        try {
            term2 = new BigDecimal(listTokens.get(operationIndex + 1));
        } catch (NumberFormatException exception) {
            throw new NumberFormatException(listTokens.get(operationIndex+1) + " não é um número válido.");
        }
        String operation = listTokens.get(operationIndex);
        switch (operation) {
            case "+":
                listTokens.set(operationIndex, term1.add(term2).toString());
                break;
            case "-":
                listTokens.set(operationIndex, term1.subtract(term2).toString());
                break;
            case "*":
                listTokens.set(operationIndex, term1.multiply(term2).toString());
                break;
            case "/":
                if (term2.equals(BigDecimal.ZERO)) {
                    throw new ArithmeticException("Divisão por zero não é permitida.");
                }
                listTokens.set(operationIndex, term1.divide(term2, RoundingMode.DOWN).toString());
                break;
            default:
                throw new IllegalArgumentException("Equação incorreta.");
        }
        listTokens.remove(operationIndex+1);
        listTokens.remove(operationIndex-1);
    }
}