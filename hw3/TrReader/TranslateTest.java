import org.junit.Test;
import ucb.junit.textui;

import java.io.StringReader;

import static org.junit.Assert.*;

public class TranslateTest {

    @Test
    public void TestTranslate() {
       String str = Translate.translate("hello, I am the autograder", "hIthr", "HiTHR");
//       String str2 = Translate.translate("","","");
       String except = "Hello, i am THe auTogRadeR";
       assertEquals(str,except);
        TrReader r1 = new TrReader(new StringReader("azbzczdz"), "edcab", "EDCAB");

    }
    public static void main(String[] args) {
        System.exit(textui.runClasses(TrReaderTest.class));
    }
}
