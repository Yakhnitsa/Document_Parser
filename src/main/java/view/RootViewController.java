package view;

import bin.RDocEnum;
import bin.RailroadDocument;
import model.*;
import util.*;
import view.components.DocumentTableOverviewController;
import view.components.OrderManagerOverviewController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Admin on 17.05.2017.
 */
public class RootViewController {

    private DocumentParser parser = new DocumentParser();
    private DocumentSaver saver = new DocumentSaver();
    private FilesRenamer filesRenamer = new FilesRenamerImp();
    private FilesFinder filesFinder = new FilesFinderImpl();
    private DocumentTableOverviewController docController;
    private RDocEnum[] columnOrder = PropertiesManager.getInstance().getDefaultColumnOrder();
    private Stage primaryStage;
    private ObservableList<RailroadDocument> documentList = FXCollections.observableArrayList();

    @FXML
    public void initialController(BorderPane rootPane, Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(RootViewController.class.getResource("/fxml/DocumentTableOverview.fxml"));
            AnchorPane docTable = (AnchorPane) loader.load();
            rootPane.setCenter(docTable);
            docController = loader.getController();
            docController.setRootController(this);
            docController.setPrimaryStage(primaryStage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void addFilesFromFolder() {
        File dir = FileChooseDialog.getFolderToLoad(primaryStage);
        if ((dir != null) && (dir.exists())) {
            PropertiesManager.getInstance().setDefaultAddFolder(dir);
            int count = 0;
            for (File file : dir.listFiles()) {
                count += loadDocument(file);
            }
            String message = "Успешно загружено " + count + " документов";
            MessageManager.getInstance().showLoadMessage(primaryStage, message, "");
        } else {
            String message = "Ошибка загрузки, файлы не выбраны";
            String context = "";
            MessageManager.getInstance().showErrorMessage(primaryStage, message, context);
        }
    }

    @FXML
    public void addFiles() {
        List<File> files = FileChooseDialog.getXMLFilesToLoad(primaryStage);
        if ((files != null) && (files.size() > 0)) {
            if (files.size() > 0) {
                PropertiesManager.getInstance().setDefaultAddFolder(files.get(0).getParentFile());
            }
            int count = 0;
            for (File file : files) {
                count += loadDocument(file);
            }
            String message = "Успешно загружено " + count + " документов";
            MessageManager.getInstance().showLoadMessage(primaryStage, message, "");
        } else {
            String message = "Ошибка загрузки, файлы не выбраны";
            String context = "";
            MessageManager.getInstance().showErrorMessage(primaryStage, message, context);
        }

    }

    @FXML
    public void saveAll() {
        List<RailroadDocument> documents = docController.getDocuments();
        if (documents.size() < 1) {
            MessageManager.getInstance().showInfoMessage(primaryStage, "Нет данных для сохранения", "");
            return;
        }
        File file = FileChooseDialog.getXLSFileToSave(primaryStage);
        if (file != null) {

            PropertiesManager.getInstance().setDefaultSaveFolder(file.getParentFile());

            int count = saver.saveGroupToFile(documents, file, columnOrder);
            showSaveResultMessage(file, count);
        } else {
            String message = "файлы не сохранены";
            String context = "Путь для сохранения документов не выбран";
            MessageManager.getInstance().showErrorMessage(primaryStage, message, context);
        }
    }

    private void showSaveResultMessage(File file, int count) {
        String message = String.format("Успешно сохранено %d документов, открыть файл экспорта?", count);
        String title = "Сохранение.";
        String contentText = String.format("Документы находятся в файле: %n%s", file);
        ButtonType choice = MessageManager.getInstance().showConfirmMessage(primaryStage, message, title, contentText);
        if (choice == ButtonType.OK) {
            openFileInDesktop(file);
        }
    }

    @FXML
    public void saveSelected() {
        List<RailroadDocument> documents = docController.getSelectedDocuments();
        if (documents.size() < 1) {
            MessageManager.getInstance().showInfoMessage(primaryStage, "Нет данных для сохранения", "Документы для сохранения не выбраны");
            return;
        }
        File file = FileChooseDialog.getXLSFileToSave(primaryStage);
        if (file != null) {
            PropertiesManager.getInstance().setDefaultSaveFolder(file.getParentFile());
            int count = saver.saveGroupToFile(docController.getSelectedDocuments(), file, columnOrder);
            showSaveResultMessage(file, count);
        } else {
            MessageManager.getInstance().showErrorMessage(primaryStage, "Документы не сохранены", "Ошибка сохранения");
        }

    }

    @FXML
    public void testButtonAction() {
        renameFiles();
    }

    @FXML
    public void renameFiles() {
        File directory = FileChooseDialog.getFolderToLoad(primaryStage);
        List<File> renameFiles = Arrays.asList(directory.listFiles());
        renameFiles = renameFiles.stream()
                .filter(file -> file.getName().endsWith(".pdf"))
                .collect(Collectors.toList());
        showRenameResultMessage(renameFiles, documentList);

    }

    @FXML
    public void findFiles() {
        File filePath = FileChooseDialog.getFolderToLoad(getPrimaryStage());
        if ((filePath == null) || !filePath.isDirectory() || !filePath.exists()) {
            ExceptionHandler.showMessage("Данный путь не является директорией или не существует");
            return;
        }
        String textConditions = MessageManager.getInstance().showInputTextMessage("Ввод данных", getPrimaryStage());
        List<String> conditions = Arrays.asList(textConditions.split("\\n"));
        List<File> sortedFiles;
        try {
            sortedFiles = filesFinder.findDocuments(filePath, conditions);
        } catch (IOException e) {
            ExceptionHandler.handleException(e);
            return;
        }

        List<String> notFoundConditions = filesFinder.testFoundResult(sortedFiles, conditions);

        if (sortedFiles.size() > 0 && notFoundConditions.size() == 0) {
            String title = "Файлы найдены";
            String message = "Найдено файлов: " + sortedFiles.size();
            MessageManager.getInstance().showInfoMessage(getPrimaryStage(), title, message);
        } else {
            String title = "Присутствуют не найденные файлы!";
            String message = "Файлы, удовлетворяющие условиям поиска не найдены";

            MessageManager.getInstance().showDetailedMessage(title,notFoundConditions,getPrimaryStage());
        }
        File outputDir = FileChooseDialog.getFolder(getPrimaryStage(), PropertiesManager.getInstance().getDefaultAddFolder());
        if (outputDir == null) {
            return;
        }
        int copyFiles = 0;
        for (File sortFile : sortedFiles) {
            Path outPath = new File(outputDir.getAbsolutePath() + File.separator + sortFile.getName()).toPath();
            try {
                Files.copy(sortFile.toPath(), outPath);
                copyFiles++;
            } catch (IOException e) {
                e.printStackTrace();
                ExceptionHandler.handleException(e);
            }
        }
        String title = "Файлы сохранены";
        String message = "Сохранено файлов " + copyFiles + System.lineSeparator()
                + outputDir.getAbsolutePath();
        MessageManager.getInstance().showInfoMessage(getPrimaryStage(), title, message);
    }

    public ObservableList<RailroadDocument> getDocumentsData() {
        return documentList;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    @FXML
    public void changeColumnOrder() {
        this.columnOrder = OrderManagerOverviewController.getNewOrder(this);
    }

    public void testDocumentList() {
        File testFile = FileChooseDialog.getExcelFileToLoad(primaryStage);

    }

    @FXML
    public void loadColumnOrder() {
        File defaultFile = PropertiesManager.getInstance().getDefaultColumnOrderFile();
        File file = FileChooseDialog.getXMLFileToLoad(primaryStage, defaultFile);
        try {
            RDocEnum[] order = RDocEnumLoader.getInstance().loadOrderFromXML(file);
            String message = "Порядок колонок успешно загружен";
            columnOrder = order;
            MessageManager.getInstance().showLoadMessage(primaryStage, message, "");
        } catch (Exception e) {
            MessageManager.getInstance().showErrorMessage(primaryStage, "Ошибка загрузки", "Файл не выбран или имеет пеподдерживаемый формат");
        }
    }

    @FXML
    public void saveColumnOrder() {
        File defaultFile = PropertiesManager.getInstance().getDefaultColumnOrderFile();
        File file = FileChooseDialog.getXMLFileToSave(primaryStage, defaultFile);
        try {
            RDocEnumLoader.getInstance().saveOrderToXML(columnOrder, file);
            String message = "Порядок колонок успешно сохранен";
            String context = "Путь для сохранения: \n" + file;
            MessageManager.getInstance().showLoadMessage(primaryStage, message, context);
        } catch (Exception e) {
            MessageManager.getInstance().showErrorMessage(primaryStage, "Ошибка сохранения", "Файл не выбран или имеет пеподдерживаемый формат");
        }
    }

    @FXML
    public void changeDefaultLoadFolder() {
        File defaultFile = PropertiesManager.getInstance().getDefaultAddFolder();
        File file = FileChooseDialog.getFolder(primaryStage, defaultFile);
        if ((file != null) && file.exists()) {
            PropertiesManager.getInstance().setDefaultAddFolder(file);
            String message = "Папка импорта успешно изменена";
            String context = "Папка импорта по уполчанию \n" + file;
            MessageManager.getInstance().showSaveMesage(primaryStage, message, context);
        } else {
            MessageManager.getInstance().showErrorMessage(primaryStage, "Ошибка сохранения", "Путь не выбран или не существует");
        }
    }

    @FXML
    public void changeDefaultSaveFolder() {
        File defaultFile = PropertiesManager.getInstance().getDefaultSaveFolder();
        File file = FileChooseDialog.getFolder(primaryStage, defaultFile);
        if ((file != null) && file.exists()) {
            PropertiesManager.getInstance().setDefaultSaveFolder(file);
            String message = "Папка экспорта успешно изменена";
            String context = "Папка экспорта по уполчанию \n" + file;
            MessageManager.getInstance().showSaveMesage(primaryStage, message, context);
        } else {
            MessageManager.getInstance().showErrorMessage(primaryStage, "Ошибка сохранения", "Путь не выбран или не существует");
        }
    }

    @FXML
    public void magicWang() {
        File inputFolder = FileChooseDialog.getFolder(primaryStage, PropertiesManager.getInstance().getDefaultAddFolder());
        if((inputFolder==null)||(!inputFolder.exists())){
            return;
        }
        try {
            List<File> xmlFiles = FilesUtil.getFilteredFilesFromFolder(inputFolder,".xml");

            List<File> pdfFiles = FilesUtil.getFilteredFilesFromFolder(inputFolder,".pdf");

            List<RailroadDocument> documents = new ArrayList<>();

            for (File xmlFile : xmlFiles) {
                documents.add(parser.parseFromFile(xmlFile));
            }

            documentList.addAll(documents);

            showRenameResultMessage(pdfFiles, documents);

            File resultXlsFile = new File(inputFolder.getAbsolutePath() + FileSystems.getDefault().getSeparator() + "summ.xlsx");
            if(!resultXlsFile.exists()){
                resultXlsFile.createNewFile();
            }

            int count = saver.saveGroupToFile(documents, resultXlsFile, columnOrder);
            showSaveResultMessage(resultXlsFile, count);


        } catch (IOException e) {
            e.printStackTrace();
            ExceptionHandler.handleException(e);
        } catch (ParseException e) {
            ExceptionHandler.handleException(e);
        }

    }

    private void showRenameResultMessage(List<File> pdfFiles, List<RailroadDocument> documents) {
        List<String> renameResult = filesRenamer.renameFiles(pdfFiles,documents);

        if (renameResult.isEmpty()) {
            String message = "Переименование завершено!";
            String contentText = "Файлы в папке успешно переименованны";
            MessageManager.getInstance().showInfoMessage(primaryStage, message, contentText);
        } else {
            MessageManager.getInstance().showDetailedMessage("Ошибка переименования: ", renameResult, primaryStage);
        }
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }


    /*
     * Методы для тестирования приложения
     */
    public void loadTestDocuments() {
        File testFolder = new File("test_resources");
        for (File file : testFolder.listFiles()) {
            try {
                RailroadDocument doc = parser.parseFromFile(file);
                documentList.add(doc);

            } catch (ParseException | IOException e) {
                ExceptionHandler.handleException(e);
            }
        }
    }

    public RDocEnum[] getColumnOrder() {
        return columnOrder;
    }

    /**
     * Закрывает приложение.
     */
    @FXML
    private void handleExit() {
        System.exit(0);
    }

    private int loadDocument(File file) {
        if (!file.getName().endsWith(".xml")) {
            return 0;
        }
        try {
            RailroadDocument doc = parser.parseFromFile(file);
            if (documentList.contains(doc) && !(doc.getDocNumber().equals(""))) {
                return 0;
            }
            documentList.add(doc);
            return 1;
        } catch (ParseException | IOException e) {
            String message = "Ошибка загрузки файла " + System.lineSeparator() + file.getName();
            String context = e.getLocalizedMessage();
            MessageManager.getInstance().showErrorMessage(primaryStage, message, context);
        }
        return 0;
    }

    private void openFileInDesktop(File file) {
        Desktop desktop = null;
        if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
        }
        try {
            assert desktop != null;
            desktop.open(file);
        } catch (IOException ioe) {
            MessageManager.getInstance().showExceptionMessage(ioe, null);
            ioe.printStackTrace();
        }
    }

}
