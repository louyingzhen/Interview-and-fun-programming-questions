package 母牛问题;

import lombok.Data;

// 牛对象
@Data
public class Cow {

    /** 年龄 */
    private int age;
    /** 是否活着 */
    private boolean alive = true;

    public Cow(int age) {
        this.age = age;
    }

    /** 生牛动作，是否能生的出来看年龄 */
    public Cow produce() {
        if (this.age < 5 || !this.isAlive()) {
            return null;
        }
        return new Cow(0);
    }
    /** 每年长一岁 */
    public void addAge() {
        this.age += 1;
        // 年龄大于15就挂掉
        if (this.age > 15) {
            this.alive = false;
        }
    }
}
