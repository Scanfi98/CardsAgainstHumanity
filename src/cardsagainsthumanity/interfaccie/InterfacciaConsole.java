package cardsagainsthumanity.interfaccie;

import cardsagainsthumanity.cards.Mazzo;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import cardsagainsthumanity.networking.Server;

import java.io.IOException;

/**
 * Created by Giovanni on 25/04/2016.
 */
public class InterfacciaConsole {

    private Server server;
    private ControllerConsole controllerConsole;

    public InterfacciaConsole(Server server) {
        try {
            start(new Stage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("consoleServer.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("Console Server");
        primaryStage.setScene(new Scene(root));
        String path = Mazzo.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath() + "CAH/icon.png";
        primaryStage.getIcons().add(new Image("file:"+path));
        primaryStage.show();
        controllerConsole = fxmlLoader.getController();
        controllerConsole.setServer(server);
        primaryStage.setOnCloseRequest(event -> {
            if(server.isAlive()){
                try {
                    server.getServerSocket().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                server.interrupt();
            }
        });
    }

    public ControllerConsole getControllerConsole() {
        return controllerConsole;
    }

    public void setServer(Server server) {
        this.server = server;
        controllerConsole.setServer(server);
    }
}
