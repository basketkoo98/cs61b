package arrays;

/* NOTE: The file Arrays/Utils.java contains some functions that may be useful
 * in testing your answers. */

/** HW #2 */

/** Array utilities.
 *  @author
 */
class Arrays {
    /* C. */
    /** Returns a new array consisting of the elements of A followed by the
     *  the elements of B. */
    static int[] catenate(int[] A, int[] B) {
        /* *Replace this body with the solution. */
        int [] result = new int [A.length + B.length];
        for (int i = 0;i<A.length;i++){
            result[i] = A[i];
        }
        for (int j = 0; j<B.length;j++){
            result[A.length+j] = B[j];
        }
        return result;
    }

    /** Returns the array formed by removing LEN items from A,
     *  beginning with item #START. */
    static int[] remove(int[] A, int start, int len) {
        /* *Replace this body with the solution. */
        int [] result = new int [A.length-len];
        int index, find;
        index = find = 0;
        while (find<A.length){
            if (find<start){
                result[index] = A[find];
                index++;
                find++;
            }else if (find >= start + len){
                result[index] = A[find];
                index++;
                find++;
            }else {
                find++;
            }
        }
        return result;
    }

    /* E. */
    /** Returns the array of arrays formed by breaking up A into
     *  maximal ascending lists, without reordering.
     *  For example, if A is {1, 3, 7, 5, 4, 6, 9, 10}, then
     *  returns the three-element array
     *  {{1, 3, 7}, {5}, {4, 6, 9, 10}}. */
    static int[][] naturalRuns(int[] A) {
        /* *Replace this body with the solution. */
        if (A.length == 1){
            int [][] result = {A};
            return result;
        }else if (A.length==0) {
            int [][]result = {};
            return result;
        }else{
                int [] begin = new int[A.length];
                int [] end = new int[A.length];
                int breaking = 1;
                begin[0] = 0;
                for (int i = 0;i<A.length-1;i++){
                    if (A[i]<A[i+1]){
                        end[breaking-1] = i+1;
                    }else {
                        breaking++;
                        begin[breaking-1] = i+1;
                        end[breaking-1]= i+1;
                    }
                }
                int [][] result = new int[breaking][];
                for (int i = 0;i<breaking;i++){
                    result[i] = new int[end[i]-begin[i]+1];
                    int k =0;
                    for (int j = begin[i];j<=end[i];j++){
                        result[i][k]=A[j];
                        k++;
                    }
                }
                return result;
            }
        }
}

