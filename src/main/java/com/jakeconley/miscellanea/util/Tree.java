package com.jakeconley.miscellanea.util;

import java.util.LinkedList;
import java.util.List;

public class Tree<T>
{
    public class Node<T>
    {
        private Node<T> Parent;
        private List<Node<T>> Children;
        private T Value;
        
        public Node<T> getParent(){ return Parent; }
        public List<Node<T>> getChildren(){ return Children; }
        public boolean addChild(T _Value)
        {
            return Children.add(new Node(_Value, this));
        }
        public boolean removeChild(Node<T> child){ return Children.remove(child); }
        
        public T getValue(){ return Value; }
        public void setValue(T _Value){ Value = _Value; }
        
        public Node(T _Value, Node<T> _Parent)
        {
            this.Parent = _Parent;
            this.Children = new LinkedList<>();
            this.Value = _Value;
        }
        public Node(T _Value){ this(_Value, null); }
    }

    private Node<T> Root;
    
    public Tree(T RootValue)
    {
        this.Root = new Node<>(RootValue);
    }
}
