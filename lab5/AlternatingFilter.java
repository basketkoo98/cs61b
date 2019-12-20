import java.util.Iterator;
import utils.Filter;

/** A kind of Filter that lets through every other VALUE element of
 *  its input sequence, starting with the first.
 *  @author Your Name
 */
class AlternatingFilter<Value> extends Filter<Value> {

    /**
     * A filter of values from INPUT that lets through every other
     * value.
     */
    private boolean toKeep;

    AlternatingFilter(Iterator<Value> input) {
        super(input); //FIXME?
        toKeep = true;// FIXME: REPLACE THIS LINE WITH YOUR CODE
    }

    @Override
    protected boolean keep() {// FIXME: REPLACE THIS LINE WITH YOUR CODE
        toKeep = !toKeep;
        return !toKeep;
        // FIXME: REPLACE THIS LINE WITH YOUR CODE

    }
}