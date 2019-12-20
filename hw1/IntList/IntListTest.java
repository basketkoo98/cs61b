import static org.junit.Assert.*;

import afu.org.checkerframework.checker.igj.qual.I;
import org.junit.Test;
import sun.rmi.rmic.iiop.IDLNames;

public class IntListTest {

    /** Sample test that verifies correctness of the IntList.list static
     *  method. The main point of this is to convince you that
     *  assertEquals knows how to handle IntLists just fine.
     */

    @Test
    public void testList() {
        IntList one = new IntList(1, null);
        IntList twoOne = new IntList(2, one);
        IntList threeTwoOne = new IntList(3, twoOne);

        IntList x = IntList.list(3, 2, 1);
        assertEquals(threeTwoOne, x);
    }

    /** Do not use the new keyword in your tests. You can create
     *  lists using the handy IntList.list method.
     *
     *  Make sure to include test cases involving lists of various sizes
     *  on both sides of the operation. That includes the empty list, which
     *  can be instantiated, for example, with
     *  IntList empty = IntList.list().
     *
     *  Keep in mind that dcatenate(A, B) is NOT required to leave A untouched.
     *  Anything can happen to A.
     */

    @Test
    public void testDcatenate() {
        IntList shortList = IntList.list(1);
        IntList longList = IntList.list(2,3);
        assertEquals(IntList.list(1,2,3),IntList.dcatenate(shortList,longList));
        shortList = IntList.list(1);
        longList = IntList.list(2,3);
        assertEquals(IntList.list(2,3,1),IntList.dcatenate(longList,shortList));
        shortList = IntList.list(1);
        IntList empty = IntList.list();
        assertEquals(IntList.list(1),IntList.dcatenate(shortList,empty));
        longList = IntList.list(2,3);
        assertEquals(IntList.list(2,3),IntList.dcatenate(empty,longList));

    }

    /** Tests that subtail works properly. Again, don't use new.
     *
     *  Make sure to test that subtail does not modify the list.
     */

    @Test
    public void testSubtail() {
        IntList A = IntList.list(0,1,2,3,4);
        assertEquals(IntList.list(2,3,4),IntList.subTail(A,2));
        assertEquals(IntList.list(0,1,2,3,4),A);

    }

    /** Tests that sublist works properly. Again, don't use new.
     *
     *  Make sure to test that sublist does not modify the list.
     */

    @Test
    public void testSublist() {
        IntList A = IntList.list(1,2,3);
        IntList empty = IntList.list();
        IntList B = IntList.list(1,2,3,4,5,6,7);
        IntList C = IntList.list(1,2,3,4,5,6,7,8);
        IntList D = IntList.list(1,2,3,4,5,6);
        IntList result1 = IntList.subList(empty,0,0);
        IntList result2 = IntList.subList(A,0,3);
        IntList result3 = IntList.subList(D,0,3);
        IntList result4 = IntList.subList(B,4,3);
        IntList result5 = IntList.subList(C,4,3);
        IntList result6 = IntList.subList(D,0,3);
        assertEquals(IntList.list(),result1);
        assertEquals(IntList.list(1,2,3),result2);
        assertEquals(IntList.list(1,2,3),result3);
        assertEquals(IntList.list(5,6,7),result4);
        assertEquals(IntList.list(5,6,7),result5);
        assertEquals(IntList.list(1,2,3),result6);
//        assertEquals(IntList.list(0,1,2,3,4),A);
    }

    /** Tests that dSublist works properly. Again, don't use new.
     *
     *  As with testDcatenate, it is not safe to assume that list passed
     *  to dSublist is the same after any call to dSublist
     */

    @Test
    public void testDsublist() {
        IntList A = IntList.list(1,2,3);
        IntList empty = IntList.list();
        IntList result1 = IntList.dsublist(A,0,3);
        IntList result2 = IntList.dsublist(empty,0,0);
        assertEquals(IntList.list(1,2,3),result1);
        assertEquals(IntList.list(),result2);
    }






    /* Run the unit tests in this file. */
    public static void main(String... args) {
        System.exit(ucb.junit.textui.runClasses(IntListTest.class));
    }
}
