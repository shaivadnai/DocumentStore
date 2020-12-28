package edu.yu.cs.com1320.project.stage5.impl;

import edu.yu.cs.com1320.project.Utils;
import edu.yu.cs.com1320.project.impl.MinHeapImpl;
import edu.yu.cs.com1320.project.stage5.DocumentStore;
import edu.yu.cs.com1320.project.stage5.PersistenceManager;
import edu.yu.cs.com1320.project.stage5.DocumentStore.DocumentFormat;
import edu.yu.cs.com1320.project.stage5.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage5.impl.DocumentStoreImpl;
import edu.yu.cs.com1320.project.stage5.Document;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.DocumentType;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.print.attribute.standard.DocumentName;

import static org.junit.Assert.*;

public class DocumentStoreImplTest {

    //variables to hold possible values for doc1
    private URI uri1;
    private String txt1;
    private byte[] pdfData1;
    private String pdfTxt1;

    //variables to hold possible values for doc2
    private URI uri2;
    private String txt2;
    private byte[] pdfData2;
    private String pdfTxt2;

    private File file;


    @Before
    public void init() throws Exception {
        //init possible values for doc1
        this.uri1 = new URI("http://edu.yu.cs/com1320/project/doc1");
        this.txt1 = "This this is the text of doc1, in plain text. No fancy file format - just plain old String";
        this.pdfTxt1 = "This is some PDF text for doc1, hat tip to Adobe.";
        this.pdfData1 = Utils.textToPdfData(this.pdfTxt1);

        //init possible values for doc2
        this.uri2 = new URI("http://edu.yu.cs/com1320/project/doc2");
        this.txt2 = "Text this for doc2. A plain old String.";
        this.pdfTxt2 = "PDF content for doc2: PDF format was opened in 2008.";
        this.pdfData2 = Utils.textToPdfData(this.pdfTxt2);

        this.file = new File("C:/Users/sv126/VadnaiCharles/DataStructures/project/stage5/src/test/jsontests");

    }

    @Test
    public void testPutPdfDocumentNoPreviousDocAtURI(){
        DocumentStoreImpl store = new DocumentStoreImpl(file);
        int returned = store.putDocument(new ByteArrayInputStream(this.pdfData1),this.uri1, DocumentStoreImpl.DocumentFormat.PDF);
        DocumentImpl doc = new DocumentImpl(this.uri1,this.pdfTxt1, this.pdfTxt1.hashCode(), this.pdfData1);
        assertTrue(returned == 0);
        byte[] expected = doc.getDocumentAsPdf();
        assertTrue(store.getDocument(this.uri1).getLastUseTime()>0);
        byte[] actual = store.searchPDFs("PDF").get(0);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testPutTxtDocumentNoPreviousDocAtURI(){
        DocumentStoreImpl store = new DocumentStoreImpl(file);
        int returned = store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStoreImpl.DocumentFormat.TXT);
        DocumentImpl doc = new DocumentImpl(this.uri1,this.txt1,this.txt1.hashCode());
        assertTrue(returned == 0);
        assertTrue(store.getDocument(this.uri1).getLastUseTime()>0);
        assertEquals(doc.getDocumentAsTxt().trim(), store.search("doc1").get(0));
    }

