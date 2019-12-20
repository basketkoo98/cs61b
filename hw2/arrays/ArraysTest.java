package arrays;

import com.sun.tools.corba.se.idl.Util;
import com.sun.xml.internal.xsom.impl.scd.Iterators;
import org.junit.Test;
import static org.junit.Assert.*;

/** FIXME
 *  @author FIXME
 */

public class ArraysTest {
    /** FIXME
     */
    @Test
    public void testCatenate(){
        int [] A = {0,1,2,3,4};
        int[] B = {5,6,7,8};
        int []empty = {};
        int [] C = Arrays.catenate(A,B);
        int [] except = {0,1,2,3,4,5,6,7,8};
        assertArrayEquals(except,C);

    }
    @Test
    public void testRmove(){
        int [] A = {0,1,2,3,4};
        int [] except = {0,1};
        int [] D = Arrays.remove(A,2,3);
        assertArrayEquals(except,D);
    }

    @Test
    public void testNaturalRuns(){
        int []A = {1, 3, 7, 5, 4, 6, 9, 10};
        int [][]except={{1,3,7},{5},{4,6,9,10}};
        int [][] B = Arrays.naturalRuns(A);
        assertEquals(B,except);
    }



    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(ArraysTest.class));
    }
}
