package edu.yu.cs.com1320.project.stage4.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import edu.yu.cs.com1320.project.impl.TrieImpl;


public class TrieImplTest{


    @Test
    @SuppressWarnings("unused")
    public void noArgsConstructor(){
        TrieImpl<String> test = new TrieImpl<>();
    }

    static String one = "A a A a A a A a A a";
    static String two ="A a A a A a A a A";
    static String three ="A a A a A a A a";
    static String four ="A a A a A a A";
    static String five ="A a A a A a";
    static String six ="A a A a A";
    static String seven ="A a A a";
    static String eight ="A a A";
    static String nine ="A a";
    static String ten ="A";


    @Test
    public void listAllTest() {
        TrieImpl<String> test = new TrieImpl<>();
        test.put("two",five);
        test.put("two",four);
        test.put("one",one);
        test.put("one",three);
        test.put("one",two);
        List<String> that = new ArrayList<>();
        Comparator<String> compare = new StringComparator<>();
        that.add(one);
        that.add(two);
        that.add(three);
        assertEquals(that, test.getAllSorted("one", compare));
    }

    @Test
    public void deleteTest() {
        TrieImpl<String> test = new TrieImpl<>();
        test.put("two",five);
        test.put("two",four);
        test.put("one",two);
        test.put("one",three);
        test.put("one",one);
        List<String> that = new ArrayList<>();
        assertEquals(three, test.delete("one", three));
        Comparator<String> compare = new StringComparator<>();
        that.add(one);
        that.add(two);
        assertEquals(that, test.getAllSorted("one", compare));
    }

    @Test
    public void deleteNull() {
        TrieImpl<String> test = new TrieImpl<>();
        test.put("two",five);
        test.put("two",four);
        test.put("one",two);
        test.put("one",three);
        test.put("one",one);
        List<String> that = new ArrayList<>();
        assertEquals(null, test.delete("one", "eight"));
        Comparator<String> compare = new StringComparator<>();
        that.add(one);
        that.add(two);
        that.add(three);
        assertEquals(that, test.getAllSorted("one", compare));
    }

    @Test
    public void deleteAllTest() {
        TrieImpl<String> test = new TrieImpl<>();
        test.put("two",five);
        test.put("two",four);
        test.put("one",one);
        test.put("one",three);
        test.put("one",two);
        //test.put("onel",two);
        test.put("on",two);
        Set<String> that = new HashSet<>();
        that.add(one);
        that.add(two);
        that.add(three);
        assertEquals(that, test.deleteAll("one"));
        List<String> list = new ArrayList<>();
        Comparator<String> compare = new StringComparator<>();
        assertEquals(list, test.getAllSorted("one", compare));
        list.add(four);
        list.add(five);
        assertEquals(list, test.getAllSorted("two", compare));
        List<String> list2 = new ArrayList<>();
        list2.add(two);
        //assertEquals(list2,test.getAllSorted("onel", null));
        assertEquals(list2,test.getAllSorted("on", null));
    }

    @Test
    public void listAllPrefixTest(){
        TrieImpl<String> test = new TrieImpl<>();
        test.put("one",one);
        test.put("one",two);
        test.put("one",three);
        test.put("ooo",four);
        test.put("ooooooo",five);
        test.put("oasda",six);
        test.put("obsao",seven);
        test.put("oscpef",one);//duplicate value with prefix
        test.put("adada",eight);
        test.put("oerdfjb",nine);
        test.put("cbhtfbj",ten);
        Comparator<String> compare = new StringComparator<String>();
        List<String> that = new ArrayList<>();
        that.add(one);
        that.add(two);
        that.add(three);
        that.add(four);
        that.add(five);
        that.add(six);
        that.add(seven);
        that.add(nine);
        assertEquals(that, test.getAllWithPrefixSorted("o", compare));
    }

    @Test
    public void nullKey(){
        TrieImpl<String> test = new TrieImpl<>();
        test.put(null, "15");
    }

    @Test
    public void emptySet(){
        TrieImpl<String> test = new TrieImpl<>();
        Comparator<String> compare = new StringComparator<>();
        Collection<String> col = new ArrayList<>();
        assertEquals(col,test.getAllSorted("0", compare));
        assertEquals(col,test.getAllWithPrefixSorted("0", compare));
    }

