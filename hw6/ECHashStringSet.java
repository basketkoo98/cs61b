import java.rmi.activation.ActivationGroup_Stub;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/** A set of String values.
 *  @author
 */
class ECHashStringSet implements StringSet {
    private final double MIN_LOAD = 0.2;
    private final double MAX_LOAD = 5;

    ECHashStringSet() {
        _size = 0;
        _buckets = new LinkedList[((int)(1/MIN_LOAD))];
    }

    @Override
    public void put(String s) {
        if (load() > MAX_LOAD) {
            reSize();
        }
        int index = nonNegativeHashCode(s.hashCode());
        if (_buckets[index] == null) {
            _buckets[index] = new LinkedList<>();
        }
        _buckets[index].add(s);
        _size++;
        
    }

    private int nonNegativeHashCode(int hashCode) {
        return hashCode & 0x7fffffff % _buckets.length;
    }
    private double load() {
        return (double)_size / (double)_buckets.length;
    }

    private void reSize() {
        LinkedList<String>[] oldBuckets = _buckets;
        _buckets = new LinkedList[oldBuckets.length * 2];
        _size = 0;
        for (LinkedList<String> bucket : oldBuckets) {
            if (bucket != null) {
                for (String items: bucket) {
                    put(items);
                }
            }
        }
    }
    @Override
    public boolean contains(String s) {
        if (s != null) {
            int index = nonNegativeHashCode(s.hashCode());
            if (_buckets[index] != null) {
                if (_buckets[index].contains(s)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<String> asList() {
        ArrayList<String> result = new ArrayList<>();
        for (int index = 0; index < _buckets.length; index++) {
            if (_buckets[index] != null) {
                result.addAll(_buckets[index]);
            }
        }
        return result;
    }

    /** the number of items */
    private int _size;

    /** buckets to store items */
    private LinkedList<String>[] _buckets;
}
