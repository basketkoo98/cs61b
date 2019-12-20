public class ThreeSum{
    public static boolean threeSum(int[] args){
        int first = 0;
        int second = 0;
        int third = 0;
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
                    third = second;
                }
                first += 1;
                second = first;
                third = second;
                
            }
        }else{
            return true;
        }
        return false;
    }
}