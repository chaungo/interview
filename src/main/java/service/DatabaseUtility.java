package service;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

import util.Constant;
import util.PropertiesUtil;

public class DatabaseUtility {
	protected MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://"+PropertiesUtil.getString(Constant.DATABASE_HOST)+":"+PropertiesUtil.getString(Constant.DATABASE_PORT)));
	protected MongoDatabase  db = mongoClient.getDatabase(PropertiesUtil.getString(Constant.DATABASE_SCHEMA));
	protected ObjectMapper mapper = new ObjectMapper();
	
	public String getObjectId(Document dbObj){
		ObjectId id = (ObjectId) dbObj.get("_id");
		return (String) id.toString();
	}

}
