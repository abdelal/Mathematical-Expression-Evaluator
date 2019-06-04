import main.java.Calc;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CalcTest {

    private Calc calculatorTestObject = new Calc();

    @Test
    public void SimpleAdditionTest() {
        double calculatorResult = calculatorTestObject.eval("2+2");
        Assert.assertEquals(calculatorResult, 4.0);
        calculatorResult = calculatorTestObject.eval("2+0");
        Assert.assertEquals(calculatorResult, 2.0);

    }



    @Test
    public void simpleBindingTest() {
        double calculatorResult = calculatorTestObject.eval("x=10");
        Assert.assertEquals(calculatorResult, 10.0);
        calculatorResult = calculatorTestObject.eval("x");
        Assert.assertEquals(calculatorResult, 10.0);
        calculatorResult = calculatorTestObject.eval("x+10");
        Assert.assertEquals(calculatorResult, 20.0);
        calculatorResult = calculatorTestObject.eval("x+x");
        Assert.assertEquals(calculatorResult, 20.0);
        calculatorResult = calculatorTestObject.eval("x*3");
        Assert.assertEquals(calculatorResult, 30.0);
        calculatorResult = calculatorTestObject.eval("x/2");
        Assert.assertEquals(calculatorResult, 5.0);
    }

    @Test
    public void simpleBindingWithFunctionTest() {
        double calculatorResult = calculatorTestObject.eval("id=20");
        Assert.assertEquals(calculatorResult, 20.0);
        calculatorResult = calculatorTestObject.eval("cos(id)");
        Assert.assertEquals(calculatorResult, 0.408082061813);

    }

    @Test
    public void simpleDecimalAdditionTest() {
        double calculatorResult = calculatorTestObject.eval(".5+.5");
        Assert.assertEquals(calculatorResult, 1.0);
        calculatorResult = calculatorTestObject.eval("1+0.5");
        Assert.assertEquals(calculatorResult, 1.5);
    }

    @Test
    public void simpleSubtractionTest() {
        double calculatorResult = calculatorTestObject.eval("2-1");
        Assert.assertEquals(calculatorResult, 1.0);
    }


    @Test
    public void doubleSubtractionTest() {
        double calculatorResult = calculatorTestObject.eval("1--1");
        Assert.assertEquals(calculatorResult, 2.0);
    }

    @Test
    public void tribleSubtractionInTheMiddleWithMutiplyTest() {
        double calculatorResult = calculatorTestObject.eval("2+3*---1");
        Assert.assertEquals(calculatorResult, -1.0);
    }


    @Test
    public void previousResultsTest() {
        double calculatorResult = calculatorTestObject.eval("2+3*---1");
        Assert.assertEquals(calculatorResult, -1.0);
        calculatorResult = calculatorTestObject.eval("_");
        Assert.assertEquals(calculatorResult, -1.0);

    }

    @Test
    public void doubleSubtractionInStartTest() {
        double calculatorResult = calculatorTestObject.eval("--1");
        Assert.assertEquals(calculatorResult, 1.0);
    }

    @Test
    public void SubtractionWithParentheseTest() {
        double calculatorResult = calculatorTestObject.eval("2-(-1+2)");
        Assert.assertEquals(calculatorResult, 1.0);
    }

    @Test
    public void SimpleNegativeTermTest() {
        double calculatorResult = calculatorTestObject.eval("-1");
        Assert.assertEquals(calculatorResult, -1.0);
        calculatorResult = calculatorTestObject.eval("-1+2");
        Assert.assertEquals(calculatorResult, 1.0);
        calculatorResult = calculatorTestObject.eval("-1*2");
        Assert.assertEquals(calculatorResult, -2.0);

    }


    @Test
    public void simpleMultiplicationTest() {
        double calculatorResult = calculatorTestObject.eval("2*3");
        Assert.assertEquals(calculatorResult, 6.0);
    }

    @Test
    public void simpleDivisionTest() {
        double calculatorResult = calculatorTestObject.eval("6/3");
        Assert.assertEquals(calculatorResult, 2.0);
    }

    @Test
    public void redundentParenthesesTest() {
        double calculatorResult = calculatorTestObject.eval("((6/3))");
        Assert.assertEquals(calculatorResult, 2.0);
    }

    @Test
    public void simpleSqrtFunctionTest() {
        double calculatorResult = calculatorTestObject.eval("sqrt(4)");
        Assert.assertEquals(calculatorResult, 2.0);
    }

    @Test
    public void simpleCosFunctionTest() {
        double calculatorResult = calculatorTestObject.eval("cos(0)");
        Assert.assertEquals(calculatorResult, 1.0);
        calculatorResult = calculatorTestObject.eval("cos(3.14159265359)");
        Assert.assertEquals(calculatorResult, -1.0);
    }

    @Test
    public void simpleLogFunctionTest() {
        double calculatorResult = calculatorTestObject.eval("log(10)");
        Assert.assertEquals(calculatorResult, 2.302585092994);
        calculatorResult = calculatorTestObject.eval("log(1)");
        Assert.assertEquals(calculatorResult, 0.0);
    }

    @Test
    public void functionInaFunctionTest() {
        double calculatorResult = calculatorTestObject.eval("sqrt(sqrt(16))");
        Assert.assertEquals(calculatorResult, 2.0);
        calculatorResult = calculatorTestObject.eval("sqrt(sqrt(8*2))");
        Assert.assertEquals(calculatorResult, 2.0);
        calculatorResult = calculatorTestObject.eval("sqrt(sqrt(12+4))");
        Assert.assertEquals(calculatorResult, 2.0);
        calculatorResult = calculatorTestObject.eval("sqrt(sqrt(32/2))");
        Assert.assertEquals(calculatorResult, 2.0);
    }

    @Test
    public void simpleSinFunctionTest() {
        int a = 90;
        double calculatorResult = calculatorTestObject.eval("sin(90)");
        Assert.assertEquals(calculatorResult, 0.893996663601);
    }

    @Test
    public void NegativeTermWithFunction() {
        double calculatorResult = calculatorTestObject.eval("-log(10)");
        Assert.assertEquals(calculatorResult, -2.302585092994);
        calculatorResult = calculatorTestObject.eval("-sqrt(4)");
        Assert.assertEquals(calculatorResult, -2.0);
        calculatorResult = calculatorTestObject.eval("-cos(0)");
        Assert.assertEquals(calculatorResult, -1.0);
        calculatorResult = calculatorTestObject.eval("-sin(90)");
        Assert.assertEquals(calculatorResult, -0.893996663601);
    }

    @Test
    public void simpleEquationWithAdditionAndMultiplyTest() {
        double calculatorResult = calculatorTestObject.eval("4+4*3");
        Assert.assertEquals(calculatorResult, 16.0);
        calculatorResult = calculatorTestObject.eval("2*3+5*2");
        Assert.assertEquals(calculatorResult, 16.0);
    }

    @Test
    public void equationWithAdditionAndMultiplyTest() {
        double calculatorResult = calculatorTestObject.eval("2*1+(1+(5*(10/2))+3)+5");
        Assert.assertEquals(calculatorResult, 36.0);
        calculatorResult = calculatorTestObject.eval("2*(1+(1+(5*(10/2))+3)/5)");
        Assert.assertEquals(calculatorResult, 13.6);
    }


    @Test
    public void mixedEquationTest() {
        double calculatorResult = calculatorTestObject.eval("5*2+(((5+7)*2)-5+6)-(((4+5-10+20)-10)*3)*2");
        Assert.assertEquals(calculatorResult, -19.0);
        calculatorResult = calculatorTestObject.eval("-(5*2+(((5+7)*2)-5+6)-(((4+5-10+20)-10)*3)*2)");
        Assert.assertEquals(calculatorResult, 19.0);
    }

    @Test
    public void givenExamplesTest() {
        double calculatorResult = calculatorTestObject.eval("1+2*3+4");
        Assert.assertEquals(calculatorResult, 11.0);
        calculatorResult = calculatorTestObject.eval("(1+2)*(3+4)");
        Assert.assertEquals(calculatorResult, 21.0);
        calculatorResult = calculatorTestObject.eval("sqrt(2)*sqrt(2)");
        Assert.assertEquals(calculatorResult, 2.0);
        calculatorResult = calculatorTestObject.eval("pi=3.14159265359");
        Assert.assertEquals(calculatorResult, 3.14159265359);
        calculatorResult = calculatorTestObject.eval("cos(pi)");
        Assert.assertEquals(calculatorResult, -1.0);
    }


    @Test
    public void mixedEquationWithFunctionTest() {
        double calculatorResult = calculatorTestObject.eval("5*2+(((5+7)*2)-5+6)-(((4+5-10+20)-10)*3)*sqrt(4)");
        Assert.assertEquals(calculatorResult, -19.0);
        calculatorResult = calculatorTestObject.eval("-(5*2+(((5+7)*2)-5+6)-(((4+5-10+20)-10)*3)*sqrt(4))");
        Assert.assertEquals(calculatorResult, 19.0);
    }

    @Test
    public void variableNotDefinedExceptionTest() {
        boolean exceptionFlag=false;
        try {
            calculatorTestObject.eval("y");
        } catch (Exception e) {
            exceptionFlag=true;
            Assert.assertTrue(e instanceof UnsupportedOperationException);
            Assert.assertTrue(e.getMessage().contains("y is not defined"));
        }
        Assert.assertTrue(exceptionFlag);
    }

    @Test
    public void DivisionByZeroTest() {
        boolean exceptionFlag=false;
        try {
            calculatorTestObject.eval("2/0");
        } catch (Exception e) {
            exceptionFlag=true;
            Assert.assertTrue(e instanceof UnsupportedOperationException);
            Assert.assertTrue(e.getMessage().contains("Division By Zero Exception"));
        }
        Assert.assertTrue(exceptionFlag);

    }
}
