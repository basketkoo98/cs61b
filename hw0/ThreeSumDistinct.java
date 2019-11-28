public class ThreeSumDistinct{
    // public static void main(String[] args){
    //     int [] a = {-6, 3, 10, 200};
    //     System.out.println(threeSumDistinct(a));
    // }
    public static boolean threeSumDistinct(int[] args){
        int first = 0;
        int second = 1;
        int third = 2;
        if(args[first]+args[second]+args[third] != 0 ){
            while (first < args.length) {
                while (second < args.length){
                    while (third < args.length){
                        if(args[first]+args[second]+args[third] == 0 ){                            
                            return true;
                        }
                        third += 1;
                    }
                    second += 1;
                    third = second+1;
                }
                first += 1;
                second = first+1;
                third = second+1;
                
            }
        }else{
            return true;
        }
        return false;
    }
}