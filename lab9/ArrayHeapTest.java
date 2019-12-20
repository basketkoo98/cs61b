import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class ArrayHeapTest {

    /** Basic test of adding, checking, and removing two elements from a heap */
    @Test
    public void simpleTest() {
        ArrayHeap<String> pq = new ArrayHeap<>();
        pq.insert("Tab", 2);
        pq.insert("Lut", 1);
        assertEquals(2, pq.size());

        String first = pq.removeMin();
        assertEquals("Lut", first);
        assertEquals(1, pq.size());

        String second = pq.removeMin();
        assertEquals("Tab", second);
        assertEquals(0, pq.size());

        ArrayHeap<String> q = new ArrayHeap<String>();
        q.insert("1",1);
        q.insert("17",17);
        q.insert("4", 4);
        q.insert("5",5);
        q.insert("9",9);
        q.insert("0",0);
        q.insert("-1",-1);
        q.insert("20",20);
        assertEquals(q.peek().item(),"-1");
        assertEquals(q.size(),8);
        assertEquals(q.removeMin(),"-1");
        assertEquals(q.size(),7);
        assertEquals(q.peek().item(),"0");
        q.changePriority("0",6);
        assertEquals(q.peek().item(),"1");
        assertEquals(q.removeMin(),"1");
        assertEquals(q.removeMin(),"4");
        assertEquals(q.removeMin(),"5");
        assertEquals(q.removeMin(),"0");
        assertEquals(q.removeMin(),"9");
        assertEquals(q.removeMin(),"17");
        assertEquals(q.removeMin(),"20");


    }

    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(ArrayHeapTest.class));
    }
}
