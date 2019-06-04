package main.java;

import javafx.util.Pair;
import main.java.exceptions.UnknownCharacterException;
import main.java.exceptions.VariableIsNotDefinedException;
import org.testng.log4testng.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;


/**
 * A simple calculator program reading arithmetic expressions from the standard
 * input, evaluating them, and printing the results on the standard output.
 */
public class Calc {


    private final Logger LOGGER = Logger.getLogger(Calc.class);

    /**
     * Evaluates an arithmetic expression. The grammar of accepted expressions
     * is the following:
     *
     * <code>
     * <p>
     * expr ::= factor | expr ('+' | '-') expr
     * factor ::= term | factor ('*' | '/') factor
     * term ::= '-' term | '(' expr ')' | number | id | function | binding
     * number ::= int | decimal
     * int ::= '0' | posint
     * posint ::= ('1' - '9') | posint ('0' - '9')
     * decimal ::= int '.' ('0' - '9') | '.' ('0' - '9')
     * id ::= ('a' - 'z' | 'A' - 'Z' | '_') | id ('a' - 'z' | 'A' - 'Z' | '_' | '0' - '9')
     * function ::= ('sqrt' | 'log' | 'sin' | 'cos') '(' expr ')'
     * binding ::= id '=' expr
     *
     * </code>
     * <p>
     * The binary operators are left-associative, with multiplication and division
     * taking precedence over addition and subtraction.
     * <p>
     * Functions are implemented in terms of the respective static methods of
     * the class java.lang.Math.
     * <p>
     * The bindings produced during the evaluation of the given expression
     * are stored in a map, where they remain available for the evaluation
     * of subsequent expressions.
     * <p>
     * Before leaving this method, the value of the given expression is bound
     * to the special variable named "_".
     *
     * @param expr well-formed arithmetic expression
     * @return the value of the given expression
     */


    // used to store the numbers
    private Stack<Double> numbersStack = new Stack();

    // used to store parsed operators
    private Stack<String> operatorsStack = new Stack();


    //stores the index of the matching parentheses in the expression
    private HashMap<Integer, Integer> parenthesesIndex;


    //seen variable names are stored in this stack
    private Stack<String> variablesStack = new Stack<>();

    // the output of the tokenizer will be in this variable
    private List<Pair<TokenType, String>> expressionAsTokens;
    private StringBuilder stringBuilder = new StringBuilder();

    public double eval(String expr) {
        LOGGER.info("started Evaluating the expression " + expr);
        try {
            initializeDataForExpression(expr);
            double expressionResult = evaluateExpression();

            LOGGER.info("done Evaluating the expression " + expr + " and the result was " + expressionResult);

            if (!numbersStack.isEmpty() || !operatorsStack.isEmpty() || !variablesStack.isEmpty()) {
                LOGGER.warn("evaluating finished but one of the stacks was not empty");
                LOGGER.debug("the stacks are after evaulating the expression " + " Operators Stack " + operatorsStack +
                        "numbers Stack " + numbersStack + " variables stack " + variablesStack);
            }
            // the result of the last evaluated expression is stored in the variable _
            assignValueToVariable(Constants.SPECIAL_VARIABLE, expressionResult);
            return expressionResult;
        } catch (ArithmeticException | VariableIsNotDefinedException | UnknownCharacterException e) {
            // if there was an exception , clear all stacks from any leftovers
            emptyStacks();
            e.printStackTrace();
            throw new UnsupportedOperationException(e.getMessage());

        }

    }

    /*
     * populates the needed dataStructures before evaluating the expression
     * */
    private void initializeDataForExpression(String expr) throws UnknownCharacterException {
        Tokenizer tokenizer = new Tokenizer();
        expr = wrapString(expr);
        expressionAsTokens = tokenizer.tokenizeString(expr);
        parenthesesIndex = indexParentheses(expressionAsTokens);

    }

    private void emptyStacks() {
        numbersStack.clear();
        operatorsStack.clear();
        variablesStack.clear();

    }


