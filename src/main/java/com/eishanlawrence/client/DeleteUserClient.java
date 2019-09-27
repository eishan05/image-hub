package com.eishanlawrence.client;

import com.eishanlawrence.ImageHub;
import com.eishanlawrence.UserServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public final class DeleteUserClient {

  public static void main(String[] args) throws Exception {
    final ManagedChannel channel =
        ManagedChannelBuilder.forTarget("localhost:8080").usePlaintext().build();
    UserServiceGrpc.UserServiceBlockingStub stub = UserServiceGrpc.newBlockingStub(channel);
    ImageHub.DeleteUserRequest request =
        ImageHub.DeleteUserRequest.newBuilder()
            .setEmail("eishanlawrence5@gmail.com")
            .setApiKey(ImageHub.AuthKey.newBuilder().setValue("test").build())
            .build();
    System.out.println("Sending a request!");
    ImageHub.DeleteUserResponse response = stub.deleteUser(request);
    System.out.println("success is: " + response.getSuccess());
    channel.shutdown();
  }

  private DeleteUserClient() {}
}
