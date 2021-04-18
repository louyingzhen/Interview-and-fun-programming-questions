package Guava;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import java.util.ArrayList;

public class Test2 {

    public static void main(String[] args) {
        /*String param = "www.wdbyte.com2";
        String wdbyte = "www.wdbyte.com";
        Preconditions.checkArgument(wdbyte.equals(param), "[%s] 404 NOT FOUND", param);*/

        // 创建方式1：of
        ImmutableSet<String> immutableSet = ImmutableSet.of("a", "b", "c");
        immutableSet.forEach(System.out::println);
// a
// b
// c

// 创建方式2：builder
        ImmutableSet<String> immutableSet2 = ImmutableSet.<String>builder()
                .add("hello")
                .add(new String("未读代码"))
                .build();
        immutableSet2.forEach(System.out::println);
// hello
// 未读代码

// 创建方式3：从其他集合中拷贝创建
        ArrayList<String> arrayList = new ArrayList();
        arrayList.add("www.wdbyte.com");
        arrayList.add("https");
        ImmutableSet<String> immutableSet3 = ImmutableSet.copyOf(arrayList);
        immutableSet3.forEach(System.out::println);
// www.wdbyte.com
// https

    }
}
