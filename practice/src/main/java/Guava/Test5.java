package Guava;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Test5 {


    public static void main(String[] args) {
        List<String> arrayList = new ArrayList<>();
        arrayList.add("a");
        arrayList.add("b");
        List<String> jdkList = Collections.unmodifiableList(arrayList);
        ImmutableList<String> immutableList = ImmutableList.copyOf(arrayList);
        arrayList.add("ccc");
        jdkList.forEach(System.out::println);// result: a b ccc
        System.out.println("-------");
        immutableList.forEach(System.out::println);// result: a b
    }


}
