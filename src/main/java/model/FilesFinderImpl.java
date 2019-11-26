package model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Admin on 13.03.2018.
 */
public class FilesFinderImpl implements FilesFinder {
    @Override
    public List<File> findDocuments(File path, List<String> conditions) throws IOException {
        List<File> allFiles = Files.walk(Paths.get(path.getAbsolutePath()))
                .filter(Files::isRegularFile)
                .map(Path::toFile)
                .collect(Collectors.toList());

        List<File> sortedFiles = new ArrayList<>();
        for(File file: allFiles){
            if(isFileContains(file, conditions)){
                sortedFiles.add(file);
            }
        }
        return sortedFiles;
    }

    @Override
    public List<String> testFoundResult(List<File> sortedFiles, List<String> conditions) {
        List<String> resultList = new ArrayList<>(conditions);
        for(String condition: conditions){
            if(fileIsFound(sortedFiles,condition)){
                resultList.remove(condition);
            }
        }
        return resultList;
    }

    private boolean fileIsFound(List<File> sortedFiles, String condition) {
        for(File file: sortedFiles){
            if(file.getName().contains(condition)){
                return true;
            }
        }
        return false;
    }

    private boolean isFileContains(File file, List<String> conditions){
        if(file.isDirectory()) return false;
        for(String condition: conditions){
            if(condition.matches("\\s*")){
                continue;
            }
            if(file.getName().contains(condition))
                return true;

        }
        return false;
    }
    private boolean isFileMatches(File file, List<String> conditions){
        if(!file.isDirectory()) return false;
        for(String condition: conditions){
            if(file.getName().toLowerCase().matches(condition.toLowerCase()))
                return true;

        }
        return false;
    }


}
