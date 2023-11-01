package WizardTD;

import java.util.*;

import processing.core.PApplet;
import processing.core.PImage;

public class Monster extends App{
    PImage type;
    String spritePath;
    final int maxHealth;
    int health;
    float speed;
    float armour;
    int manaGainedOnKill;
    float x_pos;
    float y_pos;
    ArrayList<int[]> pathPoints;
    ArrayList<int[]> permanentPath;
    int[] spawnPoint = new int[2];

    public Monster(PApplet app, int health, float speed, float armour, int manaGainedOnKill, String path, float x_pos, float y_pos, ArrayList<int[]> aiPath){
        this.type = app.loadImage(path);
        this.health = health;
        this.maxHealth = health;
        this.speed = speed;
        this.armour = armour;
        this.manaGainedOnKill = manaGainedOnKill;
        this.x_pos = x_pos;
        this.y_pos = y_pos;
        this.pathPoints = aiPath;
        permanentPath = new ArrayList<int[]>(aiPath);
        this.spawnPoint[0] = parseInt(x_pos);
        this.spawnPoint[1] = parseInt(y_pos);
    }

    public void setPathPoints(ArrayList<int[]> pathPoints){
        this.pathPoints = pathPoints;
    }

    public void drawMonster(PApplet app){
        app.fill(255, 0, 0);
        app.rect(x_pos + 1, y_pos, 30, 3);
        app.fill(0, 255, 0);
        app.rect(x_pos + 1, y_pos, parseFloat(health)/parseFloat(maxHealth)*30, 3);
        app.image(type, x_pos + 6, y_pos + 6);
    }

    public void monsterDeathAnimation(PApplet app, PImage sprite){
        app.image(sprite, x_pos + 6, y_pos + 6);
    }

    public void takeDamage(float amount){
        health -= (amount * armour);
    }

    public int getManaGained(){
        return manaGainedOnKill;
    }

    public void movePath(){
        float[] moveVector = new float[2];
        float x;
        float y;
        if (pathPoints.size() == 0)
        {
            App.mana -= health;
            x_pos = spawnPoint[0];
            y_pos = spawnPoint[1];
            // need to fix delay in spawns
            pathPoints.addAll(permanentPath);
            return;
        }
        x = pathPoints.get(0)[0] - x_pos;
        y = pathPoints.get(0)[1] - y_pos;
        
        //normalize moveVector
        moveVector[0] = (x/sqrt(pow(x, 2)+pow(y,2)));
        moveVector[1] = (y/sqrt(pow(x, 2)+pow(y,2)));
        if (abs(moveVector[0] * speed) >= abs(pathPoints.get(0)[0] - x_pos) && abs(moveVector[1] * speed) >= abs(pathPoints.get(0)[1] - y_pos)){
            x_pos = pathPoints.get(0)[0];
            y_pos = pathPoints.get(0)[1];
            pathPoints.remove(0);
            return;
        }
        x_pos += moveVector[0] * speed;
        y_pos += moveVector[1] * speed;
    }
}