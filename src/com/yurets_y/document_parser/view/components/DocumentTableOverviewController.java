package com.yurets_y.document_parser.view.components;

import com.yurets_y.document_parser.bin.RailroadDocument;
import com.yurets_y.document_parser.util.DateUtil;
import com.yurets_y.document_parser.util.ExceptionHandler;
import com.yurets_y.document_parser.util.MessageManager;
import com.yurets_y.document_parser.view.RootViewController;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by Admin on 17.05.2017.
 */
public class DocumentTableOverviewController {


    private Stage primaryStage;
    @FXML
    private TableView<RailroadDocument> documentTable;
    @FXML
    private TableColumn<RailroadDocument, String> docNumberColumn;
    @FXML
    private TableColumn<RailroadDocument, String> senderNameColumn;
    @FXML
    private TableColumn<RailroadDocument, String> docDateColumn;
    @FXML
    private TableColumn<RailroadDocument, String> sendStaticonColumn;
    @FXML
    private TableColumn<RailroadDocument, String> receiveStationColumn;
    @FXML
    private TableColumn<RailroadDocument, String> receiverNameColumn;
    @FXML
    private TableColumn<RailroadDocument, String> cargoNameColumn;
    @FXML
    private TableColumn<RailroadDocument, Integer> vagonCountColumn;

    private RootViewController rootController;

    @FXML
    private void initialize() {
        // Инициализация таблицы документов
        docNumberColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDocNumber()));
        senderNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCargoSender().getName()));
        docDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(DateUtil.format(cellData.getValue().getDocDate())));
        sendStaticonColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSendStation().getName()));
        receiveStationColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getReceiveStation().getName()));
        receiverNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCargoReceiver().getName()));
        cargoNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCargoName()));
        vagonCountColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getVagonCount()).asObject());
        //Добавление слушателя к определенному пункту меню
        documentTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }
    @FXML
    public void deleteAll(){
        documentTable.getItems().clear();
        MessageManager.getInstance().showWarningMessage(primaryStage,"все документы удалены","");
    }
    @FXML
    public void deleteSelected(){
        List<RailroadDocument> selected = documentTable.getSelectionModel().getSelectedItems();
        if(selected.size() > 0){
            documentTable.getItems().removeAll(selected);
        }else{
            MessageManager.getInstance().showWarningMessage(primaryStage,"Документ не выбран","Выберите документ для удаления");
        }
    }
    @FXML
    public void viewDetails(){
        RailroadDocument selectedDocument = documentTable.getSelectionModel().getSelectedItem();
        // Загружаем fxml-файл и создаём новую сцену
        // для всплывающего диалогового окна.
        if(selectedDocument != null){
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(DocumentTableOverviewController.class.getResource("DocumentInfoOverview.fxml"));
                AnchorPane page = loader.load();

                // Создаём диалоговое окно Stage.
                Stage dialogStage = new Stage();
                dialogStage.setTitle("Детальная информация");
                dialogStage.initModality(Modality.WINDOW_MODAL);
                dialogStage.initOwner(primaryStage);

                Scene scene = new Scene(page);
                dialogStage.setScene(scene);

                // Передаём адресата в контроллер.
                DocumentInfoOverviewController controller = loader.getController();
//            controller.setDialogStage(dialogStage);
                controller.showInfo(selectedDocument);

                // Отображаем диалоговое окно и ждём, пока пользователь его не закроет
                dialogStage.showAndWait();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            MessageManager.getInstance().showWarningMessage(primaryStage,"Документ не выбран","Выберите документ для получения информации");
        }

    }

    public List<RailroadDocument> getDocuments(){
        return documentTable.getItems();
    }

    public List<RailroadDocument> getSelectedDocuments(){
        return documentTable.getSelectionModel().getSelectedItems();
    }

    public void setRootController(RootViewController rootController) {
        this.rootController = rootController;

        // Добавление в таблицу данных из наблюдаемого списка
        documentTable.setItems(rootController.getDocumentsData());
        setPrimaryStage(rootController.getPrimaryStage());
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
}
