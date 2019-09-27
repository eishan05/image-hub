package com.eishanlawrence;

import com.eishanlawrence.firestore.FirestoreDatabaseReference;
import com.eishanlawrence.firestore.FirestoreUtils;
import com.eishanlawrence.utils.ImageUtilities;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import io.grpc.stub.StreamObserver;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public final class ImageServiceImpl extends ImageServiceGrpc.ImageServiceImplBase {

  private static final ImageHub.UploadImageResponse failingUploadImageResponse =
      ImageHub.UploadImageResponse.getDefaultInstance();
  private static final ImageHub.DeleteImageResponse failingDeleteImageResponse =
      ImageHub.DeleteImageResponse.getDefaultInstance();
  private static final ImageHub.GetImagesResponse failingGetImagesResponse =
      ImageHub.GetImagesResponse.getDefaultInstance();

  @Override
  public void uploadImage(
      ImageHub.UploadImageRequest request,
      StreamObserver<ImageHub.UploadImageResponse> responseObserver) {
    Firestore db = FirestoreDatabaseReference.getFirestoreReference();
    String email = request.getEmail();
    String authKey = request.getApiKey().getValue();
    DocumentReference documentReference =
        db.collection(FirestoreUtils.USERS_COLLECTION_ID).document(email);
    try {
      // If user exists
      if (FirestoreUtils.userExistsAndIsAuthenticated(documentReference, authKey)) {
        if (uploadImageToDatabase(documentReference, request)) {
          responseObserver.onNext(
              ImageHub.UploadImageResponse.newBuilder().setSuccess(true).build());
        } else {
          responseObserver.onNext(failingUploadImageResponse);
        }
      } else {
        responseObserver.onNext(failingUploadImageResponse);
      }
    } catch (Exception e) {
      e.printStackTrace();
      responseObserver.onNext(failingUploadImageResponse);
    }
    responseObserver.onCompleted();
  }

  @Override
  public void deleteImage(
      ImageHub.DeleteImageRequest request,
      StreamObserver<ImageHub.DeleteImageResponse> streamObserver) {
    Firestore db = FirestoreDatabaseReference.getFirestoreReference();
    String email = request.getEmail();
    String authKey = request.getApiKey().getValue();
    DocumentReference documentReference =
        db.collection(FirestoreUtils.USERS_COLLECTION_ID).document(email);
    try {
      if (FirestoreUtils.userExistsAndIsAuthenticated(documentReference, authKey)) {
        // If image exists, then delete it and send a success
        if (findImageAndDelete(documentReference, request.getImage())) {
          streamObserver.onNext(ImageHub.DeleteImageResponse.newBuilder().setSuccess(true).build());
        } else {
          streamObserver.onNext(failingDeleteImageResponse);
        }
      } else {
        streamObserver.onNext(failingDeleteImageResponse);
      }
    } catch (Exception e) {
      e.printStackTrace();
      streamObserver.onNext(failingDeleteImageResponse);
    }
    streamObserver.onCompleted();
  }

  @Override
  public void getImages(
      ImageHub.GetImagesRequest request,
      StreamObserver<ImageHub.GetImagesResponse> streamObserver) {
    Firestore db = FirestoreDatabaseReference.getFirestoreReference();
    String email = request.getEmail();
    String authKey = request.getApiKey().getValue();
    DocumentReference documentReference =
        db.collection(FirestoreUtils.USERS_COLLECTION_ID).document(email);
    try {
      if (FirestoreUtils.userExistsAndIsAuthenticated(documentReference, authKey)) {
        streamObserver.onNext(getImages(documentReference));
      } else {
        streamObserver.onNext(failingGetImagesResponse);
      }
    } catch (Exception e) {
      e.printStackTrace();
      streamObserver.onNext(failingGetImagesResponse);
    }
    streamObserver.onCompleted();
  }

  private static ImageHub.GetImagesResponse getImages(DocumentReference userReference)
      throws InterruptedException, ExecutionException, IOException {
    CollectionReference images = userReference.collection(FirestoreUtils.IMAGE_COLLECTION_ID);
    ApiFuture<QuerySnapshot> future = images.get();
    List<QueryDocumentSnapshot> imageDocuments = future.get().getDocuments();
    ImageHub.GetImagesResponse.Builder builder = ImageHub.GetImagesResponse.newBuilder();
    for (QueryDocumentSnapshot imageSnapshot : imageDocuments) {
      String imageUrl = (String) imageSnapshot.get("url");
      builder.addRawImage(ImageUtilities.urlToImageProto(imageUrl));
    }
    return builder.build();
  }

  private static boolean findImageAndDelete(DocumentReference userDocument, ImageHub.ImageUrl image)
      throws InterruptedException, ExecutionException {
    ApiFuture<WriteResult> future =
        userDocument
            .collection(FirestoreUtils.IMAGE_COLLECTION_ID)
            .document(ImageUtilities.urlToId(image))
            .delete();
    future.get();
    return true;
  }

  private static boolean uploadImageToDatabase(
      DocumentReference documentReference, ImageHub.UploadImageRequest request) throws IOException {
    BufferedImage image = ImageUtilities.getImageFromUrl(request.getImage().getUrl());
    Map<String, Object> data = new HashMap<String, Object>();
    Map<String, Object> dimensions = new HashMap<String, Object>();
    data.put("name", request.getImage().getImageName());
    data.put("url", request.getImage().getUrl());
    dimensions.put("height", image.getHeight());
    dimensions.put("width", image.getWidth());
    data.put("dimensions", dimensions);
    CollectionReference imageReference =
        documentReference.collection(FirestoreUtils.IMAGE_COLLECTION_ID);
    imageReference.document(ImageUtilities.urlToId(request.getImage())).set(data);
    return true;
  }
}
