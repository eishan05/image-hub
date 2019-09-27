package com.eishanlawrence.utils;

import com.eishanlawrence.ImageHub;
import com.google.protobuf.ByteString;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

public final class ImageUtilities {
  private static final String JPG = "jpg";

  /** Given a URL, returns a buffered image */
  public static BufferedImage getImageFromUrl(String imageUrl) throws IOException {
    URL url = new URL(imageUrl);
    return ImageIO.read(url);
  }

  /** Compresses and converts a Buffered Image to ByteString */
  public static ByteString imageToByteString(BufferedImage image) throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    ImageIO.write(image, JPG, byteArrayOutputStream);
    return ByteString.copyFrom(byteArrayOutputStream.toByteArray());
  }

  /** Given a document snapshot, return true if it matches the given image url */
  public static String urlToId(ImageHub.ImageUrl url) {
    return url.getUrl().replace("/", "");
  }

  public static ImageHub.Image urlToImageProto(String imageUrl) throws IOException {
    BufferedImage image = getImageFromUrl(imageUrl);
    return ImageHub.Image.newBuilder()
        .setImageUrl(ImageHub.ImageUrl.newBuilder().setUrl(imageUrl).build())
        .setHeight(image.getHeight())
        .setWidth(image.getWidth())
        .setImageData(imageToByteString(image))
        .build();
  }

  private ImageUtilities() {}
}
