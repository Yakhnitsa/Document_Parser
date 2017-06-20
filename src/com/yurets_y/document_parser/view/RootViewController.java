package com.yurets_y.document_parser.view;

import com.yurets_y.document_parser.bin.RDocEnum;
import com.yurets_y.document_parser.bin.RailroadDocument;
import com.yurets_y.document_parser.model.DocumentParser;
import com.yurets_y.document_parser.model.DocumentSaver;
import com.yurets_y.document_parser.util.*;
import com.yurets_y.document_parser.view.components.DocumentTableOverviewController;
import com.yurets_y.document_parser.view.components.OrderManagerOverviewController;
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
import java.text.ParseException;
import java.util.List;

/**
 * Created by Admin on 17.05.2017.
 */
public class RootViewController {

    private DocumentParser parser = new DocumentParser();
    private DocumentSaver saver = new DocumentSaver();
    private DocumentTableOverviewController docController;
    private RDocEnum[] columnOrder = PropertiesManager.getInstance().getDefaultColumnOrder();

    private Stage primaryStage;

    private ObservableList<RailroadDocument> documentList = FXCollections.observableArrayList();

    @FXML
    public void initialController(BorderPane rootPane, Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(RootViewController.class.getResource("components/DocumentTableOverview.fxml"));
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
            int count = saver.saveGroupToFile(documents, file, columnOrder);
            String message = String.format("Успешно сохранено %d документов, открыть файл экспорта?", count);
            String title = "Сохранение.";
            String contentText = String.format("Документы находятся в файле: %n%s", file);
            ButtonType choice = MessageManager.getInstance().showConfirmMessage(primaryStage,message,title,contentText);
            if(choice == ButtonType.OK){
                openFileInDesktop(file);
            }
        } else {
            String message = "файлы не сохранены";
            String context = "Путь для сохранения документов не выбран";
            MessageManager.getInstance().showErrorMessage(primaryStage, message, context);
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
            int count = saver.saveGroupToFile(docController.getSelectedDocuments(), file, columnOrder);
            String message = String.format("Успешно сохранено %d документов, открыть файл экспорта?", count);
            String title = "Сохранение.";
            String contentText = String.format("Документы находятся в файле: %n%s", file);
            ButtonType choice = MessageManager.getInstance().showConfirmMessage(primaryStage,message,title,contentText);
            if(choice == ButtonType.OK){
                openFileInDesktop(file);
            }
        } else {
            MessageManager.getInstance().showErrorMessage(primaryStage, "Документы не сохранены", "Ошибка сохранения");
        }

    }

    @FXML
    public void testButtonAction() {
        File file = FileChooseDialog.getFolderToLoad(primaryStage);
        System.out.println(file);
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

    public void testDocumentList(){
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
        try {
            RailroadDocument doc = parser.parseFromFile(file);
            if(documentList.contains(doc)){
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

    private void openFileInDesktop(File file){
        Desktop desktop = null;
        if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
        }
        try {
            assert desktop != null;
            desktop.open(file);
        } catch (IOException ioe) {
            MessageManager.getInstance().showExceptionMessage(ioe,null);
            ioe.printStackTrace();
        }
    }

}
