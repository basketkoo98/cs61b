/** An accumulator of ints.  This object is given a sequence of
 *  int values via its accum method.  It performs some processing on
 *  this sequence, and returns a result (or partial result) when
 *  its result() method is called. */
abstract class Accum {

    /** Return the accumulated result. */
    abstract int result();

    /** Process and accumulate the value X. */
    abstract void accum(int x);

    /** Return the result of accumulating all of the values in vals. */
    int reduce(int[] vals) {
        for (int x : vals) 
            accum (x);
        return result ();
    }
}

public class Quiz2 {
    /** The sum VALS'[0] + VALS'[1] + ... where VALS'[i] is the 
     *  minimum of VALS[i] and MAX. */
    public static int clippedSum(int[] vals, int max) {
        Accum acc = new AccumInt(vals,max); /* REPLACE WITH ANSWER */;
        return acc.reduce (vals);
    }
}

class AccumInt extends Accum {
    private int result = 0;
    private int minimum;

    AccumInt(int[] vals, int max) {
        minimum = max;
    }

    int result() {
        return result;
    }

    void accum(int x) {
        result += x;
    }

    int reduce(int[] vals) {
        for (int x : vals)
            if (x <= minimum) {
                accum(x);
            }
        return result();
    }
}
// Add any other classes you need here:
