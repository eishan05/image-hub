package com.eishanlawrence.firestore;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;

public final class FirestoreDatabaseReference {
  private static final Firestore firestore = createFirestoreReference();

  public static Firestore getFirestoreReference() {
    return firestore;
  }

  private static Firestore createFirestoreReference() {
    FirestoreOptions firestoreOptions =
        FirestoreOptions.getDefaultInstance()
            .toBuilder()
            .setProjectId(ProjectVariables.PROJECT_ID.getValue())
            .build();
    return firestoreOptions.getService();
  }

  private FirestoreDatabaseReference() {}
}
