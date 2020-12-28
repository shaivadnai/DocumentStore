package edu.yu.cs.com1320.project.impl;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import edu.yu.cs.com1320.project.MinHeap;
import edu.yu.cs.com1320.project.stage5.impl.DocumentImpl;

/**
 * MinHeapImplTest
 */
public class MinHeapImplTest {

    //docs
    private DocumentImpl txtdoc1;
    private DocumentImpl txtdoc2;
    private DocumentImpl txtdoc3;
    private DocumentImpl txtdoc4;
    private DocumentImpl txtdoc5;
    private DocumentImpl txtdoc6;
    private DocumentImpl txtdoc7;
    private DocumentImpl txtdoc8;
    private DocumentImpl txtdoc9;
    private DocumentImpl txtdoc10;
    private DocumentImpl txtdoc11;
    private DocumentImpl txtdoc21;
    private DocumentImpl txtdoc31;
    private DocumentImpl txtdoc41;
    private DocumentImpl txtdoc51;
    private DocumentImpl txtdoc61;
    private DocumentImpl txtdoc71;
    private DocumentImpl txtdoc81;
    private DocumentImpl txtdoc91;
    private DocumentImpl txtdoc101;
    private long origin;

    @Before
    public void init() throws Exception {
        this.txtdoc1 = new DocumentImpl(URI.create("1"), "1", "1".hashCode());
        this.txtdoc2 = new DocumentImpl(URI.create("2"), "2", "2".hashCode());
        this.txtdoc3 = new DocumentImpl(URI.create("3"), "3", "3".hashCode());
        this.txtdoc4 = new DocumentImpl(URI.create("4"), "4", "4".hashCode());
        this.txtdoc5 = new DocumentImpl(URI.create("5"), "5", "5".hashCode());
        this.txtdoc6 = new DocumentImpl(URI.create("6"), "6", "6".hashCode());
        this.txtdoc7 = new DocumentImpl(URI.create("7"), "7", "7".hashCode());
        this.txtdoc8 = new DocumentImpl(URI.create("8"), "8", "8".hashCode());
        this.txtdoc9 = new DocumentImpl(URI.create("9"), "9", "9".hashCode());
        this.txtdoc10 = new DocumentImpl(URI.create("10"), "10", "10".hashCode());
        this.txtdoc11 = new DocumentImpl(URI.create("11"), "11", "11".hashCode());
        this.txtdoc21 = new DocumentImpl(URI.create("21"), "21", "21".hashCode());
        this.txtdoc31 = new DocumentImpl(URI.create("31"), "31", "31".hashCode());
        this.txtdoc41 = new DocumentImpl(URI.create("41"), "41", "41".hashCode());
        this.txtdoc51 = new DocumentImpl(URI.create("51"), "51", "51".hashCode());
        this.txtdoc61 = new DocumentImpl(URI.create("61"), "61", "61".hashCode());
        this.txtdoc71 = new DocumentImpl(URI.create("71"), "71", "71".hashCode());
        this.txtdoc81 = new DocumentImpl(URI.create("81"), "81", "81".hashCode());
        this.txtdoc91 = new DocumentImpl(URI.create("91"), "91", "91".hashCode());
        this.txtdoc101 = new DocumentImpl(URI.create("101"), "101", "101".hashCode());
        
        this.origin = System.nanoTime();
        TimeUnit.NANOSECONDS.sleep(1);
        this.setDocumentTime(this.txtdoc1);
        TimeUnit.NANOSECONDS.sleep(1);
        this.setDocumentTime(this.txtdoc2);
        TimeUnit.NANOSECONDS.sleep(1);
        this.setDocumentTime(this.txtdoc3);
        TimeUnit.NANOSECONDS.sleep(1);
        this.setDocumentTime(this.txtdoc4);
        TimeUnit.NANOSECONDS.sleep(1);
        this.setDocumentTime(this.txtdoc5);
        TimeUnit.NANOSECONDS.sleep(1);
        this.setDocumentTime(this.txtdoc6);
        TimeUnit.NANOSECONDS.sleep(1);
        this.setDocumentTime(this.txtdoc7);
        TimeUnit.NANOSECONDS.sleep(1);
        this.setDocumentTime(this.txtdoc8);
        TimeUnit.NANOSECONDS.sleep(1);
        this.setDocumentTime(this.txtdoc9);
        TimeUnit.NANOSECONDS.sleep(1);
        this.setDocumentTime(this.txtdoc10);
        TimeUnit.NANOSECONDS.sleep(1);
        this.setDocumentTime(this.txtdoc11);
        TimeUnit.NANOSECONDS.sleep(1);
        this.setDocumentTime(this.txtdoc21);
        TimeUnit.NANOSECONDS.sleep(1);
        this.setDocumentTime(this.txtdoc31);
        TimeUnit.NANOSECONDS.sleep(1);
        this.setDocumentTime(this.txtdoc41);
        TimeUnit.NANOSECONDS.sleep(1);
        this.setDocumentTime(this.txtdoc51);
        TimeUnit.NANOSECONDS.sleep(1);
        this.setDocumentTime(this.txtdoc61);
        TimeUnit.NANOSECONDS.sleep(1);
        this.setDocumentTime(this.txtdoc71);
        TimeUnit.NANOSECONDS.sleep(1);
        this.setDocumentTime(this.txtdoc81);
        TimeUnit.NANOSECONDS.sleep(1);
        this.setDocumentTime(this.txtdoc91);
        TimeUnit.NANOSECONDS.sleep(1);
        this.setDocumentTime(this.txtdoc101);
    }

