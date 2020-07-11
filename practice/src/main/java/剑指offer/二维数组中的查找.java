package 剑指offer;

public class 二维数组中的查找 {

    public static void main(String[] args) {
        int a = 66;
        int[][] arr = {
            {22, 15, 32, 20, 18}, {12, 21, 25, 19, 33}, {14, 58, 34, 24, 66},
        };
        boolean result = find(a, arr);
        System.out.println("result:" + result);
    }

    public static boolean find(int target, int[][] array) {
        if (array.length == 0 || array[0].length == 0) return false;
        int m = array[0].length - 1;
        int n = 0;
        int temp = array[n][m];
        while (target != temp) {
            if (m > 0 && n < array.length - 1) {
                if (target > temp) {
                    n = n + 1;
                } else if (target < temp) {
                    m = m - 1;
                }
                temp = array[n][m];
            } else {
                return false;
            }
        }
        return true;
    }
}
