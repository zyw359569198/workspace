package com.reign.util.characterFilter.PatternAC;

import com.reign.util.characterFilter.*;
import java.util.*;

public class TrieTree
{
    private Node root;
    
    public TrieTree() {
        this.root = new Node('*');
    }
    
    public void addPattern(final String str) {
        Node node = this.root;
        for (int i = 0; i < str.length(); ++i) {
            final char content = str.charAt(i);
            if (!node.isContains(content)) {
                node.addNext(new Node(content));
            }
            node = node.getNext(content);
        }
        node.setEndChar(str.length());
    }
    
    public void setFailNode() {
        final Deque<Node> queue = new LinkedList<Node>();
        queue.push(this.root);
        while (!queue.isEmpty()) {
            final Node node = queue.pop();
            for (final Node nextNode : node.getNext()) {
                if (node == this.root) {
                    nextNode.setFail(this.root);
                }
                else {
                    Node tempNode;
                    for (tempNode = node.getFail(); tempNode != null; tempNode = tempNode.getFail()) {
                        if (tempNode.isContains(nextNode.getContent())) {
                            nextNode.setFail(tempNode.getNext(nextNode.getContent()));
                            break;
                        }
                    }
                    if (tempNode == null) {
                        nextNode.setFail(this.root);
                    }
                }
                queue.push(nextNode);
            }
        }
    }
    
    public boolean isContrainsPattern(final String str) {
        Node node = this.root;
        for (int i = 0; i < str.length(); ++i) {
            char content;
            for (content = str.charAt(i); !node.isContains(content) && node != this.root; node = node.getFail()) {}
            node = node.getNext(content);
            if (node == null) {
                node = this.root;
            }
            for (Node temp = node; temp != this.root; temp = temp.getFail()) {
                if (temp.isEndChar()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public String filterStr(final String str, final IReplaceCharacterGetter replaceCharacterGetter) {
        final List<Interval> intervalList = new ArrayList<Interval>();
        Node node = this.root;
        for (int i = 0; i < str.length(); ++i) {
            char content;
            for (content = str.charAt(i); !node.isContains(content) && node != this.root; node = node.getFail()) {}
            node = node.getNext(content);
            if (node == null) {
                node = this.root;
            }
            Node temp = node;
            while (temp != this.root) {
                if (temp.isEndChar()) {
                    intervalList.add(new Interval(i - temp.getStrLen() + 1, i));
                }
                temp = temp.getFail();
                if (temp == null) {
                    System.out.println("pimp:" + node.getContent());
                }
            }
        }
        if (intervalList.size() == 0) {
            return str;
        }
        final char[] rtn = str.toCharArray();
        for (final Interval interval : intervalList) {
            for (int j = interval.getFrom(); j <= interval.getTo(); ++j) {
                rtn[j] = replaceCharacterGetter.getChar();
            }
        }
        return new String(rtn);
    }
}