    private void setDocumentTime(DocumentImpl doc) {
        long currentTime = System.nanoTime();
        doc.setLastUseTime(currentTime-this.origin);
    }

    @Test
    @SuppressWarnings("unused")
    public void testConstructor(){
        MinHeapImpl<DocumentImpl> heap = new MinHeapImpl<>();
    }

    @Test
    public void testInsert(){//inserting sequentially works
        MinHeapImpl<DocumentImpl> heap = new MinHeapImpl<>();
        heap.insert(this.txtdoc1);
        heap.insert(this.txtdoc2);
        heap.insert(this.txtdoc3);
        heap.insert(this.txtdoc4);
        heap.insert(this.txtdoc5);
        assertEquals(1,heap.getArrayIndex(this.txtdoc1));
        assertEquals(2,heap.getArrayIndex(this.txtdoc2));
        assertEquals(3,heap.getArrayIndex(this.txtdoc3));
        assertEquals(4,heap.getArrayIndex(this.txtdoc4));
        assertEquals(5,heap.getArrayIndex(this.txtdoc5));
    }

    @Test(expected = NoSuchElementException.class )
    public void testEmpty() {
        MinHeapImpl<DocumentImpl> heap = new MinHeapImpl<>();
        heap.insert(this.txtdoc1);
        heap.insert(this.txtdoc2);
        heap.insert(this.txtdoc3);
        heap.insert(this.txtdoc4);
        heap.insert(this.txtdoc5);
        heap.insert(this.txtdoc6);
        heap.insert(this.txtdoc7);
        heap.insert(this.txtdoc8);
        heap.insert(this.txtdoc9);
        this.setDocumentTime(this.txtdoc2);
        heap.reHeapify(this.txtdoc2);
        assertEquals(this.txtdoc1.getLastUseTime(), heap.removeMin().getLastUseTime());
        assertEquals(this.txtdoc3.getLastUseTime(), heap.removeMin().getLastUseTime());
        assertEquals(this.txtdoc4.getLastUseTime(), heap.removeMin().getLastUseTime());
        assertEquals(this.txtdoc5.getLastUseTime(), heap.removeMin().getLastUseTime());
        assertEquals(this.txtdoc6.getLastUseTime(), heap.removeMin().getLastUseTime());
        assertEquals(this.txtdoc7.getLastUseTime(), heap.removeMin().getLastUseTime());
        assertEquals(this.txtdoc8.getLastUseTime(), heap.removeMin().getLastUseTime());
        assertEquals(this.txtdoc9.getLastUseTime(), heap.removeMin().getLastUseTime());
        assertEquals(this.txtdoc2.getLastUseTime(), heap.removeMin().getLastUseTime());
        heap.removeMin();
    }

