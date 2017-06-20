package com.yurets_y.document_parser.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

/**
 * Created by Admin on 20.05.2017.
 */
public class MessageManager {
    private static MessageManager ourInstance = new MessageManager();

    public static MessageManager getInstance() {
        return ourInstance;
    }

    private MessageManager() {
    }

    private void showMessage(Stage stage, Alert.AlertType messageType, String message, String title, String contentText) {
        Alert alert = new Alert(messageType);
        alert.initOwner(stage);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    public void showSaveMesage(Stage stage, String message, String contentText) {
        showMessage(stage, Alert.AlertType.INFORMATION, message, "Сохранение документа", contentText);
    }

    public void showInfoMessage(Stage stage, String message, String contentText) {
        showMessage(stage, Alert.AlertType.INFORMATION, message, "Информация", contentText);
    }

    public void showLoadMessage(Stage stage, String message, String contentText) {
        showMessage(stage, Alert.AlertType.INFORMATION, message, "Загрузка документа", contentText);
    }

    public void showErrorMessage(Stage stage, String message, String contentText) {
        showMessage(stage, Alert.AlertType.ERROR, message, "Ошибка!!!", contentText);
    }

    public void showWarningMessage(Stage stage, String message, String contentText) {
        showMessage(stage, Alert.AlertType.INFORMATION, message, "Предупреждение", contentText);
    }

    public void showExceptionMessage(Throwable ex, Stage owner) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(ex.getMessage());
        alert.initOwner(owner);

        //Пролучение стактрейс ошибки
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        //Добавление панели исключения на панель ошибки
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }

    public ButtonType showConfirmMessage(Stage stage, String message, String tytle, String contentText) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(tytle);
        alert.setHeaderText(message);
        alert.setContentText(contentText);
        alert.initOwner(stage);

        Optional<ButtonType> result = alert.showAndWait();
        return result.get();
    }
}
