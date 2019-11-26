package view.components;

import bin.RailroadDocument;
import util.DateUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Created by Admin on 17.05.2017.
 */
public class DocumentInfoOverviewController {



    private Stage primaryStage;
    @FXML
    private Label docNumbLabel;
    @FXML
    private Label docDateLabel;
    @FXML
    private Label senderLabel;
    @FXML
    private Label receiverbLabel;
    @FXML
    private Label payerLabel;
    @FXML
    private Label outStationLabel;
    @FXML
    private Label innStationLabel;
    @FXML
    private Label cargoNameLabel;
    @FXML
    private Label cargoWeightLabel;
    @FXML
    private Label vagonCountLabel;
    @FXML
    private Label info7Label;
    @FXML
    private Label info15Label;


    void showInfo(RailroadDocument document){
        docNumbLabel.setText(document.getDocNumber());
        docDateLabel.setText(DateUtil.format(document.getDocDate()));
//        docDateLabel.setPromptText("dd.mm.yyyy");
        senderLabel.setText(document.getCargoSender().getName());
        receiverbLabel.setText(document.getCargoReceiver().getName());
        payerLabel.setText(document.getTarifPayer().getName());
        outStationLabel.setText(document.getOutStation().getNameAndCode());
        innStationLabel.setText(document.getInnStation().getNameAndCode());
        cargoNameLabel.setText(document.getCargoName());
        cargoWeightLabel.setText(Integer.toString(document.getFullVeight()));
        vagonCountLabel.setText(Integer.toString(document.getVagonCount()));
        info7Label.setText(document.getColumn7info());
        info15Label.setText(document.getColumn15info());
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
}