    @Test
    public void deleteAllWithPrefixTest(){
        TrieImpl<String> test = new TrieImpl<>();
        test.put("one",one);
        test.put("one",two);
        test.put("one",three);
        test.put("ooo",four);
        test.put("ooooooo",five);
        test.put("oasda",six);
        test.put("obsao",seven);
        test.put("adada",eight);
        test.put("oerdfjb",nine);
        test.put("cbhtfbj",ten);
        Set<String> expectedSet = new HashSet<>();
        expectedSet.add(four);
        expectedSet.add(five);
        assertEquals(expectedSet, test.deleteAllWithPrefix("oo"));
        Comparator<String> compare = new StringComparator<String>();
        List<String> that = new ArrayList<>();
        that.add(one);
        that.add(two);
        that.add(three);
        that.add(six);
        that.add(seven);
        that.add(nine);
        assertEquals(that, test.getAllWithPrefixSorted("o", compare));
    }

    @Test
    public void deleteAllWithPrefixTest2(){
        TrieImpl<String> test = new TrieImpl<>();
        test.put("one",one);
        test.put("one",two);
        test.put("one",three);
        test.put("ooo",four);
        test.put("ooooooo",five);
        test.put("oasda",six);
        test.put("obsao",seven);
        test.put("adada",eight);
        test.put("oerdfjb",nine);
        test.put("cbhtfbj",ten);
        Set<String> expectedSet = new HashSet<>();
        expectedSet.add(one);
        expectedSet.add(two);
        expectedSet.add(three);
        expectedSet.add(four);
        expectedSet.add(five);
        expectedSet.add(six);
        expectedSet.add(seven);
        expectedSet.add(nine);
        assertEquals(expectedSet, test.deleteAllWithPrefix("o"));
        List<String> list =  new ArrayList<>();
        list.add(eight);
        assertEquals(list, test.getAllWithPrefixSorted("a", null));
        System.out.println();
    }

    private class StringComparator<E> implements Comparator<String>{

        @Override
        public int compare(String o1, String o2) {
            char charachter = 'A';
            int o1count = 0;
            for(int i = 0;i<o1.length();i++){
                if(o1.toUpperCase().charAt(i)==charachter){
                    o1count++;
                }
            }
            
            int o2count = 0;
            for(int i = 0;i<o2.length();i++){
                if(o2.toUpperCase().charAt(i)==charachter){
                    o2count++;
                }
            }
            if(o1count<o2count){
                return 1;
            }
            else if(o1count==o2count) {
                return 0;
            }
            else{
                return -1;
            }
        }

    }

    private class PrefixSorter<E> implements Comparator<String>{
        private String prefix;
        private int string1count;
        private int string2count;
        private PrefixSorter (String prfx){
            this.prefix = prfx.toUpperCase();
            this.string1count=0;
            this.string2count=0;
        }


        @Override
        public int compare(String o1, String o2) {
            if(o1.length()>=this.prefix.length()){
                for(int i = 0; i<o1.length();i++){
                    if(o1.charAt(i)==this.prefix.charAt(i)){
                        if(o1.charAt(i+1)==this.prefix.charAt(i+1)){
                            this.string1count++;
                        }
                    }
                }
            }
            if(o2.length()>=this.prefix.length()){
                for(int i = 0; i<o2.length();i++){
                    if(o2.charAt(i)==this.prefix.charAt(i)){
                        if(o2.charAt(i+1)==this.prefix.charAt(i+1)){
                            this.string2count++;
                        }
                    }
                }
            }
            int firstArgument = this.string1count;
            int secondArgument = this.string2count;
            
            if(firstArgument<secondArgument){
                return 1;
            }
            else if(firstArgument==secondArgument) {
                return 0;
            }
            else{
                return -1;
            }

        }
    }

    @Test
    public void nullSearchOrDelete(){
        TrieImpl<String> test = new TrieImpl<>();
        test.put("this", "this");
        String string = null;
        Set<String> set = new HashSet<>();
        List<String> list = new ArrayList<>();
        assertEquals(set, test.deleteAll(null));
        assertEquals(set, test.deleteAllWithPrefix(null));
        assertEquals(list, test.getAllSorted(null, null));
        assertEquals(list, test.getAllWithPrefixSorted(null, null));
        assertEquals(string, test.delete(null, null));
        assertEquals(string, test.delete(null, "this"));
        assertEquals(string, test.delete("this", null));
    }

}