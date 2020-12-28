package edu.yu.cs.com1320.project.stage5.impl;

import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.PersistenceManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * created by the document store and given to the BTree via a call to
 * BTree.setPersistenceManager
 */
public class DocumentPersistenceManager implements PersistenceManager<URI, Document> {

    private String basePath;

    public DocumentPersistenceManager(File baseDir) {
        if (baseDir == null) {
            this.basePath = System.getProperty("user.dir");
        } else {
            this.basePath = baseDir.getPath();
        }
    }

    protected boolean isOnDisk(URI uri){
        boolean onDisk = false;
        Document doc;
        try{
            doc = deserialize(uri);
            if(doc != null){
                onDisk = true;
                serialize(uri, doc);
            }
        }
        catch(Exception e){
            onDisk = false;
        }
        return onDisk;
    }

    @Override
    public void serialize(URI uri, Document val) throws IOException {
        Gson gson = new GsonBuilder().addSerializationExclusionStrategy(new ExcludeDocumentFields()).registerTypeAdapter(Document.class, new DocumentSerializer()).create();
        String json = gson.toJson(val);
        String path = this.basePath + File.separator + uri.getHost() + uri.getPath() + ".json";
        File file = new File(path);
        try {
            file.getParentFile().mkdirs();
            file.createNewFile();
            FileOutputStream out = new FileOutputStream(file);
            out.write(json.getBytes());
            out.close();
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("Path is invalid" + e);
        }
    }

    @Override
    public Document deserialize(URI uri) throws IOException {
        String path = this.basePath + File.separator + uri.getHost() + uri.getPath() + ".json";
        File file = new File(path);
        String data;
        Document doc = null;
        data = new String(Files.readAllBytes(Paths.get(path)));
        Gson gson = new GsonBuilder().registerTypeAdapter(Document.class, new DocumentDeserializer()).create();
        doc = gson.fromJson(data, DocumentImpl.class);
        Path toDelete = file.toPath();
        while(!toDelete.toString().equals(this.basePath)){
            File delete = toDelete.toFile();
            delete.delete();
            toDelete = toDelete.getParent();
        }
        return doc;
    }

    private class DocumentSerializer implements JsonSerializer<Document> {

        @Override
        public JsonElement serialize(Document src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonDocument = new JsonObject();
            jsonDocument.addProperty("txt", src.getDocumentAsTxt());
            jsonDocument.addProperty("map", src.getWordMap().toString());
            jsonDocument.addProperty("txtHashcode", src.getDocumentTextHashCode());
            jsonDocument.addProperty("uri", src.getKey().toString());
            return jsonDocument;
        }

    }

    private class DocumentDeserializer implements JsonDeserializer<Document> {

        @Override
        public Document deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            String txt = json.getAsJsonObject().get("txt").getAsString();
            URI uri = URI.create(json.getAsJsonObject().get("uri").getAsString());
            Map<String, Integer> map = jsonStringToMap(json.getAsJsonObject().get("map").getAsString());
            int hashCode = json.getAsJsonObject().get("txtHashcode").getAsInt();
            Document doc = new DocumentImpl(uri, txt, hashCode);
            doc.setWordMap(map);
            return doc;
        }
        private Map<String, Integer> jsonStringToMap(String json) {
            Map<String, Integer> map = new HashMap<>();
            String[] pairs = json.split(",");
            for (int i = 0; i < pairs.length; i++) {
                String pair = pairs[i];
                String[] keyValue = pair.split(":");
                map.put(keyValue[0], Integer.valueOf(keyValue[1]));
            }
            return map;
        }

        
    }
    private class ExcludeDocumentFields implements ExclusionStrategy {

        @Override
        public boolean shouldSkipField(FieldAttributes f) {
            if(f.getName().equals("txt")){
                return false;
            }
            else if(f.getName().equals("words")){
                return false;
            }
            else if(f.getName().equals("uri")){
                return false;
            }
            else if(f.getName().equals("txtHash")){
                return false;
            }
            return true;
        }

        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
            return false;
        }
        
    }
}
