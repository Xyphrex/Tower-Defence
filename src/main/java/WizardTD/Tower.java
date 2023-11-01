package WizardTD;

import processing.core.PApplet;
import processing.core.PImage;
import java.util.*;

public class Tower extends App{
    protected static int initialTowerCost = Integer.valueOf(String.valueOf(jsonList.get("tower_cost")));
    protected static int initialRange = Integer.valueOf(String.valueOf(jsonList.get("initial_tower_range")));
    protected static float initialFireRate = Float.valueOf(String.valueOf(jsonList.get("initial_tower_firing_speed")));
    protected static float initialDamage = Float.valueOf(String.valueOf(jsonList.get("initial_tower_damage")));
    protected int towerCost;
    protected int range;
    protected float fireRate;
    protected float damage;
    protected int rangeLevel;
    protected int fireRateLevel;
    protected int damageLevel;
    protected int x_pos;
    protected int y_pos;
    protected PImage sprite;
    protected float timer;
    protected boolean shotgun = false;

    public Tower(PImage sprite,int x, int y, int fireRateLevel, int damageLevel, int rangeLevel){
        this.x_pos = x;
        this.y_pos = y;
        this.sprite = sprite;
        this.towerCost = initialTowerCost;
        this.range = initialRange;
        this.fireRate = initialFireRate;
        this.damage = initialDamage;
        this.fireRateLevel = fireRateLevel;
        this.damageLevel = damageLevel;
        this.rangeLevel = rangeLevel;
    }

    public void addTime(float time){
        timer += time;
    }

    public float getFireRate(){
        return 1/((float) (fireRate + (fireRateLevel * 0.5)));
    }

    public void drawTower(PApplet app){
        app.image(sprite, x_pos, y_pos);
        app.textSize(12);
        app.fill(150, 0, 255);
        for (int i = 0; i < rangeLevel; i++){
            app.text("o", x_pos+(i*5), y_pos+6);
        }
        for (int i = 0; i < damageLevel; i++){
            app.text("x", x_pos+(i*5), y_pos+32);
        }
        app.fill(0, 0, 0);
        if (fireRateLevel > 0){
            app.noFill();
            app.strokeWeight(1 * fireRateLevel);
            app.stroke(0, 240, 255);
            app.rect(x_pos+6, y_pos+6, 20, 20);
            app.stroke(0);
        }
        app.strokeWeight(1);
    }

    public void upgradeShotgun(){
        shotgun = true;
    }

    public void upgradeRange(boolean upgrade){
        if (!upgrade){
            return;
        }
        rangeLevel += 1;
        if (mana - (20 + (10 * (rangeLevel - 1))) > 0){
            mana -= (20 + (10 * (rangeLevel - 1)));
        }
        else{
            rangeLevel -= 1;
        }
    }

    public void upgradeDamage(boolean upgrade){
        if (!upgrade){
            return;
        }
        damageLevel += 1;
        if (mana - (20 + (10 * (damageLevel - 1))) > 0){
            mana -= (20 + (10 * (damageLevel - 1)));
        }
        else{
            damageLevel -= 1;
        }
    }

    public void upgradeFireRate(boolean upgrade){
        if (!upgrade){
            return;
        }
        fireRateLevel += 1;
        if (mana - (20 + (10 * (fireRateLevel - 1))) > 0){
            mana -= (20 + (10 * (fireRateLevel - 1)));
        }
        else{
            fireRateLevel -= 1;
        }
    }

    public float getTowerRange(){
        return (range + (rangeLevel * 32));
    }

    public float getTowerTime(){
        return timer;
    }

    public void attackEnemy(ArrayList<Monster> monsters, ArrayList<Fireball> fireballs){
        for (Monster monster : monsters){
            if (sqrt(pow(monster.x_pos - x_pos, 2) + pow(monster.y_pos - y_pos, 2)) < (range + (rangeLevel * 32)) / 2){
                timer = 0f;
                //replace with fireball spawning
                fireballs.add(new Fireball(x_pos + 14, y_pos + 14, monster, ((float) (damage + ((initialDamage * 0.5)*damageLevel)))));
                if (!shotgun){
                    return;
                }
            }
        }
    }

}
