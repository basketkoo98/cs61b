import java.io.Reader;
import java.io.IOException;

/** Translating Reader: a stream that is a translation of an
 *  existing reader.
 *  @author Huixuan Lin
 */
public class TrReader extends Reader {
    /** A new TrReader that produces the stream of characters produced
     *  by STR, converting all characters that occur in FROM to the
     *  corresponding characters in TO.  That is, change occurrences of
     *  FROM.charAt(i) to TO.charAt(i), for all i, leaving other characters
     *  in STR unchanged.  FROM and TO must have the same length. */
    private String from;
    private String to;
    private Reader toRead;
    public TrReader(Reader str, String from, String to) {
        this.from = from;
        this.to = to;
        this.toRead = str;
    }

    public int read(char buf[], int offset, int count) throws IOException {
        int result = toRead.read(buf,offset,count);
        for (int index = 0; index < count; index++) {
            if (from.indexOf(buf[index]) != -1) {
                buf[index] = to.charAt(from.indexOf(buf[index]));
            }
        }
        return result;
    }

    public void close() throws IOException {
        toRead.close();
    }
    /* TODO: IMPLEMENT ANY MISSING ABSTRACT METHODS HERE
     * NOTE: Until you fill in the necessary methods, the compiler will
     *       reject this file, saying that you must declare TrReader
     *       abstract. Don't do that; define the right methods instead!
     */
}