    @Test
    public void testPutDocumentWithNullArguments(){
        DocumentStoreImpl store = new DocumentStoreImpl(this.file);
        try {
            store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()), null, DocumentStoreImpl.DocumentFormat.TXT);
            fail("null URI should've thrown IllegalArgumentException");
        }catch(IllegalArgumentException e){}
        try {
            store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, null);
            fail("null format should've thrown IllegalArgumentException");
        }catch(IllegalArgumentException e){}
    }

    @Test
    public void testPutNewVersionOfDocumentPdf(){
        //put the first version
        DocumentStoreImpl store = new DocumentStoreImpl(file);
        int returned = store.putDocument(new ByteArrayInputStream(this.pdfData1),this.uri1, DocumentStoreImpl.DocumentFormat.PDF);
        assertTrue(returned == 0);
        List<String> list = new ArrayList<>();
        list.add(this.pdfTxt1);
        assertEquals("failed to return correct pdf text",this.pdfTxt1,Utils.pdfDataToText(store.getDocumentAsPdf(this.uri1)));
        assertEquals(this.pdfTxt1,store.search("Adobe").get(0));
        assertEquals(list, store.search("Adobe"));

        //put the second version, testing both return value of put and see if it gets the correct text
        returned = store.putDocument(new ByteArrayInputStream(this.pdfData2),this.uri1, DocumentStoreImpl.DocumentFormat.PDF);
        assertTrue("should return hashcode of old text",this.pdfTxt1.hashCode() == returned);
        assertEquals("failed to return correct pdf text", this.pdfTxt2,Utils.pdfDataToText(store.getDocumentAsPdf(this.uri1)));
        assertEquals(this.pdfTxt2,store.search("PDF").get(0));
        assertEquals(1,store.search("PDF").size());
        assertEquals(0,store.search("Adobe").size());
    }

    @Test
    public void testPutNewVersionOfDocumentTxt(){
        //put the first version
        DocumentStoreImpl store = new DocumentStoreImpl(file);
        int returned = store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStoreImpl.DocumentFormat.TXT);
        Document doc = store.getDocument(this.uri1);
        long putTime = doc.getLastUseTime();
        assertTrue(putTime!=0);
        assertTrue(returned == 0);
        assertEquals("failed to return correct text",this.txt1,store.getDocumentAsTxt(this.uri1));
        long getTxtTime = doc.getLastUseTime();
        assertTrue(getTxtTime>putTime);
        assertEquals(this.txt1,store.search("text").get(0));
        assertEquals(this.txt1,store.search("doc1").get(0));
        assertTrue(doc.getLastUseTime()>getTxtTime);

        //put the second version, testing both return value of put and see if it gets the correct text
        returned = store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri1, DocumentStoreImpl.DocumentFormat.TXT);
        Document doc2 = store.getDocument(this.uri1);
        putTime = doc2.getLastUseTime();
        assertTrue("should return hashcode of old text",this.txt1.hashCode() == returned);
        assertEquals("failed to return correct text",this.txt2,store.getDocumentAsTxt(this.uri1));
        getTxtTime = doc2.getLastUseTime();
        assertTrue(getTxtTime>putTime);
        assertEquals(this.txt2,store.search("text").get(0));
        assertEquals(1,store.search("text").size());
        assertTrue(0==store.search("doc1").size());
        assertTrue(doc2.getLastUseTime()>getTxtTime);
    }

    @Test
    public void testGetTxtDocAsPdf(){
        DocumentStoreImpl store = new DocumentStoreImpl(file);
        int returned = store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStoreImpl.DocumentFormat.TXT);
        Document doc = store.getDocument(this.uri1);
        long putTime = doc.getLastUseTime();
        assertTrue(returned == 0);
        assertEquals("failed to return correct pdf text",this.txt1,Utils.pdfDataToText(store.getDocumentAsPdf(this.uri1)));
        assertTrue(doc.getLastUseTime()>putTime);
    }

    @Test
    public void testGetTxtDocAsTxt(){
        DocumentStoreImpl store = new DocumentStoreImpl(file);
        int returned = store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStoreImpl.DocumentFormat.TXT);
        Document doc = store.getDocument(this.uri1);
        long putTime = doc.getLastUseTime();
        assertTrue(returned == 0);
        assertEquals("failed to return correct text",this.txt1,store.getDocumentAsTxt(this.uri1));
        assertTrue(doc.getLastUseTime()>putTime);
    }

    @Test
    public void testGetPdfDocAsPdf(){
        DocumentStoreImpl store = new DocumentStoreImpl(file);
        int returned = store.putDocument(new ByteArrayInputStream(this.pdfData1),this.uri1, DocumentStoreImpl.DocumentFormat.PDF);
        Document doc = store.getDocument(this.uri1);
        long putTime = doc.getLastUseTime();
        assertTrue(returned == 0);
        assertEquals("failed to return correct pdf text",this.pdfTxt1,Utils.pdfDataToText(store.getDocumentAsPdf(this.uri1)));
        assertTrue(doc.getLastUseTime()>putTime);
    }

    @Test
    public void testGetPdfDocAsTxt(){
        DocumentStoreImpl store = new DocumentStoreImpl(file);
        int returned = store.putDocument(new ByteArrayInputStream(this.pdfData1),this.uri1, DocumentStoreImpl.DocumentFormat.PDF);
        Document doc = store.getDocument(this.uri1);
        long putTime = doc.getLastUseTime();
        assertTrue(returned == 0);
        assertEquals("failed to return correct text",this.pdfTxt1,store.getDocumentAsTxt(this.uri1));
        assertTrue(doc.getLastUseTime()>putTime);
    }

    @Test
    public void testDeleteDoc(){
        DocumentStoreImpl store = new DocumentStoreImpl(file);
        store.putDocument(new ByteArrayInputStream(this.pdfData1),this.uri1, DocumentStoreImpl.DocumentFormat.PDF);
        store.deleteDocument(this.uri1);
        assertEquals("calling get on URI from which doc was deleted should've returned null", null, store.getDocumentAsPdf(this.uri1));
        assertEquals(0,store.search("pdf").size());
        assertEquals(new ArrayList<String>(),store.search("PDF"));
        store.deleteDocument(this.uri1);
        store.deleteDocument(this.uri2);
    }

    @Test
    public void testDeleteDocReturnValue(){
        DocumentStoreImpl store = new DocumentStoreImpl(file);
        store.putDocument(new ByteArrayInputStream(this.pdfData1),this.uri1, DocumentStoreImpl.DocumentFormat.PDF);
        //should return true when deleting a document
        assertEquals("failed to return true when deleting a document",true,store.deleteDocument(this.uri1));
        //should return false if I try to delete the same doc again
        assertEquals("failed to return false when trying to delete that which was already deleted",false,store.deleteDocument(this.uri1));
        //should return false if I try to delete something that was never there to begin with
        assertEquals("failed to return false when trying to delete that which was never there to begin with",false,store.deleteDocument(this.uri2));
    }

    @Test
    public void testSearchByPrefix(){
        DocumentStoreImpl store = new DocumentStoreImpl(file);
        store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStoreImpl.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStoreImpl.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream("this is very interesting thello this text that has the most tprefixes".getBytes()), URI.create("uri3"), DocumentFormat.TXT);
        List<String> list = store.searchByPrefix("t");
        assertEquals(this.txt1,list.get(1));
        assertEquals(this.txt2,list.get(2));
        assertEquals("this is very interesting thello this text that has the most tprefixes",list.get(0));
        store.deleteDocument(this.uri1);
        store.deleteDocument(this.uri2);
    }

    @Test
    public void testSearchByPrefixPdf(){
        DocumentStoreImpl store = new DocumentStoreImpl(file);
        store.putDocument(new ByteArrayInputStream(this.pdfData1), this.uri1, DocumentStoreImpl.DocumentFormat.PDF);
        store.putDocument(new ByteArrayInputStream(this.pdfData2), this.uri2, DocumentStoreImpl.DocumentFormat.PDF);
        List<byte[]> list = store.searchPDFsByPrefix("p");
        assertArrayEquals(this.pdfData1,(list.get(1)));
        assertArrayEquals(this.pdfData2,(list.get(0)));
        store.deleteDocument(this.uri1);
        store.deleteDocument(this.uri2);
    }

    @Test
    public void testDeleteAll(){
        DocumentStoreImpl store = new DocumentStoreImpl(file);
        store.putDocument(new ByteArrayInputStream(this.pdfData1), this.uri1, DocumentStoreImpl.DocumentFormat.PDF);
        store.putDocument(new ByteArrayInputStream(this.pdfData2), this.uri2, DocumentStoreImpl.DocumentFormat.PDF);
        store.deleteAll("PDF");
        assertEquals(new ArrayList<>(), store.search("for"));
        assertEquals(null, store.getDocumentAsTxt(this.uri1));
        assertEquals(null, store.getDocumentAsTxt(this.uri2));
        store.undo();
        assertEquals(this.pdfTxt1, store.getDocumentAsTxt(this.uri1));
        List<String> list = new ArrayList<>();
        list.add(this.pdfTxt2);
        list.add(this.pdfTxt1);
        assertEquals(list, store.search("pdf"));
        store.deleteDocument(this.uri1);
        store.deleteDocument(this.uri2);
    }

    @Test
    public void testDeleteAllUndoSpecific(){
        DocumentStoreImpl store = new DocumentStoreImpl(file);
        //store.deleteDocument(this.uri1);
        //store.deleteDocument(this.uri2);
        store.putDocument(new ByteArrayInputStream(this.pdfData1), this.uri1, DocumentStoreImpl.DocumentFormat.PDF);
        store.putDocument(new ByteArrayInputStream(this.pdfData2), this.uri2, DocumentStoreImpl.DocumentFormat.PDF);
        store.deleteAll("PDF");
        assertEquals(new ArrayList<>(), store.search("for"));
        String doc1 = store.getDocumentAsTxt(this.uri1);
        assertEquals(null, doc1);
        assertEquals(null, store.getDocumentAsTxt(this.uri2));
        store.undo(this.uri1);
        assertEquals(this.pdfTxt1, store.getDocumentAsTxt(this.uri1));
        List<String> list = new ArrayList<>();
        list.add(this.pdfTxt1);
        assertEquals(list, store.search("for"));
    }

    @Test
    public void testDeleteAllByPrefix(){
        DocumentStoreImpl store = new DocumentStoreImpl(file);
        store.putDocument(new ByteArrayInputStream(this.pdfData1), this.uri1, DocumentStoreImpl.DocumentFormat.PDF);
        store.putDocument(new ByteArrayInputStream(this.pdfData2), this.uri2, DocumentStoreImpl.DocumentFormat.PDF);
        store.deleteAllWithPrefix("PD");
        assertEquals(new ArrayList<>(), store.search("for"));
        assertEquals(null, store.getDocumentAsTxt(this.uri1));
        assertEquals(null, store.getDocumentAsTxt(this.uri2));
        store.undo();
        assertEquals(this.pdfTxt1, store.getDocumentAsTxt(this.uri1));
        List<String> list = new ArrayList<>();
        list.add(this.pdfTxt2);
        list.add(this.pdfTxt1);
        assertEquals(list, store.searchByPrefix("PD"));
        store.deleteDocument(this.uri1);
        store.deleteDocument(this.uri2);
    }

    @Test
    public void testDeleteAllByPrefixUndoSpecific(){
        DocumentStoreImpl store = new DocumentStoreImpl(file);
        store.deleteDocument(this.uri1);
        store.deleteDocument(this.uri2);
        store.putDocument(new ByteArrayInputStream(this.pdfData1), this.uri1, DocumentStoreImpl.DocumentFormat.PDF);
        store.putDocument(new ByteArrayInputStream(this.pdfData2), this.uri2, DocumentStoreImpl.DocumentFormat.PDF);
        store.deleteAllWithPrefix("PD");
        assertEquals(new ArrayList<>(), store.search("for"));
        assertEquals(null, store.getDocumentAsTxt(this.uri1));
        assertEquals(null, store.getDocumentAsTxt(this.uri2));
        store.undo(this.uri2);
        assertEquals(this.pdfTxt2, store.getDocumentAsTxt(this.uri2));
        List<String> list = new ArrayList<>();
        list.add(this.pdfTxt2);
        assertEquals(list, store.searchByPrefix("PD"));
        store.undo(this.uri1);
        List<String> listtwo = new ArrayList<>();
        listtwo.add(this.pdfTxt1);
        assertEquals(listtwo,store.searchByPrefix("doc1"));
        store.undo(this.uri1);
        assertEquals(null, store.getDocumentAsTxt(this.uri1));
    }

    @Test
    (expected = IllegalStateException.class)
    public void testDeleteAllByPrefixUndoSpecificTillException(){
        DocumentStoreImpl store = new DocumentStoreImpl(file);
        store.putDocument(new ByteArrayInputStream(this.pdfData1), this.uri1, DocumentStoreImpl.DocumentFormat.PDF);
        store.putDocument(new ByteArrayInputStream(this.pdfData2), this.uri2, DocumentStoreImpl.DocumentFormat.PDF);
        store.deleteAllWithPrefix("PD");
        assertEquals(new ArrayList<>(), store.search("for"));
        assertEquals(null, store.getDocumentAsTxt(this.uri1));
        assertEquals(null, store.getDocumentAsTxt(this.uri2));
        store.undo(this.uri2);
        assertEquals(this.pdfTxt2, store.getDocumentAsTxt(this.uri2));
        List<String> list = new ArrayList<>();
        list.add(this.pdfTxt2);
        assertEquals(list, store.searchByPrefix("PD"));
        store.undo(this.uri1);
        List<String> listtwo = new ArrayList<>();
        listtwo.add(this.pdfTxt1);
        assertEquals(listtwo,store.searchByPrefix("doc1"));
        store.undo(this.uri1);
        assertEquals(null, store.getDocumentAsTxt(this.uri1));
        store.undo(this.uri1);
    }

    @Test
    (expected = IllegalStateException.class)
    public void testDeleteAllByPrefixUndoGeneralTillException(){
        DocumentStoreImpl store = new DocumentStoreImpl(file);
        store.putDocument(new ByteArrayInputStream(this.pdfData1), this.uri1, DocumentStoreImpl.DocumentFormat.PDF);
        store.putDocument(new ByteArrayInputStream(this.pdfData2), this.uri2, DocumentStoreImpl.DocumentFormat.PDF);
        store.deleteAllWithPrefix("PD");
        assertEquals(new ArrayList<>(), store.search("for"));
        assertEquals(null, store.getDocumentAsTxt(this.uri1));
        assertEquals(null, store.getDocumentAsTxt(this.uri2));
        store.undo();//1
        assertEquals(this.pdfTxt1, store.getDocumentAsTxt(this.uri1));
        List<String> list = new ArrayList<>();
        list.add(this.pdfTxt2);
        list.add(this.pdfTxt1);
        assertEquals(list, store.searchByPrefix("PD"));
        store.undo();//2
        assertEquals(null, store.getDocumentAsTxt(this.uri2));
        assertEquals(this.pdfTxt1, store.getDocumentAsTxt(this.uri1));
        store.undo();//3
        assertEquals(null, store.getDocumentAsTxt(this.uri1));
        store.undo();//4
    }

    @Test
    (expected = IllegalStateException.class)
    public void testDeleteUndoSpecificTillException(){
        DocumentStoreImpl store = new DocumentStoreImpl(file);
        store.putDocument(new ByteArrayInputStream(this.pdfData1), this.uri1, DocumentStoreImpl.DocumentFormat.PDF);
        store.putDocument(new ByteArrayInputStream(this.pdfData2), this.uri2, DocumentStoreImpl.DocumentFormat.PDF);
        store.deleteAllWithPrefix("PD");
        assertEquals(new ArrayList<>(), store.search("for"));
        assertEquals(null, store.getDocumentAsTxt(this.uri1));
        assertEquals(null, store.getDocumentAsTxt(this.uri2));
        store.undo();//1
        assertEquals(this.pdfTxt1, store.getDocumentAsTxt(this.uri1));
        List<String> list = new ArrayList<>();
        list.add(this.pdfTxt2);
        list.add(this.pdfTxt1);
        assertEquals(list, store.searchByPrefix("PD"));
        store.undo();//2
        assertEquals(null, store.getDocumentAsTxt(this.uri2));
        assertEquals(this.pdfTxt1, store.getDocumentAsTxt(this.uri1));
        store.undo(this.uri2);
    }

    private void putTXTDocuments1(DocumentStoreImpl store){
        store.putDocument(new ByteArrayInputStream("1 hi".getBytes()), URI.create("1"), DocumentStoreImpl.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream("2 hi".getBytes()), URI.create("2"), DocumentStoreImpl.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream("3 hi".getBytes()), URI.create("3"), DocumentStoreImpl.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream("4 hi".getBytes()), URI.create("4"), DocumentStoreImpl.DocumentFormat.TXT);
    }

    private void putTXTDocuments2(DocumentStoreImpl store){
        store.putDocument(new ByteArrayInputStream("5 hi".getBytes()), URI.create("5"), DocumentStoreImpl.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream("6 hi".getBytes()), URI.create("6"), DocumentStoreImpl.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream("7 hi".getBytes()), URI.create("7"), DocumentStoreImpl.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream("8 hi".getBytes()), URI.create("8"), DocumentStoreImpl.DocumentFormat.TXT);
    }
    private void putTXTDocuments3(DocumentStoreImpl store){
        store.putDocument(new ByteArrayInputStream("5 hi".getBytes()), URI.create("1"), DocumentStoreImpl.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream("6 hi".getBytes()), URI.create("2"), DocumentStoreImpl.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream("7 hi".getBytes()), URI.create("3"), DocumentStoreImpl.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream("8 hi".getBytes()), URI.create("4"), DocumentStoreImpl.DocumentFormat.TXT);
    }

    private void putTXTDocuments4(DocumentStoreImpl store){
        store.putDocument(new ByteArrayInputStream("9 hi".getBytes()), URI.create("9"), DocumentStoreImpl.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream("10 hi".getBytes()), URI.create("10"), DocumentStoreImpl.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream("11 hi".getBytes()), URI.create("11"), DocumentStoreImpl.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream("12 hi".getBytes()), URI.create("12"), DocumentStoreImpl.DocumentFormat.TXT);
    }
    
    @Test
    public void putNewTXTGoesOverCount(){
        DocumentStoreImpl store = new DocumentStoreImpl(file);
        store.setMaxDocumentCount(4);
        putTXTDocuments1(store);
        assertEquals("1 hi",store.getDocumentAsTxt(URI.create("1")));
        assertEquals("2 hi",store.getDocumentAsTxt(URI.create("2")));
        assertEquals("3 hi",store.getDocumentAsTxt(URI.create("3")));
        assertEquals("4 hi",store.getDocumentAsTxt(URI.create("4")));
        putTXTDocuments2(store);
        List<String> list = store.search("hi");
        assertEquals(8, list.size());
        assertEquals("5 hi",store.getDocumentAsTxt(URI.create("5")));
        assertEquals("6 hi",store.getDocumentAsTxt(URI.create("6")));
        assertEquals("7 hi",store.getDocumentAsTxt(URI.create("7")));
        assertEquals("8 hi",store.getDocumentAsTxt(URI.create("8")));
        assertEquals("1 hi",store.getDocumentAsTxt(URI.create("1")));
        assertEquals("2 hi",store.getDocumentAsTxt(URI.create("2")));
        assertEquals("3 hi",store.getDocumentAsTxt(URI.create("3")));
        assertEquals("4 hi",store.getDocumentAsTxt(URI.create("4")));
    }

    @Test
    public void putReplaceTXTGoesOverCount(){
        DocumentStoreImpl store = new DocumentStoreImpl(file);
        store.setMaxDocumentCount(4);
        putTXTDocuments1(store);
        assertEquals("1 hi",store.getDocumentAsTxt(URI.create("1")));
        assertEquals("2 hi",store.getDocumentAsTxt(URI.create("2")));
        assertEquals("3 hi",store.getDocumentAsTxt(URI.create("3")));
        assertEquals("4 hi",store.getDocumentAsTxt(URI.create("4")));
        putTXTDocuments3(store);
        List<String> list = store.search("hi");
        assertEquals(4, list.size());
        assertEquals("5 hi",store.getDocumentAsTxt(URI.create("1")));
        assertEquals("6 hi",store.getDocumentAsTxt(URI.create("2")));
        assertEquals("7 hi",store.getDocumentAsTxt(URI.create("3")));
        assertEquals("8 hi",store.getDocumentAsTxt(URI.create("4")));
    }

    @Test
    public void undoDelTXTGoesOverCount(){
        DocumentStoreImpl store = new DocumentStoreImpl(file);
        store.setMaxDocumentCount(4);
        putTXTDocuments1(store);
        assertEquals("1 hi",store.getDocumentAsTxt(URI.create("1")));
        assertEquals("2 hi",store.getDocumentAsTxt(URI.create("2")));
        assertEquals("3 hi",store.getDocumentAsTxt(URI.create("3")));
        assertEquals("4 hi",store.getDocumentAsTxt(URI.create("4")));
        store.deleteAll("hi");
        putTXTDocuments2(store);
        List<String> list = store.search("hi");
        assertEquals(4, list.size());
        assertEquals("5 hi",store.getDocumentAsTxt(URI.create("5")));
        assertEquals("6 hi",store.getDocumentAsTxt(URI.create("6")));
        assertEquals("7 hi",store.getDocumentAsTxt(URI.create("7")));
        assertEquals("8 hi",store.getDocumentAsTxt(URI.create("8")));
        store.undo(URI.create("1"));
        store.undo(URI.create("2"));
        store.undo(URI.create("3"));
        store.undo(URI.create("4"));
        list = store.search("hi");
        assertEquals(8, list.size());
        assertEquals("1 hi",store.getDocumentAsTxt(URI.create("1")));
        assertEquals("2 hi",store.getDocumentAsTxt(URI.create("2")));
        assertEquals("3 hi",store.getDocumentAsTxt(URI.create("3")));
        assertEquals("4 hi",store.getDocumentAsTxt(URI.create("4")));
    }

    @Test
    public void undoReplaceTXTGoesOverCount(){
        DocumentStoreImpl store = new DocumentStoreImpl(file);
        store.setMaxDocumentCount(4);
        putTXTDocuments1(store);
        assertEquals("1 hi",store.getDocumentAsTxt(URI.create("1")));
        assertEquals("2 hi",store.getDocumentAsTxt(URI.create("2")));
        assertEquals("3 hi",store.getDocumentAsTxt(URI.create("3")));
        assertEquals("4 hi",store.getDocumentAsTxt(URI.create("4")));
        putTXTDocuments2(store);
        List<String> list = store.search("hi");
        assertEquals(8, list.size());
        assertEquals("5 hi",store.getDocumentAsTxt(URI.create("5")));
        assertEquals("6 hi",store.getDocumentAsTxt(URI.create("6")));
        assertEquals("7 hi",store.getDocumentAsTxt(URI.create("7")));
        assertEquals("8 hi",store.getDocumentAsTxt(URI.create("8")));
        putTXTDocuments4(store);
        store.undo(URI.create("1"));
        store.undo(URI.create("2"));
        store.undo(URI.create("3"));
        store.undo(URI.create("4"));
    }

    @Test
    public void deleteTXTForCount(){
        DocumentStoreImpl store = new DocumentStoreImpl(file);
        store.setMaxDocumentCount(4);
        putTXTDocuments1(store);
        store.deleteDocument(URI.create("1"));
        store.putDocument(new ByteArrayInputStream("30 hi".getBytes()), URI.create("30"), DocumentFormat.TXT);
        List<String> list = store.search("hi");
        assertTrue(list.size()==4);
        assertTrue(list.contains("30 hi"));
        assertTrue(list.contains("2 hi"));
        assertTrue(list.contains("3 hi"));
        assertTrue(list.contains("4 hi"));
    }

    @Test
    public void undoNewTXTForCount(){
        DocumentStoreImpl store = new DocumentStoreImpl(file);
        store.setMaxDocumentCount(4);
        putTXTDocuments1(store);
        store.undo(URI.create("1"));
        store.setMaxDocumentCount(3);
        store.putDocument(new ByteArrayInputStream("30 hi".getBytes()), URI.create("30"), DocumentFormat.TXT);
        List<String> list = store.search("hi");
        assertTrue(list.size()==4);
        assertTrue(list.contains("30 hi"));
        assertTrue(list.contains("3 hi"));
        assertTrue(list.contains("4 hi"));
    }

    @Test
    public void undoReplaceTXTForCount(){
        DocumentStoreImpl store = new DocumentStoreImpl(file);
        store.setMaxDocumentCount(4);
        putTXTDocuments1(store);
        store.putDocument(new ByteArrayInputStream("31 hi".getBytes()), URI.create("1"), DocumentFormat.TXT);
        store.undo(URI.create("1"));
        store.setMaxDocumentCount(3);
        store.putDocument(new ByteArrayInputStream("30 hi".getBytes()), URI.create("30"), DocumentFormat.TXT);
        List<String> list = store.search("hi");
        assertTrue(list.size()==5);
        assertTrue(list.contains("30 hi"));
        assertTrue(list.contains("1 hi"));
        assertTrue(list.contains("4 hi"));
    }

    @Test
    public void changeCountLimit(){
        DocumentStoreImpl store = new DocumentStoreImpl(file);
        store.setMaxDocumentCount(4);
        putTXTDocuments1(store);
        store.setMaxDocumentCount(3);
        List<String> list = store.search("hi");
        assertTrue(list.size()==4);
        assertTrue(list.contains("2 hi"));
        assertTrue(list.contains("3 hi"));
        assertTrue(list.contains("4 hi"));
    }

    private void putPDFDocuments1(DocumentStoreImpl store){
        try{
            store.putDocument(new ByteArrayInputStream(Utils.textToPdfData("1 hi")), URI.create("1"), DocumentStoreImpl.DocumentFormat.PDF);
            store.putDocument(new ByteArrayInputStream(Utils.textToPdfData("2 hi")), URI.create("2"), DocumentStoreImpl.DocumentFormat.PDF);
            store.putDocument(new ByteArrayInputStream(Utils.textToPdfData("3 hi")), URI.create("3"), DocumentStoreImpl.DocumentFormat.PDF);
            store.putDocument(new ByteArrayInputStream(Utils.textToPdfData("4 hi")), URI.create("4"), DocumentStoreImpl.DocumentFormat.PDF);
    
        }
        catch(IOException e){
            throw new IllegalArgumentException();
        }
    }

    private void putPDFDocuments2(DocumentStoreImpl store){
        try{
            store.putDocument(new ByteArrayInputStream(Utils.textToPdfData("5 hi")), URI.create("5"), DocumentStoreImpl.DocumentFormat.PDF);
            store.putDocument(new ByteArrayInputStream(Utils.textToPdfData("6 hi")), URI.create("6"), DocumentStoreImpl.DocumentFormat.PDF);
            store.putDocument(new ByteArrayInputStream(Utils.textToPdfData("7 hi")), URI.create("7"), DocumentStoreImpl.DocumentFormat.PDF);
            store.putDocument(new ByteArrayInputStream(Utils.textToPdfData("8 hi")), URI.create("8"), DocumentStoreImpl.DocumentFormat.PDF);
    
        }
        catch(IOException e){
            throw new IllegalArgumentException();
        }
    }

    private void putPDFDocuments3(DocumentStoreImpl store){
        try{
            store.putDocument(new ByteArrayInputStream(Utils.textToPdfData("5 hi")), URI.create("1"), DocumentStoreImpl.DocumentFormat.PDF);
            store.putDocument(new ByteArrayInputStream(Utils.textToPdfData("6 hi")), URI.create("2"), DocumentStoreImpl.DocumentFormat.PDF);
            store.putDocument(new ByteArrayInputStream(Utils.textToPdfData("7 hi")), URI.create("3"), DocumentStoreImpl.DocumentFormat.PDF);
            store.putDocument(new ByteArrayInputStream(Utils.textToPdfData("8 hi")), URI.create("4"), DocumentStoreImpl.DocumentFormat.PDF);
    
        }
        catch(IOException e){
            throw new IllegalArgumentException();
        }
    }

    private void putPDFDocuments4(DocumentStoreImpl store){
        try{
            store.putDocument(new ByteArrayInputStream(Utils.textToPdfData("9 hi")), URI.create("9"), DocumentStoreImpl.DocumentFormat.PDF);
            store.putDocument(new ByteArrayInputStream(Utils.textToPdfData("10 hi")), URI.create("10"), DocumentStoreImpl.DocumentFormat.PDF);
            store.putDocument(new ByteArrayInputStream(Utils.textToPdfData("11 hi")), URI.create("11"), DocumentStoreImpl.DocumentFormat.PDF);
            store.putDocument(new ByteArrayInputStream(Utils.textToPdfData("12 hi")), URI.create("12"), DocumentStoreImpl.DocumentFormat.PDF);
    
        }
        catch(IOException e){
            throw new IllegalArgumentException();
        }
    }


    @Test
    public void putNewPDFGoesOverCount(){
        DocumentStoreImpl store = new DocumentStoreImpl(file);
        store.setMaxDocumentCount(4);
        putPDFDocuments1(store);
        assertEquals("1 hi",store.getDocumentAsTxt(URI.create("1")));
        assertEquals("2 hi",store.getDocumentAsTxt(URI.create("2")));
        assertEquals("3 hi",store.getDocumentAsTxt(URI.create("3")));
        assertEquals("4 hi",store.getDocumentAsTxt(URI.create("4")));
        putPDFDocuments2(store);
        List<String> list = store.search("hi");
        assertEquals(8, list.size());
        assertEquals("5 hi",store.getDocumentAsTxt(URI.create("5")));
        assertEquals("6 hi",store.getDocumentAsTxt(URI.create("6")));
        assertEquals("7 hi",store.getDocumentAsTxt(URI.create("7")));
        assertEquals("8 hi",store.getDocumentAsTxt(URI.create("8")));
    }

    @Test
    public void putReplacePDFGoesOverCount(){
        DocumentStoreImpl store = new DocumentStoreImpl(file);
        store.setMaxDocumentCount(4);
        putPDFDocuments1(store);
        assertEquals("1 hi",store.getDocumentAsTxt(URI.create("1")));
        assertEquals("2 hi",store.getDocumentAsTxt(URI.create("2")));
        assertEquals("3 hi",store.getDocumentAsTxt(URI.create("3")));
        assertEquals("4 hi",store.getDocumentAsTxt(URI.create("4")));
        putPDFDocuments3(store);
        List<String> list = store.search("hi");
        assertEquals(4, list.size());
        assertEquals("5 hi",store.getDocumentAsTxt(URI.create("1")));
        assertEquals("6 hi",store.getDocumentAsTxt(URI.create("2")));
        assertEquals("7 hi",store.getDocumentAsTxt(URI.create("3")));
        assertEquals("8 hi",store.getDocumentAsTxt(URI.create("4")));
    }

    @Test
    public void undoDelPDFGoesOverCount(){
        DocumentStoreImpl store = new DocumentStoreImpl(file);
        store.setMaxDocumentCount(4);
        putPDFDocuments1(store);
        assertEquals("1 hi",store.getDocumentAsTxt(URI.create("1")));
        assertEquals("2 hi",store.getDocumentAsTxt(URI.create("2")));
        assertEquals("3 hi",store.getDocumentAsTxt(URI.create("3")));
        assertEquals("4 hi",store.getDocumentAsTxt(URI.create("4")));
        store.deleteAll("hi");
        putPDFDocuments2(store);
        List<String> list = store.search("hi");
        assertEquals(4, list.size());
        assertEquals("5 hi",store.getDocumentAsTxt(URI.create("5")));
        assertEquals("6 hi",store.getDocumentAsTxt(URI.create("6")));
        assertEquals("7 hi",store.getDocumentAsTxt(URI.create("7")));
        assertEquals("8 hi",store.getDocumentAsTxt(URI.create("8")));
        store.undo(URI.create("1"));
        store.undo(URI.create("2"));
        store.undo(URI.create("3"));
        store.undo(URI.create("4"));
        list = store.search("hi");
        assertEquals(8, list.size());
        assertEquals("1 hi",store.getDocumentAsTxt(URI.create("1")));
        assertEquals("2 hi",store.getDocumentAsTxt(URI.create("2")));
        assertEquals("3 hi",store.getDocumentAsTxt(URI.create("3")));
        assertEquals("4 hi",store.getDocumentAsTxt(URI.create("4")));
        assertEquals("5 hi",store.getDocumentAsTxt(URI.create("5")));
        assertEquals("6 hi",store.getDocumentAsTxt(URI.create("6")));
        assertEquals("7 hi",store.getDocumentAsTxt(URI.create("7")));
        assertEquals("8 hi",store.getDocumentAsTxt(URI.create("8")));
    }
    @Test(expected = IllegalStateException.class)
    public void undoReplacePDFGoesOverCount(){
        DocumentStoreImpl store = new DocumentStoreImpl(file);
        store.setMaxDocumentCount(4);
        putPDFDocuments1(store);
        assertEquals("1 hi",store.getDocumentAsTxt(URI.create("1")));
        assertEquals("2 hi",store.getDocumentAsTxt(URI.create("2")));
        assertEquals("3 hi",store.getDocumentAsTxt(URI.create("3")));
        assertEquals("4 hi",store.getDocumentAsTxt(URI.create("4")));
        putPDFDocuments2(store);
        List<String> list = store.search("hi");
        assertEquals(8, list.size());
        assertEquals("5 hi",store.getDocumentAsTxt(URI.create("5")));
        assertEquals("6 hi",store.getDocumentAsTxt(URI.create("6")));
        assertEquals("7 hi",store.getDocumentAsTxt(URI.create("7")));
        assertEquals("8 hi",store.getDocumentAsTxt(URI.create("8")));
        assertEquals("1 hi",store.getDocumentAsTxt(URI.create("1")));
        assertEquals("2 hi",store.getDocumentAsTxt(URI.create("2")));
        assertEquals("3 hi",store.getDocumentAsTxt(URI.create("3")));
        assertEquals("4 hi",store.getDocumentAsTxt(URI.create("4")));
        putPDFDocuments4(store);
        store.undo(URI.create("1"));
        store.undo(URI.create("2"));
        store.undo(URI.create("3"));
        store.undo(URI.create("4"));
        store.undo(URI.create("5"));
        store.undo(URI.create("6"));
        store.undo(URI.create("7"));
        store.undo(URI.create("8"));
        store.undo(URI.create("9"));
        store.undo(URI.create("10"));
        store.undo(URI.create("11"));
        store.undo(URI.create("1"));
    }


    @Test
    public void putNewTXTGoesOverBytes(){
        DocumentStoreImpl store = new DocumentStoreImpl(file);
        store.setMaxDocumentBytes(825*4);
        putTXTDocuments1(store);
        assertEquals("1 hi",store.getDocumentAsTxt(URI.create("1")));
        assertEquals("2 hi",store.getDocumentAsTxt(URI.create("2")));
        assertEquals("3 hi",store.getDocumentAsTxt(URI.create("3")));
        assertEquals("4 hi",store.getDocumentAsTxt(URI.create("4")));
        putTXTDocuments2(store);
        List<String> list = store.search("hi");
        assertEquals(8, list.size());
        assertEquals("5 hi",store.getDocumentAsTxt(URI.create("5")));
        assertEquals("6 hi",store.getDocumentAsTxt(URI.create("6")));
        assertEquals("7 hi",store.getDocumentAsTxt(URI.create("7")));
        assertEquals("8 hi",store.getDocumentAsTxt(URI.create("8")));
        assertEquals("1 hi",store.getDocumentAsTxt(URI.create("1")));
        assertEquals("2 hi",store.getDocumentAsTxt(URI.create("2")));
        assertEquals("3 hi",store.getDocumentAsTxt(URI.create("3")));
        assertEquals("4 hi",store.getDocumentAsTxt(URI.create("4")));
    }

    @Test
    public void putReplaceTXTGoesOverBytes(){
        DocumentStoreImpl store = new DocumentStoreImpl(file);
        store.setMaxDocumentBytes(3328);
        putTXTDocuments1(store);
        assertEquals("1 hi",store.getDocumentAsTxt(URI.create("1")));
        assertEquals("2 hi",store.getDocumentAsTxt(URI.create("2")));
        assertEquals("3 hi",store.getDocumentAsTxt(URI.create("3")));
        assertEquals("4 hi",store.getDocumentAsTxt(URI.create("4")));
        putTXTDocuments3(store);
        List<String> list = store.search("hi");
        assertEquals(4, list.size());
        assertEquals("5 hi",store.getDocumentAsTxt(URI.create("1")));
        assertEquals("6 hi",store.getDocumentAsTxt(URI.create("2")));
        assertEquals("7 hi",store.getDocumentAsTxt(URI.create("3")));
        assertEquals("8 hi",store.getDocumentAsTxt(URI.create("4")));
    }

    @Test
    public void undoDelTXTGoesOverBytes(){
        DocumentStoreImpl store = new DocumentStoreImpl(file);
        store.setMaxDocumentBytes(3328);
        putTXTDocuments1(store);
        assertEquals("1 hi",store.getDocumentAsTxt(URI.create("1")));
        assertEquals("2 hi",store.getDocumentAsTxt(URI.create("2")));
        assertEquals("3 hi",store.getDocumentAsTxt(URI.create("3")));
        assertEquals("4 hi",store.getDocumentAsTxt(URI.create("4")));
        store.deleteAll("hi");
        putTXTDocuments2(store);
        List<String> list = store.search("hi");
        assertEquals(4, list.size());
        assertEquals("5 hi",store.getDocumentAsTxt(URI.create("5")));
        assertEquals("6 hi",store.getDocumentAsTxt(URI.create("6")));
        assertEquals("7 hi",store.getDocumentAsTxt(URI.create("7")));
        assertEquals("8 hi",store.getDocumentAsTxt(URI.create("8")));
        store.undo(URI.create("1"));
        store.undo(URI.create("2"));
        store.undo(URI.create("3"));
        store.undo(URI.create("4"));
        
        list = store.search("hi");
        assertEquals(8, list.size());
        assertEquals("1 hi",store.getDocumentAsTxt(URI.create("1")));
        assertEquals("2 hi",store.getDocumentAsTxt(URI.create("2")));
        assertEquals("3 hi",store.getDocumentAsTxt(URI.create("3")));
        assertEquals("4 hi",store.getDocumentAsTxt(URI.create("4")));
        assertEquals("5 hi",store.getDocumentAsTxt(URI.create("5")));
        assertEquals("6 hi",store.getDocumentAsTxt(URI.create("6")));
        assertEquals("7 hi",store.getDocumentAsTxt(URI.create("7")));
        assertEquals("8 hi",store.getDocumentAsTxt(URI.create("8")));
    }

    @Test
    public void undoReplaceTXTGoesOverBytes(){
        DocumentStoreImpl store = new DocumentStoreImpl(file);
        store.setMaxDocumentBytes(3328);
        putTXTDocuments1(store);
        assertEquals("1 hi",store.getDocumentAsTxt(URI.create("1")));
        assertEquals("2 hi",store.getDocumentAsTxt(URI.create("2")));
        assertEquals("3 hi",store.getDocumentAsTxt(URI.create("3")));
        assertEquals("4 hi",store.getDocumentAsTxt(URI.create("4")));
        putTXTDocuments2(store);
        List<String> list = store.search("hi");
        assertEquals(8, list.size());
        assertEquals("5 hi",store.getDocumentAsTxt(URI.create("5")));
        assertEquals("6 hi",store.getDocumentAsTxt(URI.create("6")));
        assertEquals("7 hi",store.getDocumentAsTxt(URI.create("7")));
        assertEquals("8 hi",store.getDocumentAsTxt(URI.create("8")));
        putTXTDocuments4(store);
        store.undo(URI.create("1"));
        store.undo(URI.create("2"));
        store.undo(URI.create("3"));
        store.undo(URI.create("4"));
    }

    @Test
    public void deleteTXTForBytes(){
        DocumentStoreImpl store = new DocumentStoreImpl(file);
        store.setMaxDocumentBytes(3328);
        putTXTDocuments1(store);
        store.deleteDocument(URI.create("1"));
        store.putDocument(new ByteArrayInputStream("9 hi".getBytes()), URI.create("30"), DocumentFormat.TXT);
        List<String> list = store.search("hi");
        assertTrue(list.size()==4);
        assertTrue(list.contains("9 hi"));
        assertTrue(list.contains("2 hi"));
        assertTrue(list.contains("3 hi"));
        assertTrue(list.contains("4 hi"));
    }

    @Test
    public void undoNewTXTForBytes(){
        DocumentStoreImpl store = new DocumentStoreImpl(file);
        store.setMaxDocumentBytes(3300);
        putTXTDocuments1(store);
        store.undo(URI.create("1"));
        store.setMaxDocumentBytes(2475);
        store.putDocument(new ByteArrayInputStream("9 hi".getBytes()), URI.create("30"), DocumentFormat.TXT);
        List<String> list = store.search("hi");
        assertTrue(list.size()==4);
        assertTrue(list.contains("9 hi"));
        assertTrue(list.contains("3 hi"));
        assertTrue(list.contains("4 hi"));
    }

    @Test
    public void undoReplaceTXTForBytes(){
        DocumentStoreImpl store = new DocumentStoreImpl(file);
        store.setMaxDocumentBytes(3328);
        putTXTDocuments1(store);
        store.putDocument(new ByteArrayInputStream("9 hi".getBytes()), URI.create("1"), DocumentFormat.TXT);
        store.undo(URI.create("1"));
        store.setMaxDocumentBytes(2496);
        store.putDocument(new ByteArrayInputStream("8 hi".getBytes()), URI.create("30"), DocumentFormat.TXT);
        List<String> list = store.search("hi");
        assertEquals(5,list.size());
        assertTrue(list.contains("8 hi"));
        assertTrue(list.contains("1 hi"));
        assertTrue(list.contains("4 hi"));
    }

    @Test
    public void checkScrubStack(){
        DocumentStoreImpl store = new DocumentStoreImpl(file);
        store.setMaxDocumentBytes(3328);
        putTXTDocuments1(store);
        store.putDocument(new ByteArrayInputStream("9 hi".getBytes()), URI.create("1"), DocumentFormat.TXT);
        store.undo(URI.create("1"));
        store.setMaxDocumentBytes(2496);
        store.putDocument(new ByteArrayInputStream("8 hi".getBytes()), URI.create("30"), DocumentFormat.TXT);
        List<String> list = store.search("hi");
        assertTrue(list.size()==5);
        assertTrue(list.contains("8 hi"));
        assertTrue(list.contains("1 hi"));
        assertTrue(list.contains("4 hi"));
    }

    @Test
    public void changeBytesLimit(){
        DocumentStoreImpl store = new DocumentStoreImpl(file);
        store.setMaxDocumentBytes(3300);
        putTXTDocuments1(store);
        store.setMaxDocumentBytes(2475);
        List<String> list = store.search("hi");
        assertTrue(list.size()==4);
        assertTrue(list.contains("1 hi"));
        assertTrue(list.contains("2 hi"));
        assertTrue(list.contains("3 hi"));
        assertTrue(list.contains("4 hi"));
    }

    @Test(expected = NoSuchElementException.class)
    public void countLimit0(){
        DocumentStoreImpl store = new DocumentStoreImpl(file);
        store.setMaxDocumentCount(0);
        putTXTDocuments1(store);
    }
    @Test(expected = NoSuchElementException.class)
    public void byteLimit0(){
        DocumentStoreImpl store = new DocumentStoreImpl(file);
        store.setMaxDocumentBytes(0);
        putTXTDocuments1(store);
    }

    @Test
    public void putNewTXTGoesOverBytesNotCount(){
        DocumentStoreImpl store = new DocumentStoreImpl(file);
        store.setMaxDocumentBytes(3299);
        store.setMaxDocumentCount(4);
        putTXTDocuments1(store);
        assertEquals("2 hi",store.getDocumentAsTxt(URI.create("2")));
        assertEquals("3 hi",store.getDocumentAsTxt(URI.create("3")));
        assertEquals("4 hi",store.getDocumentAsTxt(URI.create("4")));
        putTXTDocuments2(store);
        List<String> list = store.search("hi");
        assertEquals(8, list.size());
        assertEquals("6 hi",store.getDocumentAsTxt(URI.create("6")));
        assertEquals("7 hi",store.getDocumentAsTxt(URI.create("7")));
        assertEquals("8 hi",store.getDocumentAsTxt(URI.create("8")));
    }

    @Test
    public void putNewTXTGoesOverCountNotBytes(){
        DocumentStoreImpl store = new DocumentStoreImpl(file);
        store.setMaxDocumentBytes(3328);
        store.setMaxDocumentCount(3);
        putTXTDocuments1(store);
        assertEquals("2 hi",store.getDocumentAsTxt(URI.create("2")));
        assertEquals("3 hi",store.getDocumentAsTxt(URI.create("3")));
        assertEquals("4 hi",store.getDocumentAsTxt(URI.create("4")));
        putTXTDocuments2(store);
        List<String> list = store.search("hi");
        assertEquals(8, list.size());
        assertEquals("6 hi",store.getDocumentAsTxt(URI.create("6")));
        assertEquals("7 hi",store.getDocumentAsTxt(URI.create("7")));
        assertEquals("8 hi",store.getDocumentAsTxt(URI.create("8")));
    }
}