    @Test
    public void testHeapify() {
        MinHeapImpl<DocumentImpl> heap = new MinHeapImpl<>();
        heap.insert(this.txtdoc1);
        heap.insert(this.txtdoc2);
        heap.insert(this.txtdoc3);
        heap.insert(this.txtdoc4);
        heap.insert(this.txtdoc5);
        heap.insert(this.txtdoc6);
        heap.insert(this.txtdoc7);
        heap.insert(this.txtdoc8);
        heap.insert(this.txtdoc9);
        this.setDocumentTime(this.txtdoc2);
        heap.reHeapify(this.txtdoc2);
        assertEquals(this.txtdoc1.getLastUseTime(), heap.removeMin().getLastUseTime());
        assertEquals(this.txtdoc3.getLastUseTime(), heap.removeMin().getLastUseTime());
        assertEquals(this.txtdoc4.getLastUseTime(), heap.removeMin().getLastUseTime());
        assertEquals(this.txtdoc5.getLastUseTime(), heap.removeMin().getLastUseTime());
        assertEquals(this.txtdoc6.getLastUseTime(), heap.removeMin().getLastUseTime());
        assertEquals(this.txtdoc7.getLastUseTime(), heap.removeMin().getLastUseTime());
        assertEquals(this.txtdoc8.getLastUseTime(), heap.removeMin().getLastUseTime());
        assertEquals(this.txtdoc9.getLastUseTime(), heap.removeMin().getLastUseTime());
        assertEquals(this.txtdoc2.getLastUseTime(), heap.removeMin().getLastUseTime());
    }

    @Test
    public void hashTable(){
        MinHeapImpl<DocumentImpl> heap = new MinHeapImpl<>();
        heap.insert(this.txtdoc1);
        heap.insert(this.txtdoc8);
        this.setDocumentTime(this.txtdoc1);
        heap.reHeapify(this.txtdoc1);
        assertEquals(1,heap.getArrayIndex(this.txtdoc8));
    }

