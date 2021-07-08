package dev.bloodcore.thread.listeners;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.OperationType;
import dev.bloodcore.Core;
import dev.bloodcore.etc.User;
import dev.bloodcore.ranks.Rank;
import dev.bloodcore.ranks.RankManager;
import dev.bloodcore.thread.DisablingThread;
import lombok.AllArgsConstructor;
import org.bson.Document;

@AllArgsConstructor
public class RankListenerThread extends DisablingThread {
    private final RankManager manager;

    @Override
    public void run() {
        MongoCursor<ChangeStreamDocument<Document>> iterator = Core.i().getMongoManager().getRanksCollection().watch().iterator();
        while (iterator.hasNext() && isEnabled()) {
            ChangeStreamDocument<Document> bson = iterator.next();
            if (!isEnabled()) {
                break;
            }
            if (bson.getDocumentKey() != null) {
                if (bson.getOperationType() == OperationType.INSERT || bson.getOperationType() == OperationType.UPDATE) {
                    Document data = Core.i().getMongoManager().getRanksCollection().find(bson.getDocumentKey()).first();
                    if (data != null) {
                        manager.updateRank(data, true);
                    }
                } else if (bson.getOperationType() == OperationType.DELETE) {
                    Rank rank = manager.getRank(bson.getDocumentKey().getString("_id").getValue());
                    if (rank != null && !rank.getId().equals("default")) {
                        for (User user : Core.i().getUsers()) {
                            if (user.getRank() == rank) {
                                user.setRank(manager.getRank("default"));
                                Core.i().getMongoManager().getUsersCollection().updateOne(Filters.eq("uuid", user.uuid()), new Document("$set", new Document("rank", "Default")));
                            }
                        }
                        manager.getRanks().remove(rank);
                        Core.i().rankLog("rank &6" + rank.getId() + " &ewas removed.");
                    }
                }
            }
        }
        System.out.println("breaking from RankListenerThread #" + getId());
    }
}
