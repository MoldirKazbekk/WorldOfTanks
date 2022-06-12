package project2;

import javafx.scene.image.ImageView;

interface Player {
    abstract void moveRight();
    abstract void moveLeft();
    abstract void moveUp();
    abstract void moveDown();
    abstract void setMap(Map map);
    abstract Position getPosition();
}
//simple class for controlling a tank in the map
public class MyPlayer implements Player {
    private Map map;
    private Position position;
    boolean isUnderTree;
    public char playerChar = 'P';
    ImageView imageView = MapPane.imageView;
    @Override
    public void moveRight() {
        imageView.setRotate(90);
        if(position.getY()!= map.getSize()-1 && map.getValueAt(position.getX(),position.getY()+1)=='0') {
            System.out.println("Right");

            map.mapMatrix[position.getX()][position.getY() + 1] = playerChar;
            if(!isUnderTree)
                map.mapMatrix[position.getX()][position.getY()]='0';
            else {
                map.mapMatrix[position.getX()][position.getY()] = 'T';
            isUnderTree = false;
            }
            position.setY(position.getY() + 1);
        }
         else if(getPosition().getY()!= map.getSize()-1 && map.getValueAt(getPosition().getX(),getPosition().getY()+1)=='T') {
            System.out.println("Right");
            map.mapMatrix[position.getX()][position.getY() + 1] = 'T';
            if(!isUnderTree)
            map.mapMatrix[position.getX()][position.getY()]='0';
             else
                map.mapMatrix[position.getX()][position.getY()]='T';
            position.setY(position.getY() + 1);
            isUnderTree = true;
        }else
            System.out.println("Invalid position!");
    }
    @Override
    public void moveLeft() {
        imageView.setRotate(-90);
        if(position.getY()!=0 && map.getValueAt(getPosition().getX(),getPosition().getY()-1)=='0') {
            System.out.println("Left");
            map.mapMatrix[position.getX()][position.getY() - 1] = playerChar;
            if(!isUnderTree)
            map.mapMatrix[position.getX()][position.getY()] = '0';
            else {
                map.mapMatrix[position.getX()][position.getY()] = 'T';
                isUnderTree =false;
            }
            position.setY(position.getY()-1);
        }
       else if(position.getY()!=0 && map.getValueAt(getPosition().getX(),getPosition().getY()-1)=='T') {
            System.out.println("Left");
            map.mapMatrix[position.getX()][position.getY() - 1] = 'T';
            if(!isUnderTree)
            map.mapMatrix[position.getX()][position.getY()] = '0';
            else
                map.mapMatrix[position.getX()][position.getY()] = 'T';
            position.setY(position.getY()-1);
            isUnderTree = true;
        }else
            System.out.println("Invalid position!");
    }
    @Override
    public void moveUp() {
        imageView.setRotate(0);
        if(position.getX()!=0 && map.getValueAt(getPosition().getX()-1, getPosition().getY())=='0') {
            System.out.println("Up");
            map.mapMatrix[position.getX()-1][position.getY()] = playerChar;
            if(!isUnderTree)
            map.mapMatrix[position.getX()][position.getY()]='0';
            else {
                map.mapMatrix[position.getX()][position.getY()]='T';
                isUnderTree = false;
            }
            position.setX(position.getX()-1);
        }
        else if(position.getX()!=0 && map.getValueAt(getPosition().getX()-1, getPosition().getY())=='T'){
            System.out.println("Up");
            map.mapMatrix[position.getX()-1][position.getY()] = 'T';
            if(!isUnderTree)
            map.mapMatrix[position.getX()][position.getY()]='0';
            else
                map.mapMatrix[position.getX()][position.getY()]='T';
            position.setX(position.getX()-1);
            isUnderTree = true;
        }
        else if(position.getX()!=0 && map.getValueAt(getPosition().getX()-1, getPosition().getY())=='L'){
            map.mapMatrix[position.getX()-1][position.getY()] = playerChar;
            map.mapMatrix[position.getX()][position.getY()]='0';

        }

    }
    @Override
    public void moveDown() {
        imageView.setRotate(180);
        if(position.getX()!= map.getSize()-1 && map.getValueAt(getPosition().getX()+1,getPosition().getY())=='0') {
            System.out.println("Down");
            map.mapMatrix[position.getX()+1][position.getY()] = playerChar;
            if(!isUnderTree)
            map.mapMatrix[position.getX()][position.getY()]='0';
            else {
                map.mapMatrix[position.getX()][position.getY()]='T';
                isUnderTree = false;
            }
            position.setX(position.getX()+1);
        }
        else if(position.getX()!= map.getSize()-1 && map.getValueAt(getPosition().getX()+1,getPosition().getY())=='T'){
            System.out.println("Down");
            map.mapMatrix[position.getX()+1][position.getY()] = 'T';
            if(!isUnderTree)
            map.mapMatrix[position.getX()][position.getY()]='0';
            else
                map.mapMatrix[position.getX()][position.getY()]='T';
            position.setX(position.getX()+1);
            isUnderTree = true;
        }else
            System.out.println("Invalid position!");
    }
    //update an image of an created instance
    public void updateImageView(ImageView imageView){ this.imageView = imageView; }

    public void setMap(Map map) {
        this.map =map;
        for(int i = 0; i< map.getSize(); i++){
            for(int j = 0; j< map.getSize(); j++)
                if (map.mapMatrix[i][j] == playerChar) {
                    position = new Position(i, j);
                }
        }
    }
//returns a map
    public Map getMap(){
        return map;
    }
//returns a position of player
    public Position getPosition() {
       return position;
    }
}
class Position{
    private int x;
    private int y;
    public Position(int x,int y){
        this.x = x;
        this.y = y;
    }
    public void setX(int x){
        this.x = x;
    }
    public void setY(int y) {
        this.y = y;
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public boolean equals(Position p){
        if(x==p.getX() && y==p.getY())
            return true;
        else return false;
    }
    public String toString(){
        return "(" + x + ", " + y + ")";
    }
}


