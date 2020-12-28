package edu.yu.cs.com1320.project.impl;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import edu.yu.cs.com1320.project.MinHeap;

public class MinHeapImpl<E extends Comparable> extends MinHeap<E> {


    public MinHeapImpl(){
        super.elements = (E[])Array.newInstance(Comparable.class, 10);
        super.count = 0;
        super.elementsToArrayIndex = new HashMap<>();
    }

    @Override
    protected int getArrayIndex(E element) {
        populateElementsToArrayIndex();
        Integer index = this.elementsToArrayIndex.get(element);
        if(index==null){
            return -1;
        }
        else{
            return index;
        }
    }

    @Override
    protected void doubleArraySize() {
        super.elements = Arrays.copyOf(super.elements, super.elements.length*2);
    }

    @Override
    public void reHeapify(E element) {
        populateElementsToArrayIndex();
        int index = getArrayIndex(element);
        for (index = (this.count / 2); index >= 1; index--) { 
            reHeapify(index);
        } 
        populateElementsToArrayIndex();
    }

    private void reHeapify(int index){
        if(isLeaf(index)==false){
            if((this.elements[index*2]!=null&&this.elements[index*2+1]!=null) && (this.elements[index].compareTo(this.elements[index*2])==1 || this.elements[index].compareTo(this.elements[index*2+1])==1)){
                if(this.elements[2*index].compareTo(this.elements[2*index+1])==-1){
                    swap(index, index*2);
                    reHeapify(index*2);
                }
                else{
                    swap(index,index*2+1);
                    reHeapify(index*2+1);
                }
            }
            else{
                if(this.elements[index].compareTo(this.elements[index*2])==1){
                    swap(index,index*2);
                    reHeapify(index*2);
                }
            }
        }
    }

    private boolean isLeaf(int index){
        if(index >(this.count/2) && index<=this.count){
            return true;
        }
        else{
            return false;
        }
    }

    private void populateElementsToArrayIndex(){
        Map<E,Integer> newIndex = new HashMap<>();
        for(int i = 1;i<=count;i++){
            if(this.elements[i]==null){//when reheapified, should be all to left. first null will be end of count. Should never hit this due to count accuracy
                break;
            }
            else{
                newIndex.put(this.elements[i],i);
            }
        }
        this.elementsToArrayIndex = newIndex;
    }

    protected Object[] getElements(){
        return super.elements;
    }

    public void insert(E x)
    {
        // double size of array if necessary
        if (this.count >= this.elements.length - 1)
        {
            this.doubleArraySize();
        }
        //add x to the bottom of the heap
        this.elements[++this.count] = x;
        //percolate it up to maintain heap order property
        this.upHeap(this.count);
    }

    public E removeMin()
    {
        if (isEmpty())
        {
            throw new NoSuchElementException("Heap is empty");
        }
        E min = this.elements[1];
        //swap root with last, decrement count
        this.swap(1, this.count--);
        //move new root down as needed
        this.downHeap(1);
        this.elements[this.count + 1] = null; //null it to prepare for GC
        return min;
    }
}