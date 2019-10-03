package 母牛问题;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** 面向对象编程的思路 */
public class duixiang {
    public static void main(String[] args) {
        Farm farm = new Farm();
        Cow cow = new Cow(5);
        farm.getCowList().add(cow);
        for (int year = 0; year < 20; year++) {
            int count_dead = 0; // 死亡数量
            List<Cow> cowChilds = new ArrayList<Cow>();
            for (Cow cowTemp : farm.getCowList()) {
                cowTemp.addAge();
                Cow child = cowTemp.produce();
                if (child != null) {
                    cowChilds.add(child);
                }
                if (!cowTemp.isAlive()) {
                    count_dead++;
                }
            }
            farm.getCowList().addAll(cowChilds);
            System.out.println(
                    String.format("第%d年，出生：%d,死亡：%d", year, cowChilds.size(), count_dead));
        }
        // 最后移除已经死亡的牛
        Iterator<Cow> iterator = farm.getCowList().iterator();
        while (iterator.hasNext()) {
            if (!iterator.next().isAlive()) {
                iterator.remove();
            }
        }
        System.out.println("20年后农场存活牛的数量为：" + farm.getCowList().size());
    }
}