    /*
     *
     * this function iterates over the expression tokens and evaluates it
     *
     * */
    private double evaluateExpression() throws VariableIsNotDefinedException {
        int currentPositionInExpression = 0;
        LOGGER.info("evaluating Expression ");
        LOGGER.debug("expression tokens are " + expressionAsTokens);
        while (currentPositionInExpression < expressionAsTokens.size()) {
            TokenType currentTokenType = expressionAsTokens.get(currentPositionInExpression).getKey();
            String currentTokenValue = expressionAsTokens.get(currentPositionInExpression).getValue();
            switch (currentTokenType) {
                case NUMBER:
                    pushNumberToNumbersStack(Double.valueOf(currentTokenValue));
                    break;
                case FUNCTION:
                    pushOperatorToOperatorsStack(currentTokenValue);
                    break;
                case VARIABLE:
                    pushOperatorToOperatorsStack(String.valueOf(Constants.VARIABLE));
                    pushVariableToVariablesStack(currentTokenValue);
                    break;
                case BINARY_OPERATOR:
                    evaluateBinaryOperator(currentPositionInExpression, currentTokenValue);
                    break;
                case PARENTHESES:
                    evaluateParentheses(currentTokenValue, currentPositionInExpression);

            }
            currentPositionInExpression++;
        }
        LOGGER.info("done evaluating Expression ");

        return getNumberFromNumbersStack();
    }


    /*
     *
     * evaluates Parentheses
     *
     * */

    private void evaluateParentheses(String currentTokenValue, int currentPositionInExpression) throws VariableIsNotDefinedException {
        LOGGER.debug("evaluating Parentheses ");
        String currentSubExpression;

        switch (currentTokenValue) {
            //if current token is a closing Parentheses then we need to apply all operators until we reach
            // an opening parentheses
            case Constants.CLOSING_PARENTHESES:
                while (operatorsStack.peek() != Constants.OPENING_PARENTHESES) {
                    evaluateOperator(Constants.SPECIAL_OPERATOR);
                }
                // builds a the string of the current subexpression
                currentSubExpression = getCurrentSubExpression(currentPositionInExpression, false);

                LOGGER.debug("done evaluating the sub Expression " + currentSubExpression + " and the result was " + numbersStack.peek());
                //pop the opening Parentheses from the stack( since we just saw a closing parentheses
                getOperatorFromOperatorsStack();
                break;
            case Constants.OPENING_PARENTHESES:
                currentSubExpression = getCurrentSubExpression(currentPositionInExpression, true);

                LOGGER.debug("Started evaluating the sub Expression " + currentSubExpression);
                pushOperatorToOperatorsStack(Constants.OPENING_PARENTHESES);
        }
        LOGGER.debug("done evaluating Parentheses ");

    }


    /*
     *
     * returns a string that contains all the token in between 2 matching parentheses
     *
     * */
    private String getCurrentSubExpression(int startingParentheses, boolean fromStart) {
        Integer endingParentheses;
        int currentPositioninExpression;
        if (fromStart) {
            currentPositioninExpression = startingParentheses;
            endingParentheses = parenthesesIndex.get(startingParentheses);
        } else {
            currentPositioninExpression = parenthesesIndex.get(startingParentheses);
            endingParentheses = startingParentheses;

        }
        while (currentPositioninExpression < endingParentheses + 1) {
            stringBuilder.append(expressionAsTokens.get(currentPositioninExpression).getValue());

            currentPositioninExpression++;
        }

        String subExpression = stringBuilder.toString();
        clearStringBuilder();
        return subExpression;
    }


    /*
     *
     * this method will evaluates binary Operators (- + / *)
     *
     * */
    private void evaluateBinaryOperator(int currentPositionInExperssion, String currentTokenValue) throws VariableIsNotDefinedException {
        Pair<TokenType, String> previousToken = expressionAsTokens.get(currentPositionInExperssion - 1);
        LOGGER.info("evaluating binary operator");

        //this is for cases like 1--1
        // path for such expression
        // expr-> number-expr -> number-factor -> number-term ->number --term -> number--number
        if (currentTokenValue.equals(Constants.SUBSTRACTION) &&
                previousToken.getValue().equals(Constants.OPENING_PARENTHESES)
                || previousToken.getKey() == TokenType.BINARY_OPERATOR) {
            LOGGER.debug("substituting - after operator");
            // if a (-) is found after ( or another binary Operator then we translate it to 0-
            // we push 0 to numbersStack and ("-") to the operators stack
            // for example --1 will be 0-0-1
            pushNumberToNumbersStack(0);
            pushOperatorToOperatorsStack(Constants.SUBSTRACTION);

        } else {
            evaluateOperator(currentTokenValue);
        }
        LOGGER.info("done evaluating binary operator");
    }


