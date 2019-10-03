package 母牛问题;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class guocheng {

    public static void main(String[] args) {
        // 所有牛的集合
        List<Integer> cowAgeList = new ArrayList<Integer>();
        // 添加第一只5岁的大母牛
        cowAgeList.add(5);
        // 循环20年
        for (int i = 0; i < 20; i++) {
            int count_new = 0; // 当年新生数量
            int count_dead = 0; // 当年死亡数量
            // 当年新生的集合
            List<Integer> cowChild = new ArrayList<Integer>();
            // 遍历当年所有母牛，年龄自增，看看生不生
            for (int index = 0; index < cowAgeList.size(); index++) {
                // 新的一年，加一岁
                int age = cowAgeList.get(index) + 1;
                // 将新年龄设置回集合中
                cowAgeList.set(index, age);
                // 看下这头母牛是否该GameOver了
                if (age > 15) {
                    count_dead++;
                    continue;
                }
                // 试下生只小母牛把，生不生的出来说不准（看年龄）
                Integer cowNew = produce(age);
                // 如果生出来了，就放入新生牛犊集合中
                if (cowNew != null) {
                    count_new++;
                    // 为什么要先放到新生母牛集合中，因为当前年份的遍历还没结束，如果放入，当年生的新母牛年龄会+1，数据出错
                    cowChild.add(cowNew);
                }
            }
            // 在今年生牛过程结束后，把当前新生母牛集合放到所有牛的集合中
            cowAgeList.addAll(cowChild);
            System.out.println(String.format("第%d年，出生：%d,死亡：%s", i, count_new, count_dead));
        }
        // 用迭代器移除已经死亡的母牛，剩下的就是20年后所有存活的母牛了
        Iterator<Integer> iterator = cowAgeList.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().intValue() > 15) {
                iterator.remove();
            }
        }
        System.out.println(String.format("20年后农场存活牛的数量为：%d", cowAgeList.size()));
    }

    /**
     * 根据母牛年龄生产小母牛
     *
     * @param age-母牛年龄
     * @return 新生小牛年龄（null表示没有生）
     */
    private static Integer produce(int age) {
        if (age >= 5 && age <= 15) {
            return 0;
        }
        return null;
    }
}
