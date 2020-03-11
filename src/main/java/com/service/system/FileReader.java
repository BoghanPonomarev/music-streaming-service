package com.service.system;

import com.service.exception.FileSystemOperationException;

public interface FileReader {

  String readFile(String filePath) throws FileSystemOperationException;

}