    /*
     *
     * inserts a number to the numbers Stack , before that it only takes the first 12 digits after the decimal point
     *
     * */
    private void pushNumberToNumbersStack(double numberToAdd) {
        // before adding a number to the numbers stack we only take the first
        double scaledNumberToAdd = BigDecimal.valueOf(numberToAdd)
                .setScale(Constants.NUMBER_OF_DIGITS_AFTER_DECEMAL_POINT, RoundingMode.HALF_UP)
                .doubleValue();
        LOGGER.debug("inserting " + scaledNumberToAdd + "  to the numbers stack");

        numbersStack.add(scaledNumberToAdd);
    }

    /*
     *
     * inserts an operator to the operators Stack
     *
     * */
    private void pushOperatorToOperatorsStack(String operatorToAdd) {
        LOGGER.debug("inserting " + operatorToAdd + "  to the operators stack");
        operatorsStack.add(operatorToAdd);
    }


    /*
     *
     * inserts a variable name to the variables Stack
     *
     * */
    private void pushVariableToVariablesStack(String variableName) {
        LOGGER.debug("inserting " + variableName + "  to the variables stack");
        variablesStack.add(variableName);
    }


    /*
     *  wraps a given string with "(" ")"
     *
     *
     * */
    private String wrapString(String expr) {

        LOGGER.debug("wrapping the expression");

        stringBuilder.append(Constants.OPENING_PARENTHESES);
        stringBuilder.append(expr);
        stringBuilder.append(Constants.CLOSING_PARENTHESES);

        expr = stringBuilder.toString();
        clearStringBuilder();
        return expr;

    }


    private void clearStringBuilder() {
        stringBuilder.setLength(0);
    }

    /*
     * returns map of which key value is the position a parentheses is open at , and the value is where
     * the matching parentheses is
     *
     * */
    private HashMap<Integer, Integer> indexParentheses(List<Pair<TokenType, String>> expressionAsTokens) {
        HashMap<Integer, Integer> parenthesesIndex = new HashMap<>();
        Stack<Integer> startOfParentheses = new Stack<>();

        int currentTokenindexInTokensList = 0;
        while (currentTokenindexInTokensList < expressionAsTokens.size()) {
            String currentTokenValue = expressionAsTokens.get(currentTokenindexInTokensList).getValue();
            switch (currentTokenValue) {
                case Constants.OPENING_PARENTHESES:
                    startOfParentheses.push(currentTokenindexInTokensList);
                    break;
                case Constants.CLOSING_PARENTHESES:
                    Integer lastOpeningParentheses = startOfParentheses.pop();
                    parenthesesIndex.put(lastOpeningParentheses, currentTokenindexInTokensList);
                    parenthesesIndex.put(currentTokenindexInTokensList, lastOpeningParentheses);
                    break;
            }
            currentTokenindexInTokensList++;
        }


        if (!startOfParentheses.isEmpty()) {
            LOGGER.warn("your expression is not valid");

        }
        return parenthesesIndex;


    }


    /*
     *   evaluates operator if possible
     * */

    private void evaluateOperator(String currentOperator) throws VariableIsNotDefinedException {
        LOGGER.info("evaluating Operator " + currentOperator);
        // if we reached the end of the current expression , we need to evaluate all operators in current expression
        // for example (5+(5+5*6/2))
        // when we are evaluating subexpression (5+5*6/2) and we reach ")"
        // we need to apply any operators that appeared between the parentheses
        if (currentOperator == Constants.SPECIAL_OPERATOR) {
            String lastOperatorInStack = getOperatorFromOperatorsStack();

            if (!lastOperatorInStack.equals(Constants.OPENING_PARENTHESES)) {
                LOGGER.debug("applying Operator " + lastOperatorInStack);

                double evaluationOutput = calculateValueWithOperator(lastOperatorInStack);

                pushNumberToNumbersStack(evaluationOutput);

            }
        } else {
            // if top of the stack is "(" and the operators stack is empty , or current operator has precedence over the
            // last seen operator push current operator to the operators stack
            if (operatorsStack.isEmpty() || operatorsStack.peek().equals(Constants.OPENING_PARENTHESES)
                    || operatorHasPrecedence(currentOperator)) {
                pushOperatorToOperatorsStack(currentOperator);

            } else {
                // apply the operators that can be applied
                applyPreviousOperatorsWithHigherPrecedence(currentOperator);

            }
        }
        LOGGER.info("done evaluating Operator " + currentOperator);

    }


