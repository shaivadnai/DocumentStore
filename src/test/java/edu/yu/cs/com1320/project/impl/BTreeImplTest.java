package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.BTree;
import edu.yu.cs.com1320.project.impl.BTreeImpl;
import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage5.impl.DocumentPersistenceManager;
import edu.yu.cs.com1320.project.stage5.PersistenceManager;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.net.URI;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class BTreeImplTest {

    private BTreeImpl<String, String> btree;

    @Before
    public void initbtree() {
        this.btree = new BTreeImpl<>();
        this.btree.put("Key1", "Value1");
        this.btree.put("Key2", "Value2");
        this.btree.put("Key3", "Value3");
        this.btree.put("Key4", "Value4");
        this.btree.put("Key5", "Value5");
        this.btree.put("Key6", "Value6");
    }

    @Test
    public void testNeedForSentinel(){
        BTree<Integer,Integer> that = new BTreeImpl<Integer,Integer>();
        that.put(5,5);
        that.put(6,6);
        that.put(7,7);
        that.put(8,8);
        that.put(1,1);
        assertEquals(that.get(6), new Integer("6"));
        assertEquals(that.get(1), new Integer("1"));
    }

    @Test
    public void testGet() {
        assertEquals("Value1", this.btree.get("Key1"));
        assertEquals("Value2", this.btree.get("Key2"));
        assertEquals("Value3", this.btree.get("Key3"));
        assertEquals("Value4", this.btree.get("Key4"));
        assertEquals("Value5", this.btree.get("Key5"));
    }

    @Test
    public void testGetChained() {
        // second node in chain
        assertEquals("Value6", this.btree.get("Key6"));
        // second node in chain after being modified
        this.btree.put("Key6", "Value6+1");
        assertEquals("Value6+1", this.btree.get("Key6"));
        // check that other values still come back correctly
        testGet();
    }

    @Test
    public void testGetMiss() {
        assertEquals(null, this.btree.get("Key20"));
    }

    @Test
    public void testPutReturnValue() {
        assertEquals("Value3", this.btree.put("Key3", "Value3+1"));
        assertEquals("Value6", this.btree.put("Key6", "Value6+1"));
        assertEquals(null, this.btree.put("Key7", "Value7"));
    }

    @Test
    public void testGetChangedValue() {
        BTreeImpl<String, String> btree = new BTreeImpl<>();
        String key1 = "hello";
        String value1 = "how are you today?";
        String value2 = "HI!!!";
        btree.put(key1, value1);
        assertEquals(value1, btree.get(key1));
        btree.put(key1, value2);
        assertEquals(value2, btree.get(key1));
    }

    @Test
    public void testDeleteViaPutNull() {
        BTreeImpl<URI, Document> btree = new BTreeImpl<>();
        btree.setPersistenceManager(new DocumentPersistenceManager(null));
        DocumentImpl doc1 = new DocumentImpl(URI.create("http://www.yu.edu/shai/is/awesome/1"), "1", "1".hashCode());
        URI key1 = URI.create("http://www.yu.edu/shai/is/awesome/1");
        Document value1 = doc1;
        Document value2 = null;
        btree.put(key1, value1);
        btree.put(key1, value2);
        assertEquals(value2, btree.get(key1));
    }

    @Test
    public void testSeparateChaining() {
        BTreeImpl<Integer, String> btree = new BTreeImpl<>();
        for (int i = 0; i <= 23; i++) {
            btree.put(i, "entry " + i);
        }
        assertEquals("entry 12", btree.put(12, "entry 12v2"));
        assertEquals("entry 12v2", btree.get(12));
        assertEquals("entry 23", btree.get(23));
    }

    @Test
    public void stage2TestArrayDoubling() {
        testSeparateChaining();
    }

    @Test
    @SuppressWarnings("parameterized")
    public void stage2TestNoArgsConstructorExists() {
        try {
            new BTreeImpl<String, Double>();
        } catch (RuntimeException e) {
        }
    }

    @Test
    public void writeToDiskTest() throws Exception {
        File baseDir = new File("C:/Users/sv126/VadnaiCharles/DataStructures/project/stage5/src/test/jsontests");
        BTreeImpl<URI, Document> btree = new BTreeImpl<>();
        PersistenceManager<URI, Document> io = new DocumentPersistenceManager(baseDir);
        btree.setPersistenceManager(io);
        DocumentImpl doc1 = new DocumentImpl(URI.create("http://www.yu.edu/shai/is/awesome/1"), "1", "1".hashCode());
        DocumentImpl doc2 = new DocumentImpl(URI.create("http://www.yu.edu/shai/is/awesome/2"), "2", "2".hashCode());
        DocumentImpl doc3 = new DocumentImpl(URI.create("http://www.yu.edu/shai/is/awesome/3"), "3", "3".hashCode());
        DocumentImpl doc4 = new DocumentImpl(URI.create("http://www.yu.edu/shai/is/awesome/4"), "4", "4".hashCode());
        DocumentImpl doc5 = new DocumentImpl(URI.create("http://www.yu.edu/shai/is/awesome/5"), "5", "5".hashCode());
        btree.put(doc1.getKey(), doc1);
        btree.put(doc2.getKey(), doc2);
        btree.put(doc3.getKey(), doc3);
        btree.put(doc4.getKey(), doc4);
        btree.put(doc5.getKey(), doc5);
        btree.moveToDisk(doc1.getKey());
        Document deserializedDoc = io.deserialize(doc1.getKey());
        assertEquals(doc1.getKey(), deserializedDoc.getKey());

    }

    @Test
    public void interfaceCount() {//tests that the class only implements one interface and its the correct one
        @SuppressWarnings("rawtypes")
        Class[] classes = BTreeImpl.class.getInterfaces();
        assertTrue(classes.length == 1);
        assertTrue(classes[0].getName().equals("edu.yu.cs.com1320.project.BTree"));
    }

    @Test
    public void methodCount() {//need only test for non constructors
        Method[] methods = BTreeImpl.class.getDeclaredMethods();
        int publicMethodCount = 0;
        for (Method method : methods) {
            if (Modifier.isPublic(method.getModifiers())) {
                if (!method.getName().equals("equals") && !method.getName().equals("hashCode")) {
                    publicMethodCount++;
                }
            }
        }
        assertTrue(publicMethodCount == 4);
    }
}