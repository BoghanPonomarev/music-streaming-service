package com.service.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class S3ServiceImpl {

  @Autowired
  private AmazonS3 amazonS3;

  public File getFile(String fileName) {
    File destinationFile = new File("file.gif");
    try {
      destinationFile.createNewFile();
    } catch (IOException e) {
      e.printStackTrace();
    }
    amazonS3.getObject(
            new GetObjectRequest("media-service-bucket1", fileName),
            destinationFile
    );
    return destinationFile;
  }
}