    @Test
    public void arrayDoubling(){
        MinHeapImpl<DocumentImpl> heap = new MinHeapImpl<DocumentImpl>();
        heap.insert(this.txtdoc1);
        heap.insert(this.txtdoc2);
        heap.insert(this.txtdoc3);
        heap.insert(this.txtdoc4);
        heap.insert(this.txtdoc5);
        heap.insert(this.txtdoc6);
        heap.insert(this.txtdoc7);
        heap.insert(this.txtdoc8);
        heap.insert(this.txtdoc9);
        heap.insert(this.txtdoc10);
        heap.insert(this.txtdoc11);
        heap.insert(this.txtdoc21);
        heap.insert(this.txtdoc31);
        heap.insert(this.txtdoc41);
        heap.insert(this.txtdoc51);
        heap.insert(this.txtdoc61);
        heap.insert(this.txtdoc71);
        heap.insert(this.txtdoc81);
        heap.insert(this.txtdoc91);
        heap.insert(this.txtdoc101);
        assertEquals(40,heap.getElements().length);
    }
    @Test
    public void testHeapifyOnRight(){
        MinHeapImpl<DocumentImpl> heap = new MinHeapImpl<DocumentImpl>();
        heap.insert(this.txtdoc1);
        heap.insert(this.txtdoc2);
        heap.insert(this.txtdoc3);
        heap.insert(this.txtdoc4);
        heap.insert(this.txtdoc5);
        heap.insert(this.txtdoc6);
        heap.insert(this.txtdoc7);
        heap.insert(this.txtdoc8);
        heap.insert(this.txtdoc9);
        heap.insert(this.txtdoc10);
        heap.insert(this.txtdoc11);
        heap.insert(this.txtdoc21);
        heap.insert(this.txtdoc31);
        heap.insert(this.txtdoc41);
        heap.insert(this.txtdoc51);
        heap.insert(this.txtdoc61);
        heap.insert(this.txtdoc71);
        heap.insert(this.txtdoc81);
        heap.insert(this.txtdoc91);
        heap.insert(this.txtdoc101);
        
        setDocumentTime(this.txtdoc31);
        heap.reHeapify(this.txtdoc31);

        assertEquals(this.txtdoc1.getLastUseTime(),heap.removeMin().getLastUseTime());
        assertEquals(this.txtdoc2.getLastUseTime(),heap.removeMin().getLastUseTime());
        assertEquals(this.txtdoc3.getLastUseTime(),heap.removeMin().getLastUseTime());
        assertEquals(this.txtdoc4.getLastUseTime(),heap.removeMin().getLastUseTime());
        assertEquals(this.txtdoc5.getLastUseTime(),heap.removeMin().getLastUseTime());
        assertEquals(this.txtdoc6.getLastUseTime(),heap.removeMin().getLastUseTime());
        assertEquals(this.txtdoc7.getLastUseTime(),heap.removeMin().getLastUseTime());
        assertEquals(this.txtdoc8.getLastUseTime(),heap.removeMin().getLastUseTime());
        assertEquals(this.txtdoc9.getLastUseTime(),heap.removeMin().getLastUseTime());
        assertEquals(this.txtdoc10.getLastUseTime(),heap.removeMin().getLastUseTime());
        assertEquals(this.txtdoc11.getLastUseTime(),heap.removeMin().getLastUseTime());
        assertEquals(this.txtdoc21.getLastUseTime(),heap.removeMin().getLastUseTime());
        assertEquals(this.txtdoc41.getLastUseTime(),heap.removeMin().getLastUseTime());
        assertEquals(this.txtdoc51.getLastUseTime(),heap.removeMin().getLastUseTime());
        assertEquals(this.txtdoc61.getLastUseTime(),heap.removeMin().getLastUseTime());
        assertEquals(this.txtdoc71.getLastUseTime(),heap.removeMin().getLastUseTime());
        assertEquals(this.txtdoc81.getLastUseTime(),heap.removeMin().getLastUseTime());
        assertEquals(this.txtdoc91.getLastUseTime(),heap.removeMin().getLastUseTime());
        assertEquals(this.txtdoc101.getLastUseTime(),heap.removeMin().getLastUseTime());
        assertEquals(this.txtdoc31.getLastUseTime(),heap.removeMin().getLastUseTime());
    }

    @Test
    public void reheapify1(){
        MinHeapImpl<DocumentImpl> heap = new MinHeapImpl<>();
        heap.insert(this.txtdoc1);
        heap.insert(this.txtdoc2);
        heap.insert(this.txtdoc3);
        heap.insert(this.txtdoc4);
        heap.insert(this.txtdoc5);
        heap.insert(this.txtdoc6);
        heap.insert(this.txtdoc7);
        heap.insert(this.txtdoc8);
        heap.insert(this.txtdoc9);
        this.txtdoc1.setLastUseTime(System.nanoTime());
        heap.reHeapify(this.txtdoc1);
        assertEquals(this.txtdoc2.getDocumentAsTxt(), heap.removeMin().getDocumentAsTxt());
        assertEquals(this.txtdoc3.getDocumentAsTxt(), heap.removeMin().getDocumentAsTxt());
        assertEquals(this.txtdoc4.getDocumentAsTxt(), heap.removeMin().getDocumentAsTxt());
        assertEquals(this.txtdoc5.getDocumentAsTxt(), heap.removeMin().getDocumentAsTxt());
        assertEquals(this.txtdoc6.getDocumentAsTxt(), heap.removeMin().getDocumentAsTxt());
        assertEquals(this.txtdoc7.getDocumentAsTxt(), heap.removeMin().getDocumentAsTxt());
        assertEquals(this.txtdoc8.getDocumentAsTxt(), heap.removeMin().getDocumentAsTxt());
        assertEquals(this.txtdoc9.getDocumentAsTxt(), heap.removeMin().getDocumentAsTxt());
        assertEquals(this.txtdoc1.getDocumentAsTxt(), heap.removeMin().getDocumentAsTxt());
    }

