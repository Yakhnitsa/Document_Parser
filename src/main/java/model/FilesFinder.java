package model;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Admin on 13.03.2018.
 */
public interface FilesFinder {
    List<File> findDocuments(File path, List<String> condition) throws IOException;


    List<String> testFoundResult(List<File> sortedFiles, List<String> conditions);
}
