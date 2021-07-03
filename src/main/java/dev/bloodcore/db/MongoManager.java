package dev.bloodcore.db;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Collation;
import com.mongodb.client.model.CollationStrength;
import com.mongodb.client.model.Filters;
import dev.bloodcore.Core;
import dev.bloodcore.etc.User;
import lombok.Getter;
import org.bson.Document;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
public class MongoManager {
    private final MongoClient client;
    private final MongoCollection<Document> usersCollection;
    private final MongoCollection<Document> ranksCollections;

    public MongoManager() {
        Logger.getLogger("org.mongodb.driver").setLevel(Level.OFF);

        client = MongoClients.create(Core.i().getConfig().getString("mongo-db.connection-string"));
        MongoDatabase database = client.getDatabase("blood_core");
        usersCollection = database.getCollection("users");
        ranksCollections = database.getCollection("ranks");
    }

    public void updateUser(User user) {
        Document update = new Document("name", user.name()).append("ip", user.ip()).append("display", user.getRank().getColor() + user.name());

        usersCollection.updateOne(Filters.eq("uuid", user.uuid()), new Document("$set", update));
    }

    public Document getData(String name) {
        return usersCollection.find(new Document("name", name)).collation(Collation.builder().locale("en").collationStrength(CollationStrength.PRIMARY).build()).first();
    }

    public Document getData(UUID uuid) {
        return usersCollection.find(new Document("uuid", uuid.toString())).first();
    }
}
