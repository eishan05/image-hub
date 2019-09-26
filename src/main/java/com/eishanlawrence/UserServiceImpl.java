package com.eishanlawrence;

import com.eishanlawrence.firestore.FirestoreDatabaseReference;
import com.eishanlawrence.firestore.FirestoreUtils;
import com.eishanlawrence.utils.Utilities;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.protobuf.Timestamp;
import io.grpc.stub.StreamObserver;
import java.util.HashMap;
import java.util.Map;

public final class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase {

  @Override
  public void createUser(
      ImageHub.CreateUserRequest request,
      StreamObserver<ImageHub.CreateUserResponse> responseObserver) {
    String email = request.getEmail();
    Firestore db = FirestoreDatabaseReference.getFirestoreReference();
    DocumentReference documentReference =
        db.collection(FirestoreUtils.USERS_COLLECTION_ID).document(email);
    ImageHub.CreateUserResponse response = getCreateUserResponse(documentReference);
    if (response.getSuccess()) {
      addToDatabase(documentReference, email, response);
    }
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void deleteUser(
      ImageHub.DeleteUserRequest request,
      StreamObserver<ImageHub.DeleteUserResponse> responseObserver) {
    String email = request.getEmail();
    Firestore db = FirestoreDatabaseReference.getFirestoreReference();
    DocumentReference documentReference =
        db.collection(FirestoreUtils.USERS_COLLECTION_ID).document(email);
    ImageHub.DeleteUserResponse response = getDeleteUserResponse(documentReference, request);
    if (response.getSuccess()) {
      deleteFromDatabase(documentReference);
    }
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  private static ImageHub.DeleteUserResponse getDeleteUserResponse(
      final DocumentReference documentReference, ImageHub.DeleteUserRequest request) {
    final ApiFuture<DocumentSnapshot> documentFuture = documentReference.get();
    final ImageHub.DeleteUserResponse.Builder builder = ImageHub.DeleteUserResponse.newBuilder();
    try {
      DocumentSnapshot snapshot = documentFuture.get();
      if (snapshot.exists()
          && FirestoreUtils.authenticateUser(snapshot, request.getApiKey().getValue())) {
        builder.setSuccess(true);
        deleteFromDatabase(documentReference);
      }
    } catch (Exception e) {
      builder.setSuccess(false);
    }
    return builder.build();
  }

  private static ImageHub.CreateUserResponse getCreateUserResponse(
      final DocumentReference documentReference) {
    final ApiFuture<DocumentSnapshot> documentFuture = documentReference.get();
    final ImageHub.CreateUserResponse.Builder builder = ImageHub.CreateUserResponse.newBuilder();
    try {
      if (FirestoreUtils.documentSnapshotExists(documentFuture)) {
        builder.setSuccess(false);
      } else {
        builder.setSuccess(true);
        builder.setApiKey(
            ImageHub.AuthKey.newBuilder().setValue(Utilities.getRandomString()).build());
      }
    } catch (Exception e) {
      builder.setSuccess(false);
    }
    return builder.build();
  }

  private static void deleteFromDatabase(DocumentReference documentReference) {
    CollectionReference collectionReference =
        documentReference.collection(FirestoreUtils.IMAGE_COLLECTION_ID);
    FirestoreUtils.deleteCollection(collectionReference, FirestoreUtils.DEFAULT_DELETION_BATCH);
    documentReference.delete();
  }

  private static void addToDatabase(
      DocumentReference reference, String email, ImageHub.CreateUserResponse response) {
    Map<String, Object> data = new HashMap<String, Object>();
    ImageHub.User user =
        ImageHub.User.newBuilder()
            .setEmail(email)
            .setAuthCode(response.getApiKey())
            .setCreateDate(Timestamp.newBuilder().setSeconds(System.currentTimeMillis() / 1000))
            .setRole(ImageHub.Role.USER)
            .build();
    data.put("UserProtoBuf", Blob.fromBytes(user.toByteArray()));
    data.put("ApiKey", response.getApiKey().getValue());
    reference.set(data);
  }
}
