package util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import manament.log.LoggerWapper;
import models.SessionInfo;
import models.exception.APIException;
import models.main.ReleaseVO;
import org.bson.Document;
import service.DatabaseUtility;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import static util.Constant.NAME;

public class AdminUtility extends DatabaseUtility {
    private static final LoggerWapper logger = LoggerWapper.getLogger(AdminUtility.class);

    private static final String PRODUCT_COLLECTION = "Product";
    private static final String RELEASE_COLLECTION = "GreenHopperRelease";
    private static AdminUtility INSTANCE = new AdminUtility();
    private MongoCollection<Document> productCollection;
    private MongoCollection<Document> releaseCollection;

    private AdminUtility() {
        productCollection = db.getCollection(PRODUCT_COLLECTION);
        releaseCollection = db.getCollection(RELEASE_COLLECTION);
    }

    public static AdminUtility getInstance() {
        return INSTANCE;
    }

    public static void main(String[] args) {
        AdminUtility.INSTANCE.insertProduct("ANV");
        System.out.println(AdminUtility.INSTANCE.getAllProduct());
    }

    public boolean insertProduct(String product) {
        if (getAllProduct().contains(product)) {
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

    public Set<String> getAllRelease() {
        Set<String> releases = new HashSet<>();
        FindIterable<Document> documents = releaseCollection.find();
        documents.forEach(new Consumer<Document>() {
            @Override
            public void accept(Document doc) {
                releases.add((String) doc.get(NAME));
            }
        });
        return releases;
    }

    public boolean insertRelease(String release, SessionInfo sessionInfo) throws APIException {
        if (release == null || getAllRelease().contains(release)) {
            return false;
        }

        ReleaseVO releaseVO = new ReleaseVO();
        releaseVO.setName(release);
        releaseVO.setDate(new Date());
        releaseVO.setUsername(sessionInfo.getUsername());

        Document document;
        try {
            document = Document.parse(mapper.writeValueAsString(releaseVO));
            releaseCollection.insertOne(document);
        } catch (JsonProcessingException e) {
            logger.fastDebug("error during mapper.writeValueAsString", e);
            throw new APIException("cannot insert release", e);
        }
        return true;
    }

    public long deleteRelease(String release) {
        Document document = new Document(NAME, release);
        DeleteResult result = releaseCollection.deleteOne(document);
        return result.getDeletedCount();
    }
}
