package com.eishanlawrence;

import com.eishanlawrence.firestore.FirestoreDatabaseReference;
import com.eishanlawrence.firestore.FirestoreUtils;
import com.eishanlawrence.utils.ImageUtilities;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import io.grpc.stub.StreamObserver;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class ImageServiceImpl extends ImageServiceGrpc.ImageServiceImplBase {

  private static final ImageHub.UploadImageResponse failingResponse =
      ImageHub.UploadImageResponse.newBuilder().setSuccess(false).build();

  @Override
  public void uploadImage(
      ImageHub.UploadImageRequest request,
      StreamObserver<ImageHub.UploadImageResponse> responseObserver) {
    Firestore db = FirestoreDatabaseReference.getFirestoreReference();
    String email = request.getEmail();
    String authKey = request.getApiKey().getValue();
    DocumentReference documentReference =
        db.collection(FirestoreUtils.USERS_COLLECTION_ID).document(email);
    ApiFuture<DocumentSnapshot> documentFuture = documentReference.get();
    System.out.println("Got an image upload request!");
    try {
      DocumentSnapshot snapshot = documentFuture.get();
      // If the user does not exist, or sends in a wrong AuthKey
      if (!snapshot.exists() || !FirestoreUtils.authenticateUser(snapshot, authKey)) {
        responseObserver.onNext(failingResponse);
      } else {
        uploadImageToDatabase(documentReference, request);
        responseObserver.onNext(ImageHub.UploadImageResponse.newBuilder().setSuccess(true).build());
      }
    } catch (Exception e) {
      e.printStackTrace();
      responseObserver.onNext(failingResponse);
    }
    responseObserver.onCompleted();
  }

  private static void uploadImageToDatabase(
      DocumentReference documentReference, ImageHub.UploadImageRequest request) throws IOException {
    BufferedImage image = ImageUtilities.getImageFromUrl(request.getImage().getUrl());
    Map<String, Object> data = new HashMap<String, Object>();
    Map<String, Object> dimensions = new HashMap<String, Object>();
    data.put("name", request.getImage().getImageName());
    data.put("url", request.getImage().getUrl());
    dimensions.put("height", image.getHeight());
    dimensions.put("width", image.getWidth());
    data.put("dimensions", dimensions);
    documentReference.collection(FirestoreUtils.IMAGE_COLLECTION_ID).add(data);
  }
}
