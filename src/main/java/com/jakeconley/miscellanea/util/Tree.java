package com.jakeconley.miscellanea.util;

import java.util.LinkedList;
import java.util.List;

public class Tree<T>
{
    public static class Node<T>
    {
        private Node<T> Parent;
        private List<Node<T>> Children;
        private T Value;
        
        public Node<T> getParent(){ return Parent; }
        public List<Node<T>> getChildren(){ return Children; }
        public Node<T> addChild(T _Value)
        {
            Node<T> ret = new Node(_Value, this);
            Children.add(ret);
            return ret;
        }
        public boolean removeChild(Node<T> child){ return Children.remove(child); }
        public void setChildren(List<Node<T>> children){ Children = children; }
        
        public T getValue(){ return Value; }
        public void setValue(T _Value){ Value = _Value; }
        
        public Node(T _Value, Node<T> _Parent)
        {
            this.Parent = _Parent;
            this.Children = new LinkedList<>();
            this.Value = _Value;
        }
        public Node(T _Value){ this(_Value, null); }
        
        @Override
        public String toString(){ return (Value != null ? Value.toString() : null); }
        
        private void print(String indent, boolean last)
        {
            String printable = indent;
            if(last)
            {
                printable += "\\-";
                indent += "  ";
            }
            else
            {
                printable += "|-";
                indent += "| ";
            }
            System.out.println(printable + this.toString());
            
            for(int i = 0; i < Children.size(); i++) Children.get(i).print(indent, i == Children.size() - 1);
        }
        public void print(){ print("", false); }
    }

    private Node<T> Root;
    public Node<T> getRoot(){ return Root; }
    
    public Tree(T RootValue)
    {
        this.Root = new Node<>(RootValue);
    }
    
    public List<Node<T>> getLowestNodes(Node<T> node)
    {
        List<Node<T>> res = new LinkedList<>();
        
        for(Node<T> child : node.getChildren())
        {
            if(child.getChildren().isEmpty()) res.add(child);
            else getLowestNodes(child);
        }
        
        return res;
    }
    public List<Node<T>> getLowestNodes(){ return getLowestNodes(Root); }
    public void print(){ Root.print(); }
}
