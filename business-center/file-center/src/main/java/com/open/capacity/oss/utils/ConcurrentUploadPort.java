package com.open.capacity.oss.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.obs.services.ObsClient;
import com.obs.services.ObsConfiguration;
import com.obs.services.exception.ObsException;
import com.obs.services.model.CompleteMultipartUploadRequest;
import com.obs.services.model.InitiateMultipartUploadRequest;
import com.obs.services.model.InitiateMultipartUploadResult;
import com.obs.services.model.ListPartsRequest;
import com.obs.services.model.ListPartsResult;
import com.obs.services.model.Multipart;
import com.obs.services.model.PartEtag;
import com.obs.services.model.UploadPartRequest;
import com.obs.services.model.UploadPartResult;
/**
 * @program: open-capacity-platform
 * @author: GuoGaoJu
 * This sample demonstrates how to multipart upload an object concurrently
 * from OBS using the OBS SDK for Java.
 */
public class ConcurrentUploadPort {


    private static ExecutorService executorService = Executors.newFixedThreadPool(5);

    private static List<PartEtag> partETags = Collections.synchronizedList(new ArrayList<PartEtag>());


    public static void upload(ObsClient obsClient,String bucketName,String objectKey,File sampleFile) throws IOException
    {
        try {
            //初始化分段上传，拿到uploadID
            String uploadId = claimUploadId(obsClient,bucketName,objectKey);
            System.out.println("Claiming a new upload id " + uploadId + "\n");

            //分段大小
            long partSize = 100 * 1024 * 1024l;// 100MB
            //文件大小
            long fileLength = sampleFile.length();
            //计算分段总数
            long partCount = fileLength % partSize == 0 ? fileLength / partSize : fileLength / partSize + 1;

            if (partCount > 10000) {
                throw new RuntimeException("Total parts count should not exceed 10000");
            }
            else {
                System.out.println("Total parts count " + partCount + "\n");
            }

            // Upload multiparts to your bucket
            System.out.println("Begin to upload multiparts to OBS from a file\n");
            for (int i = 0; i < partCount; i++)
            {
                long offset = i * partSize;
                long currPartSize = (i + 1 == partCount) ? fileLength - offset : partSize;
                executorService.execute(new PartUploader(sampleFile, offset, currPartSize, i + 1, uploadId,bucketName,objectKey,obsClient));
            }

            /*
             * Wait for all tasks to finish
             */
            executorService.shutdown();
            while (!executorService.isTerminated())
            {
                try
                {
                    executorService.awaitTermination(5, TimeUnit.SECONDS);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }

            /*
             * Verify whether all tasks are finished
             */
            if (partETags.size() != partCount)
            {
                throw new IllegalStateException("Some parts are not finished");
            }
            else
            {
                System.out.println("Succeed to complete multiparts into an object named " + objectKey + "\n");
            }

            /*
             * View all parts uploaded recently
             */
            listAllParts(uploadId,obsClient,bucketName,objectKey);

            /*
             * Complete to upload multiparts
             */
            completeMultipartUpload(uploadId,obsClient,bucketName,objectKey);

        }
        catch (ObsException e)
        {
            System.out.println("Response Code: " + e.getResponseCode());
            System.out.println("Error Message: " + e.getErrorMessage());
            System.out.println("Error Code:       " + e.getErrorCode());
            System.out.println("Request ID:      " + e.getErrorRequestId());
            System.out.println("Host ID:           " + e.getErrorHostId());
        }

    }
    private static class PartUploader implements Runnable
    {

        private File sampleFile;

        private long offset;

        private long partSize;

        private int partNumber;

        private String uploadId;

        private String bucketName;

        private String objectKey;

        private ObsClient obsClient;

        public PartUploader(File sampleFile, long offset, long partSize, int partNumber, String uploadId,String bucketName,String objectKey,ObsClient obsClient)
        {
            this.sampleFile = sampleFile;
            this.offset = offset;
            this.partSize = partSize;
            this.partNumber = partNumber;
            this.uploadId = uploadId;
            this.bucketName = bucketName;
            this.objectKey = objectKey;
            this.obsClient = obsClient;
        }

        @Override
        public void run()
        {
            try
            {
                UploadPartRequest uploadPartRequest = new UploadPartRequest();
                uploadPartRequest.setBucketName(bucketName);
                uploadPartRequest.setObjectKey(objectKey);
                uploadPartRequest.setUploadId(this.uploadId);
                uploadPartRequest.setFile(this.sampleFile);
                uploadPartRequest.setPartSize(this.partSize);
                uploadPartRequest.setOffset(this.offset);
                uploadPartRequest.setPartNumber(this.partNumber);

                UploadPartResult uploadPartResult = obsClient.uploadPart(uploadPartRequest);
                System.out.println("Part#" + this.partNumber + " done\n");
                partETags.add(new PartEtag(uploadPartResult.getEtag(), uploadPartResult.getPartNumber()));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private static String claimUploadId(ObsClient obsClient,String bucketName,String objectKey)
            throws ObsException
    {
        InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(bucketName, objectKey);
        InitiateMultipartUploadResult result = obsClient.initiateMultipartUpload(request);
        return result.getUploadId();
    }

    private static void completeMultipartUpload(String uploadId,ObsClient obsClient,String bucketName,String objectKey)
            throws ObsException
    {
        // Make part numbers in ascending order
        Collections.sort(partETags, new Comparator<PartEtag>()
        {

            @Override
            public int compare(PartEtag o1, PartEtag o2)
            {
                return o1.getPartNumber() - o2.getPartNumber();
            }
        });

        System.out.println("Completing to upload multiparts\n");
        CompleteMultipartUploadRequest completeMultipartUploadRequest =
                new CompleteMultipartUploadRequest(bucketName, objectKey, uploadId, partETags);
        obsClient.completeMultipartUpload(completeMultipartUploadRequest);
    }

    private static void listAllParts(String uploadId,ObsClient obsClient,String bucketName,String objectKey)
            throws ObsException
    {
        System.out.println("Listing all parts......");
        ListPartsRequest listPartsRequest = new ListPartsRequest(bucketName, objectKey, uploadId);
        ListPartsResult partListing = obsClient.listParts(listPartsRequest);

        for (Multipart part : partListing.getMultipartList())
        {
            System.out.println("\tPart#" + part.getPartNumber() + ", ETag=" + part.getEtag());
        }
        System.out.println();
    }

}