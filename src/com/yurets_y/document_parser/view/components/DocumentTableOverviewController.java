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
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.TextAlignment;
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
        docNumberColumn.setCellValueFactory(new PropertyValueFactory<>("docNumber"));
        senderNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCargoSender().getName()));
        docDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(DateUtil.format(cellData.getValue().getDocDate())));
        sendStaticonColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSendStation().getName()));
        receiveStationColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getReceiveStation().getName()));
        receiverNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCargoReceiver().getName()));
        cargoNameColumn.setCellValueFactory(new PropertyValueFactory<>("cargoCode"));
        vagonCountColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getVagonCount()).asObject());

        documentTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        documentTable.setTableMenuButtonVisible(true);
        documentTable.setPlaceholder(getPlaceHolder());
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

    private Node getPlaceHolder(){
        BorderPane basicPane = new BorderPane();
        basicPane.setPrefSize(300,100);
        basicPane.setMaxSize(300,100);
        basicPane.setVisible(true);
        basicPane.setOpacity(0.8);

        Label label = new Label("Нет документов для отображения");
        label.setAlignment(Pos.CENTER);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setStyle("-fx-font: 11pt System;-fxbackground-color:lightgrey;");

        Button loadDocButton = new Button("Добавить файл");
        loadDocButton.setOnAction(action -> rootController.addFiles());
        Button loadFolderButton = new Button("Добавить папку");
        loadFolderButton.setOnAction(action -> rootController.addFilesFromFolder());
        ButtonBar buttons = new ButtonBar();
        buttons.getButtons().addAll(loadDocButton,loadFolderButton);

        basicPane.setCenter(label);
        basicPane.setBottom(buttons);
        basicPane.setStyle("-fx-border-width:1pt;-fx-border-color:lightgray");

        return basicPane;
    }
}
