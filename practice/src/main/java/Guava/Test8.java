package Guava;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test8 {
    public static void main(String[] args) {
        // Java 统计相同元素出现的次数。
        List<String> words = Lists.newArrayList("a", "b", "c", "d", "a", "c");
        Map<String, Integer> countMap = new HashMap<String, Integer>();
        for (String word : words) {
            Integer count = countMap.get(word);
            count = (count == null) ? 1 : ++count;
            countMap.put(word, count);
        }
        countMap.forEach((k, v) -> System.out.println(k + ":" + v));
        /**
         * result:
         * a:2
         * b:1
         * c:2
         * d:1
         */

        // guava精简之后
        ArrayList<String> arrayList = Lists.newArrayList("a", "b", "c", "d", "a", "c");
        HashMultiset<String> multiset = HashMultiset.create(arrayList);
        multiset.elementSet().forEach(s -> System.out.println(s + ":" + multiset.count(s)));
        

    }

}
