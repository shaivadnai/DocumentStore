package edu.yu.cs.com1320.project.stage5.impl;

import edu.yu.cs.com1320.project.stage5.*;
import edu.yu.cs.com1320.project.impl.*;

import java.io.*;
import java.net.URI;
import java.util.*;
import java.util.function.Function;

import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import edu.yu.cs.com1320.project.*;

public class DocumentStoreImpl implements DocumentStore {

    protected BTreeImpl<URI, Document> btree;
    private StackImpl<Undoable> stack;
    private TrieImpl<URI> trie;
    private int documentBytes;
    private int documentCount;
    private Integer maxBytes;
    private Integer maxDocs;
    private MinHeap<HeapElement> heap;
    private Set<URI> urisInMemory;

    public DocumentStoreImpl() {
        this.btree = new BTreeImpl<>();
        btree.setPersistenceManager(new DocumentPersistenceManager(null));
        setMostInstanceVariables();
    }

    public DocumentStoreImpl(File baseDir) {
        this.btree = new BTreeImpl<>();
        btree.setPersistenceManager(new DocumentPersistenceManager(baseDir));
        setMostInstanceVariables();
    }

    private void setMostInstanceVariables() {
        this.stack = new StackImpl<>();
        this.trie = new TrieImpl<>();
        this.maxBytes = null;
        this.maxDocs = null;
        this.heap = new MinHeapImpl<>();
        this.documentBytes = 0;
        this.documentCount = 0;
        this.urisInMemory = new HashSet<>();
        this.btree.put(URI.create(""), null);
    }

    @Override
    public int putDocument(InputStream input, URI uri, DocumentFormat format) {
        if (uri == null || format == null) {
            throw new IllegalArgumentException("URI and/or format can not be null");
        }
        if (input == null) {
            return putDelete(uri);
        }
        Document doc;
        String txt;
        byte[] bytes = inputStreamToByteArray(input);
        if (format == DocumentFormat.PDF) {
            txt = pdfBytesToString(bytes).trim();
            doc = new DocumentImpl(uri, txt, txt.hashCode(), bytes);
        } else {
            txt = new String(bytes).trim();
            doc = new DocumentImpl(uri, txt, txt.hashCode());
        }
        setDocumentTime(doc);
        if (this.btree.get(uri) == null) {
            return putNew(doc);
        } else {
            return putReplace(doc);
        }
    }

    private int putNew(Document doc) {
        makeRoom(doc);
        insertAll(doc);
        addCommandToStack(doc.getKey(), undoPutNew);
        return 0;
    }

    private int putDelete(URI uri) {
        Document doc = getFromBTree(uri, false);
        if (doc == null) {
            addCommandToStack(uri, undoNoOp);
            return 0;
        }
        int oldHashCode = doc.getDocumentTextHashCode();
        addCommandToStack(uri, undoDocumentDelete.apply(uri));
        removeAll(doc);
        return oldHashCode;
    }

    private void addToCounts(Document doc) {
        this.documentCount++;
        this.documentBytes = this.documentBytes + countBytes(doc);
    }

    private void subtractFromCounts(Document doc) {
        this.documentCount--;
        this.documentBytes = this.documentBytes - countBytes(doc);
    }

    private void makeRoom(Document doc) {
        if (maxBytes == null && maxDocs == null) {
            return;
        }
        if (maxBytes == null) {
        } else {
            if (maxBytes >= 0) {
                while (maxBytes < documentBytes + countBytes(doc)) {
                    moveOldestToDisk();
                }
            }
        }
        if (maxDocs == null) {
        } else {
            if (maxDocs >= 0) {
                while (maxDocs < documentCount + 1) {
                    moveOldestToDisk();
                }
            }
        }
    }

    private void moveOldestToDisk() {
        HeapElement element = this.heap.removeMin();
        URI uri = element.getUri();
        this.urisInMemory.remove(uri);
        Document doc = getFromBTree(uri, true);
        subtractFromCounts(doc);
        try {
            this.btree.moveToDisk(uri);
        } catch (Exception e) {
            throw new IllegalArgumentException("Something went wrong during serialization");
        }
    }

    private int countBytes(Document doc) {
        return doc.getDocumentAsTxt().getBytes().length + doc.getDocumentAsPdf().length;
    }

    private void insertAll(Document doc) {
        setDocumentTime(doc);
        btreeInsert(doc);
        heapInsert(doc);
        trieInsert(doc);
    }

    private void heapInsert(Document doc) {
        HeapElement element = new HeapElement(doc);
        this.heap.insert(element);
        this.urisInMemory.add(doc.getKey());
        this.heap.reHeapify(element);
        addToCounts(doc);
    }

