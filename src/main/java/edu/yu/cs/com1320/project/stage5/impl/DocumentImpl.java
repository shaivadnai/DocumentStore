package edu.yu.cs.com1320.project.stage5.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.lang.System;

import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import edu.yu.cs.com1320.project.stage5.Document;

public class DocumentImpl implements Document {
    private String txt;
    private int txtHash;
    private URI uri;
    private byte[] bytes;
    private Map<String, Integer> words;
    private Map<String, Integer> prefix;
    private long lastUseTime;

    private enum StringType {
        word, prefix;
    }

    // Text Document Constructor
    public DocumentImpl(URI uri, String txt, int txtHash) {
        if (txt == null) {
            throw new IllegalArgumentException("Document txt can not be null");
        }
        if (uri == null) {
            throw new IllegalArgumentException("Uri can not be null");
        }
        this.txt = txt;
        this.txtHash = txt.hashCode();
        this.uri = uri;
        this.bytes = this.getDocumentAsPdf();
        this.words = new HashMap<>();
        this.prefix = new HashMap<>();
        populateMaps(txt);
        this.lastUseTime = 0;
    }

    // PDF Document Constructor
    public DocumentImpl(URI uri, String txt, int txtHash, byte[] pdfBytes) {
        if (txt == null) {
            throw new IllegalArgumentException("Document txt can't be null");
        }
        if (uri == null) {
            throw new IllegalArgumentException("Uri can not be null");
        }
        this.txt = txt;
        this.txtHash = txt.hashCode();
        this.uri = uri;
        this.bytes = pdfBytes;
        this.words = new HashMap<>();
        this.prefix = new HashMap<>();
        populateMaps(txt);
        this.lastUseTime = 0;
    }

    private void populateMaps(String txt) {
        String uppercasetxt = txt.toUpperCase();

        char[] charArray = uppercasetxt.toCharArray();
        char[] wordarray = new char[uppercasetxt.length()];
        int wordarraycounter = 0;
        for (char i : charArray) {
            int charvalue = i;
            if (charvalue == 32) { // means that it is a space
                putStringInMap(wordarray, StringType.word);
                wordarray = new char[wordarray.length];
                wordarraycounter = 0;
                continue;
            }
            if (charvalue >= 48 && charvalue <= 57 || charvalue >= 65 && charvalue <= 90) {
                wordarray[wordarraycounter++] = i;
                putStringInMap(wordarray, StringType.prefix);
            }
        }
        putStringInMap(wordarray, StringType.word);
    }

    @Override
    public byte[] getDocumentAsPdf() {
        if (this.bytes != null) {
            return this.bytes;
        }
        PDDocument pdf = new PDDocument();
        PDPage page = new PDPage();
        pdf.addPage(page);
        PDFont font = PDType1Font.COURIER;
        PDPageContentStream content;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            content = new PDPageContentStream(pdf, page);
            content.beginText();
            content.setFont(font, 14);
            content.newLineAtOffset(100, 700);
            content.showText(this.getDocumentAsTxt());
            content.endText();
            content.close();
            pdf.save(output);
            pdf.close();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        } finally {
            IOUtils.closeQuietly(pdf);
        }
        // System.out.println(output.toString());
        return output.toByteArray();
    }

    @Override
    public String getDocumentAsTxt() {
        return this.txt;
    }

    @Override
    public int getDocumentTextHashCode() {
        return this.txtHash;
    }

    @Override
    public URI getKey() {
        return this.uri;
    }

    @Override
    public int wordCount(String word) {
        Integer occurences = this.words.get(word.toUpperCase());
        if (occurences == null) {
            return 0;
        } else {
            return occurences;
        }
    }

    protected Map<String, Integer> getPrefixMap() {
        return this.prefix;
    }

    protected int prefixCount(String prefix) {
        Integer occurences = this.prefix.get(prefix);
        if (occurences == null) {
            return 0;
        } else {
            return occurences;
        }
    }

    private void putStringInMap(char[] wordarray, StringType type) {
        String string = new String(wordarray).trim();
        if (string == "") {// if there were no valid charachters between last space and this one
            return;
        }
        if (type == StringType.word) {
            if (this.words.get(string) == null) {
                this.words.put(string, 1);
            } else {
                this.words.put(string, this.words.get(string) + 1);
            }
        }
        if (type == StringType.prefix) {
            if (this.prefix.get(string) == null) {
                this.prefix.put(string, 1);
            } else {
                this.prefix.put(string, this.prefix.get(string) + 1);
            }
        }
    }

    @Override
    public int compareTo(Document o) {
        if (this.getLastUseTime() < o.getLastUseTime()) {// older
            return -1;
        } else if (this.getLastUseTime() > o.getLastUseTime()) {// newer
            return 1;
        } else {// same
            return 0;
        }
    }

    @Override
    public long getLastUseTime() {
        return this.lastUseTime;
    }

    @Override
    public void setLastUseTime(long timeInNanoseconds) {
        this.lastUseTime = timeInNanoseconds;
    }

    @Override
    public void setWordMap(Map<String, Integer> wordMap) {
        this.words = wordMap;
    }

    @Override
    public Map<String, Integer> getWordMap() {
        return this.words;
    }
}