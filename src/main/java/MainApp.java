import util.ExceptionHandler;
import util.PropertiesManager;
import view.RootViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {
    private Stage primaryStage;
    private BorderPane rootLayout;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Thread.currentThread().setUncaughtExceptionHandler(new ExceptionHandler());
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle(PropertiesManager.getInstance().getProgramName());
        String url = "file:res/icons/DocParserIcon.png";
        this.primaryStage.getIcons().add(new Image(url));
        initRootLayout();
    }

    /**
     * Инициализирует корневой макет.
     */
    private void initRootLayout() {
        try {
            // Загружаем корневой макет из fxml файла.
            FXMLLoader loader = new FXMLLoader();
            System.out.println(getClass());
            loader.setLocation(getClass().getResource("fxml/RootView.fxml"));
            this.rootLayout = loader.load();
            RootViewController rootController = (loader.getController());
            rootController.initialController(rootLayout,primaryStage);
            rootController.setPrimaryStage(primaryStage);
            // Отображаем сцену, содержащую корневой макет.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

