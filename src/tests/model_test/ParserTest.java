package tests.model_test;

import com.yurets_y.document_parser.bin.RailroadDocument;
import com.yurets_y.document_parser.model.DocumentParser;
import com.yurets_y.document_parser.model.Parser;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

/**
 * Created by Admin on 31.05.2017.
 */
public class ParserTest {
    public static void main(String[] args) throws IOException, ParseException {
        DocumentParser parser = new DocumentParser();
        File file = new File("test_resources/Ичня.xml");
        RailroadDocument document = parser.parseFromFile(file);


    }
}
