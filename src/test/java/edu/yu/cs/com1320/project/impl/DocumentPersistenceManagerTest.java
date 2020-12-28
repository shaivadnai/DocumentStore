package edu.yu.cs.com1320.project.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.PersistenceManager;
import edu.yu.cs.com1320.project.stage5.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage5.impl.DocumentPersistenceManager;

public class DocumentPersistenceManagerTest {

    
    private File baseDir;
    
    @Before
    public void baseDir(){
        //if(File.separator.equals("/")){
            baseDir = new File("C:/Users/sv126/VadnaiCharles/DataStructures/project/stage5/src/test/jsontests");
        //}
        //else{
            baseDir = new File("C:\\Users\\sv126\\VadnaiCharles\\DataStructures\\project\\stage5/src\\test\\jsontests");
        //}
    }
    //

    @Test
    public void suppliedBasePathStandardURI() throws IOException {
        Document doc = new DocumentImpl(URI.create("http://Hello/World/587"), "Hello, World! 7","Hello, World 7".hashCode());
        Document doc3 = new DocumentImpl(URI.create("http://Hello/World/588"), "Hello, World! 8","Hello, World 8".hashCode());
        DocumentPersistenceManager io = new DocumentPersistenceManager(this.baseDir);
        io.serialize(doc.getKey(), doc);
        io.serialize(doc3.getKey(), doc3);
        Document doc2 = io.deserialize(URI.create("http://Hello/World/587"));
        Document doc4 = io.deserialize(URI.create("http://Hello/World/588"));
        URI uri1 = doc.getKey();
        URI uri2 = doc2.getKey();
        URI uri3 = doc3.getKey();
        URI uri4 = doc4.getKey();
        Map<String, Integer> map1 = doc.getWordMap();
        Map<String, Integer> map2 = doc2.getWordMap();
        Map<String, Integer> map3 = doc3.getWordMap();
        Map<String, Integer> map4 = doc4.getWordMap();
        assertTrue(uri1.equals(uri2));
        assertTrue(map1.equals(map2));
        assertTrue(uri3.equals(uri4));
        assertTrue(map3.equals(map4));
    }

    @Test
    public void suppliedBasePathNonStandardURI() throws IOException {
        Document doc = new DocumentImpl(URI.create("https://Hello/World/587"), "Hello, World!","Hello, World".hashCode());
        DocumentPersistenceManager io = new DocumentPersistenceManager(this.baseDir);
        io.serialize(doc.getKey(), doc);
        Document doc2 = io.deserialize(URI.create("https://Hello/World/587"));
        URI uri1 = doc.getKey();
        URI uri2 = doc2.getKey();
        Map<String, Integer> map1 = doc.getWordMap();
        Map<String, Integer> map2 = doc2.getWordMap();
        assertTrue(uri1.equals(uri2));
        assertTrue(map1.equals(map2));
    }

    @Test
    public void suppliedBasePathNonStandard2() throws IOException {
        Document doc = new DocumentImpl(URI.create("blahblah://Hello/World/587"), "Hello, World!",
                "Hello, World".hashCode());
        DocumentPersistenceManager io = new DocumentPersistenceManager(this.baseDir);
        io.serialize(doc.getKey(), doc);
        Document doc2 = io.deserialize(URI.create("blahblah://Hello/World/587"));
        URI uri1 = doc.getKey();
        URI uri2 = doc2.getKey();
        Map<String, Integer> map1 = doc.getWordMap();
        Map<String, Integer> map2 = doc2.getWordMap();
        assertTrue(uri1.equals(uri2));
        assertTrue(map1.equals(map2));
    }

    @Test
    public void nullBasePath() throws IOException {
        Document doc = new DocumentImpl(URI.create("blahblah://Hello/World/587"), "Hello, World!",
                "Hello, World".hashCode());
        DocumentPersistenceManager io = new DocumentPersistenceManager(null);
        io.serialize(doc.getKey(), doc);
        Document doc2 = io.deserialize(URI.create("blahblah://Hello/World/587"));
        URI uri1 = doc.getKey();
        URI uri2 = doc2.getKey();
        Map<String, Integer> map1 = doc.getWordMap();
        Map<String, Integer> map2 = doc2.getWordMap();
        assertTrue(uri1.equals(uri2));
        assertTrue(map1.equals(map2));
    }

    @Test
    public void testWordMapDeserialize() throws Exception {
        URI uri = new URI("http://edu.yu.cs/com1320/project/doc1");
        String txt = "This is the text of doc1, in plain text. No fancy file format - just plain old String. Computer. Headphones.";
        Document doc = new DocumentImpl(uri, txt, txt.hashCode());
        DocumentPersistenceManager pm = new DocumentPersistenceManager(null);
        Map<String,Integer> words = doc.getWordMap();
        pm.serialize(uri, doc);
        Document doc2 = pm.deserialize(uri);
        Map<String,Integer> words2 = doc2.getWordMap();
        assertEquals(words,words2);
        words2.put("hello", 5);
        pm.serialize(uri, doc2);
        Document doc3 = pm.deserialize(uri);
        assertNotEquals(doc3.getWordMap(),words);
    }

    @Test(expected = IOException.class)
    public void testIfPassEmptyPathToDeserialize() throws IOException {
        PersistenceManager<URI,Document> pm = new DocumentPersistenceManager(null);
        pm.deserialize(URI.create("str"));
    }    
}