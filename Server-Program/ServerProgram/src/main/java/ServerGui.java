import javafx.application.Application;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ServerGui extends Application {

	Server serverConnection;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	//feel free to remove the starter code from this method
	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		primaryStage.setTitle("Server");
		
		TextField portField = new TextField(); 
		portField.setPromptText("port #");
		portField.setMaxWidth(150);

		Button start = new Button("start server!");
		
		ListView gameplayStatus = new ListView(); 
		gameplayStatus.setMaxWidth(400);
		gameplayStatus.setMaxHeight(400);

		BorderPane pane = new BorderPane();
		pane.setStyle("-fx-background-color: gold");
		
		pane.setCenter(gameplayStatus);

		Scene gameplayStatsScene = new Scene(pane, 600,600);

		gameplayStatus.getItems().addAll("num clients connected to server: 0");

		start.setOnAction(x -> {
			int port = Integer.parseInt(portField.getText());
			serverConnection = new Server(port, gameplayStatus);
			primaryStage.setScene(gameplayStatsScene);
		});
				
		Scene startServerScene = new Scene(new VBox(portField, start), 250, 250);
		primaryStage.setScene(startServerScene);
		primaryStage.show();
	}

}