    /*
     * applies previous operators with higher precedence (just not "(" )
     * */
    private void applyPreviousOperatorsWithHigherPrecedence(String currentOperator) throws VariableIsNotDefinedException {
        String lastOperatorInStack;
        LOGGER.debug("started applying previous operators ");

        // if the input is something like 1*3*4*5+1
        // we need to do all the * before adding
        // if the top of the operators stack is "(" then we stop
        // "(" is the start of the current subexpression  for example ((1*3*4+5*8)+5)
        // when the first + is encountered we can evaluate all previous Operators until "("
        while (!operatorsStack.isEmpty() && !operatorsStack.peek().equals(Constants.OPENING_PARENTHESES) &&
                !operatorHasPrecedence(currentOperator)) {

            // apply the operator on top of the operators stack
            lastOperatorInStack = getOperatorFromOperatorsStack();
            LOGGER.debug("applying Operator " + lastOperatorInStack);
            double evaluationOutput = calculateValueWithOperator(lastOperatorInStack);
            // push the output of that operator to the numbersStack
            pushNumberToNumbersStack(evaluationOutput);
        }
        //after finishing , push the current operator to the operators sz
        pushOperatorToOperatorsStack(currentOperator);
        LOGGER.debug("done applying previous operators ");


    }

    /**
     * this method will apply the given operator on the first n elements in the stack
     * where n is the number of arguments the operator takes
     * for example for "+" n=2
     */
    private double calculateValueWithOperator(String currentOperator) throws VariableIsNotDefinedException {
        double lastSeenNumber = 0;
        // if the current operator is not a variable then we need to get the top number in the numbers stack
        if (!currentOperator.equals(Constants.VARIABLE)) {
            lastSeenNumber = getNumberFromNumbersStack();
        }
        double firstNumberInOperationNeeded = 0;
        // check if the currentOperator is a binary operator , if yes fetch the next operand
        if (isBinaryOperator(currentOperator)) {
            firstNumberInOperationNeeded = fetchFirstOperandForOperator();
        }
        switch (currentOperator) {
            case Constants.ADDITION:
                return firstNumberInOperationNeeded + lastSeenNumber;
            case Constants.SUBSTRACTION:
                return firstNumberInOperationNeeded - lastSeenNumber;
            case Constants.MULTIPLICATION:
                return firstNumberInOperationNeeded * lastSeenNumber;

            case Constants.DIVISION:
                if (lastSeenNumber == 0) {
                    throw new ArithmeticException("Division By Zero Exception");
                }
                return firstNumberInOperationNeeded / lastSeenNumber;
            case Constants.SQRT_FUNTION:
                return Math.sqrt(lastSeenNumber);
            case Constants.LOG_FUNCTION:
                return Math.log(lastSeenNumber);
            case Constants.SIN_FUNCTION:
                return Math.sin(lastSeenNumber);
            case Constants.COS_FUNCTION:
                return Math.cos(lastSeenNumber);
            case Constants.BINDING:
                bindVariableToValue(lastSeenNumber);
                return lastSeenNumber;
            case Constants.VARIABLE:
                double variableValue = gerVariableValue();
                return variableValue;
            default:
                return lastSeenNumber;
        }

    }

    /*
     * binds the last seen variable to a given value
     *
     * */
    private void bindVariableToValue(double lastSeenNumber) {
        LOGGER.debug("binding vale to variable ");
        //discard of the variable operator
        getOperatorFromOperatorsStack();
        // fetch the variable's name and assign the give value to it
        String variableName = getVariableFromVariablesStack();
        assignValueToVariable(variableName, lastSeenNumber);
    }


    /*
     *  checks if one of the two terms is a variable , if yes , substitute the variable with its value
     *
     * */
    private double fetchFirstOperandForOperator() throws VariableIsNotDefinedException {
        LOGGER.debug("fetching first Operand for operation");

        double firstOperand = 0;

        // if the first operand is not a variable
        if (!hasVariableBefore()) {
            firstOperand = getNumberFromNumbersStack();
        } else {
            firstOperand = gerVariableValue();
            getOperatorFromOperatorsStack();
        }
        LOGGER.debug("done fetching first Operand for operation");

        return firstOperand;
    }


    /*
     *
     * this method returns the value of the last seen variable , if the variable is not defined an exception will be thrown
     *
     * */
    private double gerVariableValue() throws VariableIsNotDefinedException {
        String variableName = getVariableFromVariablesStack();
        double firstOperand = 0;
        // check if the there is a value assigned to the variable
        if (bindings().containsKey(variableName)) {
            LOGGER.debug("getting the value of variable " + variableName);

            firstOperand = bindings().get(variableName);

        } else {
            LOGGER.warn(String.format("you need to assign a value to variable %s before using it", variableName));
            throw new VariableIsNotDefinedException(variableName + " is not defined");

        }

        return firstOperand;
    }


