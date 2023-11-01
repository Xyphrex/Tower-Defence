package WizardTD;

import java.io.*;
import java.util.*;

import processing.core.PApplet;
import processing.core.PImage;


public class Board extends App{
    private String[][] pathList = new String[20][20];
    PImage grass;
    PImage shrub;
    PImage pathStraight;
    PImage pathTurn;
    PImage pathT;
    PImage pathX;
    PImage wizHouse;
    private int wizHouseX = 0;
    private int wizHouseY = 0;




    public Board(String filename, PApplet app, PImage grass, PImage shrub, PImage pathStraight, PImage pathTurn, PImage pathT, PImage pathX, PImage wizHouse){
        this.grass = grass;
        this.shrub = shrub;
        this.pathStraight = pathStraight;
        this.pathTurn = pathTurn;
        this.pathT = pathT;
        this.pathX = pathX;
        this.wizHouse = wizHouse;

        try{
            File fileRead = new File(filename);
            Scanner reader = new Scanner(fileRead);
            int y = 0;
            while (reader.hasNextLine()){ 
                String[] lineArray = reader.nextLine().split("");
                for (int x = 0; x < 20; x++){
                    try{
                        pathList[y][x] = lineArray[x];
                    }
                    catch(IndexOutOfBoundsException e){
                        pathList[y][x] = " ";
                    }
                }
                y++;
            }
            reader.close();
        }
        catch (FileNotFoundException e){
            System.out.println("No Level File Found!");
        }
    }


    public void drawSprite(PApplet app, PImage image, int x, int y){
        app.image(image, x, y);
    }

    public int getWizHouseX(){
        return wizHouseX;
    }

    public int getWizHouseY(){
        return wizHouseY;
    }

    public String[][] getPathList(){
        return pathList;
    }

    // make detect surrounding tiles as currently would say that any path along the edge is a valid spawn
    public ArrayList<int[]> getStartPoints(){
        ArrayList<int[]> startPoints = new ArrayList<int[]>();
        for (int y = 0; y < 20; y++){
            for (int x = 0; x < 20; x++){
                if ((x == 19 || x == 0 || y == 0 || y == 19) && pathList[y][x].equals("X") && getSurroundCount(x, y) == 1){
                    if (x == 19 && pathList[y][18].equals("X")){
                        startPoints.add(new int[] {y, x});
                    }
                    else if (x == 0 && pathList[y][1].equals("X")){
                        startPoints.add(new int[] {y, x});
                    }
                    else if (y == 19 && pathList[18][x].equals("X")){
                        startPoints.add(new int[] {y, x});
                    }
                    else if (y == 0 && pathList[1][x].equals("X")){
                        startPoints.add(new int[] {y, x});
                    }
                        
                }
            }
        }
        return startPoints;
    }

    public int getSurroundCount(int x, int y){
        int surroundCount = 0;
        if (!(y-1 < 0)){
            if (pathList[y-1][x].equals("X")){
                surroundCount++;
            }
        }
        if ((y+1 < 20)){
            if (pathList[y+1][x].equals("X")){
                surroundCount++;
            }
        }
        if (!(x-1 < 0)){
            if (pathList[y][x-1].equals("X")){
                surroundCount++;
            }
        }
        if ((x+1 < 20)){
            if (pathList[y][x+1].equals("X")){
                surroundCount++;
            }
        }
        return surroundCount;
    }

    public int[] pathTileSelector(String[][] pathList, int x, int y){
        int surroundCount = 0;
        boolean tileUp = false;
        boolean tileDown = false;
        boolean tileLeft = false;
        boolean tileRight = false;

        if (!(y-1 < 0)){
            if (pathList[y-1][x].equals("X")){
                surroundCount++;
                tileUp = true;
            }
        }
        if (!(y+1 > 19)){
            if (pathList[y+1][x].equals("X")){
                surroundCount++;
                tileDown = true;
            }
        }
        if (!(x-1 < 0)){
            if (pathList[y][x-1].equals("X")){
                surroundCount++;
                tileLeft = true;
            }
        }
        if (!(x+1 > 19)){
            if (pathList[y][x+1].equals("X")){
                surroundCount++;
                tileRight = true;
            }
        }

        if (surroundCount == 1){
            if (tileUp || tileDown){
                return new int[] {1, 90};
            }
            else{
                return new int[] {1, 0};
            }

        }
        else if (surroundCount == 2){
            if (tileUp && tileDown){
                return new int[] {1, 90};
            }
            else if (tileLeft && tileRight){
                return new int[] {1, 0};
            }
            else if (tileDown && tileRight){
                return new int[] {2, -90};
            }
            else if (tileDown && tileLeft){
                return new int[] {2, 0};
            }
            else if (tileUp && tileLeft){
                return new int[] {2, 90};
            }
            else{
                return new int[] {2, 180};
            } 
        }
            
            
        else if (surroundCount == 3){
            if (tileDown && tileUp && tileRight){
                return new int[] {3, -90};
            }
            else if (tileDown && tileUp && tileLeft){
                return new int[] {3, 90};
            }
            else if (tileLeft && tileRight && tileUp){
                return new int[] {3, -180};
            }
            else{
                return new int[] {3, 0};
            }
            
        }
        else{
            return new int[] {4, 0};
        }
    }

    public void drawBoard(PApplet app){
        for (int y = 0; y < 640; y += 32){
            for (int x = 0; x < 640; x += 32){
                if (pathList[y/32][x/32].equals("X")){
                    if (pathTileSelector(pathList, x/32, y/32)[0] == 1){
                        drawSprite(app, rotateImageByDegrees(pathStraight, pathTileSelector(pathList, x/32, y/32)[1]), x, y+40);
                    }
                    else if (pathTileSelector(pathList, x/32, y/32)[0] == 2){
                        drawSprite(app, rotateImageByDegrees(pathTurn, pathTileSelector(pathList, x/32, y/32)[1]), x, y+40);
                    }
                    else if (pathTileSelector(pathList, x/32, y/32)[0] == 3){
                        drawSprite(app, rotateImageByDegrees(pathT, pathTileSelector(pathList, x/32, y/32)[1]), x, y+40);
                    }
                    else if (pathTileSelector(pathList, x/32, y/32)[0] == 4){
                        drawSprite(app, rotateImageByDegrees(pathX, pathTileSelector(pathList, x/32, y/32)[1]), x, y+40);
                    }
                    
                }
                else if (pathList[y/32][x/32].equals(" ")){
                    drawSprite(app, grass, x, y+40);
                }
                else if (pathList[y/32][x/32].equals("S")){
                    drawSprite(app, shrub, x, y+40);
                }
                else if (pathList[y/32][x/32].equals("W")){
                    wizHouseX = x;
                    wizHouseY = y+40;
                    drawSprite(app, grass, x, y+40);
                }
                
                
            }
        }
    }
}