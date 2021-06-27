package Guava;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Test6 {

    public static void main(String[] args) {
        List<String> list1 = Lists.newArrayList();

        List<String> list2 = Lists.newArrayList("a","b","c");

        List<String> list3 = Lists.newArrayListWithCapacity(10);

        LinkedList<String> linkedList1 = Lists.newLinkedList();

        CopyOnWriteArrayList<String> copyOnWriteArrayList = Lists.newCopyOnWriteArrayList();

        HashMap<Object,Object> hashMap = Maps.newHashMap();

        ConcurrentMap<Object,Object> concurrentNavigableMap = Maps.newConcurrentMap();

        TreeMap<Comparable, Object> treeMap = Maps.newTreeMap();

        HashSet<Object> hashSet = Sets.newHashSet();
        HashSet<String> newHashSet = Sets.newHashSet("a", "a", "b", "c");
    }


}
