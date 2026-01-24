
public class Main {

//    int fibo(int n) {
//        if (n < 2) return n;
//        return fibo(n - 1) + fibo(n - 2);
//    }

// 0, 1, 2, 3, 5, 8

    // UUID v7
    //  booking - metadata

    // dealerMetadata -





    public static void main(String[] args) {

        int[][] cache = new int[100][2];
        int[] test = fibonacci(5, cache);
        System.out.printf(test[0] + ", " + test[1]);
    }

    static int[] fibonacci(int n, int[][] cache) {
        if(n == 0 || n == 1) return new int[]{n, 0};
        if(cache[n][0] != 0) {
            return cache[n];
            //curVal[1]  = curVal[1];
        };
        int[] val1 = fibonacci(n-1, cache) ;
        int[] val2 = fibonacci(n-2, cache) ;
        int[] val3= new int[]{val1[0]+val2[0], 2 + val1[1] + val2[1]};
        cache[n] = val3;
        return val3;
    }
}