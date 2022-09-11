import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Client extends Thread {
    Socket socketClient; 
    
    ObjectOutputStream out;
    ObjectInputStream in;

    String ip; 
    int port;

    //GUI elements that must be modified during runtime
    TextField gameStatus, guessField, oppGuessed; 
    ListView gameStatusPane;
    ArrayList<Button> handBtns;
    Button ready, nextRoundbtn, playAgainBtn, quit;
    StackPane displayOppHand;
    Scene connectScene; 
    Stage primaryStage; 

    int points;

    Client(String ip, int port, Stage primaryStage, Scene connectScene, TextField gameStatus, TextField guessField, ListView gameStatusPane, ArrayList<Button> handBtns, Button ready, TextField
     oppGuessed, StackPane displayOppHand, Button nextRoundBtn, Button playAgainBtn, Button quit) {
        this.ip = ip;
        this.port = port;

        this.primaryStage = primaryStage;
        this.connectScene = connectScene;
        this.gameStatus = gameStatus;
        this.guessField = guessField;
        this.gameStatusPane = gameStatusPane;
        this.handBtns = handBtns; 
        this.ready = ready; 
        this.oppGuessed = oppGuessed;
        this.displayOppHand = displayOppHand;
        this.nextRoundbtn = nextRoundBtn;
        this.playAgainBtn = playAgainBtn;
        this.quit = quit; 

        points = 0;
    }
    public void run() {
        try { //try connecting to ip, port. open in/out streams 
            socketClient = new Socket(ip, port);
            out = new ObjectOutputStream(socketClient.getOutputStream());
            in = new ObjectInputStream(socketClient.getInputStream()); 
            socketClient.setTcpNoDelay(true);
        } catch (Exception e) {System.out.println("error - unable to connect to server"); Platform.runLater(() -> {primaryStage.setScene(connectScene);}); return;}

    while (true) {//wait to start game
        try {
            MorraInfo newInfo = (MorraInfo)in.readObject();  //read new morraInfo

            if (newInfo.have2players() || newInfo.isPlayingAgain()) { //ready to start game
                Platform.runLater(() -> {
                    gameStatus.setText("have 2 players! game start.");
                    guessField.clear();
                    handBtns.forEach(btn -> {
                        btn.setDisable(false);
                    });
                    ready.setDisable(false);
                });         
                break;        
            }
    } catch(Exception e) {}
    }
    while (true) {
        points = 0;
        playGame(); 

        //wait for response from server to start playing again
        try {
            
            MorraInfo startSign = (MorraInfo)in.readObject();
            Platform.runLater(() -> {
                gameStatus.setText("resuming gameplay!");
                handBtns.forEach(y -> {y.setDisable(false);});
                ready.setDisable(false);
            });
        } catch(Exception e) {e.printStackTrace();}
    }
}

    private void playGame() {
        while (true) { //play game 
            try {
                //read opp move
                MorraInfo oppMove = (MorraInfo)in.readObject(); 

                //display to screen
                Platform.runLater(() -> {  
                    boolean updateUI = true;              
                    if (oppMove.isVictor()) { //opponent won the game
                        oppGuessed.setText("your opponent won :(. would you like to play again?");
                        playAgainBtn.setVisible(true);
                        nextRoundbtn.setVisible(false);
                        quit.setVisible(true);
                        updateUI = false; 
                    } else if (!(oppMove.isVictor()) && !(oppMove.isDraw()) && !(oppMove.isWinnner()) && points == 1) { //you won the game
                        oppGuessed.setText("you won the game! would you like to play again?");
                        playAgainBtn.setVisible(true);
                        nextRoundbtn.setVisible(false);
                        quit.setVisible(true);

                        updateUI = false; 
                    } else if (oppMove.isDraw()) {
                        oppGuessed.setText("draw! they guessed " + Integer.toString(oppMove.getp1guess()) + " and played:");
                    } else if (oppMove.isWinnner()) {
                        oppGuessed.setText("opponent won this round, they guessed " + Integer.toString(oppMove.getp1guess()) + " and played: ");
                    } else {
                        oppGuessed.setText("you won this round! they guessed " + Integer.toString(oppMove.getp1guess()) + ", played: ");
                        points++;
                    }
                    if (updateUI) {
                        gameStatusPane.getItems().clear();
                        gameStatusPane.getItems().addAll("your points: " + Integer.toString(points), "opponents points: " + Integer.toString(oppMove.getp2Points()));
                        gameStatus.setText("next round!");

                        displayOppHand.getChildren().get(oppMove.getp1hand()).setVisible(true);
                        nextRoundbtn.setDisable(false);
                    }
                });

                if (oppMove.isVictor() || (!(oppMove.isVictor()) && !(oppMove.isDraw()) && !(oppMove.isWinnner()) && points == 1)) {
                    break; 
                }

            } catch(SocketException s) {}
              catch(Exception e) {e.printStackTrace();}
        }
    }
    
}