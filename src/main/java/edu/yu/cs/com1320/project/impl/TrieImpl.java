package edu.yu.cs.com1320.project.impl;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.yu.cs.com1320.project.Trie;

public class TrieImpl<Value> implements Trie<Value> {


    private final Node<Value> root;
    //Val is a hashset

    public TrieImpl(){
        this.root = new Node<Value>();
    }

    /**
     * add the given value at the given key
     * @param key
     * @param val
     */
    @Override
    public void put(String key, Value val) {
        if(key == null || val == null){
            return;
        }
        else{
            iterateToNode(key.toUpperCase()).val.add(val);
        }

    }

    /**
     * get all exact matches for the given key, sorted in descending order.
     * Search is CASE INSENSITIVE.
     * @param key
     * @param comparator used to sort  values
     * @return a List of matching Values, in descending order
     */
    @Override
    public List<Value> getAllSorted(String key, Comparator<Value> comparator) {
        if(key==null || key.isEmpty()){
            return new ArrayList<>();
        }
        else{
            Node<Value> current = iterateToNode(key.toUpperCase());
            List<Value> list = new ArrayList<>(current.val);
            list.sort(comparator);
            return list;
        }
    }
    
    /**
     * get all matches which contain a String with the given prefix, sorted in descending order.
     * For example, if the key is "Too", you would return any value that contains "Tool", "Too", "Tooth", "Toodle", etc.
     * Search is CASE INSENSITIVE.
     * @param prefix
     * @param comparator used to sort values
     * @return a List of all matching Values containing the given prefix, in descending order
     */
    
    @Override
    public List<Value> getAllWithPrefixSorted(String prefix, Comparator<Value> comparator) {
        if(prefix==null || prefix.isEmpty()) {
            return new ArrayList<>();
        }
        else{
            Node<Value> current = iterateToNode(prefix.toUpperCase());
            Set<Value> set = collectAllValues(current, new HashSet<>());
            List<Value> list = new ArrayList<>(set);
            list.sort(comparator);
            return list;
        }
    }

    
    
    /**
     * Delete all matches that contain a String with the given prefix.
     * Search is CASE INSENSITIVE.
     * @param prefix
     * @return a Set of all Values that were deleted.
     */
    @Override
    public Set<Value> deleteAllWithPrefix(String prefix) {
        if(prefix == null || prefix.isEmpty()){
            return new HashSet<>();
        }
        else{
            Node<Value> current = iterateToNode(prefix.toUpperCase());
            Set<Value> old = collectAllValues(current, new HashSet<>());
            current.val.clear();
            Arrays.fill(current.links, null);
            boolean nodeHasLink = false;
            for(Node<Value> c : current.links){
                if(c!=null){
                    nodeHasLink = true;
                }
            }
            if(nodeHasLink==false && current.val.size()==0){
                Integer lastNodeWithValue = getLastValueNode(prefix.toUpperCase());
                deleteNodes(prefix, lastNodeWithValue);
            }
            return old;
        }
    }
    
    /**
     * delete ALL exact matches for the given key
     * @param key
     * @return a Set of all Values that were deleted.
     */
    @Override
    public Set<Value> deleteAll(String key) {
        if(key == null || key.isEmpty()){
            return new HashSet<>();
        }
        else{
            Node<Value> current = iterateToNode(key.toUpperCase());
            Set<Value> old = new HashSet<>(current.val);
            current.val.clear();
            boolean nodeHasLink = false;
            for(Node<Value> c : current.links){
                if(c!=null){
                    nodeHasLink = true;
                }
            }
            if(nodeHasLink==false && old.size()==0){
                Integer lastNodeWithValue = getLastValueNode(key.toUpperCase());
                deleteNodes(key, lastNodeWithValue);
            }
            return old;
        }
    }
    
    /**
     * delete ONLY the given value from the given key. Leave all other values.
     * @param key
     * @param val
     * @return if there was a Value already at that key, return that previous Value. Otherwise, return null.
     */
    @Override
    public Value delete(String key, Value val) {
        if(key==null || val==null || key.isEmpty()){
            return null;
        }
        else{
            Node<Value> current = iterateToNode(key.toUpperCase());
            if(current.val.contains(val)==false){
                return null;
            }
            else{
                current.val.remove(val);
                boolean nodeHasLink = false;
                for(Node<Value> c : current.links){
                    if(c!=null){
                        nodeHasLink = true;
                    }
                }
                if(current.val.size()==0 && nodeHasLink==false){
                    Integer lastNodeWithValue = getLastValueNode(key.toUpperCase());
                    deleteNodes(key, lastNodeWithValue);
                }
                return val;
            }
        }
    }

    private Node<Value> iterateToNode(String key){
        Node<Value> current = this.root;
        String upperCaseKey = key.toUpperCase();
        for(int i = 0; i<key.length();i++){
            if(current.links[upperCaseKey.charAt(i)] == null){
                current = current.links[upperCaseKey.charAt(i)] = new Node<Value>();
            }
            else{
                current = current.links[upperCaseKey.charAt(i)];
            }
        }
        return current;
    }

    private Integer getLastValueNode(String key){
        Node<Value> current = this.root;
        String upperCaseKey = key.toUpperCase();
        Integer lastValueNode = null;
        for(int i = 0; i<key.length()-1;i++){//check up to last last node
            if(current.val.size()>0 && !current.equals(this.root)){
                lastValueNode = i;
            }
            current = current.links[upperCaseKey.charAt(i)];
        }
        return lastValueNode;
    }

    private void deleteNodes(String key, Integer i){
        String upperCaserString = key.toUpperCase();
        if(i==null){
            i=0;
        }
        /*if(upperCaserString.length()-1==0 && i==0){
            Node<Value> current = iterateToNode(upperCaserString);
            
        }*/
        for(int j = upperCaserString.length()-1; j>=i; j--){
            String that = upperCaserString.substring(0, j+1);
            Node<Value> current = iterateToNode(that);
            current.links[upperCaserString.charAt(j)] = null;
        }
    }

    private Set<Value> collectAllValues(Node<Value> root, Set<Value> set){
        set.addAll(root.val);
        for (char i = 0; i < 256; i++){
            if(root.links[i]!=null){
                set.addAll(root.links[i].val);
                this.collectAllValues(root.links[i], set);
            }
        }
        return set;
    }
    
    @SuppressWarnings("unchecked")
    private class Node<Blank>{
        protected Set<Blank> val;
        protected Node<Blank>[] links;
        protected Node(){
            this.val = new HashSet<>();
            this.links = (Node[]) Array.newInstance(Node.class, 256);
        }
    }
    
}