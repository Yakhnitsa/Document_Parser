package tests.model_test;

import bin.RailroadDocument;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import model.DocumentParser;
import model.FilesRenamer;
import model.FilesRenamerImp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ParserTest {

    public static void main(String[] args) throws IOException, ParseException, ParseException {
        parseTest();
    }
    public static void parseTest(){
        DocumentParser parser = new DocumentParser();
        File file = new File("test_resources/34976860.xml");
        try {
            RailroadDocument document = parser.parseFromFile(file);

            System.out.println(document);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    public static void renameFiles(){
        DocumentParser parser = new DocumentParser();
        File folder = new File("test_resources");
        List<File> xmlFiles = Arrays.asList(folder.listFiles())
                .stream()
                .filter(file->file.getName().endsWith(".xml"))
                .collect(Collectors.toList());
        List<File> pdfFiles = Arrays.asList(folder.listFiles())
                .stream()
                .filter(file-> file.getName().endsWith(".pdf"))
                .collect(Collectors.toList());

        List<RailroadDocument> railroadDocuments = new ArrayList<>();
        for(File file: xmlFiles){
            try{
                railroadDocuments.add(parser.parseFromFile(file));
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        FilesRenamer fileRenamer = new FilesRenamerImp();
        List<String> result = fileRenamer.renameFiles(pdfFiles,railroadDocuments);
        result.forEach(string->System.out.println(string));
    }
}
