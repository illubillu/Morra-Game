import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javafx.application.Platform;
import javafx.scene.control.ListView;

public class Server {
    int port;
    TheServer theServer; 
    MorraInfo mInfo;
    ListView gameplayStatus; 
    static int numConnected;

    ObjectOutputStream player1out, player2out;
    ObjectInputStream player1in, player2in;

    Socket player1, player2; 

    Server(int port, ListView gameplayStatus) {//constructor accepts port to start server on, and gameplayStatus pane to update GUI
        this.port = port;
        this.gameplayStatus = gameplayStatus; 
        numConnected = 0;

        theServer = new TheServer();
        mInfo = new MorraInfo();
        theServer.start();
    }

    public class TheServer extends Thread {
        public void run() {
            try(ServerSocket serverSocket = new ServerSocket(port);) {
                System.out.println("server waiting for client");

                 //only accept at most two clients 
                 if (numConnected == 2) {
                     System.out.println("please wiat for the users currently playing to finish.");
                 } else {
                        player1 = serverSocket.accept();
                        player1out = new ObjectOutputStream(player1.getOutputStream());
                        player1in = new ObjectInputStream(player1.getInputStream());
                        numConnected++;

                        Platform.runLater(() -> {
                            gameplayStatus.getItems().clear();
                            gameplayStatus.getItems().addAll("num clients connected to server: " + Integer.toString(numConnected));
                        }
                        );

                        System.out.println("player 1 has connected to the server");   
                        
                        player1out.writeObject(mInfo); //send player 1 morra info
                        player1out.reset();

                        player2 = serverSocket.accept(); 
                        player2out = new ObjectOutputStream(player2.getOutputStream());
                        player2in = new ObjectInputStream(player2.getInputStream());
                        System.out.println("playet 2 has connecetd to the server");
                        numConnected++;

                        Platform.runLater(() -> {
                            gameplayStatus.getItems().clear();
                            gameplayStatus.getItems().addAll("num clients connected to server: " + Integer.toString(numConnected), "p1 points: 0", "p2 points: 0", "p1 played: ", "p2 played: ");
                        }
                        );
                        
                        mInfo.setHave2Players(true); 
                        player1out.writeObject(mInfo); player2out.writeObject(mInfo);
                        player1out.reset(); player2out.reset();

                    while (true) { //play the game 
                        if (mInfo.getp1Points() == 2 || mInfo.getp2Points() == 2) { //someobdy won the game, see who wants to play again
                            //update server on woh won
                            if (mInfo.getp1Points() == 2) {
                                Platform.runLater(() -> {
                                    gameplayStatus.getItems().add("p1 is the victor!");
                                });
                            } else {
                                Platform.runLater(() -> {
                                    gameplayStatus.getItems().add("p2 is the victor!"); 
                                });
                            }
                            //see who is playing again
                            try {
                                MorraInfo p1 = (MorraInfo)player1in.readObject();
                                MorraInfo p2 = (MorraInfo)player2in.readObject();

                                p1.print(); p2.print();

                                if (!(p1.isPlayingAgain()) && !(p2.isPlayingAgain())) {
                                    Platform.runLater(() -> {gameplayStatus.getItems().add("both players not playing again... looking for new players...");});
                                    player1.close(); player2.close(); numConnected -= 2;

                                    Platform.runLater(() -> {gameplayStatus.getItems().set(0, "num clients connected to server: " + Integer.toString(numConnected));});
                                    player1 = serverSocket.accept(); numConnected++;
                                    Platform.runLater(() -> {gameplayStatus.getItems().set(0, "num clients connected to server: " + Integer.toString(numConnected));});
                                    player2 = serverSocket.accept(); numConnected++;
                                    Platform.runLater(() -> {gameplayStatus.getItems().set(0, "num clients connected to server: " + Integer.toString(numConnected));});
                                    Platform.runLater(() -> {gameplayStatus.getItems().set(6, "found new players!");});

                                    player1in = new ObjectInputStream(player1.getInputStream()); player1out = new ObjectOutputStream(player1.getOutputStream());
                                    player2in = new ObjectInputStream(player2.getInputStream()); player2out = new ObjectOutputStream(player2.getOutputStream());                                  
                                }
                                else if (!(p1.isPlayingAgain())) {//wait for another player to connect
                                    Platform.runLater(() -> {gameplayStatus.getItems().add("p1 not playing again... looking for another player");});
                                    player1.close(); numConnected--;

                                    Platform.runLater(() -> {gameplayStatus.getItems().set(0, "num clients connected to server: " + Integer.toString(numConnected));});
                                    player1 = serverSocket.accept(); numConnected++;

                                    Platform.runLater(() -> {gameplayStatus.getItems().set(0, "num clients connected to server: " + Integer.toString(numConnected));});
                                    Platform.runLater(() -> {gameplayStatus.getItems().set(6, "found new player!");});

                                    player1in = new ObjectInputStream(player1.getInputStream()); player1out = new ObjectOutputStream(player1.getOutputStream());
                                } else if (!(p2.isPlayingAgain())) {
                                    Platform.runLater(() -> {gameplayStatus.getItems().add("p2 not playing again... looking for another player");});
                                    player2.close(); numConnected--;

                                    Platform.runLater(() -> {gameplayStatus.getItems().set(0, "num clients connected to server: " + Integer.toString(numConnected));});
                                    player2 = serverSocket.accept(); numConnected++;

                                    Platform.runLater(() -> {gameplayStatus.getItems().set(6, "found new player!");});

                                    player2in = new ObjectInputStream(player2.getInputStream()); player2out = new ObjectOutputStream(player2.getOutputStream());
                                } else { //both players playing again, update server and start game  
                                    System.out.println("both players playing again");
                                    Platform.runLater(() -> {
                                        gameplayStatus.getItems().add("both players are playing again!");
                                    });
                                }
                                MorraInfo s = new MorraInfo(); s.setIsPlayingAgain(true);
                                player1out.writeObject(s);
                                player2out.writeObject(s);
                                
                                player1out.reset();
                                player2out.reset();
                                mInfo.reset();
                            } catch(Exception e) {
                                System.out.println("exceptoin when restarting game"); e.printStackTrace();
                            }
                        }

                        //read player moves
                        MorraInfo p1Move = (MorraInfo)player1in.readObject();
                        MorraInfo p2Move = (MorraInfo)player2in.readObject();

                        //determine winner of round 
                        int total = p1Move.getp1hand() + p2Move.getp1hand(); 
                        if (p1Move.getp1guess() == total && p2Move.getp1guess() == total) {
                            p1Move.setIsDraw(true);
                            p2Move.setIsDraw(true);
                        } else if (p1Move.getp1guess() == total) {
                            mInfo.setp1Points(mInfo.getp1Points() + 1);
                            p1Move.setWinner(true);
                        } else if (p2Move.getp1guess() == total) {
                            mInfo.setp2Points(mInfo.getp2Points() + 1);
                            p2Move.setWinner(true);
                        }

                        p1Move.setp2Points(mInfo.getp1Points());
                        p2Move.setp2Points(mInfo.getp2Points());

                        if (mInfo.getp1Points() == 2) {
                            p1Move.setIsVictor(true);
                            
                            Platform.runLater(() -> {
                                gameplayStatus.getItems().add("player1 is the victor!");
                            });
                        } else if (mInfo.getp2Points() == 2) {
                            p2Move.setIsVictor(true);

                            Platform.runLater(() -> {
                                gameplayStatus.getItems().add("player2 is the victor!");
                            });
                        }

                        //send opponents moves to players
                        player1out.writeObject(p2Move); player1out.reset();
                        player2out.writeObject(p1Move); player2out.reset();                        

                        //update server interface 
                        Platform.runLater(() -> {
                            gameplayStatus.getItems().clear();
                            gameplayStatus.getItems().addAll("num clients connected to server: " + Integer.toString(numConnected), "p1 points: " + Integer.toString(mInfo.getp1Points()), "p2 points: " + Integer.toString(mInfo.getp2Points()), "p1 played: " + Integer.toString(p1Move.getp1hand()) + ", guessed: " + Integer.toString(p1Move.getp1guess()), "p2 played: " + Integer.toString(p2Move.getp1hand()) + ", guessed: " + Integer.toString(p2Move.getp1guess()));
                        });

                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        private void playGame() {
            
        } 
    }

}
