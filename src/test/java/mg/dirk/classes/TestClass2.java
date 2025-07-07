package mg.dirk.classes;

public class TestClass2 {
    private int something;
    private TestClass1 class1;

    public TestClass1 getClass1() {
        return class1;
    }

    public int getSomething() {
        return something;
    }

    public void setClass1(TestClass1 class1) {
        this.class1 = class1;
    }

    public void setSomething(int something) {
        this.something = something;
    }

    public TestClass2() {
    }

    public TestClass2(int something, TestClass1 class1) {
        this.setSomething(something);
        this.setClass1(class1);
    }

}
