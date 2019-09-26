package com.eishanlawrence;

import io.grpc.stub.StreamObserver;

import javax.imageio.ImageIO;
import java.net.URL;

public final class ImageServiceImpl extends ImageServiceGrpc.ImageServiceImplBase {
    @Override
    public void uploadImage(ImageHub.UploadImageRequest request, StreamObserver<ImageHub.UploadImageResponse> responseObserver) {
    }
}
