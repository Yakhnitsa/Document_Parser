package view.components;

import bin.RDocEnum;
import javafx.scene.control.TableRow;
import javafx.scene.input.*;
import util.*;
import view.RootViewController;
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

/**
 * Created by Admin on 21.05.2017.
 */
public class OrderManagerOverviewController {

    private static Stage dialogStage;

    private RootViewController rootViewController;


    @FXML
    private TableView<RDocEnum> leftTable;
    @FXML
    private TableView<RDocEnum> rightTable;
    @FXML
    private TableColumn<RDocEnum, String> leftTableColumn;
    @FXML
    private TableColumn<RDocEnum, String> rightTableColumn;

    private ObservableList<RDocEnum> columnList;

    private static final DataFormat SERIALIZED_MIME_TYPE = new DataFormat("application/x-java-serialized-object");

    public static RDocEnum[] getNewOrder(RootViewController controller){
        RDocEnum[] oldValue = controller.getColumnOrder();
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(OrderManagerOverviewController.class.getResource("/fxml/OrderManagerOverview.fxml"));
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
        return rightTable.getItems().toArray(new RDocEnum[0]);
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
                    rightTable.setItems(columnList);
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
        RDocEnum item = rightTable.getSelectionModel().getSelectedItem();
        columnList.remove(item);
    }
    @FXML
    private void applyButtonEvent(){
        dialogStage.close();
    }
    public RootViewController getRootViewController() {
        return rootViewController;
    }

    private void setRootViewController(RootViewController rootViewController) {
        columnList = FXCollections.observableArrayList();
        columnList.addAll(rootViewController.getColumnOrder());
        rightTable.setItems(columnList);
        leftTable.setItems(FXCollections.observableArrayList(RDocEnum.values()));

        setEventHandlers();

        // Инициализация таблицы
        leftTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().toString()));
        rightTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().toString()));


        this.rootViewController = rootViewController;
    }
    /*
     * Применение технологии drag and Drop для правой таблицы и настройка действий мыши.
     */
    private void setEventHandlers() {
        leftTable.setRowFactory(rf->{
            TableRow<RDocEnum> row = new TableRow<>();
            row.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                    if (mouseEvent.getClickCount() == 2) {
                        handleLeftTableClick();
                    }
                }
            });
            return row;
        });

        rightTable.setRowFactory(tv -> {
            TableRow<RDocEnum> row = new TableRow<>();
            row.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                    if (mouseEvent.getClickCount() == 2) {
                        handleRightTableClick();
                    }
                }
            });

            row.setOnDragDetected(event -> {
                if (! row.isEmpty()) {
                    Integer index = row.getIndex();
                    Dragboard db = row.startDragAndDrop(TransferMode.MOVE);
                    db.setDragView(row.snapshot(null, null));
                    ClipboardContent cc = new ClipboardContent();
                    cc.put(SERIALIZED_MIME_TYPE, index);
                    db.setContent(cc);
                    event.consume();
                }
            });

            row.setOnDragOver(event -> {
                Dragboard db = event.getDragboard();
                if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                    if (row.getIndex() != ((Integer)db.getContent(SERIALIZED_MIME_TYPE)).intValue()) {
                        event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                        event.consume();
                    }
                }
            });

            row.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                    int draggedIndex = (Integer) db.getContent(SERIALIZED_MIME_TYPE);
                    RDocEnum draggedPerson = rightTable.getItems().remove(draggedIndex);

                    int dropIndex ;

                    if (row.isEmpty()) {
                        dropIndex = rightTable.getItems().size() ;
                    } else {
                        dropIndex = row.getIndex();
                    }

                    rightTable.getItems().add(dropIndex, draggedPerson);

                    event.setDropCompleted(true);
                    rightTable.getSelectionModel().select(dropIndex);
                    event.consume();
                }
            });

            return row ;
        });
    }

}
