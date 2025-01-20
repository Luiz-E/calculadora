package org.example;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class CalculatorTest {

    Calculator calculator;

    @BeforeEach
    void setup() {
        calculator = new Calculator();
    }

    @Test
    @DisplayName("Should throw exception for operator in invalid position.")
    void invalidExpression() {
        Throwable exception = Assertions.assertThrows(NumberFormatException.class, () -> calculator.calculate("4**5"));
        Assertions.assertEquals("* não é um número válido.", exception.getMessage());
    }

    @Test
    @DisplayName("Throws exception for null equation")
    void nullEquation() {
        Throwable exception = Assertions.assertThrows(IllegalArgumentException.class, () -> calculator.calculate(null));
        Assertions.assertEquals("A equação não pode ser nula ou vazia.", exception.getMessage());
    }

    @Test
    @DisplayName("Throws exception for empty equation")
    void emptyEquation() {
        Throwable exception = Assertions.assertThrows(IllegalArgumentException.class, () -> calculator.calculate(""));
        Assertions.assertEquals("A equação não pode ser nula ou vazia.", exception.getMessage());
    }

    @Test
    @DisplayName("Throws exception for division by 0")
    void divideByZero() {
        Throwable exception = Assertions.assertThrows(ArithmeticException.class, () -> calculator.calculate("4*4+12/0"));
        Assertions.assertEquals("Divisão por zero não é permitida.", exception.getMessage());
    }

    public static Stream<Arguments> invalidEquations() {
        return Stream.of(
                Arguments.of("4/t"),
                Arguments.of("a*4"),
                Arguments.of("4+a32"),
                Arguments.of("7*%"),
                Arguments.of("&$*2"),
                Arguments.of("4+&*14")
        );
    }
    @ParameterizedTest
    @MethodSource("invalidEquations")
    @DisplayName("Should throw for invalid characters in the equation")
     void testInvalidCharacters(String equation) {
        Throwable exception = Assertions.assertThrows(IllegalArgumentException.class, () -> calculator.calculate(equation));
        Assertions.assertEquals("A equação não deve conter letras ou caracteres especiais.", exception.getMessage());
    }

    public static Stream<Arguments> invalidStartingCharacters() {
        return Stream.of(
                Arguments.of("*4/2"),
                Arguments.of("/4+5")
        );
    }
    @ParameterizedTest
    @MethodSource("invalidStartingCharacters")
    @DisplayName("Should thrown exception for equations that starts with * or /")
    void testStartingCharacters(String equation) {
        Throwable exception = Assertions.assertThrows(IllegalArgumentException.class, () -> calculator.calculate(equation));
        Assertions.assertEquals("A equação não pode iniciar com os operadores * ou /.", exception.getMessage());
    }

    public static Stream<Arguments> invalidEndingCharacters() {
        return Stream.of(
                Arguments.of("5+9*"),
                Arguments.of("2+5/"),
                Arguments.of("4+2+"),
                Arguments.of("4+2-")
        );
    }
    @ParameterizedTest
    @MethodSource("invalidEndingCharacters")
    @DisplayName("Should thrown exception for equations that ends with +, -, * or /")
    void testEndingCharacters(String equation) {
        Throwable exception = Assertions.assertThrows(IllegalArgumentException.class, () -> calculator.calculate(equation));
        Assertions.assertEquals("A equação não pode terminar com um operador matemático.", exception.getMessage());
    }

    public static Stream<Arguments> invalidParentheses() {
        return Stream.of(
                Arguments.of("((4+21*2)"),
                Arguments.of(")2+1"),
                Arguments.of("4+1)"),
                Arguments.of("4+)+2*1")
        );
    }
    @ParameterizedTest
    @MethodSource("invalidParentheses")
    @DisplayName("Should thrown exception for equations with incomplete parentheses")
    void testInvalidParentheses(String equation) {
        Throwable exception = Assertions.assertThrows(IllegalArgumentException.class, () -> calculator.calculate(equation));
        Assertions.assertEquals("Os parênteses na equação devem estar equilibrados.", exception.getMessage());
    }

    public static Stream<Arguments> multiplicationTests() {
        return Stream.of(
                Arguments.of("2*5*10*9", "900"),
                Arguments.of("3*-5", "-15"),
                Arguments.of("-3*(-5)", "15"),
                Arguments.of("23*14*1000*1240", "399280000"),
                Arguments.of("2147483647*5", "10737418235"),
                Arguments.of("-2147483647*11", "-23622320117")
        );
    }
    @ParameterizedTest
    @MethodSource("multiplicationTests")
    @DisplayName("Multiplication tests")
    void testMultiplication(String equation, String expected) {
        Assertions.assertEquals(expected, calculator.calculate(equation));
    }

    public static Stream<Arguments> divisionTests() {
        return Stream.of(
                Arguments.of("140/2/7/5", "2"),
                Arguments.of("60/-3", "-20"),
                Arguments.of("-60/3", "-20"),
                Arguments.of("1000000000/2", "500000000"),
                Arguments.of("-1000/-5/-2", "-100"),
                Arguments.of("2147483647/3", "715827882")
        );
    }
    @ParameterizedTest
    @MethodSource("divisionTests")
    @DisplayName("Division tests")
    void testDivision(String equation, String expected) {
        Assertions.assertEquals(expected, calculator.calculate(equation));
    }

    @Test
    @DisplayName("Basic addition test")
    void basicAdditionTest() {
        Assertions.assertEquals("55", calculator.calculate("17+6+18+14"));
    }

    @Test
    @DisplayName("Addition with bigger numbers")
    void additionEdgeCase() {
        Assertions.assertEquals("8589934588", calculator.calculate("2147483647 + 2147483647+2147483647 + 2147483647"));
    }

    @Test
    @DisplayName("Basic subtraction")
    void basicSubtractionTest() {
        Assertions.assertEquals("-3", calculator.calculate("120-14-20-89"));
    }

    @Test
    @DisplayName("Subtraction with bigger numbers")
    void subtractionEdgeCase() {
        Assertions.assertEquals("-2147483648", calculator.calculate("2147483648-2147483648-2147483648"));
    }

    public static Stream<Arguments> equations() {
        return Stream.of(
                Arguments.of("4+21-0*2", "25"),
                Arguments.of("4*3/6+5", "7"),
                Arguments.of("(4+24)*5/7", "20"),
                Arguments.of("-5*3+63/(5+2)", "-6"),
                Arguments.of("-9+1", "-8"),
                Arguments.of("65-17", "48"),
                Arguments.of("(4*2)+4", "12"),
                Arguments.of("12+-6+13+-14", "5"),
                Arguments.of("-80/-2+3", "43"),
                Arguments.of("-80/(-2+3)", "-80"),
                Arguments.of("(((15+3)*40)+(10*5))*(2+(9*(6+4)))", "70840"),
                Arguments.of("2+3*(4-3)", "5")
        );
    }
    @ParameterizedTest
    @MethodSource(value = "equations")
    @DisplayName("A more diverse set of tests")
    void testWithStringParameter(String equation, String expected) {
        Assertions.assertEquals(expected, calculator.calculate(equation));
    }

}


/*
* @Test
    void desempenho() {
        long start = System.currentTimeMillis();
        for (int i = 0; i < 1_000_000_000; i++) {
            calculator.testStream("-5*3+63/(5+2)");
        }
        System.out.println("Stream: " + (System.currentTimeMillis() - start));

        start = System.currentTimeMillis();

        for (int i = 0; i < 1_000_000_000; i++) {
            calculator.testFor("-5*3+63/(5+2)");
        }
        System.out.println("For: " + (System.currentTimeMillis() - start));
    }*/
