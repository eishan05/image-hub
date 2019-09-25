package com.eishanlawrence;

import com.eishanlawrence.firestore.FirestoreDatabaseReference;
import com.eishanlawrence.utils.Utilities;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureToListenableFuture;
import com.google.cloud.firestore.Blob;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.protobuf.ByteString;
import com.google.protobuf.Timestamp;
import io.grpc.stub.StreamObserver;
import org.apache.commons.lang3.concurrent.ConcurrentException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public final class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase {

  private static final String USERS_COLLECTION_ID = "users";

  @Override
  public void createUser(
      ImageHub.CreateUserRequest request,
      StreamObserver<ImageHub.CreateUserResponse> responseObserver) {
    String email = request.getEmail();
    String apiKey = Utilities.getRandomString();
    Firestore db = FirestoreDatabaseReference.getFirestoreReference();
    // Create a document
    DocumentReference documentReference = db.collection(USERS_COLLECTION_ID).document(email);
    ImageHub.CreateUserResponse response = getCreateUserResponse(documentReference.get());
    if (response.getSuccess()) {
      updateDatabase(documentReference, email, response);
    }
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  private static ImageHub.CreateUserResponse getCreateUserResponse(
      final ApiFuture<DocumentSnapshot> documentFuture) {
    final ImageHub.CreateUserResponse.Builder builder = ImageHub.CreateUserResponse.newBuilder();
    try {
      if (documentSnapshotExists(documentFuture)) {
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

  private static void updateDatabase(
      DocumentReference reference, String email, ImageHub.CreateUserResponse response) {
    Map<String, Object> data = new HashMap<String, Object>();
    ImageHub.User user =
        ImageHub.User.newBuilder()
            .setEmail(email)
            .setAuthCode(response.getApiKey())
            .setCreateDate(Timestamp.newBuilder().setSeconds(System.currentTimeMillis() / 1000))
            .setRole(ImageHub.Role.USER)
            .build();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    try {
      user.writeTo(outputStream);
    } catch (IOException e) {
      throw new RuntimeException("Cannot write protobuf", e);
    }
    data.put("UserProtoBuf", Blob.fromBytes(outputStream.toByteArray()));
    data.put("ApiKey", response.getApiKey().getValue());
    reference.set(data);
  }

  private static boolean documentSnapshotExists(final ApiFuture<DocumentSnapshot> documentFuture)
      throws InterruptedException, ExecutionException {
    DocumentSnapshot snapshot = documentFuture.get();
    return snapshot.exists();
  }

  @Override
  public void deleteUser(
      ImageHub.DeleteUserRequest request,
      StreamObserver<ImageHub.DeleteUserResponse> responseObserver) {}
}