import java.util.HashSet;
import java.util.Iterator;
import utils.Filter;

/** A kind of Filter that lets all the VALUE elements of its input sequence
 *  that are larger than all the preceding values to go through the
 *  Filter.  So, if its input delivers (1, 2, 3, 3, 2, 1, 5), then it
 *  will produce (1, 2, 3, 5).
 *  @author You
 */
class MonotonicFilter<Value extends Comparable<Value>> extends Filter<Value> {

    /** A filter of values from INPUT that delivers a monotonic
     *  subsequence.  */
    private HashSet<Object> set;
    MonotonicFilter(Iterator<Value> input) {
        super(input); //FIXME?
        set = new HashSet<Object>();

    }

    @Override
    protected boolean keep() {
        if (set.contains(super._next) ){
            return false;
        } else {
            set.add(super._next);
            return true;
        }
         // FIXME: REPLACE THIS LINE WITH YOUR CODE
    }
    
    // FIXME: ADD ANY ADDITIONAL FIELDS REQUIRED HERE

}
