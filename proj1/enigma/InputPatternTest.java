package enigma;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import static org.junit.Assert.*;

/** The suite of all JUnit tests for the InputPattern class.
 *  @author Huixuan Lin
 */
public class InputPatternTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTS ***** */

    @Test
    public void testAlphabet() {
        String good1 = "ABCDE";
        String good2 = ".-$abc";
        String good3 = "123456789";
        String bad1 = "(abc)";
        String bad2 = "ab c";
        String bad3 = "123*456";
        assertTrue(InputPattern.checkMatch(InputPattern.ALPHABET, good1));
        assertTrue(InputPattern.checkMatch(InputPattern.ALPHABET, good2));
        assertTrue(InputPattern.checkMatch(InputPattern.ALPHABET, good3));
        assertFalse(InputPattern.checkMatch(InputPattern.ALPHABET, bad1));
        assertFalse(InputPattern.checkMatch(InputPattern.ALPHABET, bad2));
        assertFalse(InputPattern.checkMatch(InputPattern.ALPHABET, bad3));
    }

    @Test
    public void testRotorName() {
        String good1 = "ABCDE";
        String good2 = ".-$abc";
        String good3 = "123456789";
        String good4 = "123*456";
        String bad1 = "(abc)";
        String bad2 = "ab c";
        assertTrue(InputPattern.checkMatch(InputPattern.ROTORNAME, good1));
        assertTrue(InputPattern.checkMatch(InputPattern.ROTORNAME, good2));
        assertTrue(InputPattern.checkMatch(InputPattern.ROTORNAME, good3));
        assertFalse(InputPattern.checkMatch(InputPattern.ROTORNAME, bad1));
        assertFalse(InputPattern.checkMatch(InputPattern.ROTORNAME, bad2));
        assertTrue(InputPattern.checkMatch(InputPattern.ROTORNAME, good4));
    }

    @Test
    public void testRotorType() {
        String good1 = "N";
        String good2 = "R";
        String good3 = "Ma9";
        String bad1 = "Nd";
        String bad2 = "Ra";
        String bad3 = "Ma 9";
        String bad4 = "M(a9)";
        String bad5 = "Ma*";
        assertTrue(InputPattern.checkMatch(InputPattern.ROTORTYPE, good1));
        assertTrue(InputPattern.checkMatch(InputPattern.ROTORTYPE, good2));
        assertTrue(InputPattern.checkMatch(InputPattern.ROTORTYPE, good3));
        assertFalse(InputPattern.checkMatch(InputPattern.ROTORTYPE, bad1));
        assertFalse(InputPattern.checkMatch(InputPattern.ROTORTYPE, bad2));
        assertFalse(InputPattern.checkMatch(InputPattern.ROTORTYPE, bad3));
        assertFalse(InputPattern.checkMatch(InputPattern.ROTORTYPE, bad4));
        assertFalse(InputPattern.checkMatch(InputPattern.ROTORTYPE, bad5));
    }
}
