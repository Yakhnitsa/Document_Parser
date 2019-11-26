package tests.model_test;

import model.FilesFinder;
import model.FilesFinderImpl;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Admin on 13.03.2018.
 */
public class FilesFinderTest {
    public static void main(String[] args) {
        FilesFinder finder = new FilesFinderImpl();
        File filePath = new File("C:\\Users\\Admin\\Dropbox\\Development\\intelliJProjects\\JavaFX\\DocumentParserFXML\\test_resources\\07_03_2018");
        List<String> condition = Arrays.asList(new String[]{
                "34012260",
                "34012674",
                "34013524",
                "",
                " ",
                "   ",
        });

        try {
            List<File> sortedFiles = finder.findDocuments(filePath, condition);
            sortedFiles.stream()
                    .forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
