package com.eishanlawrence.client;

import com.eishanlawrence.ImageHub;
import com.eishanlawrence.ImageServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.List;

public final class GetImagesClient {

  public static void main(String[] args) throws Exception {
    final ManagedChannel channel =
        ManagedChannelBuilder.forTarget("localhost:8080").usePlaintext().build();
    // Create one stub for uploading image, and one stub for creating a user
    ImageServiceGrpc.ImageServiceBlockingStub imageStub = ImageServiceGrpc.newBlockingStub(channel);
    System.out.println("Now sending request to get uploaded images");
    ImageHub.GetImagesRequest request =
        ImageHub.GetImagesRequest.newBuilder()
            .setApiKey(ImageHub.AuthKey.newBuilder().setValue(args[1]).build())
            .setEmail(args[0])
            .build();
    ImageHub.GetImagesResponse response = imageStub.getImages(request);
    List<ImageHub.Image> imageList = response.getRawImageList();
    if (imageList.size() > 0) {
      ImageHub.Image image = imageList.get(0);
      ByteArrayInputStream byteArrayInputStream =
          new ByteArrayInputStream(image.getImageData().toByteArray());
      BufferedImage bufferedImage = ImageIO.read(byteArrayInputStream);
      int w = image.getWidth();
      int h = image.getHeight();
      ImageIcon icon = new ImageIcon(bufferedImage);
      JFrame frame = new JFrame();
      frame.setLayout(new FlowLayout());
      frame.setSize(w, h);
      JLabel lbl = new JLabel();
      lbl.setIcon(icon);
      frame.add(lbl);
      frame.setVisible(true);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      System.out.println("Height: " + h);
      System.out.println("Width: " + w);
    } else {
      System.out.println("Image size is 0");
    }
    channel.shutdown();
  }

  private GetImagesClient() {}
}
