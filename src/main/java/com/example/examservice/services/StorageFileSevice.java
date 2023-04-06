
package com.example.examservice.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.Transfer;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferProgress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.amazonaws.services.s3.transfer.Upload;



import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * @author Le Hoang Nhat a.k.a Rei202
 * @Date 3/21/2023
 */
@Service
public class StorageFileSevice {
    @Autowired
    private TransferManager transferManager;
    @Autowired
    private AmazonS3 amazonS3Client;
    @Value("${amazon.s3.bucket-name}")
    private String bucketName;

    public String saveFile(MultipartFile file, String key) throws IOException, InterruptedException {
        ObjectMetadata metadata = new ObjectMetadata();
        long totalLengthBytes = file.getSize();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(totalLengthBytes);

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file.getInputStream(), metadata);
        putObjectRequest.setCannedAcl(CannedAccessControlList.PublicRead);
        Upload upload = transferManager.upload(putObjectRequest);
        StorageFileSevice.showTransferProgress(upload); // you can comment this line if it makes console messy, it's used to let me know file was tranfered using multipart or not
        upload.waitForCompletion();
        URL resourceUrl = amazonS3Client.getUrl(bucketName,key);
//        transferManager.shutdownNow();
        return resourceUrl.toString();
    }

    // copy from amazon doc
    public static void printProgressBar(double pct) {
        // if bar_size changes, then change erase_bar (in eraseProgressBar) to
        // match.
        final int bar_size = 40;
        final String empty_bar = "                                        ";
        final String filled_bar = "########################################";
        int amt_full = (int) (bar_size * (pct / 100.0));
        System.out.format("  [%s%s]", filled_bar.substring(0, amt_full),
                empty_bar.substring(0, bar_size - amt_full));
    }
    public static void showTransferProgress(Transfer xfer) {
        // snippet-start:[s3.java1.s3_xfer_mgr_progress.poll]
        // print the transfer's human-readable description
        System.out.println(xfer.getDescription());
        // print an empty progress bar...
        printProgressBar(0.0);
        // update the progress bar while the xfer is ongoing.
        do {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                return;
            }
            // Note: so_far and total aren't used, they're just for
            // documentation purposes.
            TransferProgress progress = xfer.getProgress();
            long so_far = progress.getBytesTransferred();
            long total = progress.getTotalBytesToTransfer();
            double pct = progress.getPercentTransferred();
            eraseProgressBar();
            printProgressBar(pct);
        } while (xfer.isDone() == false);
        Transfer.TransferState xfer_state = xfer.getState();
        System.out.println(": " + xfer_state);

        // print the final state of the transfer.
        // snippet-end:[s3.java1.s3_xfer_mgr_progress.poll]
    }
    public static void eraseProgressBar() {
        // erase_bar is bar_size (from printProgressBar) + 4 chars.
        final String erase_bar = "\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b";
        System.out.format(erase_bar);
    }
}