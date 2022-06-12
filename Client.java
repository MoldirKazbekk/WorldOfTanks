package project2;

import java.io.*;
import java.net.*;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.text.*;


public class Client extends Application {
    private static Socket socket;
    @Override
    public void start(Stage primaryStage) throws Exception {
        //Creates a socket of the client in order to connecting to the specified Server
        socket = new Socket("localhost",8000);
        //Streams for getting and sending signals to the server
     DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
     DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        StackPane stackPane = new StackPane();
        Label text = new Label();
        text.setFont(new Font("Times New Roman",50));

        stackPane.getChildren().add(text);
        Scene scene = new Scene(stackPane, 500,250);
        primaryStage.setScene(scene);
        primaryStage.show();

        scene.setOnKeyPressed(event -> {
            String output = "";
            String input = event.getText();
            if(input.trim().equals(""))
                output = "FIRE";
            else if(input.toUpperCase().equals("A") || input.toUpperCase().equals("Ф"))
                output = "LEFT";
            else if(input.toUpperCase().equals("D") || input.toUpperCase().equals("В"))
                output = "RIGHT";
            else if(input.toUpperCase().equals("W") || input.toUpperCase().equals("Ц"))
                output = "UP";
            else if(input.toUpperCase().equals("S") || input.toUpperCase().equals("Ы"))
                output = "DOWN";
            text.setText(output);
            try {
                outputStream.writeUTF(output);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });


    }
}
