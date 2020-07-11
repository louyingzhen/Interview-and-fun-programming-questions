package 剑指offer.从头到尾打印链表;

import java.util.ArrayList;

public class Solution {

    public ArrayList<Integer> printListFromTailToHead(ListNode listNode) {
        if (listNode == null) return new ArrayList<Integer>();
        ListNode head = listNode;
        ListNode cur = listNode.next;
        while (cur != null) {
            ListNode temp = cur.next;
            cur.next = head;
            head = cur;
            cur = temp;
        }
        // 此时listNode的next还指向第二个node，所以要让listNode.next=null,防止循环
        listNode.next = null;
        ArrayList<Integer> res = new ArrayList<Integer>();
        while (head != null) {
            res.add(head.val);
            head = head.next;
        }
        return res;
    }
}
