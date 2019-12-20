import java.util.Iterator;
import utils.Predicate;
import utils.Filter;

/** A kind of Filter that tests the elements of its input sequence of
 *  VALUES by applying a Predicate object to them.
 *  @author You
 */
class PredicateFilter<Value> extends Filter<Value> {

    /**
     * A filter of values from INPUT that tests them with PRED,
     * delivering only those for which PRED is true.
     */
    private Predicate<Value> predict;

    PredicateFilter(Predicate<Value> pred, Iterator<Value> input) {
        super(input);//FIXME ??
        predict = pred;// FIXME: REPLACE THIS LINE WITH YOUR CODE
    }


    @Override
    protected boolean keep() {
        // FIXME: REPLACE THIS LINE WITH YOUR CODE
        if (predict.test(super._next)) {
            return true;
        } else {
            return false;
        }
        // FIXME: REPLACE THIS LINE WITH YOUR CODE

    }
}
