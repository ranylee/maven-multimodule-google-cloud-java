package com.houglum.signurlexample;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.HttpMethod;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainModule {

  public static void main(String[] args) {
    // Use ADCs; should "just work" on a GCE instance. See the docs:
    // https://cloud.google.com/storage/docs/reference/libraries#client-libraries-install-java
    Storage storageClient = StorageOptions.getDefaultInstance().getService();

    String bucketName = args[0];
    String objectNameForGet = args[1];
    String objectNameForPut = args[2];
    String vhostname = "https://" + bucketName + ".storage.googleapis.com";

    BlobId blobIdForGet = BlobId.of(bucketName, objectNameForGet);
    BlobInfo blobInfoForGet =
        BlobInfo.newBuilder(blobIdForGet).setContentType("text/plain").build();
    BlobId blobIdForPut = BlobId.of(bucketName, objectNameForPut);
    BlobInfo blobInfoForPut =
        BlobInfo.newBuilder(blobIdForPut).setContentType("text/plain").build();

    ArrayList<URL> urls;

    System.out.printf("\n============ Virtual hosted-style URLs:\n");

    // Method: GET
    urls = new ArrayList<URL>();
    // V4 with virtual hostname
    urls.add(storageClient.signUrl(
        blobInfoForGet,
        1, TimeUnit.HOURS,
        Storage.SignUrlOption.withVirtualHostName(vhostname),
        Storage.SignUrlOption.withV4Signature()));
    // V2 with virtual hostname
    urls.add(storageClient.signUrl(
        blobInfoForGet,
        1,
        TimeUnit.HOURS,
        Storage.SignUrlOption.withVirtualHostName(vhostname),
        Storage.SignUrlOption.withV2Signature()));

    System.out.printf("\n[GET]\n");
    for (URL signedUrl : urls) {
      System.out.printf("\n%s\n", signedUrl.toString());
    }

    // Method: PUT
    urls = new ArrayList<URL>();
    // V4 with virtual hostname
    urls.add(storageClient.signUrl(
        blobInfoForPut,
        1, TimeUnit.HOURS,
        Storage.SignUrlOption.httpMethod(HttpMethod.PUT),
        Storage.SignUrlOption.withVirtualHostName(vhostname),
        Storage.SignUrlOption.withV4Signature()));
    // V2 with virtual hostname
    urls.add(storageClient.signUrl(
        blobInfoForPut,
        1,
        TimeUnit.HOURS,
        Storage.SignUrlOption.httpMethod(HttpMethod.PUT),
        Storage.SignUrlOption.withVirtualHostName(vhostname),
        Storage.SignUrlOption.withV2Signature()));

    System.out.printf("\n[PUT]\n");
    for (URL signedUrl : urls) {
      // Note: curl adds a content-type, and the server will complain for v2-signed URLs if the
      // provided content-type doesn't match what was used at signing time.
      System.out.printf("\nCurl command to test PUT:\n");
      System.out.printf("  curl -X PUT -H \"Content-Type:\" -d \"testbytes\" \"%s\"\n",
                        signedUrl.toString());
    }
    System.out.printf("\n===================================================\n");

    ///////////////////////////////////////////////////////////////
    // Sanity check to make sure other path style still works; used for testing. Adding quick return
    // statement here to switch between printing test spam vs not.
    boolean skipSanityCheck = true;
    if (skipSanityCheck) {
      return;
    }
    ///////////////////////////////////////////////////////////////
    System.out.printf("\n============ Making sure other path-style host still works...\n");

    // Method: GET
    urls = new ArrayList<URL>();
    // V4 with overridden hostname
    urls.add(storageClient.signUrl(
        blobInfoForGet,
        1, TimeUnit.HOURS,
        Storage.SignUrlOption.withHostName("https://storage.googleapis.com"),
        Storage.SignUrlOption.withV4Signature()));
    // V4 without overridden hostname
    urls.add(storageClient.signUrl(
        blobInfoForGet,
        1, TimeUnit.HOURS,
        Storage.SignUrlOption.withHostName("https://storage.googleapis.com"),
        Storage.SignUrlOption.withV4Signature()));
    // V2 with overridden hostname
    urls.add(storageClient.signUrl(
        blobInfoForGet,
        1, TimeUnit.HOURS,
        Storage.SignUrlOption.withHostName("https://storage.googleapis.com"),
        Storage.SignUrlOption.withV2Signature()));
    // V2 without overridden hostname
    urls.add(storageClient.signUrl(
        blobInfoForGet,
        1, TimeUnit.HOURS,
        Storage.SignUrlOption.withV2Signature()));

    System.out.printf("\n[GET]\n");
    for (URL signedUrl : urls) {
      System.out.printf("\n%s\n", signedUrl.toString());
    }
    System.out.printf("\n===================================================\n");

    // Method: PUT
    urls = new ArrayList<URL>();
    // V4 with overridden hostname
    urls.add(storageClient.signUrl(
        blobInfoForPut,
        1, TimeUnit.HOURS,
        Storage.SignUrlOption.httpMethod(HttpMethod.PUT),
        Storage.SignUrlOption.withHostName("https://storage.googleapis.com"),
        Storage.SignUrlOption.withV4Signature()));
    // V4 without overridden hostname
    urls.add(storageClient.signUrl(
        blobInfoForPut,
        1, TimeUnit.HOURS,
        Storage.SignUrlOption.httpMethod(HttpMethod.PUT),
        Storage.SignUrlOption.withHostName("https://storage.googleapis.com"),
        Storage.SignUrlOption.withV4Signature()));
    // V2 with overridden hostname
    urls.add(storageClient.signUrl(
        blobInfoForPut,
        1, TimeUnit.HOURS,
        Storage.SignUrlOption.httpMethod(HttpMethod.PUT),
        Storage.SignUrlOption.withHostName("https://storage.googleapis.com"),
        Storage.SignUrlOption.withV2Signature()));
    // V2 without overridden hostname
    urls.add(storageClient.signUrl(
        blobInfoForPut,
        1, TimeUnit.HOURS,
        Storage.SignUrlOption.httpMethod(HttpMethod.PUT),
        Storage.SignUrlOption.withV2Signature()));

    System.out.printf("\n[PUT]\n");
    for (URL signedUrl : urls) {
      // Note: curl adds a content-type, and the server will complain for v2-signed URLs if the
      // provided content-type doesn't match what was used at signing time.
      System.out.printf("\nCurl command to test PUT:\n");
      System.out.printf("  curl -X PUT -H \"Content-Type:\" -d \"testbytes\" \"%s\"\n",
                        signedUrl.toString());
    }
    System.out.printf("\n===================================================\n");
  }
}
