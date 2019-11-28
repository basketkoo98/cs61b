package enigma;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Input pattern class.
 *  @author Huixuan Lin
 */
public class InputPattern {
    /** The pattern for alphabet. */
    public static final Pattern ALPHABET = Pattern.compile("[^()*\\s]+");

    /** The pattern for rotor's name. */
    public static final Pattern ROTORNAME = Pattern.compile("[^()\\s]+");

    /** The pattern for rotor description. */
    public static final Pattern ROTORTYPE
            = Pattern.compile("[NR]|[M][^()*\\s]+");

    /** Returns true if the string S matches pattern P. */
    public static boolean checkMatch(Pattern p, String s) {
        Matcher mat = p.matcher(s);
        return mat.matches();
    }
}
