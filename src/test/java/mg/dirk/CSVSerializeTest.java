package mg.dirk;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

import mg.dirk.classes.TestClass1;
import mg.dirk.classes.TestClass2;
import mg.dirk.csv.CSVUtils;

public class CSVSerializeTest {
    @Test
    public void testSerialization1() throws Exception {
        CSVUtils.saveToFile(Arrays.asList(new TestClass1("sdasdsa", 0, new Date())), "target/test-class1.csv");
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testSerialization2() throws Exception {
        CSVUtils.saveToFile(Arrays.asList(new TestClass2(2, new TestClass1("sdsadasd", 12, new Date(2024, 2, 15)))),
                "target/test-class2.csv");
    }

    @Test
    public void testDeserialization1() throws Exception {
        List<TestClass1> class1s = CSVUtils.deserializeFile(TestClass1.class, "target/test-class1.csv");
        assertEquals(class1s.size(), 1);
    }

    @Test
    public void testDeserialization2() throws Exception {
        List<TestClass2> class2s = CSVUtils.deserializeFile(TestClass2.class, "target/test-class2.csv");
        assertEquals(class2s.size(), 1);
    }
}
