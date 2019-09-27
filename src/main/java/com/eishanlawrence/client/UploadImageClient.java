package com.eishanlawrence.client;

import com.eishanlawrence.ImageHub;
import com.eishanlawrence.ImageServiceGrpc;
import com.eishanlawrence.UserServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public final class UploadImageClient {
  private static final String IMAGE_URL =
      "https://www.petmd.com/sites/default/files/diarrhea-long-term-dogs.jpg";
  private static final String EMAIL = "random@random.com";

  public static void main(String[] args) throws Exception {
    final ManagedChannel channel =
        ManagedChannelBuilder.forTarget("localhost:8080").usePlaintext().build();
    // Create one stub for uploading image, and one stub for creating a user
    ImageServiceGrpc.ImageServiceBlockingStub imageStub = ImageServiceGrpc.newBlockingStub(channel);
    UserServiceGrpc.UserServiceBlockingStub userStub = UserServiceGrpc.newBlockingStub(channel);
    System.out.println("Now sending request to upload image");
    ImageHub.UploadImageRequest request =
        ImageHub.UploadImageRequest.newBuilder()
            .setApiKey(ImageHub.AuthKey.newBuilder().setValue(args[1]).build())
            .setEmail(args[0])
            .setImage(ImageHub.ImageUrl.newBuilder().setImageName(args[2]).setUrl(args[3]).build())
            .build();
    ImageHub.UploadImageResponse response = imageStub.uploadImage(request);
    System.out.println("success is: " + response.getSuccess());
    channel.shutdown();
  }

  private UploadImageClient() {}
}
