

import javafx.util.Pair;
import main.java.TokenType;
import main.java.Tokenizer;
import main.java.exceptions.UnknownCharacterException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;


public class TokenizerTest {
    private Tokenizer tokenizerTestObject = new Tokenizer();


    @Test
    public void tokenizeSimpleExpressionTest() throws UnknownCharacterException {
        List<Pair<TokenType, String>> tokens = tokenizerTestObject.tokenizeString("1+1");
        List<Pair<String, String>> expectedResults = new ArrayList<>(3);
        expectedResults.add(new Pair(TokenType.NUMBER, "1"));
        expectedResults.add(new Pair(TokenType.BINARY_OPERATOR, "+"));
        expectedResults.add(new Pair(TokenType.NUMBER, "1"));

        Assert.assertEquals(tokens, expectedResults);
    }

    @Test
    public void tokenizeSimpleExpressionWithAllSimpleOperatorsTest() throws UnknownCharacterException {
        List<Pair<TokenType, String>> tokens = tokenizerTestObject.tokenizeString("1+1*4+5+(2+2)*5/3");
        List<Pair<TokenType, String>> expectedResults = buildExpectedBaseList();
        Assert.assertEquals(tokens, expectedResults);

    }

    @Test
    public void tokenizeSimpleExpressionWithAllOperatorsTest() throws UnknownCharacterException {
        List<Pair<TokenType, String>> tokens = tokenizerTestObject.tokenizeString("1+1*4+5+(2+2)*5/3+sqrt(4)");
        List<Pair<TokenType, String>> expectedResults = buildExpectedBaseList();

        expectedResults.add(new Pair(TokenType.BINARY_OPERATOR, "+"));
        expectedResults.add(new Pair(TokenType.FUNCTION, "sqrt"));
        expectedResults.add(new Pair(TokenType.PARENTHESES, "("));
        expectedResults.add(new Pair(TokenType.NUMBER, "4"));
        expectedResults.add(new Pair(TokenType.PARENTHESES, ")"));


        Assert.assertEquals(tokens, expectedResults);

    }


    public List<Pair<TokenType, String>> buildExpectedBaseList() {
        List<Pair<TokenType, String>> expectedResults = new ArrayList<>(18);
        expectedResults.add(new Pair(TokenType.NUMBER, "1"));
        expectedResults.add(new Pair(TokenType.BINARY_OPERATOR, "+"));
        expectedResults.add(new Pair(TokenType.NUMBER, "1"));
        expectedResults.add(new Pair(TokenType.BINARY_OPERATOR, "*"));
        expectedResults.add(new Pair(TokenType.NUMBER, "4"));
        expectedResults.add(new Pair(TokenType.BINARY_OPERATOR, "+"));
        expectedResults.add(new Pair(TokenType.NUMBER, "5"));
        expectedResults.add(new Pair(TokenType.BINARY_OPERATOR, "+"));
        expectedResults.add(new Pair(TokenType.PARENTHESES, "("));
        expectedResults.add(new Pair(TokenType.NUMBER, "2"));
        expectedResults.add(new Pair(TokenType.BINARY_OPERATOR, "+"));
        expectedResults.add(new Pair(TokenType.NUMBER, "2"));
        expectedResults.add(new Pair(TokenType.PARENTHESES, ")"));
        expectedResults.add(new Pair(TokenType.BINARY_OPERATOR, "*"));
        expectedResults.add(new Pair(TokenType.NUMBER, "5"));
        expectedResults.add(new Pair(TokenType.BINARY_OPERATOR, "/"));
        expectedResults.add(new Pair(TokenType.NUMBER, "3"));
        return expectedResults;
    }

    @Test
    public void UnknownCharacterException() {

        boolean exceptionFlag = false;
        try {
            tokenizerTestObject.tokenizeString("2+%");
        } catch (Exception e) {
            exceptionFlag = true;
            Assert.assertTrue(e instanceof UnknownCharacterException);
        }

        Assert.assertTrue(exceptionFlag);
    }

}