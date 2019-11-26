package util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class FilesUtil {

    public static List<File> getFilteredFilesFromFolder(File folder, String extension) throws IOException {
        return Files.walk(folder.toPath())
                .filter(Files::isRegularFile)
                .map(Path::toFile)
                .filter(file ->file.toString().endsWith(extension))
                .collect(Collectors.toList());
    }
}
