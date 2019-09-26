package com.eishanlawrence.firestore;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.common.base.Preconditions;

import java.util.List;

public final class FirestoreUtils {

  /**
   * Delete a collection in batches to avoid out-of-memory errors. Batch size may be tuned based on
   * document size (atmost 1MB) and application requirements.
   */
  public static void deleteCollection(CollectionReference collection, int batchSize) {
    try {
      // retrieve a small batch of documents to avoid out-of-memory errors
      ApiFuture<QuerySnapshot> future = collection.limit(batchSize).get();
      int deleted = 0;
      // future.get() blocks on document retrieval
      List<QueryDocumentSnapshot> documents = future.get().getDocuments();
      for (QueryDocumentSnapshot document : documents) {
        document.getReference().delete();
        ++deleted;
      }
      if (deleted >= batchSize) {
        // retrieve and delete another batch
        deleteCollection(collection, batchSize);
      }
    } catch (Exception e) {
      System.err.println("Error deleting collection : " + e.getMessage());
    }
  }

  /**
   * Authenticates a user given their document in the database and the authKey provided by them
   * @param documentSnapshot User's document in Firestore
   * @param authKeyByUser AuthKey provided by the user
   * @return true if the user's authKey matches that in the database
   */
  public static boolean authenticateUser(DocumentSnapshot documentSnapshot, String authKeyByUser) {
    Preconditions.checkNotNull(authKeyByUser);
    String authKeyInDatabase = (String) documentSnapshot.get("ApiKey");
    return authKeyInDatabase.equals(authKeyByUser);
  }

  private FirestoreUtils() {}
}