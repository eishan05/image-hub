# image-hub

[![Build Status](https://travis-ci.org/eishan05/image-hub.svg?branch=master)](https://travis-ci.org/eishan05/image-hub)

A gRPC and HTTP service to store images. Think of it like an image repository!

The 5 main RPC APIs are as follows:

1. ```rpc CreateUser (CreateUserRequest) returns (CreateUserResponse)``` takes in a ```CreateUserRequest``` protocol buffer, which consists of the user's email, and returns ```CreateUserRepsonse``` protcol buffer, that contains a ```success``` boolean flag, and ```AuthKey``` protobuf that is to be used for authentication by the user for using other APIs.

2. ```rpc DeleteUser (DeleteUserRequest) returns (DeleteUserResponse)``` will delete a user from the database given the user's email and ```AuthKey``` in the ```DeleteUserRequest``` protobuf. The ```success``` field in the response can be used to verify if the operation was successful. 

3. ```rpc UploadImage (UploadImageRequest) returns (UploadImageResponse)``` will store an image in the database, given a url. The ```success``` field in the response can be used to verify if the operation was completed. The RPC will fail if the url specified does not point to an image, or if the ```AuthKey``` is not correct for a user.

4. ```rpc GetImages (GetImagesRequest) returns (GetImagesResponse)``` will return a list of images currently stored by a user using this API.

5. ```rpc DeleteImage (DeleteImageRequest) returns (DeleteImageResponse)``` will delete an image from the user's collection of images.