    private void btreeInsert(Document doc) {
        this.btree.put(doc.getKey(), doc);
    }

    private void trieInsert(Document doc) {
        Set<String> words = doc.getWordMap().keySet();
        URI uri = doc.getKey();
        for (String word : words) {
            this.trie.put(word, uri);
        }
    }

    private int putReplace(Document doc) {
        Document oldDoc = getFromBTree(doc.getKey(), false);
        addCommandToStack(doc.getKey(), undoPutReplace.apply(doc.getKey()));
        removeAll(oldDoc);
        insertAll(doc);
        return oldDoc.getDocumentTextHashCode();
    }

    private Document getFromBTree(URI uri, boolean isInMemory) {
        if (isInMemory == false) {
            isInMemory = this.urisInMemory.contains(uri);
        }
        if (isInMemory) {
            Document doc = this.btree.get(uri);
            updateAndHeapify(doc);
            return doc;
        } else {
            Document doc = this.btree.get(uri);
            if (doc == null) {
                return null;
            }
            makeRoom(doc);
            setDocumentTime(doc);
            heapInsert(doc);
            return doc;
        }
    }

    private void removeAll(Document doc) {
        trieRemove(doc);
        heapRemove(doc);
        subtractFromCounts(doc);
        btreeRemove(doc);
    }

    private void btreeRemove(Document doc) {
        this.btree.put(doc.getKey(), null);
    }

    private void trieRemove(Document doc) {
        Set<String> words = doc.getWordMap().keySet();
        URI uri = doc.getKey();
        for (String word : words) {
            this.trie.delete(word, uri);
        }
    }

    private void heapRemove(Document doc) {
        doc.setLastUseTime(Long.MIN_VALUE);
        this.heap.reHeapify(new HeapElement(doc));
        this.heap.removeMin();
        this.urisInMemory.remove(doc.getKey());
    }

