package Guava;


import com.google.common.base.Preconditions;

public class UseOption {

    public static void main(String[] args) {
        String param = "未读代码";
        String name = Preconditions.checkNotNull(param);
        System.out.println(name); // 未读代码
        String param2 = null;
        String name2 = Preconditions.checkNotNull(param2,"lyz自定义的空指针报错信息"); // NullPointerException
        System.out.println(name2);
    }
}
