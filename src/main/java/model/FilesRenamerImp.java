package model;

import bin.RailroadDocument;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Admin on 26.08.2017.
 */
public class FilesRenamerImp implements FilesRenamer {
    @Override
    public List<String> renameFiles(List<File> files, List<RailroadDocument> documents) {
        List<String> result = new ArrayList<>();
        for(RailroadDocument document: documents){
            String docNumb = document.getDocNumber();
            List<File> docFile = files.stream()
                    .filter(file->file.getName().contains(docNumb)&&file.getName().endsWith(".pdf"))
                    .collect(Collectors.toList());
            if(!docFile.isEmpty()){
                try {
                    newName(docFile.get(0).toPath(),getFileName(document));
                    files.remove(docFile.get(0));
                } catch (IOException e) {
                    e.printStackTrace();
                    result.add("Ошибка переименования файла: " + docFile.get(0).getName());
                }
            }
            else{
                result.add("Докумена " + docNumb + " нет в списке");
            }
        }


        return result;
    }

    public String getFileName(RailroadDocument document){
        Date date = document.getDocDate();
        String station = document.getSendStation().getName();
        int vagCount = document.getVagonCount();
        String railroadCode = document.getDocNumber();
        String receiveCode = document.getCargoReceiver().getRailroadCode();
        return String.format("%1$td_%1$tm_%1$tY %2$s %3$d ваг ЖД %4$s (%5$s).pdf",date,station,vagCount,railroadCode,receiveCode);
    }

    /**
     * Переименование файла
     * @param oldName
     * @param newNameString
     * @return
     * @throws IOException
     */
    private Path newName(Path oldName, String newNameString) throws IOException{
        return Files.move(oldName, oldName.resolveSibling(newNameString));
    }
}
