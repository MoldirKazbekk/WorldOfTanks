package project2;

import javafx.animation.PathTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
//the main class for playing a tank game
public class Server extends Application {
    static Pane main = new Pane();
    static final int size_Of_Block = 64;
    static String path = "C:\\Users\\ADMIN\\IdeaProjects\\untitled\\src\\com\\project2\\map.txt";
    static Map map;
    static HBox hbox = new HBox();
    static VBox vBox = new VBox();
    static Tank tank,onlineTank;
    static Text infoAboutLives;
    DataOutputStream outputStream;
    //this method returns an instance of KeyCode from the string
   public KeyCode getKeyCode(String s){
       KeyCode keyCode = null;
       switch(s){
           case "UP": keyCode = KeyCode.W;break;
           case "DOWN": keyCode = KeyCode.S;break;
           case "RIGHT": keyCode = KeyCode.D;break;
           case "LEFT": keyCode = KeyCode.A;break;
           case "FIRE": keyCode = KeyCode.SPACE;break;
       }return keyCode;

   }
   public static void main(String []args){
       launch(args);
       //path = args[0];
   }
    public void start(Stage primaryStage) throws Exception {
         ServerSocket serverSocket = new ServerSocket(8000);
         File file = new File(path);
         Scanner sc = new Scanner(file);
         map = new Map(sc);
         Game game = new Game(map);
          tank = new Tank();
          onlineTank = new Tank();
         game.addPlayer(tank);
         game.addPlayer(onlineTank);

        MapPane mapPane = new MapPane(map);
        main.getChildren().add(mapPane);
        hbox.getChildren().addAll(main,vBox);
        vBox.setMinWidth(200);
        vBox.setPadding(new Insets(20));
        vBox.setStyle("-fx-background-color: lightgreen;-fx-border-color: black");
        infoAboutLives = new Text("Lives of PINK TANK: " + tank.lives + "\nLives of GRAY TANK: " + onlineTank.lives);
        infoAboutLives.setFont(Font.font("Calibri",FontWeight.BOLD,FontPosture.ITALIC,30));
        vBox.getChildren().add(infoAboutLives);
        Scene scene = new Scene(hbox);
        primaryStage.setTitle("World of Tanks");
        primaryStage.setScene(scene);
        primaryStage.show();

        //Move the client's tank via the input
       new Thread(()-> {
           try {
               Socket client = serverSocket.accept();
               DataInputStream inputStream = new DataInputStream(client.getInputStream());
               DataOutputStream outputStream = new DataOutputStream(client.getOutputStream());
               while(true){
                   String s = inputStream.readUTF();
                   Platform.runLater(()->{
                       if(onlineTank.lives>0) {
                           onlineTank.move(getKeyCode(s));
                           if (getKeyCode(s) == KeyCode.SPACE)
                               onlineTank.fire();
                       }if(tank.lives==0) {
                           HBox hBox = new HBox();
                           hBox.setStyle("-fx-background-color: lightblue;-fx-border-color:black;");
                           Label label = new Label("<-- This player lose:( Please try again! \nThe winner is -->");
                           label.setFont(Font.font("Times New Roman", FontWeight.SEMI_BOLD, FontPosture.REGULAR, 35));
                           label.setAlignment(Pos.CENTER);
                           ImageView imageView = new ImageView(Graphics.ONLINETANK);
                           ImageView imageView1 = new ImageView(Graphics.PLAYER);
                           imageView1.setFitHeight(150);
                           imageView1.setFitWidth(150);
                           imageView.setFitWidth(150);
                           imageView.setFitHeight(150);
                           hBox.getChildren().addAll(imageView1,label,imageView);
                           Stage stage = new Stage();
                           Scene scene1 = new Scene(hBox);
                           stage.setScene(scene1);
                           stage.show();
                       }
                   });
               }
           }
           catch (Exception ignored){
           }

       }).start();

       //handles an action event from the scene to move the main tank
       scene.setOnKeyPressed(event -> {
           if (tank.lives > 0) {
               tank.move(event.getCode());
               if (event.getCode() == KeyCode.SPACE)
                   tank.fire();
           }
            if(onlineTank.lives==0) {
                   try{
                       outputStream.writeUTF("You lose. Don't be upset!");
                   }catch (Exception e){}
               HBox hBox = new HBox();
               hBox.setStyle("-fx-background-color: lightblue;-fx-border-color:black;");
               Label label = new Label("You win!!! Congratulations:)");
               label.setFont(Font.font("Times New Roman",FontWeight.SEMI_BOLD,FontPosture.REGULAR,35));
               label.setAlignment(Pos.CENTER);
               hBox.getChildren().add(label);
               ImageView imageView = new ImageView(Graphics.PLAYER);
               imageView.setFitWidth(150);
               imageView.setFitHeight(150);
               Stage stage = new Stage();
               Scene scene1 = new Scene(hBox,500,300);
               stage.setScene(scene1);
               stage.show();
               }

       });

    }
}
//the class for creating a tank and controlling it
class Tank extends MyPlayer {
    //update the direction of the tank
     String direction = "UP";
     private static int players = 0;
     int lives = 2;
    public Tank() {
        players++;
        if (players == 2) {
           playerChar = '2';
           updateImageView(MapPane.imageView1);
        }
    }
    //method for moving a tank via the KeyCode
    public void move(KeyCode keyCode){
        switch(keyCode){
            case W: direction = "UP";moveUp();break;
            case A: direction = "LEFT";moveLeft();break;
            case S: direction = "DOWN";moveDown();break;
            case D: direction = "RIGHT";moveRight();break;
        }
        //updates the current map view after the tank moves
        MapPane.updateMapPane(getMap());

    }
      //makes a tank to fire the bullet in the current its direction
    public void fire(){
        new Bullet(getMap(),getPosition(),this,playerChar);
    }

}
//custom pane for creating a GridPane with images of the map's elements
class MapPane extends GridPane{
  Map map;
  //for storing a size of map
    int size;
    //for storing a map representation in the matrix
    char[][] matrix;
    //for storing imageViews of the main and client tanks
    static ImageView imageView = new ImageView(Graphics.PLAYER);
    static ImageView imageView1 = new ImageView(Graphics.ONLINETANK);
    public MapPane(Map map){

        this.map = map;
        size = map.mapMatrix.length;
        matrix = map.mapMatrix;
        setStyle("-fx-background-color: BLACK");
        setGridLinesVisible(true);
        //puts an appropriate image of the map element to the GridPane
        for(int i = 0;i<size;i++){
            for(int j = 0;j<size;j++){
                switch(matrix[i][j]){
                    case 'S':add(new ImageView(Graphics.STEEL),j,i);break;
                    case 'W':add(new ImageView(Graphics.WATER),j,i);break;
                    case 'T':add(new ImageView(Graphics.TREE),j,i);break;
                    case 'B':add(new ImageView(Graphics.BRICK),j,i);break;
                    case 'P':add(imageView,j,i);break;
                    case '2':add(imageView1,j,i);break;
                }
            }
        }
    }
    //updates the current map view and put it to the main pane
    public static void updateMapPane(Map map){
        Server.main.getChildren().clear();
        Server.main.getChildren().add(new MapPane(map));

    }
}
//class for storing images of the map elements
class Graphics {
    static Image STEEL = new Image(new File("C:\\Users\\ADMIN\\IdeaProjects\\untitled\\src\\com\\project2\\steel.png").toURI().toString());
    static Image TREE = new Image(new File("C:\\Users\\ADMIN\\IdeaProjects\\untitled\\src\\com\\project2\\tree.png").toURI().toString());
    static Image WATER = new Image(new File("C:\\Users\\ADMIN\\IdeaProjects\\untitled\\src\\com\\project2\\water.png").toURI().toString());
    static Image BRICK = new Image(new File("C:\\Users\\ADMIN\\IdeaProjects\\untitled\\src\\com\\project2\\brick.png").toURI().toString());
    static Image PLAYER = new Image(new File("C:\\Users\\ADMIN\\IdeaProjects\\untitled\\src\\com\\project2\\tank.jpg").toURI().toString());
    static Image ONLINETANK = new Image(new File("C:\\Users\\ADMIN\\IdeaProjects\\untitled\\src\\com\\project2\\onlinePlayer.jpg").toURI().toString());
}
//class for making an animation of the shooting bullet
class Bullet {
    //for storing a map which situates the shooting tank
     Map map;
     //for storing a position of the tank
     Position position ;
     ImageView imageView = new ImageView(Graphics.BRICK);
     //for storing coordinates of the current bullet
     double x,y;
     //for counting number of shots to bricks
     static int shots;
     //for counting number of shots to the tank
     static int shotsToTank;
     //for storing a position of the brick that situates in front of the tank
     Position positionOfBrick = new Position(0,0);
     //for storing a position of the tank that situates in front of the current tank
    Position positionOfTank = new Position(0,0);
    char anotherTank;
     //for storing a shooting tank
     Tank tank;
     //for controlling the duration of the bullet animation depending on the brick's position
     int s = 1;
    public Bullet(Map map, Position position1, Tank tank,char c){
        if(c=='2')
            anotherTank='P';
        else anotherTank='2';
        this.map = map;
        this.tank = tank;
        this.position = new Position(position1.getX(),position1.getY());
        updateCoordinates(position);
        //make the image smaller to appear like a bullet
        imageView.setFitWidth(8);
        imageView.setFitHeight(8);
        //sets the initial position of the bullet
        imageView.setX(x);
        imageView.setY(y);
        Server.main.getChildren().add(imageView);
        Line line = new Line();
        //sets the initial position of the bullet to the line's head coordinates
        line.setStartX(x);
        line.setStartY(y);
        //searches the nearest brick to the tank until it finds
           while(!nextIsBarrier(position)){
           movePosition();
           updateCoordinates(position);
               if(isBrick(position))
                   shots++;
               if(isTank(position)) {
                   shotsToTank++;
               }
           s++;
           }
         //sets the last position of the bullet to the line's tail coordinates
           line.setEndX(x);
           line.setEndY(y);

        PathTransition pathTransition = new PathTransition();
        pathTransition.setNode(imageView);
        pathTransition.setDuration(Duration.millis(40*s));
        pathTransition.setPath(line);
        pathTransition.setCycleCount(1);
        pathTransition.play();
        //make the bullet invisible if the animation is finished
        pathTransition.setOnFinished(event ->{
            //to check whether the brick is broken or not
            updateTank();
            updateBrick();
            imageView.setVisible(false);
        });
    }
    //update the bullet's coordinates depending on the tank's position
    private void updateCoordinates(Position position){

        if (tank.direction.equals("DOWN")) {
            x = position.getY()* Server.size_Of_Block + 15;
            y = (position.getX() + 1)* Server.size_Of_Block;
        }
        if (tank.direction.equals("UP")) {
            x = position.getY()* Server.size_Of_Block + 15;
            y = position.getX()* Server.size_Of_Block;

        }
        if(tank.direction.equals("RIGHT")){
            x = (position.getY()+1)* Server.size_Of_Block -15;
            y = position.getX()* Server.size_Of_Block + 27;
        }
        if(tank.direction.equals("LEFT")){
            x = position.getY()* Server.size_Of_Block -10;
            y = position.getX()* Server.size_Of_Block + 27;
        }

    }
    //moves the bullet's coordinates in the current tank's position
    public void movePosition(){
        if(position.getX()>0 && position.getX()< map.getSize() && position.getY()>0 && position.getY()< map.getSize()) {
            switch (tank.direction) {
                case "UP":
                    position.setX(position.getX() - 1);
                    break;
                case "DOWN":
                    position.setX(position.getX() + 1);
                    break;
                case "RIGHT":
                    position.setY(position.getY() + 1);
                    break;
                case "LEFT":
                    position.setY(position.getY() - 1);
                    break;
            }
        }
    }
    //check whether the next position is steel or brick
    public boolean nextIsBarrier(Position position){
        int x = position.getX();
        int y = position.getY();

        if(x>0 && x<map.getSize()-1 && y>0 && y<map.getSize()-1) {
            switch (tank.direction) {
                case "UP":
                    x--;
                    break;
                case "DOWN":
                    x++;
                    break;
                case "LEFT":
                    y--;
                    break;
                case "RIGHT":
                    y++;
                    break;
            }
        }
        if(x==0 || x== map.getSize()-1 || y==0 || y== map.getSize()-1)
            if(map.getValueAt(x,y)=='0' || map.getValueAt(x,y)=='W' || map.getValueAt(x,y)=='T') {
                movePosition();
                updateCoordinates(position);
                return true;
            }
        return map.getValueAt(x, y) != '0' && map.getValueAt(x, y) != 'W' && map.getValueAt(x, y) != 'T';
    }
    //check whether the next position is brick
    public boolean isBrick(Position position){
        int x = position.getX();
        int y = position.getY();
        if(x>0 && x<map.getSize()-1 && y>0 && y<map.getSize()-1) {
            switch (tank.direction) {
                case "UP":
                    x--;
                    break;
                case "DOWN":
                    x++;
                    break;
                case "LEFT":
                    y--;
                    break;
                case "RIGHT":
                    y++;
                    break;
            }
        }
        if(map.getValueAt(x,y)=='B') {
            positionOfBrick.setX(x);
            positionOfBrick.setY(y);
            return true;
        }
        else return false;
    }
    //checks whether the next element is Tank or not
    public boolean isTank(Position position){
        int x = position.getX();
        int y = position.getY();
        if(x>0 && x<map.getSize()-1 && y>0 && y<map.getSize()-1) {
            switch (tank.direction) {
                case "UP":
                    x--;
                    break;
                case "DOWN":
                    x++;
                    break;
                case "LEFT":
                    y--;
                    break;
                case "RIGHT":
                    y++;
                    break;
            }
        }
        if(map.getValueAt(x,y)==anotherTank) {
            positionOfTank.setX(x);
            positionOfTank.setY(y);
            return true;

        }
        else return false;
    }
    //update the map if one of the players loses
    public void updateTank(){
        if(anotherTank=='P' && shotsToTank==4) {
            Server.tank.lives--;
            shotsToTank=0;
            if(Server.tank.lives==0)
                map.mapMatrix[positionOfTank.getX()][positionOfTank.getY()] = '0';
        }
        if(anotherTank=='2' && shotsToTank==4) {
           Server.onlineTank.lives--;
            shotsToTank=0;
            if(Server.onlineTank.lives==0)
                map.mapMatrix[positionOfTank.getX()][positionOfTank.getY()] = '0';
        }
        Platform.runLater(()->{
            Server.infoAboutLives.setText("Lives of PINK TANK: " + Server.tank.lives + " \nLives of GRAY TANK: " + Server.onlineTank.lives);
        });
        MapPane.updateMapPane(map);
    }
    //make the brick invisible if the number of shots is equal to 4
    public void updateBrick(){
        if(shots ==4){
            shots = 0;
            map.mapMatrix[positionOfBrick.getX()][positionOfBrick.getY()] = '0';
            MapPane.updateMapPane(map);
        }
    }
    }





