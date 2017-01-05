package util.gadget;

import org.bson.Document;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;

import manament.log.LoggerWapper;
import models.UserVO;
import models.exception.APIException;
import service.DatabaseUtility;
import util.JSONUtil;

public class UserUtility extends DatabaseUtility {
    final static LoggerWapper logger = LoggerWapper.getLogger(GadgetUtility.class);
    public static UserUtility INSTANCE = new UserUtility();
    protected MongoCollection<Document> collection;

    private UserUtility() {
        super();
        collection = db.getCollection(UserVO.class.getSimpleName());
    }

    public static UserUtility getInstance() {
        return INSTANCE;
    }

    public UserVO find(String username, String friendlyName) throws APIException {
        UserVO userVO = null;
        BasicDBObject whereQuery = new BasicDBObject();
        whereQuery.put("username", username);
        FindIterable<Document> documents = collection.find(whereQuery);
        Document document = documents.first(); 
        if (document!=null) {
            userVO = JSONUtil.getInstance().convertJSONtoObject(document.toJson(), UserVO.class);
            userVO.setId(getObjectId(document));
        } else {
            userVO = new UserVO(username, friendlyName);
            UpdateResult result = insert(userVO);
            if (result.getModifiedCount() != 0) {
                userVO.setId((result.getUpsertedId()).asObjectId().toString());
            }
        }
        return userVO;
    }
    public UpdateResult insert(UserVO user) throws APIException {
        try {
            Document dbObject =  Document.parse(mapper.writeValueAsString(user));
            dbObject.remove("id");

            BasicDBObject searchQuery = new BasicDBObject();
            searchQuery.append("username", user.getUsername());

            return collection.updateOne(searchQuery, dbObject);
        } catch (JsonProcessingException e) {
            logger.fastDebug("Error during parse JSON", e);
            throw new APIException("cannot insert gadget", e);
        }
    }

}
