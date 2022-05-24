package objectHandler;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;

public class MongoHandler {
	
	Logger logger = LogManager.getLogger(MongoHandler.class.getName());
	
	public static void main(String[] args) {
		new MongoHandler().getMongoClient();
	}
	
	
	public void getMongoClients() {
		try {
			CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
					fromProviders(PojoCodecProvider.builder().automatic(true).build()));
			
			MongoClientSettings set= MongoClientSettings.builder().applyConnectionString(new ConnectionString("mongodb+srv://pankajkatyar:aOeDkDdAWBnYZFXW@core-nykaa-bspze.mongodb.net")).build();
			MongoClientSettings settings = MongoClientSettings.builder(set)
			        .codecRegistry(pojoCodecRegistry)
			        .build();
			
			
			MongoClient mongoClient = MongoClients.create(settings);
			
			MongoCollection<ObjectDefination> obj= mongoClient.getDatabase("ObjectRepo").getCollection("OR", ObjectDefination.class);
			//obj= obj.withCodecRegistry(pojoCodecRegistry);
			
			ObjectDefination o= obj.find().first() ;
			System.out.println(o.getPlatform().get("nykaaDesktop").get(0).getIdentifierValue());
			System.out.println(o.getObjectName());
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public MongoClient getMongoClient() {
		MongoClient mongoClient =null;
		try {
			CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
					fromProviders(PojoCodecProvider.builder().automatic(true).build()));
			
			MongoClientSettings set= MongoClientSettings.builder().applyConnectionString(new ConnectionString("mongodb+srv://pankajkatyar:aOeDkDdAWBnYZFXW@core-nykaa-bspze.mongodb.net")).build();
			MongoClientSettings settings = MongoClientSettings.builder(set)
			        .codecRegistry(pojoCodecRegistry)
			        .build();
			
			
			mongoClient = MongoClients.create(settings);
			logger.info("Mongo client object is created");
		}catch (Exception e) {
			logger.error("Exception while creating mongo client connection", e);
		}
		return mongoClient;
	}
	
	/**
	 * Method to get MongoDBObject for the given database
	 * @param mongoClient
	 * @param databaseName
	 * @return
	 */
	public MongoDatabase getMongoDBObject(MongoClient mongoClient, String databaseName) {
		MongoDatabase db =null;
		try {
			db= mongoClient.getDatabase(databaseName);
			if(db !=null) {
				logger.info("Mongo DB object is created for database="+databaseName);
			}else {
				logger.error("Mongo DB object is null for database="+databaseName);
			}
		}catch (Exception e) {
			logger.error("Exception while creating mongo database with name="+databaseName, e);
		}
		return db;
	}
	
	/**
	 * Method to get MongoDBCollection for Object Repo DB
	 * @param db
	 * @param collectionName
	 */
	public MongoCollection<ObjectDefination> getMongoDBCollectionForObjectRepo(MongoDatabase db, String collectionName) {
		MongoCollection<ObjectDefination> collection =null;
		try {
			collection= db.getCollection(collectionName, ObjectDefination.class);
			if(collection !=null) {
				logger.info("Mongo collection is created for Object");
			}else {
				logger.error("Mongo Collection object is null");
			}
		} catch (Exception e) {
			logger.error("Exception while creating mongo collection object for object repo",e);
		}
		return collection;
	}
	
	/**
	 * GetObjectDefination  object from mongodb find
	 * @param collectionObj
	 * @param objectName
	 * @return
	 */
	public ObjectDefination getObjectFromRepo(MongoCollection<ObjectDefination> collectionObj, String objectName) {
		ObjectDefination objectDefination =null;
		try {
			//String query = "{\"objectName\":\""+objectName+"\"}";
			objectDefination = collectionObj.find(Filters.eq("objectName", objectName)).first();
		}catch (Exception e) {
			logger.error("Exception while getting object from object repo DB",e);
		}
		return objectDefination;
	}
	
	/**
	 * GetObjectDefination  object from mongodb find
	 * @param collectionObj
	 * @param objectName
	 * @return
	 */
	public ObjectDefination removeObjectFromRepo(MongoCollection<ObjectDefination> collectionObj, String objectName) {
		ObjectDefination objectDefination =null;
		try {
			//String query = "{\"objectName\":\""+objectName+"\"}";
			DeleteResult result= collectionObj.deleteMany(Filters.eq("objectName", objectName));
			long delete_Count= result.getDeletedCount();
			logger.info(objectName+" deleted with count ="+delete_Count);
			objectDefination = collectionObj.findOneAndDelete(Filters.eq("objectName", objectName));
		}catch (Exception e) {
			logger.error("Exception while removing object from mongo db",e);
		}
		return objectDefination;
	}
	
	/**
	 * Method to insert ObjectDocument into mongoDB
	 * @param collection
	 * @param objectDefination
	 */
	public void insertObjectIntoMongo(MongoCollection<ObjectDefination> collection,ObjectDefination objectDefination) {
		try {
			collection.insertOne(objectDefination);
		} catch (Exception e) {
			logger.error("Exception while inserting object into mongo Object Repo",e);
		}
		
	}
	
	/**
	 * Method to get if required channel is in object defination or not
	 * @param objectDefination
	 * @param channel
	 * @return
	 */
	public boolean ifChannelExistInObject(ObjectDefination objectDefination, String channel) {
		boolean flag =false;
		try {
			Map<String, List<Identifier>> mapOfPlatform = objectDefination.getPlatform();
			if(mapOfPlatform.containsKey(channel)) {
				flag =true;
			}
		} catch (Exception e) {
			logger.error("Exception while getting object channel from mongo", e);
		}
		return flag;
	}
	

}