    /*
     *
     * assigns the given value to the give Variable
     *
     * */
    private void assignValueToVariable(String variableName, double variableValue) {
        LOGGER.debug("assigning the value  " + variableValue + "to variable " + variableName);

        bindings().put(variableName, variableValue);
    }


    /*
     *  checks if the given operator is one of the base operators ( - + / * )
     *
     * */
    private boolean isBinaryOperator(String operator) {
        return (operator.equals(Constants.MULTIPLICATION) || operator.equals(Constants.SUBSTRACTION)
                || operator.equals(Constants.ADDITION) || operator.equals(Constants.DIVISION));

    }


    /*
     *
     * returns the first element in Numbers Stack
     *
     * */
    private double getNumberFromNumbersStack() {
        LOGGER.debug("popping " + numbersStack.peek() + "from the numbers stack");
        return numbersStack.pop();
    }

    /*
     *
     * returns the first element in Operators Stack
     *
     * */
    private String getOperatorFromOperatorsStack() {
        LOGGER.debug("popping " + operatorsStack.peek() + "from the operators stack");

        return operatorsStack.pop();
    }


    /*
     *
     * returns the first element in Variables Stack
     *
     * */
    private String getVariableFromVariablesStack() {
        LOGGER.debug("popping " + operatorsStack.peek() + "from the variables stack");
        return variablesStack.pop();
    }


    /*
     * returns true if there is a Operator of Type variable on top of the stack
     * */
    private boolean hasVariableBefore() {

        return operatorsStack.isEmpty() ? false : operatorsStack.peek().equals(Constants.VARIABLE);

    }


    /*
     * this method checks if the current Operator Has precedence over the previous one
     *
     * */
    private boolean operatorHasPrecedence(String currentOperator) {
        String lastOperatorInOperatorStack = operatorsStack.peek();
        int lastOperatorInStackStrength = getOperatorStrength(lastOperatorInOperatorStack);
        int currentOperatorsStrength = getOperatorStrength(currentOperator);
        return (lastOperatorInStackStrength < currentOperatorsStrength);
    }


    /*
     * this method return the Priority of the given Operator
     *
     * */
    private int getOperatorStrength(String operator) {
        switch (operator) {
            case Constants.ADDITION:
            case Constants.SUBSTRACTION:
                return Constants.ADDITION_SUBTRACTION_PRECEDENCE;

            case Constants.MULTIPLICATION:
            case Constants.DIVISION:
                return Constants.MULTIPLICATION_DIVISION_PRECEDENCE;

            case Constants.OPENING_PARENTHESES:
                return Constants.PARENTHESE_PRECEDENCE;
            case Constants.SQRT_FUNTION:
            case Constants.LOG_FUNCTION:
            case Constants.SIN_FUNCTION:
            case Constants.COS_FUNCTION:
                return Constants.FUNCTION_PRECEDENCE;

            case Constants.VARIABLE:
                return Constants.VARIABLE_OPERATOR_PRECEDENCE;

            case Constants.BINDING:
                return Constants.BINDING_PRECEDENCE;

            default:
                return -1;
        }


    }


    public Map<String, Double> bindings() {
        return bindings;
    }

    private final Map<String, Double> bindings = new TreeMap<>();

    public static void main(String[] args) throws IOException {

        Calc calc = new Calc();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
             PrintWriter out = new PrintWriter(System.out, true)) {
            while (true) {
                String line = in.readLine();
                if (line == null) {
                    break;
                }
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                try {
                    if (!line.startsWith(":")) {
                        // handle expression
                        out.println(calc.eval(line));
                    } else {
                        // handle command
                        String[] command = line.split("\\s+", 2);
                        switch (command[0]) {
                            case ":vars":
                                calc.bindings().forEach((name, value) ->
                                        out.println(name + " = " + value));
                                break;
                            case ":clear":
                                if (command.length == 1) {
                                    // clear all
                                    calc.bindings().clear();
                                } else {
                                    // clear requested
                                    calc.bindings().keySet().removeAll(Arrays.asList(command[1].split("\\s+")));
                                }
                                break;
                            case ":exit":
                            case ":quit":
                                System.exit(0);
                                break;
                            default:
                                throw new RuntimeException("unrecognized command: " + line);
                        }
                    }
                } catch (Exception ex) {
                    System.err.println("*** ERROR: " + ex.getMessage());
                }
            }
        }
    }
}
