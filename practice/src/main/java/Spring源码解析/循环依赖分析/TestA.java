package Spring源码解析.循环依赖分析;

public class TestA {

    private TestB testB;

    public void a() {
        testB.b();
    }

    public TestB getTestB() {
        return testB;
    }

    public void setTestB(TestB testB) {
        this.testB = testB;
    }
}
