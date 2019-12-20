import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;

/**
 * Test of a BST-based String Set.
 * @author 
 */
public class ECHashStringSetTest  {


    @Test
    public void testECHash() {
        ECHashStringSet test = new ECHashStringSet();
        test.put("a");
        test.put("b");
        test.put("c");
        test.put("d");
        test.put("e");
        test.put("f");
        assertTrue(test.contains("d"));
        assertFalse(test.contains("g"));

    }
}
