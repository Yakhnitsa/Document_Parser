package util;

import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.File;
import java.util.List;

/**
 * Created by Yuriy on 14.08.2016.
 * Диалог для выбора файлов в системе
 * полностью завершен!!!
 *
 * @see JFileChooser
 */
public class FileChooseDialog {


    /*
     * Новые методы основанные на javaFX платформе
     */


    public static List<File> getXMLFilesToLoad(Stage primaryStage) {
        File defFile = PropertiesManager.getInstance().getDefaultAddFolder();
        return getFilesListToLoadFX(primaryStage, defFile, "*.xml", "xml files");
    }

    public static File getXMLFileToSave(Stage primaryStage, File defaultFile) {
        return getFileToSaveFX(primaryStage, defaultFile, "*.xml", "xml file");
    }

    public static File getExcelFileToLoad(Stage primaryStage){
        File defFile = PropertiesManager.getInstance().getDefaultAddFolder();
        return getFileToLoadFX(primaryStage, defFile, "*.xlsx", "xls excel files");
    }

    public static File getXMLFileToLoad(Stage primaryStage, File defaultFile) {
        return getFileToLoadFX(primaryStage, defaultFile, "*.xml", "xml file");
    }

    public static File getXLSFileToSave(Stage primaryStage) {
        File defSavepath = PropertiesManager.getInstance().getDefaultSaveFolder();
        return getFileToSaveFX(primaryStage, defSavepath, "*.xlsx", "excel xlsx files");
    }

    public static File getFolderToLoad(Stage primaryStage) {
        File defaultLoadFolder = PropertiesManager.getInstance().getDefaultAddFolder();
        return getFolder(primaryStage,defaultLoadFolder);
    }

    public static File getFolder(Stage primaryStage,File defaultPath){

        DirectoryChooser directoryChooser = new DirectoryChooser();
        if ((defaultPath != null)&&(defaultPath.exists())) {
            if(!defaultPath.isDirectory()){
                defaultPath = defaultPath.getParentFile();
            }
            directoryChooser.setInitialDirectory(defaultPath);
        }
        return directoryChooser.showDialog(primaryStage);
    }

    private static File getFileToSaveFX(Stage primaryStage, File defaultPath, String fileExtension, String fileDescription) {
        FileChooser fileChooser = new FileChooser();

        checkNullReferenseAndSetFilter(defaultPath, fileExtension, fileDescription, fileChooser);

        // Показываем диалог сохранения файла

        return fileChooser.showSaveDialog(primaryStage);
    }

    private static List<File> getFilesListToLoadFX(Stage primaryStage, File defaultPath, String fileExtension, String fileDescription) {

        FileChooser fileChooser = new FileChooser();

        checkNullReferenseAndSetFilter(defaultPath, fileExtension, fileDescription, fileChooser);

        return fileChooser.showOpenMultipleDialog(primaryStage);
    }

    private static void checkNullReferenseAndSetFilter(File defaultPath, String fileExtension, String fileDescription, FileChooser fileChooser) {
        if ((defaultPath != null)&&(defaultPath.exists())) {
            if(!defaultPath.isDirectory()){
                defaultPath = defaultPath.getParentFile();
            }
            fileChooser.setInitialDirectory(defaultPath);
        }
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                fileDescription, fileExtension);
        fileChooser.getExtensionFilters().add(extFilter);
    }

    private static File getFileToLoadFX(Stage primaryStage, File defaultPath, String fileExtension, String fileDescription) {

        FileChooser fileChooser = new FileChooser();
        checkNullReferenseAndSetFilter(defaultPath, fileExtension, fileDescription, fileChooser);

        // Показываем диалог сохранения файла
        return fileChooser.showOpenDialog(primaryStage);
    }


}