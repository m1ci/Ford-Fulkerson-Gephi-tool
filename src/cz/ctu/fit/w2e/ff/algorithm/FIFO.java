/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.ctu.fit.w2e.ff.algorithm;

import java.util.LinkedList;

/**
 *
 * @author Milan Dojčinovski - <dojcinovski.milan (at) gmail.com> - @m1ci 
 */
public class FIFO {
    
    LinkedList<Integer> fifo = new LinkedList<Integer>();
    
    public void add(int a){
        fifo.add(a);
    
    }
    
    public int get(){
        return fifo.removeFirst();
    }
    
    public int size(){
        return fifo.size();
    }
    
    public void test(){
        this.fifo.add(1);
        this.fifo.add(2);
        this.fifo.add(3);
        System.out.println(this.get());
        System.out.println(this.get());
        System.out.println(this.get());
    }
    
    public boolean isEmpty(){
        return fifo.isEmpty();
    }    
}