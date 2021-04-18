package 算法和数学;

/** 心得（如果代码中出现了三重循环，是可以有方法优化的） */
public class 解决1分2分5分硬币的组合问题 {

    /**
     * 题目：
     *
     * <p>1分2分5分的硬币，组成1角，共有多少种组合，如果是组成1块，又有多少种组合
     */

    // 常规方法,穷举获取所有组合情况,得到数目
    public static void main(String[] args) {
        int sum = 100;
        int count = 0;
        for (int i = 0; i <= sum / 2; i++) {
            for (int j = 0; j <= sum / 4; j++) {
                for (int k = 0; k <= sum / 6; k++) {
                    if (i * 2 + j * 4 + k * 6 == sum) {
                        System.out.println("2分:" + i + ",4分:" + j + ",6分" + k);
                        count++;
                    }
                }
            }
        }

        System.out.println("总方法数：" + count);

        // 数学思维：本题仅要求获取所有组合数目，并不需要列出所有可能
        /**
         * 如果这个和越大，那相应的复杂度会很高。所以 因此，本题目组合总数为10以内的偶数+5以内的奇数+0以内的偶数， 为什么会是10以内的偶数+5以内的奇数+0以内的偶数呢？
         * 由于题目中的条件其实可以转化为方程式 x+2y+5z = 10 也就是 5z = 10 - x -2y 那么 z = 0 时 x可以取的值为 0，2，4，6，8，10
         * （10以内的偶数）因为y前面有系数2 所以不能取奇数，而且此时不用在意y的数目，因为 xz确定，y必然确定 z = 1 时，x可以取的值为 1，3，5 （5以内的奇数） z =
         * 2 时，x可以取的值为0，（0以内的偶数）
         *
         * <p>某个偶数m以内的偶数个数（包括0）可以表示为m/2+1=(m+2)/2 某个奇数m以内的奇数个数也可以表示为(m+2)/2
         *
         * <p>所以，求总的组合次数可以编程为：
         */
        int number = 0;

        // 10为需要取的总数，5为步长，即每次的跨度
        for (int m = 0; m <= 10; m += 5) {
            number += (m + 2) / 2;
        }
        System.out.println("总方法数：" + number);

        // 练习 2分，4分，6分的硬币 取100，有多少种取法
        /** 2x+4y+6z = 100 x+2y+3z = 50 3z = 50 - x - 2y */
        int number2 = 0;
        for (int m = 0; m <= 50; m += 3) {
            number2 += (m + 2) / 2;
        }
        System.out.println("总方法数：" + number2);
    }
}
