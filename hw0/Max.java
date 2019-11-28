public class Max{
    public static int max(int[] args){
        int maxNumber = args[0];
        for (int compare : args) {
            while (compare > maxNumber){
                maxNumber = compare;
            }
        }
        return maxNumber;
    }

}