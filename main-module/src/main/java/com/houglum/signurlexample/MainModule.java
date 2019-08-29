package com.houglum.signurlexample;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.HttpMethod;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

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
        BlobInfo.newBuilder(blobIdForGet).setContentType("image/jpeg").build();
    BlobId blobIdForPut = BlobId.of(bucketName, objectNameForPut);
    BlobInfo blobInfoForPut =
        BlobInfo.newBuilder(blobIdForPut).setContentType("image/jpeg").build();
    BlobId blobIdForList = BlobId.of(bucketName, "");
    BlobInfo blobInfoForList = BlobInfo.newBuilder(blobIdForList).build();

    ArrayList<URL> urls;

    System.out.printf("\n============ Virtual hosted-style URLs:\n");

    // Method: GET (object)
    urls = new ArrayList<URL>();
    // V4 with virtual hostname
    urls.add(
        storageClient.signUrl(
            blobInfoForGet,
            6,
            TimeUnit.DAYS,
            Storage.SignUrlOption.withVirtualHostName(vhostname),
            Storage.SignUrlOption.withV4Signature()));
    // Same thing, but not supplying the hostname explicitly
    urls.add(
        storageClient.signUrl(
            blobInfoForGet,
            6,
            TimeUnit.DAYS,
            Storage.SignUrlOption.withVirtualHostName(),
            Storage.SignUrlOption.withV4Signature()));
     // V2 with virtual hostname
    urls.add(
        storageClient.signUrl(
            blobInfoForGet,
            6,
            TimeUnit.DAYS,
            Storage.SignUrlOption.withVirtualHostName(vhostname),
            Storage.SignUrlOption.withV2Signature()));

    System.out.printf("\n[GET] to fetch object bytes\n");
    for (URL signedUrl : urls) {
      System.out.printf("\n%s\n", signedUrl.toString());
    }

    // Method: GET (list objects in a bucket)
    urls = new ArrayList<URL>();
    // V4 with virtual hostname and fixed prefix query parameter
    String prefix =
        objectNameForGet.length() > 1
            ? objectNameForGet.substring(0, objectNameForGet.length() - 1)
            : objectNameForGet.substring(0, 1); // Assume length of at least 1.
    urls.add(
        storageClient.signUrl(
            blobInfoForList,
            6,
            TimeUnit.DAYS,
            Storage.SignUrlOption.withV4Signature(),
            // TODO: Add these back in once we pull in a branch of the google-cloud-java fork that
            // that contains this new method.
            // Storage.SignUrlOption.withCanonicalQueryParam("versions", "True"),
            // Storage.SignUrlOption.withCanonicalQueryParam("prefix", prefix),
            Storage.SignUrlOption.withVirtualHostName(vhostname)));
    // V4 with virtual hostname
    urls.add(
        storageClient.signUrl(
            blobInfoForList,
            6,
            TimeUnit.DAYS,
            Storage.SignUrlOption.withVirtualHostName(vhostname),
            Storage.SignUrlOption.withV4Signature()));
    // V2 with virtual hostname
    urls.add(
        storageClient.signUrl(
            blobInfoForList,
            6,
            TimeUnit.DAYS,
            Storage.SignUrlOption.withVirtualHostName(vhostname),
            Storage.SignUrlOption.withV2Signature()));

    System.out.printf("\n[GET] to list objects in a bucket\n");
    for (URL signedUrl : urls) {
      System.out.printf("\n%s\n", signedUrl.toString());
    }

    // Method: PUT (object)
    urls = new ArrayList<URL>();
    // V4 with virtual hostname
    urls.add(
        storageClient.signUrl(
            blobInfoForPut,
            6,
            TimeUnit.DAYS,
            Storage.SignUrlOption.httpMethod(HttpMethod.PUT),
            Storage.SignUrlOption.withVirtualHostName(vhostname),
            Storage.SignUrlOption.withV4Signature()));
    // V2 with virtual hostname
    urls.add(
        storageClient.signUrl(
            blobInfoForPut,
            6,
            TimeUnit.DAYS,
            Storage.SignUrlOption.httpMethod(HttpMethod.PUT),
            Storage.SignUrlOption.withVirtualHostName(vhostname),
            Storage.SignUrlOption.withV2Signature()));

    System.out.printf("\n[PUT] to upload object bytes\n");
    for (URL signedUrl : urls) {
      // Note: curl adds a content-type, and the server will complain for v2-signed URLs if the
      // provided content-type doesn't match what was used at signing time.
      System.out.printf("\nCurl command to test PUT:\n");
      System.out.printf(
          "  curl -X PUT -H \"Content-Type:\" -d \"testbytes\" \"%s\"\n", signedUrl.toString());
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

    // Method: GET (object)
    urls = new ArrayList<URL>();
    // V4 with overridden hostname
    urls.add(
        storageClient.signUrl(
            blobInfoForGet,
            6,
            TimeUnit.DAYS,
            Storage.SignUrlOption.withHostName("https://storage.googleapis.com"),
            Storage.SignUrlOption.withV4Signature()));
    // V4 without overridden hostname
    urls.add(
        storageClient.signUrl(
            blobInfoForGet,
            6,
            TimeUnit.DAYS,
            Storage.SignUrlOption.withHostName("https://storage.googleapis.com"),
            Storage.SignUrlOption.withV4Signature()));
    // V2 with overridden hostname
    urls.add(
        storageClient.signUrl(
            blobInfoForGet,
            6,
            TimeUnit.DAYS,
            Storage.SignUrlOption.withHostName("https://storage.googleapis.com"),
            Storage.SignUrlOption.withV2Signature()));
    // V2 without overridden hostname
    urls.add(
        storageClient.signUrl(
            blobInfoForGet,
            6,
            TimeUnit.DAYS,
            Storage.SignUrlOption.withV2Signature()));

    urls.add(
        storageClient.signUrl(
            blobInfoForGet,
            6,
            TimeUnit.DAYS,
            Storage.SignUrlOption.withContentType(),
            Storage.SignUrlOption.withV2Signature()));

    System.out.printf("\n[GET] to fetch object bytes\n");
    for (URL signedUrl : urls) {
      System.out.printf("\n%s\n", signedUrl.toString());
    }
    System.out.printf("\n===================================================\n");

    // Method: GET (list objects in a bucket)
    urls = new ArrayList<URL>();
    // V4 with overridden hostname
    urls.add(
        storageClient.signUrl(
            blobInfoForList,
            6,
            TimeUnit.DAYS,
            Storage.SignUrlOption.withHostName("https://storage.googleapis.com"),
            Storage.SignUrlOption.withV4Signature()));
    // V4 with standard hostname
    urls.add(
        storageClient.signUrl(
            blobInfoForList,
            6,
            TimeUnit.DAYS,
            Storage.SignUrlOption.withV4Signature()));
    // V2 with overridden hostname
    urls.add(
        storageClient.signUrl(
            blobInfoForList,
            6,
            TimeUnit.DAYS,
            Storage.SignUrlOption.withHostName("https://storage.googleapis.com"),
            Storage.SignUrlOption.withV2Signature()));
    // V2 with standard hostname
    urls.add(
        storageClient.signUrl(
            blobInfoForList,
            6,
            TimeUnit.DAYS,
            Storage.SignUrlOption.withV2Signature()));

    System.out.printf("\n[GET] to list objects in a bucket\n");
    for (URL signedUrl : urls) {
      System.out.printf("\n%s\n", signedUrl.toString());
    }
    System.out.printf("\n===================================================\n");

    // Method: PUT (object)
    urls = new ArrayList<URL>();
    // V4 with overridden hostname
    urls.add(
        storageClient.signUrl(
            blobInfoForPut,
            6,
            TimeUnit.DAYS,
            Storage.SignUrlOption.httpMethod(HttpMethod.PUT),
            Storage.SignUrlOption.withHostName("https://storage.googleapis.com"),
            Storage.SignUrlOption.withV4Signature()));
    // V4 without overridden hostname
    urls.add(
        storageClient.signUrl(
            blobInfoForPut,
            6,
            TimeUnit.DAYS,
            Storage.SignUrlOption.httpMethod(HttpMethod.PUT),
            Storage.SignUrlOption.withHostName("https://storage.googleapis.com"),
            Storage.SignUrlOption.withV4Signature()));
    // V2 with overridden hostname
    urls.add(
        storageClient.signUrl(
            blobInfoForPut,
            6,
            TimeUnit.DAYS,
            Storage.SignUrlOption.httpMethod(HttpMethod.PUT),
            Storage.SignUrlOption.withHostName("https://storage.googleapis.com"),
            Storage.SignUrlOption.withV2Signature()));
    // V2 without overridden hostname
    urls.add(
        storageClient.signUrl(
            blobInfoForPut,
            6,
            TimeUnit.DAYS,
            Storage.SignUrlOption.httpMethod(HttpMethod.PUT),
            Storage.SignUrlOption.withV2Signature()));

    System.out.printf("\n[PUT] to upload object bytes\n");
    for (URL signedUrl : urls) {
      // Note: curl adds a content-type, and the server will complain for v2-signed URLs if the
      // provided content-type doesn't match what was used at signing time.
      System.out.printf("\nCurl command to test PUT:\n");
      System.out.printf(
          "  curl -X PUT -H \"Content-Type:\" -d \"testbytes\" \"%s\"\n", signedUrl.toString());
    }
    System.out.printf("\n===================================================\n");
  }
}
