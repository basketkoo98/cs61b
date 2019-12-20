import afu.org.checkerframework.checker.igj.qual.I;

import java.util.Formatter;

/** Scheme-like pairs that can be used to form a list of integers.
 *  @author P. N. Hilfinger, with some modifications by Josh Hug
 */
public class IntList {
    /** First element of list. */
    public int head;
    /** Remaining elements of list. */
    public IntList tail;

    /** A List with head HEAD0 and tail TAIL0. */
    public IntList(int head0, IntList tail0) {
        head = head0;
        tail = tail0;
    }

    /** A List with null tail, and head = 0. */
    public IntList() {
        /* NOTE: public IntList () { }  would also work. */
        this (0, null);
    }

    /* YOU DO NOT NEED TO LOOK AT ANY CODE BELOW THIS LINE UNTIL
       YOU GET TO THE PROBLEMS YOU NEED TO SOLVE. Search for '2a'
       and you'll be where you need to go. */


    /** Returns a new IntList containing the ints in ARGS. */
    public static IntList list(Integer ... args) {
        IntList result, p;

        if (args.length > 0) {
            result = new IntList(args[0], null);
        } else {
            return null;
        }

        int k;
        for (k = 1, p = result; k < args.length; k += 1, p = p.tail) {
            p.tail = new IntList(args[k], null);
        }
        return result;
    }

    /** Returns true iff X is an IntList containing the same sequence of ints
     *  as THIS. Cannot handle IntLists with cycles. */
    @Override
    public boolean equals(Object x) {
        if (!(x instanceof IntList)) {
            return false;
        }
        IntList L = (IntList) x;
        IntList p;

        for (p = this; p != null && L != null; p = p.tail, L = L.tail) {
            if (p.head != L.head) {
                return false;
            }
        }
        if (p != null || L != null) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return head;
    }


    /** If a cycle exists in the IntList headed by A,
     *  return an integer equal to the item number of the
     *  location where the cycle is detected (i.e. the smallest item
     *  number of an item that is the same as a preceding one.  If
     *  there is no cycle, return 0.
     */
    private int detectCycles(IntList A) {
        IntList tortoise = A;
        IntList hare = A;

        if (A == null) {
            return 0;
        }

        int cnt = 0;


        while (true) {
            cnt++;
            if (hare.tail != null) {
                hare = hare.tail.tail;
            } else {
                return 0;
            }

            tortoise = tortoise.tail;

            if (tortoise == null || hare == null) {
                return 0;
            }

            if (hare == tortoise) {
                return cnt;
            }
        }
    }

    @Override
    public String toString() {
        Formatter out = new Formatter();
        String sep;
        sep = "(";
        int cycleLocation = detectCycles(this);
        int cnt = 0;

        for (IntList p = this; p != null; p = p.tail) {
            out.format("%s%d", sep, p.head);
            sep = ", ";

            cnt++;
            if ((cnt > cycleLocation) && (cycleLocation > 0)) {
                out.format("... (cycle exists) ...");
                break;
            }
        }
        out.format(")");
        return out.toString();
    }


    /* DO NOT MODIFY ANYTHING ABOVE THIS LINE! */


    /* 2a. */
    /** Returns a list consisting of the elements of A followed by the
     *  elements of B.  May modify items of A. Don't use 'new'. */

    static IntList dcatenate(IntList A, IntList B) {
        IntList result = IntList.list();
        if (A == null){
            result = B;
        }else{
            result = A;
            while (A.tail != null){
                A = A.tail;
            }
            A.tail = B;}
        return result; // REPLACE WITH YOUR CODE */

    }

    /* 2b. */
    /** Returns a list consisting of the elements of L starting from
      * position START, and going all the way to the end. The head of the
      * list L is the 0th element of the list.
      * This method should NOT modify the items in L. */

    static IntList subTail(IntList L, int start) {
        IntList result = L;
        for (int now = 0; now < start; now++){
            result = result.tail;}
        return result; // REPLACE WITH YOUR CODE
    }




    /* 2c. */
    /** Returns the sublist consisting of LEN items from list L,
     *  beginning with item #START (where the first item is #0).
     *  Does not modify the original list elements.
     *
     *  If the desired items don't exist, or the program
     *  receives negative START or LEN parameters, the behavior
     *  of this function is undefined, i.e. you can assume
     *  that start and len are always >= 0.
     */

      static IntList subList(IntList L, int start, int len) {
          IntList result, last;
          if (len == 0){
              return null;
          }else {
              for (int now = 0; now < start; now++) {
                  L = L.tail;
              }

              result = last = null;
              for (int now = 0; now < len; now++) {
                  if (last == null) {
                      result = last = new IntList(L.head, null);

                  } else {
                      last = last.tail = new IntList(L.head, null);
                  }
                  L = L.tail;
              }
          }
          return result;// REPLACE WITH YOUR SOLUTION

    }

    /* 2d. */
    /** Returns the sublist consisting of LEN items from list L,
     *  beginning with item #START (where the first item is #0).
     *  May modify the original list elements. Don't use 'new'
     *  or the sublist method.
     *  As with sublist, you can assume the items requested
     *  exist, and that START and LEN are >= 0. */

    static IntList dsublist(IntList L, int start, int len) {
        if (len == 0 ){
            return null;
        }else {
            int index = 0;
            IntList next = L;
            while (index < start + len - 1) {
                if (index == start) {
                    L = next;
                }
                next = next.tail;
                index += 1;
            }
            next.tail = null;
            return L;
        }
    }



}
