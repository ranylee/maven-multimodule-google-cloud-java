package com.houglum.signurlexample;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import java.net.URL;
import java.util.concurrent.TimeUnit;

public class App {
  public static void main( String[] args ) {
    // Use ADCs; should "just work" on a GCE instance. See the docs:
    // https://cloud.google.com/storage/docs/reference/libraries#client-libraries-install-java
    Storage storageClient = StorageOptions.getDefaultInstance().getService();

    String bucketName = args[0];
    String objectName = args[1];
    String vhostname = "https://" + bucketName + ".storage.googleapis.com";
    BlobId blobId = BlobId.of(bucketName, objectName);
    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("text/plain").build();

    URL signedUrl;

    System.out.printf("============ Virtual hosted-style URLs:\n\n");

    signedUrl = storageClient.signUrl(
        blobInfo,
        1,
        TimeUnit.HOURS,
        Storage.SignUrlOption.withVirtualHostName(vhostname),
        Storage.SignUrlOption.withV4Signature());
    System.out.printf("Your V4 signed URL is:\n%s\n\n", signedUrl.toString());

    signedUrl = storageClient.signUrl(
        blobInfo,
        1,
        TimeUnit.HOURS,
        Storage.SignUrlOption.withVirtualHostName(vhostname),
        Storage.SignUrlOption.withV2Signature());
    System.out.printf("Your V2 signed URL is:\n%s\n\n", signedUrl.toString());

    System.out.printf("============ Making sure other path-style host still works...\n\n");

    signedUrl = storageClient.signUrl(
        blobInfo,
        1,
        TimeUnit.HOURS,
        Storage.SignUrlOption.withHostName("https://storage.googleapis.com"),
        Storage.SignUrlOption.withV4Signature());
    System.out.printf("Your V4 signed URL is:\n%s\n\n", signedUrl.toString());

    signedUrl = storageClient.signUrl(
        blobInfo,
        1,
        TimeUnit.HOURS,
        Storage.SignUrlOption.withHostName("https://storage.googleapis.com"),
        Storage.SignUrlOption.withV4Signature());

    signedUrl = storageClient.signUrl(
        blobInfo,
        1,
        TimeUnit.HOURS,
        Storage.SignUrlOption.withHostName("https://storage.googleapis.com"),
        Storage.SignUrlOption.withV2Signature());
    System.out.printf("Your V2 signed URL is:\n%s\n\n", signedUrl.toString());

    signedUrl = storageClient.signUrl(
        blobInfo,
        1,
        TimeUnit.HOURS,
        Storage.SignUrlOption.withV2Signature());
    System.out.printf("Your V2 signed URL is:\n%s\n\n", signedUrl.toString());
  }
}
