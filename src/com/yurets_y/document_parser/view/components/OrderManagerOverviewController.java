package com.yurets_y.document_parser.view.components;

import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
import com.yurets_y.document_parser.bin.RDocEnum;
import com.yurets_y.document_parser.util.*;
import com.yurets_y.document_parser.view.RootViewController;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Admin on 21.05.2017.
 */
public class OrderManagerOverviewController {

    private static Stage dialogStage;

    private RootViewController rootViewController;


    @FXML
    private TableView<RDocEnum> leftTable;
    @FXML
    private TableView<RDocEnum> rithtTable;
    @FXML
    private TableColumn<RDocEnum, String> leftTableColumn;
    @FXML
    private TableColumn<RDocEnum, String> rightTableColumn;

    private ObservableList<RDocEnum> columnList;

    public static RDocEnum[] getNewOrder(RootViewController controller){
        RDocEnum[] oldValue = controller.getColumnOrder();
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(OrderManagerOverviewController.class.getResource("OrderManagerOverview.fxml"));
            AnchorPane page = loader.load();

            // Создаём диалоговое окно Stage.
            dialogStage = new Stage();
            dialogStage.setTitle("Оно выбора колонок");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(controller.getPrimaryStage());

            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            OrderManagerOverviewController orderController = loader.getController();
            orderController.setRootViewController(controller);

            // Отображаем диалоговое окно и ждём, пока пользователь его не закроет
            dialogStage.showAndWait();

            return orderController.getSelectedOrder();
        }catch(IOException e) {
            ExceptionHandler.handleException(e);
        }

        return oldValue;
    }

    private RDocEnum[] getSelectedOrder(){
        return rithtTable.getItems().toArray(new RDocEnum[0]);
    }
    @FXML
    public void saveOrder(){
        File defaultFile = PropertiesManager.getInstance().getDefaultColumnOrderFile();
        File file = FileChooseDialog.getXMLFileToSave(dialogStage,defaultFile);
        if (file != null) {
            // Make sure it has the correct extension
            if (!file.getPath().endsWith(".xml")) {
                file = new File(file.getPath() + ".xml");
            }
            try {
                RDocEnumLoader.getInstance().saveOrderToXML(getSelectedOrder(),file);
            } catch (IOException|JAXBException e) {
                String message = "Ошибка сохраения порядка импорта";
                String context = "Проверьте файл \n" + file;
                MessageManager.getInstance().showErrorMessage(dialogStage,message,context);
            }
        }
    }
    @FXML
    public void loadOrder(){
        File defaultFile = PropertiesManager.getInstance().getDefaultColumnOrderFile();
        File file = FileChooseDialog.getXMLFileToLoad(dialogStage,defaultFile);
        if (file != null) {
            try {
                RDocEnum[] order = RDocEnumLoader.getInstance().loadOrderFromXML(file);
                if(order != null){
                    columnList.clear();
                    columnList.addAll(order);
                    rithtTable.setItems(columnList);
                }
            } catch (IOException|JAXBException e) {
                String message = "Ошибка загрузки порядка импорта";
                String context = "Проверьте файл \n" + file;
                MessageManager.getInstance().showErrorMessage(dialogStage,message,context);
            }
        }
    }
    @FXML
    private void handleLeftTableClick(){
        RDocEnum item = leftTable.getSelectionModel().getSelectedItem();
        columnList.add(item);

    }
    @FXML
    private void handleRightTableClick(){
        RDocEnum item = rithtTable.getSelectionModel().getSelectedItem();
        columnList.remove(item);
    }
    public RootViewController getRootViewController() {
        return rootViewController;
    }

    private void setRootViewController(RootViewController rootViewController) {
        columnList = FXCollections.observableArrayList();
        columnList.addAll(rootViewController.getColumnOrder());
        rithtTable.setItems(columnList);
        leftTable.setItems(FXCollections.observableArrayList(RDocEnum.values()));
        // Инициализация таблицы
        leftTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().toString()));
        rightTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().toString()));

        this.rootViewController = rootViewController;
    }

}
