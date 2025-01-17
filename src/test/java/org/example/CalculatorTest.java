package org.example;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

    public static Stream<Arguments> equations() {
        return Stream.of(
                Arguments.of("4+21-0*2", "25"),
                Arguments.of("4*3/6+5", "7"),
                Arguments.of("(4+24)*5/7", "20"),
                Arguments.of("-5*3+63/(5+2)", "-6")
        );
    }

    @ParameterizedTest
    @MethodSource(value = "equations")
    void testWithStringParameter(String equation, String expected) {
        Assertions.assertEquals(expected, calculator.calculate(equation));
    }

    @Test
    @DisplayName("Throws exception for null equation")
    void nullEquation() {
        Throwable exception = Assertions.assertThrows(IllegalArgumentException.class, () -> calculator.calculate(null));
        Assertions.assertEquals("A equação não pode ser nula ou vazia.", exception.getMessage());
    }

    @Test
    @DisplayName("Throws exception for empty string")
    void emptyEquation() {
        Throwable exception = Assertions.assertThrows(IllegalArgumentException.class, () -> calculator.calculate(""));
        Assertions.assertEquals("A equação não pode ser nula ou vazia.", exception.getMessage());
    }

    @Test
    @DisplayName("Basic multiplication of two numbers")
    void multiplyTwoNumbers() {
        Assertions.assertEquals("5813020", calculator.calculate("1453255*4"));
    }

    @Test
    @DisplayName("Multiplication of multiple numbers")
    void multiplyMoreThanTwoNumbers() {
        Assertions.assertEquals("399280000", calculator.calculate("23*14*1000*1240"));
    }

    @Test
    @DisplayName("Multiply negative numbers")
    void multiplicationWithNegatives() {
        Assertions.assertEquals("-15", calculator.calculate("3*-5"));
    }

    @Test
    @DisplayName("Two negatives should make a positive in multiplication")
    void multiplyTwoNegatives() {
        Assertions.assertEquals("15", calculator.calculate("-3*(-5)"));
    }

    @Test
    @DisplayName("Basic division of two numbers")
    void divideTwoNumbers() {
        Assertions.assertEquals("2", calculator.calculate("30/15"));
    }

    @Test
    @DisplayName("Throws exception for division by 0")
    void divideByZero() {
        Throwable exception = Assertions.assertThrows(IllegalArgumentException.class, () -> calculator.calculate("4/0"));
        Assertions.assertEquals("Divisão por zero não é permitida.", exception.getMessage());
    }

    @Test
    @DisplayName("Throws exception for division by 0 in more complex equations")
    void divideByZeroInEquation() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> calculator.calculate("4*4+12/0"));
    }

    @Test
    @DisplayName("Multiple divisions to verify associativity")
    void divideMoreThanTwoNumbers() {
        Assertions.assertEquals("2", calculator.calculate("140/2/7/5"));
    }

    @Test
    @DisplayName("Divide with negative divisor")
    void divisionWithNegativeDivisor() {
        Assertions.assertEquals("-20", calculator.calculate("60/-3"));
    }

    @Test
    @DisplayName("Divide with negative dividend")
    void divisionWithNegativeDividend() {
        Assertions.assertEquals("-20", calculator.calculate("-60/3"));
    }

    @Test
    @DisplayName("Divide with both numbers negatives")
    void divisionWithNegatives() {
        Assertions.assertEquals("40", calculator.calculate("-80/-2"));
    }

    @Test
    @DisplayName("Multiplication and division order with multiplication first")
    void multiplyThenDivide() {
        Assertions.assertEquals("56", calculator.calculate("24*7/3"));
    }

    @Test
    @DisplayName("Multiplication and division order with division first")
    void divideThenMultiply() {
        Assertions.assertEquals("324", calculator.calculate("216/2*3"));
    }

    @Test
    @DisplayName("Addition and multiplication order")
    void addThenMultiply() {
        Assertions.assertEquals("135", calculator.calculate("15+3*40"));
    }

    @Test
    @DisplayName("Basic addition of two numbers")
    void addTwoNumbers() {
        Assertions.assertEquals("65", calculator.calculate("47+18"));
    }

    @Test
    @DisplayName("Addition of multiple numbers")
    void addMoreThanTwoNumbers() {
        Assertions.assertEquals("55", calculator.calculate("17+6+18+14"));
    }

    @Test
    @DisplayName("Operation order with multiplication and addition")
    void operationOrder() {
        Assertions.assertEquals("137", calculator.calculate("17+3*40"));
    }

    @Test
    @DisplayName("Mixed operations with addition, multiplication, and division")
    void addMultiplyAndDivide() {
        Assertions.assertEquals("69", calculator.calculate("4+5+10*14/7*3"));
    }

    @Test
    @DisplayName("Four basic operations")
    void fourBasicOperations() {
        Assertions.assertEquals("146", calculator.calculate("4+5+10*14-7/7*3"));
    }

    @Test
    @DisplayName("Equation with parentheses")
    void complexEquation() {
        Assertions.assertEquals("5", calculator.calculate("2+3*(4-3)"));
    }

    @Test
    @DisplayName("Addition and multiplication with parentheses")
    void addThenMultiplyWithParentheses() {
        Assertions.assertEquals("720", calculator.calculate("(15+3)*40"));
    }
}