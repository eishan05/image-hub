package com.eishanlawrence.client;

import com.eishanlawrence.ImageHub;
import com.eishanlawrence.UserServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public final class CreateUserClient {
  public static void main(String[] args) throws Exception {
    final ManagedChannel channel =
        ManagedChannelBuilder.forTarget("localhost:8080").usePlaintext().build();
    UserServiceGrpc.UserServiceBlockingStub stub = UserServiceGrpc.newBlockingStub(channel);
    ImageHub.CreateUserRequest request =
        ImageHub.CreateUserRequest.newBuilder().setEmail(args[0]).build();
    System.out.println("Sending a request!");
    ImageHub.CreateUserResponse response = stub.createUser(request);
    System.out.println("success is: " + response.getSuccess());
    System.out.println("API key is: " + response.getApiKey());
    channel.shutdown();
  }
}
