package our.company;

public class MathClass {

    @BeforeSuite
    @Test
    public int sum(int num1, int num2){
        return num1 + num2;
    }

    @Test(priority = 4)
    public int dif(int num1, int num2){
        return num1 - num2;
    }

    @Test(priority = 2)
    public int mul(int num1, int num2){
        return num1 * num2;
    }

    @AfterSuite
    @Test
    public int div(int num1, int num2){
        return num1 / num2;
    }

}
