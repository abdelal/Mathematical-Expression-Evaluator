package main.java;

import javafx.util.Pair;
import main.java.exceptions.UnknownCharacterException;
import org.testng.log4testng.Logger;

import java.util.*;

public class Tokenizer {


    private final Logger LOGGER = Logger.getLogger(Tokenizer.class);
    private static final Set<String> SUPPORTED_FUNCTIONS =
            new HashSet<>(Arrays.asList(Constants.SQRT_FUNTION, Constants.LOG_FUNCTION, Constants.SIN_FUNCTION, Constants.COS_FUNCTION));
    private static final Set<String> SUPPORTED_BINARY_OPERATORS=
            new HashSet(Arrays.asList(Constants.MULTIPLICATION_CHARACTER, Constants.SUBSTRACTION_CHARACTER,
                Constants.ADDITION_CHARACTER, Constants.BINDING_CHARACTER, Constants.DIVISION_CHARACTER));


    private StringBuilder stringBuilder;
    private List<Pair<TokenType, String>> tokens ;

    public Tokenizer() {
        tokens = new ArrayList<>();
        stringBuilder=new StringBuilder();
    }



    /*
     * takes a string and tokenizes it (numbers, variables, operators , parentheses , functions)
     * */

    public List<Pair<TokenType, String>> tokenizeString(String expression) throws UnknownCharacterException {
        LOGGER.info("started tokenizing the Expression");

        tokens.clear();
        char[] chars = expression.toCharArray();

        for (int currentCharacterIndex = 0; currentCharacterIndex < chars.length; currentCharacterIndex++) {
            char currentCharacter = chars[currentCharacterIndex];
            if (isSupportedCharacter(currentCharacter)) {
                // for both String and Number , the currentCharacterIndex needs to be set to the end of the
                // the number or the character for example 21+3 , when we read 21 , we need to keep going
                // from the first index that was different from number/string to not read it again
                if (isString(currentCharacter)) {
                    currentCharacterIndex = readWord(chars, currentCharacterIndex);
                }
                if (isNumber(currentCharacter)) {
                    currentCharacterIndex = parseNumber(chars, currentCharacterIndex);
                }
                if (isOperator(currentCharacter)) {
                    addTokenToPrasedTokensArray(TokenType.BINARY_OPERATOR, String.valueOf(currentCharacter));
                }
                if (isParentheses(currentCharacter)) {
                    addTokenToPrasedTokensArray(TokenType.PARENTHESES, String.valueOf(currentCharacter));
                }
            } else {
                throw new UnknownCharacterException(currentCharacter);
            }

        }
        LOGGER.info("done tokenizing the Expression");
        LOGGER.debug("the output of the tokenizer was " + tokens);
        return tokens;
    }


    /*
    * returns wither a given character is supported or not
    * */
    private boolean isSupportedCharacter(char characterToCheck) {

        return isNumber(characterToCheck) || isString(characterToCheck) || isOperator(characterToCheck) || isParentheses(characterToCheck);
    }

    /*
     * adds a token to the tokens List
     * */
    private void addTokenToPrasedTokensArray(TokenType tokenType, String token) {
        tokens.add(new Pair(tokenType, token));

    }

    /*
     * reads a string and decides if its a variable or a function
     * */

    private int readWord(char[] chars, int startingCharacterPosition) {
        LOGGER.debug("reading String at position " + startingCharacterPosition);

        while (startingCharacterPosition < chars.length && (isNumber(chars[startingCharacterPosition]) || isString(chars[startingCharacterPosition]))) {
            stringBuilder.append(chars[startingCharacterPosition]);
            startingCharacterPosition++;
        }
        String parsedWord = stringBuilder.toString();
        TokenType wordType = isFunction(parsedWord) ? TokenType.FUNCTION : TokenType.VARIABLE;
        addTokenToPrasedTokensArray(wordType, parsedWord);
        clearStringBuilder();
        LOGGER.debug("done reading the String ,last character position is  " + String.valueOf(startingCharacterPosition - 1));

        return startingCharacterPosition - 1;
    }


    /*
     * check if a given character is an accepted string character
     * */
    private boolean isString(char expressionChar) {
        return ((expressionChar >= Constants.FIRST_SMALL_CHARACTER && expressionChar <= Constants.LAST_SMALL_CHARACTER)
                || (expressionChar >= Constants.FIRST_CAPITAL_CHARACTER && expressionChar <= Constants.LAST_CAPITAL_CHARACTER)
                || expressionChar == Constants.SPECIAL_VARIABLE_CHARACTER);
    }


    /*
     * check if a given character is a supported Operator
     * */
    private boolean isOperator(char expressionChar) {
        return SUPPORTED_BINARY_OPERATORS.contains(expressionChar);

    }

    /*
     * check if a given character is a parentheses
     * */

    private boolean isParentheses(char expressionChar) {
        return (expressionChar == Constants.OPENING_PARENTHESES_CHARACTER || expressionChar == Constants.CLOSING_PARENTHESES_CHARACTER);

    }
    /*
     * check if a given character is a number
     * */

    private boolean isNumber(char expressionChar) {
        return ((expressionChar >= Constants.SMALLEST_DIGIT_CHARACTER && expressionChar <= Constants.HIGHEST_DIGIT_CHARACTER )
                || expressionChar == Constants.DECIMAL_POINT_CHARACTER);

    }
    /*
     * check if a given string is a function
     * */
    private boolean isFunction(String expressionChar) {
        return SUPPORTED_FUNCTIONS.contains(expressionChar);

    }


    /*
     *parses a number in the char array
     * */

    private int parseNumber(char[] chars, int numberStartingCharacter) {
        // it will keep reading until a the next character is not a number
        LOGGER.debug(" reading number startinf from  " + numberStartingCharacter);
        int currentCharacterIndex=numberStartingCharacter;
        while (currentCharacterIndex < chars.length && isNumber(chars[currentCharacterIndex])) {
            stringBuilder.append(chars[currentCharacterIndex]);
            currentCharacterIndex++;
        }
        String tokenizedNumber = stringBuilder.toString();
        clearStringBuilder();
        addTokenToPrasedTokensArray(TokenType.NUMBER, tokenizedNumber);

        LOGGER.debug("done reading the String ,last character position is  " + String.valueOf(numberStartingCharacter - 1));


        return currentCharacterIndex - 1;

    }

    /*
    * clears the string builder field
    * */
    private void clearStringBuilder() {
        stringBuilder.setLength(0);
    }


}