package util;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;

import service.DatabaseUtility;

public class AdminUtility extends DatabaseUtility {
    private static final String PRODUCT_COLLECTION = "Product";
    private static final String CYCLE_COLLECTION = "Cycle";
    private static final String NAME = "name";
    private static AdminUtility INSTANCE = new AdminUtility();
    private MongoCollection<Document> productCollection;
    private MongoCollection<Document> cycleCollection;

    private AdminUtility() {
        productCollection = db.getCollection(PRODUCT_COLLECTION);
        cycleCollection = db.getCollection(CYCLE_COLLECTION);
    }

    public static AdminUtility getInstance() {
        return INSTANCE;
    }

    public boolean insertProduct(String product) {
        if(getAllProduct().contains(product)){
            return false;
        }
        Document document = new Document(NAME, product);
        productCollection.insertOne(document);
        return true;
    }

    public Set<String> getAllProduct() {
        Set<String> products = new HashSet<>();
        FindIterable<Document> documents = productCollection.find();
        documents.forEach(new Consumer<Document>() {
            @Override
            public void accept(Document doc) {
                products.add((String) doc.get(NAME));
            }
        });
        return products;
    }

    public long deleteProduct(String product) {
        Document document = new Document(NAME, product);
        DeleteResult result = productCollection.deleteOne(document);
        return result.getDeletedCount();
    }

    public static void main(String[] args) {
        AdminUtility.INSTANCE.insertProduct("ANV");
        System.out.println(AdminUtility.INSTANCE.getAllProduct());
    }

    public Set<String> getAllCycle() {
        Set<String> cycles = new HashSet<>();
        FindIterable<Document> documents = cycleCollection.find();
        documents.forEach(new Consumer<Document>() {
            @Override
            public void accept(Document doc) {
                cycles.add((String) doc.get(NAME));
            }
        });
        return cycles;
    }

    public boolean insertCycle(String cycle) {
        if(getAllProduct().contains(cycle)){
            return false;
        }
        Document document = new Document(NAME, cycle);
       cycleCollection.insertOne(document);
        return true;
    }

    public long deleteCycle(String cycle) {
        Document document = new Document(NAME, cycle);
        DeleteResult result = cycleCollection.deleteOne(document);
        return result.getDeletedCount();
    }
}
