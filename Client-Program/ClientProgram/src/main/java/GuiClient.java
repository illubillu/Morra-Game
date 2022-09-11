import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GuiClient extends Application {

	Client clientConnection; 
	MorraInfo morraInfo; 
	int guess, going2play;


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	//feel free to remove the starter code from this method
	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		//build connectScene
		primaryStage.setTitle("Client");
		
		TextField ipField = new TextField(); 
		TextField portField = new TextField(); 

		ipField.setMaxWidth(250);
		portField.setMaxWidth(100);

		ipField.setPromptText("enter ip address");
		portField.setPromptText("enter port #");
		
		ipField.setAlignment(Pos.CENTER);
		portField.setAlignment(Pos.CENTER);
		
		Button connect = new Button("connect"); 
		connect.setPrefWidth(75);
		connect.setAlignment(Pos.CENTER);
				
		Scene connectScene = new Scene(new VBox(ipField, portField, connect), 250,250);
		primaryStage.setScene(connectScene);
		primaryStage.show();

		//build gameplay scene 
		TextField gameStatus = new TextField(); 
		gameStatus.setEditable(false);
		gameStatus.setText("waiting for second player...");
		gameStatus.setAlignment(Pos.CENTER);

		ListView gameStatusPane = new ListView(); 
		gameStatusPane.getItems().addAll("your points: 0", "opponents points: 0");
		gameStatusPane.setMaxHeight(200);

		TextField guessField = new TextField(); 
		guessField.setPromptText("enter your guess for the total number of fingers here!");
		guessField.setAlignment(Pos.CENTER);
		

		ArrayList<ImageView> handImgViews = createImgViewList();
		
		Button hand0btn = new Button(); hand0btn.setGraphic(handImgViews.get(0));
		Button hand1btn = new Button(); hand1btn.setGraphic(handImgViews.get(1));
		Button hand2btn = new Button(); hand2btn.setGraphic(handImgViews.get(2));
		Button hand3btn = new Button(); hand3btn.setGraphic(handImgViews.get(3));
		Button hand4btn = new Button(); hand4btn.setGraphic(handImgViews.get(4));
		Button hand5btn = new Button(); hand5btn.setGraphic(handImgViews.get(5));

		ArrayList<Button> handBtns = new ArrayList<>(); 
		handBtns.add(hand0btn); handBtns.add(hand1btn); handBtns.add(hand2btn); handBtns.add(hand3btn); handBtns.add(hand4btn); handBtns.add(hand5btn); 

		handBtns.forEach(x -> {
			x.setDisable(true);
		});
	
		HBox imageBox = new HBox(10); 
		imageBox.getChildren().addAll(hand0btn, hand1btn, hand2btn, hand3btn, hand4btn, hand5btn);
		
		Button ready = new Button("ready!");
		ready.setDisable(true);
		ready.setAlignment(Pos.BOTTOM_RIGHT);

		Scene gameplayScene = new Scene(new VBox(gameStatus, gameStatusPane, guessField, imageBox, ready), 750, 500);
		

		ArrayList<ImageView> resultsImgList = createImgViewList();
		StackPane displayOppHand = new StackPane();

		resultsImgList.forEach(i -> {
			i.setVisible(false);
		});

		displayOppHand.getChildren().addAll(resultsImgList);
		
		//build resultsScene
		TextField oppGuessed = new TextField(); 
		oppGuessed.setText("waiting for opponent to play...");
		oppGuessed.setEditable(false);

		Button nextRoundBtn = new Button("next round"); 
		nextRoundBtn.setDisable(true);
		nextRoundBtn.setOnAction(x -> {
			//clear all textfields, reset stackpane
			displayOppHand.getChildren().forEach(y -> {
				y.setVisible(false);
			});
			guessField.clear();
			oppGuessed.clear();
			oppGuessed.setText("waiting for opponent...");

			primaryStage.setScene(gameplayScene);
		});		

		Button quit = new Button("quit"); 
		quit.setVisible(false);

		quit.setOnAction(x -> {
			MorraInfo b = new MorraInfo(); 
			b.setIsPlayingAgain(false);
			try {
				clientConnection.out.writeObject(b);
			} catch(Exception e) {}

			Platform.exit();
		}); 

		Button playAgainBtn = new Button("play again!"); 
		playAgainBtn.setVisible(false);

		playAgainBtn.setOnAction(x -> {
			MorraInfo b = new MorraInfo();
			b.setIsPlayingAgain(true);
			try {
				clientConnection.out.writeObject(b);
				clientConnection.out.reset();
			} catch(Exception e) {e.printStackTrace();}
			gameStatus.setText("waiting for opponent!");
			primaryStage.setScene(gameplayScene);
			oppGuessed.setText("waiting for opponnent to play...");

			playAgainBtn.setVisible(false);
			nextRoundBtn.setVisible(true);

			guessField.clear();
			quit.setVisible(false);

			gameStatusPane.getItems().clear();
			gameStatusPane.getItems().addAll("your points: 0", "opponents points: 0");

			ready.setDisable(true); //disable elements
			handBtns.forEach(btn -> {
				btn.setDisable(true);
			});
		});

		ready.setStyle("-fx-background-color: gold");
		quit.setStyle("-fx-background-color: gold");
		playAgainBtn.setStyle("-fx-background-color: gold");
		gameStatusPane.setStyle("-fx-background-color: gold");
		gameStatus.setStyle("-fx-background-color: gold");
		nextRoundBtn.setStyle("-fx-background-color: gold");
		oppGuessed.setStyle("-fx-background-color: gold");

		VBox resultsVbox = new VBox(oppGuessed, displayOppHand, nextRoundBtn, playAgainBtn, quit); 
		Scene resultsScene = new Scene(resultsVbox, 300, 300); 

		//user tries conneting to server 
		connect.setOnAction(x -> {
			try { 
				String ip = ipField.getText(); 
				int port = Integer.parseInt(portField.getText());

				clientConnection = new Client(ip, port, primaryStage, connectScene, gameStatus, guessField, gameStatusPane, handBtns, ready, oppGuessed, displayOppHand, nextRoundBtn, playAgainBtn, quit); clientConnection.start(); 
				primaryStage.setScene(gameplayScene);
			} catch(Exception e) {
				System.out.println("Unable to connect to server - check input");
				return; 
			}
		});

		//configure handlers for each btn
	
		hand0btn.setOnAction(x -> {
			gameStatus.setText("you are going to play 0!");
			going2play = 0;
		});
		hand1btn.setOnAction(x -> {
			gameStatus.setText("you are going to play 1!");
			going2play = 1;
		});
		hand2btn.setOnAction(x -> {
			gameStatus.setText("you are going to play 2!");
			going2play = 2;
		});
		hand3btn.setOnAction(x -> {
			gameStatus.setText("you are going to play 3!");
			going2play = 3;
		});
		hand4btn.setOnAction(x -> {
			gameStatus.setText("you are going to play 4!");
			going2play = 4;
		});
		hand5btn.setOnAction(x -> {
			gameStatus.setText("you are going to play 5!");
			going2play = 5;
		});

		ready.setOnAction(x -> {
			try {
				 guess = Integer.parseInt(guessField.getText());
				if (guess < 0 || guess > 10) {
					gameStatus.setText("please enter a valid guess (must be from a number from 0-10)");
				} else {
					MorraInfo m = new MorraInfo();
					m.setp1guess(guess);
					m.setp1hand(going2play);

					System.out.println("sending " + Integer.toString(guess) + " " + Integer.toString(going2play) + " to server");

					clientConnection.out.writeObject(m); clientConnection.out.reset();

					nextRoundBtn.setDisable(true);
					primaryStage.setScene(resultsScene);
				}
			} catch(NumberFormatException e) {
				gameStatus.setText("check input format for your guess! (must be an integer 0-10 with no spaces)");
			} catch(IOException e) {e.printStackTrace();}
		});



	}

	private ArrayList<ImageView> createImgViewList() {
		ImageView hand0 = new ImageView();
		ImageView hand1 = new ImageView(); 
		ImageView hand2 = new ImageView(); 
		ImageView hand3 = new ImageView(); 
		ImageView hand4 = new ImageView(); 
		ImageView hand5 = new ImageView(); 


		hand0.setImage(new Image("https://as1.ftcdn.net/v2/jpg/01/29/48/72/1000_F_129487299_L21qt0svrfsOKiYTZ7DjJuw8sNOfHLlS.jpg", 100, 100, false, false));
		hand1.setImage(new Image("https://previews.123rf.com/images/illizium/illizium1904/illizium190400166/123790047-mano-con-un-dedo-apuntando-hacia-el-icono-de-l%C3%ADnea-mano-con-el-dedo-%C3%ADndice-hacia-arriba-ilustraci%C3%B3n-.jpg", 100, 100, false, false));
		hand2.setImage(new Image("https://us.123rf.com/450wm/sabuhinovruzov/sabuhinovruzov1901/sabuhinovruzov190100768/116221929-three-fingers-thin-line-icon-arm-gesture-vector-illustration-isolated-on-white-hand-gesture-outline-.jpg?ver=6", 100, 100, false, false));
		hand3.setImage(new Image("https://previews.123rf.com/images/grgroup/grgroup1611/grgroup161105462/65352942-compter-trois-doigts-vers-le-haut-geste-de-la-main-l-image-d-ic%C3%B4ne-illustration-conception.jpg", 100, 100, false, false));
		hand4.setImage(new Image("https://thumbs.dreamstime.com/b/hand-gesture-four-fingers-icon-outline-style-vector-web-design-isolated-white-background-212980466.jpg", 100, 100, false, false));
		hand5.setImage(new Image("https://media.istockphoto.com/vectors/web-vector-id1178260838?k=20&m=1178260838&s=612x612&w=0&h=rseOvA6f9v-zMFZ-HDA6dCXTa-LgCXVpO3dpRODFxzw=", 100, 100, false, false));

		ArrayList<ImageView> handImgViews = new ArrayList<>();
		handImgViews.add(hand0); handImgViews.add(hand1); handImgViews.add(hand2); handImgViews.add(hand3); handImgViews.add(hand4); handImgViews.add(hand5); 

		return handImgViews;
	}

}
