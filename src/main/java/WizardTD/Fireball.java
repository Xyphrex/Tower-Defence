package WizardTD;

import processing.core.PApplet;
import processing.core.PImage;

public class Fireball extends App{
    private float x_pos;
    private float y_pos;
    private Monster target;
    private float damage;
    boolean reachedTarged = false;

    public Fireball(float x, float y, Monster target, float damage){
        this.x_pos = x;
        this.y_pos = y;
        this.target = target;
        this.damage = damage;
    }

    public void trackTarget(){
        float[] moveVector = new float[2];
        float x = target.x_pos + 14 - x_pos;
        float y = target.y_pos + 14 - y_pos;
        moveVector[0] = (x/sqrt(pow(x, 2)+pow(y,2)));
        moveVector[1] = (y/sqrt(pow(x, 2)+pow(y,2)));
        if (abs(moveVector[0] * 5) >= abs(target.x_pos + 14 - x_pos) && abs(moveVector[1] * 5) >= abs(target.y_pos + 14 - y_pos)){
            x_pos = target.x_pos + 14;
            y_pos = target.y_pos + 14;
            target.takeDamage(damage);
            reachedTarged = true;
        }
        x_pos += moveVector[0] * 5;
        y_pos += moveVector[1] * 5;
    }

    public float getXPos(){
        return x_pos;
    }

    public float getYPos(){
        return y_pos;
    }

    public void drawFireBall(PApplet app, PImage fireball){
        app.image(fireball, x_pos, y_pos);
    }
}