    private byte[] inputStreamToByteArray(InputStream input) {
        byte[] bytes;
        try {
            bytes = new byte[input.available()];
        } catch (IOException e1) {
            throw new IllegalArgumentException(e1);
        }
        try {
            input.read(bytes);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
        return bytes;
    }

    private String pdfBytesToString(byte[] bytes) {
        PDDocument doc = new PDDocument();
        try {
            doc.close();
            doc = PDDocument.load(bytes);
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(doc);
            doc.close();
            return text;
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        } finally {
            IOUtils.closeQuietly(doc);
        }
    }

    private void updateAndHeapify(Document doc) {
        setDocumentTime(doc);
        this.heap.reHeapify(new HeapElement(doc));
    }

    private void setDocumentTime(Document doc) {
        doc.setLastUseTime(System.nanoTime());
    }

    @Override
    public byte[] getDocumentAsPdf(URI uri) {
        Document doc = getFromBTree(uri, false);
        if (doc == null) {
            return null;
        } else {
            updateAndHeapify(doc);
            return doc.getDocumentAsPdf();
        }
    }

    @Override
    public String getDocumentAsTxt(URI uri) {
        Document doc = getFromBTree(uri, false);
        if (doc == null) {
            return null;
        } else {
            updateAndHeapify(doc);
            return doc.getDocumentAsTxt();
        }
    }

    @Override
    public boolean deleteDocument(URI uri) {
        Document doc = getFromBTree(uri, false);
        if (doc == null) {
            addCommandToStack(uri, undoNoOp);
            return false;
        } else {
            addCommandToStack(uri, undoDocumentDelete.apply(uri));
            removeAll(doc);
            return true;
        }
    }

    protected Document getDocument(URI uri) {
        Document doc = this.btree.get(uri);
        if (doc == null) {
            return null;
        }
        if (urisInMemory.contains(uri)) {
            return doc;
        }
        try {
            this.btree.moveToDisk(uri);
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not move document to disk");
        }
        return doc;
    }

    @Override
    public void undo() throws IllegalStateException {
        if (this.stack.size() == 0) {
            throw new IllegalStateException("Command Stack is Empty");
        } else {
            this.stack.pop().undo();
        }
    }

    @Override
    public void undo(URI uri) throws IllegalStateException {
        if (this.stack.size() == 0) {
            throw new IllegalStateException("Undo Stack is Empty");
        }
        StackImpl<Undoable> temp = new StackImpl<>();
        boolean commandExists = false;
        while (this.stack.size() > 0 && commandExists == false) {
            Undoable command = this.stack.pop();
            if (command.getClass().equals(GenericCommand.class)) {
                GenericCommand<URI> genericCommand = (GenericCommand<URI>) command;
                if (genericCommand.getTarget().equals(uri)) {
                    genericCommand.undo();
                    commandExists = true;
                } else {
                    temp.push(command);
                }
            } else {
                CommandSet<URI> commandSet = (CommandSet<URI>) command;
                if (commandSet.containsTarget(uri)) {
                    commandSet.undo(uri);
                    commandExists = true;
                }
                if (commandSet.size() > 0) {
                    temp.push(commandSet);
                }
            }
        }
        while (temp.size() != 0) {
            this.stack.push(temp.pop());
        }
        if (commandExists == false) {
            throw new IllegalStateException("There was no command with this URI");
        }
    }

    @Override
    public List<String> search(String keyword) {
        if (keyword == null) {
            return new ArrayList<>();
        }
        List<URI> uris = trie.getAllSorted(keyword.toUpperCase(), new KeywordSorter<>(keyword.toUpperCase()));
        List<Document> docs = urisToDocuments(uris);
        setListTime(docs);
        List<String> strings = new ArrayList<>();
        for (Document doc : docs) {
            strings.add(doc.getDocumentAsTxt());
        }
        return strings;
    }

    private List<Document> urisToDocuments(List<URI> uris) {
        List<Document> docs = new ArrayList<>();
        for (URI uri : uris) {
            Document doc = getFromBTree(uri, false);
            updateAndHeapify(doc);
            docs.add(doc);

        }
        return docs;
    }

    private void setListTime(List<Document> docs) {
        long time = System.nanoTime();
        for (Document doc : docs) {
            doc.setLastUseTime(time);
            this.heap.reHeapify(new HeapElement(doc));
        }
    }

    @Override
    public List<byte[]> searchPDFs(String keyword) {
        if (keyword == null) {
            return new ArrayList<>();
        }
        List<URI> uris = trie.getAllSorted(keyword.toUpperCase(), new KeywordSorter<>(keyword.toUpperCase()));
        List<Document> docs = urisToDocuments(uris);
        setListTime(docs);
        List<byte[]> bytes = new ArrayList<>();
        for (Document doc : docs) {
            bytes.add(doc.getDocumentAsPdf());
        }
        return bytes;
    }

    @Override
    public List<String> searchByPrefix(String keywordPrefix) {
        if (keywordPrefix == null) {
            return new ArrayList<>();
        }
        List<URI> uris = trie.getAllWithPrefixSorted(keywordPrefix.toUpperCase(),
                new PrefixSorter<>(keywordPrefix.toUpperCase()));
        List<Document> docs = urisToDocuments(uris);
        setListTime(docs);
        List<String> strings = new ArrayList<>();
        for (Document doc : docs) {
            strings.add(doc.getDocumentAsTxt());
        }
        return strings;
    }

    @Override
    public List<byte[]> searchPDFsByPrefix(String keywordPrefix) {
        if (keywordPrefix == null) {
            return new ArrayList<>();
        }
        List<URI> uris = trie.getAllWithPrefixSorted(keywordPrefix.toUpperCase(),
                new PrefixSorter<>(keywordPrefix.toUpperCase()));
        List<Document> docs = urisToDocuments(uris);
        setListTime(docs);
        List<byte[]> bytes = new ArrayList<>();
        for (Document doc : docs) {
            bytes.add(doc.getDocumentAsPdf());
        }
        return bytes;
    }

    @Override
    public Set<URI> deleteAll(String keyword) {
        if (keyword == null) {
            return new HashSet<>();
        }
        Set<URI> uris = this.trie.deleteAll(keyword.toUpperCase());
        Set<Document> docs = urisToDocuments(uris);
        deleteSetContents(docs);
        return uris;
    }

    private void deleteSetContents(Set<Document> docs) {
        CommandSet<URI> commandset = new CommandSet<>();
        for (Document doc : docs) {
            GenericCommand<URI> command = new GenericCommand<URI>(doc.getKey(), undoDocumentDelete.apply(doc.getKey()));
            commandset.addCommand(command);
            removeAll(doc);
        }
        this.stack.push(commandset);
    }

    private Set<Document> urisToDocuments(Set<URI> uris) {
        Set<Document> docs = new HashSet<>();
        for (URI uri : uris) {
            docs.add(getFromBTree(uri, false));
        }
        return docs;
    }

    @Override
    public Set<URI> deleteAllWithPrefix(String keywordPrefix) {
        if (keywordPrefix == null) {
            return new HashSet<>();
        }
        Set<URI> uris = this.trie.deleteAllWithPrefix(keywordPrefix.toUpperCase());
        Set<Document> docs = urisToDocuments(uris);
        deleteSetContents(docs);
        return uris;
    }

    @Override
    public void setMaxDocumentCount(int limit) {
        if (limit < 0) {
            throw new IllegalArgumentException();
        }
        this.maxDocs = limit;
        clearMemoryLimitChanged();
    }

    @Override
    public void setMaxDocumentBytes(int limit) {
        if (limit < 0) {
            throw new IllegalArgumentException();
        }
        this.maxBytes = limit;
        clearMemoryLimitChanged();
    }

    private void clearMemoryLimitChanged() {
        if (this.maxBytes == null && this.maxDocs == null) {
            return;
        }
        if (this.maxBytes == null) {
        } else {
            if (maxBytes >= 0) {
                while (this.maxBytes < this.documentBytes) {
                    moveOldestToDisk();
                }
            }
        }
        if (this.maxDocs == null) {
        } else {
            if (this.maxDocs >= 0) {
                while (this.maxDocs < this.documentCount) {
                    moveOldestToDisk();
                }
            }
        }
    }

    private void addCommandToStack(URI uri, Function<URI, Boolean> lambda) {
        GenericCommand<URI> command = new GenericCommand<URI>(uri, lambda);
        this.stack.push(command);
    }

    Function<URI, Boolean> undoPutNew = (URI uri) -> {
        Document doc = getFromBTree(uri, false);
        if (doc == null) {
            return false;
        }
        removeAll(doc);
        return true;
    };

    Function<URI, Function<URI, Boolean>> undoDocumentDelete = (URI uri) -> {
        Document doc = getFromBTree(uri, false);
        Function<URI, Boolean> function;
        if (doc == null) {
            function = (URI uri1) -> {
                return false;
            };
            return function;
        } else {
            function = (URI uri1) -> {
                makeRoom(doc);
                insertAll(doc);
                return true;
            };
        }
        return function;
    };

    Function<URI, Function<URI, Boolean>> undoPutReplace = (URI uri) -> {
        Document doc = getFromBTree(uri, false);
        Function<URI, Boolean> function;
        if (doc == null) {
            function = (URI uri1) -> {
                return false;
            };
        } else {
            function = (URI uri1) -> {
                Document oldDoc = getFromBTree(uri1, false);
                removeAll(oldDoc);
                insertAll(doc);
                return true;
            };
        }
        return function;
    };

    Function<URI, Boolean> undoNoOp = (URI uri) -> {
        return true;
    };

    private class KeywordSorter<E> implements Comparator<URI> {
        private String word;

        private KeywordSorter(String wrd) {
            this.word = wrd;
        }

        @Override
        public int compare(URI o1, URI o2) {
            Document one;
            Document two;
            one = getFromBTree(o1, false);
            two = getFromBTree(o2, false);

            int firstArgument = one.wordCount(this.word);
            int secondArgument = two.wordCount(this.word);

            if (firstArgument < secondArgument) {
                return 1;
            } else if (firstArgument == secondArgument) {
                return 0;
            } else {
                return -1;
            }
        }
    }

    private class PrefixSorter<E> implements Comparator<URI> {
        private String prefix;

        private PrefixSorter(String prfx) {
            this.prefix = prfx;
        }

        @Override
        public int compare(URI o1, URI o2) {
            DocumentImpl one;
            DocumentImpl two;

            one = (DocumentImpl) getFromBTree(o1, false);
            two = (DocumentImpl) getFromBTree(o2, false);

            int firstArgument = one.getPrefixMap().get(this.prefix);
            int secondArgument = two.getPrefixMap().get(this.prefix);

            if (firstArgument < secondArgument) {
                return 1;
            } else if (firstArgument == secondArgument) {
                return 0;
            } else {
                return -1;
            }
        }
    }

    private class HeapElement implements Comparable<HeapElement> {
        private URI uri;

        public HeapElement(Document doc) {
            this.uri = doc.getKey();
        }

        public URI getUri() {
            return this.uri;
        }

        @Override
        public int compareTo(HeapElement o) {
            long one = btree.get(this.getUri()).getLastUseTime();
            long two = btree.get(o.getUri()).getLastUseTime();
            if (one < two) {// older
                return -1;
            } else if (one > two) {// newer
                return 1;
            } else {// same
                return 0;
            }
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof HeapElement)) {
                return false;
            }
            return this.getUri().equals(((HeapElement) obj).getUri());
        }

    }
}