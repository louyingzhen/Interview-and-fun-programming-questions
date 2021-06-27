package Guava;

import com.google.common.base.Strings;

public class Test4 {


    public static void main(String[] args) {
        System.out.println(Strings.isNullOrEmpty("")); // true
        System.out.println(Strings.isNullOrEmpty(null)); // true
        System.out.println(Strings.isNullOrEmpty("hello")); // false
        // 将null转化为""
        System.out.println(Strings.nullToEmpty(null)); // ""

        // 从尾部不断补充T只到总共8个字符，如果源字符串已经达到或操作，则原样返回。类似的有padStart
        System.out.println(Strings.padEnd("hello", 8, 'T')); // helloTTT

    }


}
