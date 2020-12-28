package edu.yu.cs.com1320.project.impl;

import java.lang.reflect.Array;

import edu.yu.cs.com1320.project.Stack;
import edu.yu.cs.com1320.project.Undoable;

public class StackImpl<T> implements Stack<T>{

    //constructor
    private T[] commands;
    private int top;
    @SuppressWarnings("unchecked")
    public StackImpl(){
        this.commands = (T[]) Array.newInstance(Undoable.class, 5);
        this.top = -1;
    }
    

    /**
     * @param element object to add to the Stack
     */
    @Override
    public void push(T element) {
        if(size() == this.commands.length){
            this.stackDoubling();
        }
        this.top++;
        this.commands[this.top] = element;
    }
    
    @SuppressWarnings("unchecked")
    private void stackDoubling(){
        T[] newStack = (T[]) Array.newInstance(Undoable.class, this.commands.length*2);
        //Command[] temp = new Command[this.commands.length*2];
        for(int i = 0; i<=this.top; i++){
            newStack[i] = this.commands[i];
        }
        this.commands = newStack;
    }

    /**
     * removes and returns element at the top of the stack
     * @return element at the top of the stack, null if the stack is empty
     */
    @Override
    public T pop() {
        if(this.top == -1){
            return null;
        }
        T command = commands[top];
        commands[top] = null;
        top--;
        return command;
                
    }

    /**
     *
     * @return the element at the top of the stack without removing it
     */
    @Override
    public T peek(){
        if(this.top == -1){
            return null;
        }
        return this.commands[top];
        
    }

    /**
     *
     * @return how many elements are currently in the stack
     */
    @Override
    public int size(){
        return this.top+1;
    }

}