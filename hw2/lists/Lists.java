package lists;

/* NOTE: The file Utils.java contains some functions that may be useful
 * in testing your answers. */

/** HW #2, Problem #1. */

import image.In;

import javax.swing.text.html.InlineView;

/** List problem.
 *  @author Lin Huixuan
 */
class Lists {
    /** Return the list of lists formed by breaking up L into "natural runs":
     *  that is, maximal strictly ascending sublists, in the same order as
     *  the original.  For example, if L is (1, 3, 7, 5, 4, 6, 9, 10, 10, 11),
     *  then result is the four-item list
     *            ((1, 3, 7), (5), (4, 6, 9, 10), (10, 11)).
     *  Destructive: creates no new IntList items, and may modify the
     *  original list pointed to by L. */
    static IntListList naturalRuns(IntList L) {
        /* *Replace this body with the solution. */
        IntListList last, next, nextListList, result;
        last =  IntListList.list(IntList.list());
        nextListList = IntListList.list(IntList.list());
        next = IntListList.list(L);
        result =IntListList.list(L);
        nextListList.tail = result;
        if (next.head == null || next.head.tail == null){
            return result;
        }else {
            last.head = next.head;
            next.head = next.head.tail;
        }
        while (next.head != null){
            if (last.head.head< next.head.head){
                last.head = next.head;
                next.head = next.head.tail;
            }else {
                nextListList.tail.tail = IntListList.list(next.head);
                nextListList.tail = nextListList.tail.tail;
                last.head.tail = null;
                last.head = next.head;
                next.head = next.head.tail;
            }
        }
        return result;



    }
}
