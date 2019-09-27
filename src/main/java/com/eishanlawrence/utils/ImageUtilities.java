package com.eishanlawrence.utils;

import com.eishanlawrence.ImageHub;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.protobuf.ByteString;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

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

    Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
    ImageWriter writer = writers.next();
    ImageOutputStream ios = ImageIO.createImageOutputStream(byteArrayOutputStream);
    writer.setOutput(ios);
    ImageWriteParam param = writer.getDefaultWriteParam();
    param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
    param.setCompressionQuality(0.0005f); // Change the quality value you prefer
    writer.write(null, new IIOImage(image, null, null), param);

    ImageIO.write(image, JPG, byteArrayOutputStream);
    return ByteString.copyFrom(byteArrayOutputStream.toByteArray());
  }

  /** Given a document snapshot, return true if it matches the given image url */
  public static String urlToId(ImageHub.ImageUrl url) {
    return url.getUrl().replace("/", "");
  }

  private ImageUtilities() {}
}
