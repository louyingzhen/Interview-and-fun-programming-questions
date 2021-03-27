package Spring源码解析.循环依赖分析;

public class TestB {

    private TestC testC;


    public void b(){
        testC.c();
    }

    public TestC getTestC(){
        return testC;
    }

    public void setTestC(TestC testC){
        this.testC = testC;
    }
}