    @Test
    public void reheapify39(){
        MinHeapImpl<DocumentImpl> heap = new MinHeapImpl<>();
        heap.insert(this.txtdoc1);
        heap.insert(this.txtdoc2);
        heap.insert(this.txtdoc3);
        heap.insert(this.txtdoc4);
        heap.insert(this.txtdoc5);
        heap.insert(this.txtdoc6);
        heap.insert(this.txtdoc7);
        heap.insert(this.txtdoc8);
        heap.insert(this.txtdoc9);
        this.txtdoc3.setLastUseTime(System.nanoTime());
        heap.reHeapify(this.txtdoc3);
        this.txtdoc9.setLastUseTime(System.nanoTime());
        heap.reHeapify(this.txtdoc9);
        assertEquals(this.txtdoc1.getDocumentAsTxt(), heap.removeMin().getDocumentAsTxt());
        assertEquals(this.txtdoc2.getDocumentAsTxt(), heap.removeMin().getDocumentAsTxt());
        assertEquals(this.txtdoc4.getDocumentAsTxt(), heap.removeMin().getDocumentAsTxt());
        assertEquals(this.txtdoc5.getDocumentAsTxt(), heap.removeMin().getDocumentAsTxt());
        assertEquals(this.txtdoc6.getDocumentAsTxt(), heap.removeMin().getDocumentAsTxt());
        assertEquals(this.txtdoc7.getDocumentAsTxt(), heap.removeMin().getDocumentAsTxt());
        assertEquals(this.txtdoc8.getDocumentAsTxt(), heap.removeMin().getDocumentAsTxt());
        assertEquals(this.txtdoc3.getDocumentAsTxt(), heap.removeMin().getDocumentAsTxt());
        assertEquals(this.txtdoc9.getDocumentAsTxt(), heap.removeMin().getDocumentAsTxt());
    }

    @Test
    public void heapify67(){
        MinHeapImpl<DocumentImpl> heap = new MinHeapImpl<>();
        heap.insert(this.txtdoc1);
        heap.insert(this.txtdoc2);
        heap.insert(this.txtdoc3);
        heap.insert(this.txtdoc4);
        heap.insert(this.txtdoc5);
        heap.insert(this.txtdoc6);
        heap.insert(this.txtdoc7);
        heap.insert(this.txtdoc8);
        heap.insert(this.txtdoc9);
        this.txtdoc6.setLastUseTime(System.nanoTime());
        heap.reHeapify(this.txtdoc6);
        this.txtdoc7.setLastUseTime(System.nanoTime());
        heap.reHeapify(this.txtdoc7);
        assertEquals(this.txtdoc1.getDocumentAsTxt(), heap.removeMin().getDocumentAsTxt());
        assertEquals(this.txtdoc2.getDocumentAsTxt(), heap.removeMin().getDocumentAsTxt());
        assertEquals(this.txtdoc3.getDocumentAsTxt(), heap.removeMin().getDocumentAsTxt());
        assertEquals(this.txtdoc4.getDocumentAsTxt(), heap.removeMin().getDocumentAsTxt());
        assertEquals(this.txtdoc5.getDocumentAsTxt(), heap.removeMin().getDocumentAsTxt());
        assertEquals(this.txtdoc8.getDocumentAsTxt(), heap.removeMin().getDocumentAsTxt());
        assertEquals(this.txtdoc9.getDocumentAsTxt(), heap.removeMin().getDocumentAsTxt());
        assertEquals(this.txtdoc6.getDocumentAsTxt(), heap.removeMin().getDocumentAsTxt());
        assertEquals(this.txtdoc7.getDocumentAsTxt(), heap.removeMin().getDocumentAsTxt());
    }
}