package dev.bloodcore.db;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Collation;
import com.mongodb.client.model.CollationStrength;
import com.mongodb.client.model.Filters;
import dev.bloodcore.etc.Config;
import dev.bloodcore.etc.User;
import lombok.Getter;
import org.bson.Document;

@Getter
public class MongoManager {
    private final MongoClient client;
    private final MongoCollection<Document> usersCollection;
    private final MongoCollection<Document> ranksCollection;
    private final MongoCollection<Document> punishmentsCollection;

    public MongoManager() {
        client = MongoClients.create(Config.MONGO_CONNECTION.getString());
        MongoDatabase database = client.getDatabase("blood_core");
        usersCollection = database.getCollection("users");
        ranksCollection = database.getCollection("ranks");
        punishmentsCollection = database.getCollection("punishments");

        if (punishmentsCollection.find(new Document("_id", "last_id")).first() == null) {
            punishmentsCollection.insertOne(new Document("_id", "last_id").append("value", 0));
        }
    }

    public void updateUser(User user) {
        Document update = new Document("name", user.name()).append("ip", user.ip()).append("display", user.getRank().getColor() + user.name());

        usersCollection.updateOne(Filters.eq("uuid", user.uuid()), new Document("$set", update));
    }

    public Document getUserFromName(String name) {
        return usersCollection.find(new Document("name", name)).collation(Collation.builder().locale("en").collationStrength(CollationStrength.PRIMARY).build()).first();
    }

    public Document getUserFromUUID(String uuid) {
        return usersCollection.find(new Document("uuid", uuid)).first();
    }
}
