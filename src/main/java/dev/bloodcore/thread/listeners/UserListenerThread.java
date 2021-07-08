package dev.bloodcore.thread.listeners;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.OperationType;
import dev.bloodcore.Core;
import dev.bloodcore.etc.User;
import dev.bloodcore.ranks.Rank;
import dev.bloodcore.thread.DisablingThread;
import org.bson.Document;

public class UserListenerThread extends DisablingThread {
    @Override
    public void run() {
        MongoCursor<ChangeStreamDocument<Document>> iterator = Core.i().getMongoManager().getUsersCollection().watch().iterator();
        while (iterator.hasNext() && isEnabled()) {
            ChangeStreamDocument<Document> bson = iterator.next();
            if (!isEnabled()) {
                break;
            }
            if (bson.getOperationType() == OperationType.UPDATE && bson.getDocumentKey() != null) {
                Document data = Core.i().getMongoManager().getUsersCollection().find(bson.getDocumentKey()).first();
                if (data != null) {
                    User user = Core.i().getUser(data.getString("name"));
                    if (user != null) {
                        Rank rank = Core.i().getRankManager().getRank(data.getString("rank"), true);
                        if (!rank.getId().equals(user.getRank().getId())) {
                            user.setRank(rank);
                            Core.i().rankLog("&6" + data.getString("name") + " &ehad rank set to &6" + rank.getId() + "&e.");
                        }
                        user.refreshPermissions(data);
                    }
                }
            }
        }
        System.out.println("breaking from UserListenerThread #" + getId());
    }
}
