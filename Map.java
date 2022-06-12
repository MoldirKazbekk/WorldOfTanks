package project2;

import java.util.Scanner;

public class Map {
    private int size;
     public char[][] mapMatrix;

    public Map(Scanner scanner) throws InvalidMapException {
        size = scanner.nextInt();
        if(size==0)
            throw new InvalidMapException("Map size can not be zero");
        char a;
        mapMatrix= new char[size][size];
        for(int i=0;i<size;i++){
            for(int j=0;j<size;j++){
                a = scanner.next().charAt(0);
                mapMatrix[i][j] =a;
            }
        }
    }public int getSize(){
        return size;
    }
    public char getValueAt(int a,int b){
        return mapMatrix[a][b];
    }
    public void print(){
        for(int i=0;i<size;i++){
            for(int j=0;j<size;j++){
                System.out.print(mapMatrix[i][j] + " ");
            }System.out.println();
        }
    }


}
class InvalidMapException extends Exception{
    public InvalidMapException(){
        super("Not enough map elements");
    }
    public InvalidMapException(String message){
        super(message);
    }
}
