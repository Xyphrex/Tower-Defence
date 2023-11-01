package WizardTD;

import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.MouseEvent;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import java.io.*;
import java.util.*;

import jogamp.graph.font.typecast.ot.table.CffTable.Index;

public class App extends PApplet {

    public static final int CELLSIZE = 32;
    public static final int SIDEBAR = 120;
    public static final int TOPBAR = 40;
    public static final int BOARD_WIDTH = 20;
    public Board gameBoard;
    public Pathfinding path;
    public ArrayList<Monster> monsters;
    public ArrayList<Monster> deadMonsters;
    public ArrayList<Tower> towers;
    public ArrayList<Fireball> fireballs;
    public int manaPoolInitialCost;
    public int manaPoolCostIncrease;
    public float manaPoolCapMultiplier;
    public float manaPoolManaGainMultiplier;
    public float manaCapMultiplier;
    public float manaGainMultiplier;
    public int manaPoolUseTimes;
    public Waves waves;
    public int seconds;
    public float towerTimer;
    public int frame;
    public int ongoingFrame;
    public int anotherFrame;
    public static int mana;
    public int maxmana;
    public int waveNumber;
    public int manaGain;
    public boolean gameOver;
    public boolean towerBuilder;
    public boolean winGame;
    public boolean pauseGame;
    public boolean upgradeRange;
    public boolean upgradeSpeed;
    public boolean upgradeDamage;
    public boolean upgradeShotgun;
    public boolean fastForward;
    public String countDown;
    public String[][] mapGrid;
    public static JSONObject jsonList;
    public PImage fireball;

    public static int WIDTH = CELLSIZE*BOARD_WIDTH+SIDEBAR;
    public static int HEIGHT = BOARD_WIDTH*CELLSIZE+TOPBAR;

    public static final int FPS = 60;

    public String configPath;

    public Random random = new Random();
	
	// Feel free to add any additional methods or attributes you want. Please put classes in different files.

    public App() {
        this.configPath = "config.json";
    }

    /**
     * Initialise the setting of the window size.
     */
	@Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    /**
     * Load all resources such as images. Initialise the elements such as the player, enemies and map elements.
     */
	@Override
    public void setup() {
        jsonList = loadJSONObject(configPath);
        frameRate(FPS);
        fireball = loadImage("src/main/resources/WizardTD/fireball.png");
        final PImage pathStraight = loadImage("src/main/resources/WizardTD/path0.png");
        final PImage pathTurn = loadImage("src/main/resources/WizardTD/path1.png");
        final PImage pathT = loadImage("src/main/resources/WizardTD/path2.png");
        final PImage pathX = loadImage("src/main/resources/WizardTD/path3.png");
        final PImage grass = loadImage("src/main/resources/WizardTD/grass.png");
        final PImage shrub = loadImage("src/main/resources/WizardTD/shrub.png");
        final PImage wizHouse = loadImage("src/main/resources/WizardTD/wizard_house.png");
        surface.setTitle("WizardTD");
        manaPoolInitialCost = Integer.valueOf(String.valueOf(jsonList.get("mana_pool_spell_initial_cost")));
        manaPoolCostIncrease = Integer.valueOf(String.valueOf(jsonList.get("mana_pool_spell_cost_increase_per_use")));
        manaPoolCapMultiplier = Float.valueOf(String.valueOf(jsonList.get("mana_pool_spell_cap_multiplier")));
        manaPoolManaGainMultiplier = Float.valueOf(String.valueOf(jsonList.get("mana_pool_spell_mana_gained_multiplier")));
        gameBoard = new Board(String.valueOf(jsonList.get("layout")), this, grass, shrub, pathStraight, pathTurn, pathT, pathX, wizHouse);
        mapGrid = Arrays.copyOf(gameBoard.getPathList(), gameBoard.getPathList().length);
        path = new Pathfinding(gameBoard.getPathList());
        waves = new Waves(jsonList.getJSONArray("waves"), gameBoard.getStartPoints(), path);
        gameOver = false;
        winGame = false;
        manaCapMultiplier = manaPoolCapMultiplier;
        manaGainMultiplier = manaPoolManaGainMultiplier;
        manaPoolUseTimes = 0;
        maxmana = parseInt(String.valueOf(jsonList.get("initial_mana_cap")));
        mana = parseInt(String.valueOf(jsonList.get("initial_mana")));
        manaGain = parseInt(String.valueOf(jsonList.get("initial_mana_gained_per_second")));
        waveNumber = 0;
        monsters = new ArrayList<Monster>();
        deadMonsters = new ArrayList<Monster>();
        towers = new ArrayList<Tower>();
        fireballs = new ArrayList<Fireball>();
        seconds = 0;
        frame = 0;
        ongoingFrame = 0;
        towerTimer = 0;
        anotherFrame = 0;
        countDown = "0";
        towerBuilder = false;
        pauseGame = false;
        fastForward = false;
        upgradeRange = false;
        upgradeSpeed = false;
        upgradeDamage = false;
        upgradeShotgun = false;
    }


    /**
     * Receive key pressed signal from the keyboard.
     */
	@Override
    public void keyPressed(){   
        if (key == 'r' && gameOver){ 
            setup();
        }
        else if (key == 't'){
            towerBuilder = !towerBuilder;
        }
        else if (key == 'p'){
            pauseGame = !pauseGame;
        }
        else if (key == 'm' && mana > manaPoolInitialCost + (manaPoolUseTimes * manaPoolCostIncrease)){
            mana -= manaPoolInitialCost + (manaPoolUseTimes * manaPoolCostIncrease);
            manaPoolUseTimes += 1;
        }
        else if (key == 'f'){
            fastForward = !fastForward;
            if (fastForward){
                frameRate(FPS * 2);
            }
            else{
                frameRate(FPS);
            }
        }
        else if (key == '1'){
            upgradeRange = !upgradeRange;
        }
        else if (key == '2'){
            upgradeSpeed = !upgradeSpeed;
        }
        else if (key == '3'){
            upgradeDamage = !upgradeDamage;
        }
        else if (key == 's'){
            upgradeShotgun = !upgradeShotgun;
        }
    }

	@Override
    public void keyReleased(){

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (towerBuilder && mouseX < 640 && mouseY >= 40){
            if (mapGrid[round((mouseY-40)/32)][round(mouseX/32)].equals(" ")){
                int x_position = round(mouseX/32);
                int y_position = round((mouseY-40)/32);
                if (mana - (Tower.initialTowerCost + (20 * ((upgradeSpeed ? 1 : 0) + (upgradeDamage ? 1 : 0) + (upgradeRange ? 1 : 0)))) > 0){
                    mana -= Tower.initialTowerCost + (20 * ((upgradeSpeed ? 1 : 0) + (upgradeDamage ? 1 : 0) + (upgradeRange ? 1 : 0)));
                    mapGrid[y_position][x_position] = "T";
                    towers.add(new Tower(loadImage("src/main/resources/WizardTD/tower0.png"), x_position*32, (y_position*32)+40, upgradeSpeed ? 1 : 0, upgradeDamage ? 1 : 0, upgradeRange ? 1 : 0));
                    return;
                }
            }
        }
        if (upgradeDamage || upgradeRange || upgradeSpeed){
            for (Tower tower : towers){
                if ((mouseX >= tower.x_pos && mouseX <= tower.x_pos+32) && (mouseY >= tower.y_pos && mouseY <= tower.y_pos+32)){
                    tower.upgradeRange(upgradeRange);
                    tower.upgradeFireRate(upgradeSpeed);
                    tower.upgradeDamage(upgradeDamage);
                }
            }
        }
        if (upgradeShotgun){
            for (Tower tower : towers){
                if ((mouseX >= tower.x_pos && mouseX <= tower.x_pos+32) && (mouseY >= tower.y_pos && mouseY <= tower.y_pos+32)){
                    if ((mana - 1200 > 0) && tower.shotgun == false){
                        tower.upgradeShotgun();
                        mana -= 1200;
                    }
                }
            }
        }
        if (mouseX >= 650 && mouseX <= 690 && mouseY >= 100 && mouseY <= 140){
            fastForward = !fastForward;
            if (fastForward){
                frameRate(FPS * 2);
            }
            else{
                frameRate(FPS);
            }
        }
        else if (mouseX >= 650 && mouseX <= 690 && mouseY >= 150 && mouseY <= 190){
            pauseGame = !pauseGame;
        }
        else if (mouseX >= 650 && mouseX <= 690 && mouseY >= 200 && mouseY <= 240){
            towerBuilder = !towerBuilder;
        }
        else if (mouseX >= 650 && mouseX <= 690 && mouseY >= 250 && mouseY <= 290){
            upgradeRange = !upgradeRange;
        }
        else if (mouseX >= 650 && mouseX <= 690 && mouseY >= 300 && mouseY <= 340){
            upgradeSpeed = !upgradeSpeed;
        }
        else if (mouseX >= 650 && mouseX <= 690 && mouseY >= 350 && mouseY <= 390){
            upgradeDamage = !upgradeDamage;
        }
        else if (mouseX >= 650 && mouseX <= 690 && mouseY >= 400 && mouseY <= 440){
            if (mana > manaPoolInitialCost + (manaPoolUseTimes * manaPoolCostIncrease)){
                mana -= manaPoolInitialCost + (manaPoolUseTimes * manaPoolCostIncrease);
                manaPoolUseTimes += 1;
            }
        }
        else if ((mouseX >= 650 && mouseX <= 690 && mouseY >= 450 && mouseY <= 490)){
            upgradeShotgun = !upgradeShotgun;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        
    }

    /*@Override
    public void mouseDragged(MouseEvent e) {

    }*/

    /**
     * Draw all elements in the game by current frame.
     */
	@Override
    public void draw() {
        if (!gameOver && !pauseGame && !winGame){
            // anotherFrame += 1;
            // towerTimer = (((float) 1/60) * (float) (anotherFrame));
            for (Tower tower : towers){
                tower.addTime((float) 1/60);
            }
            frame += 1;
            if (frame == 60){
                seconds += 1;
                countDown = String.valueOf(parseInt(countDown) - 1);
                frame = 0;
                if (mana < round(maxmana * ((manaGainMultiplier - 1) * manaPoolUseTimes + 1))){
                    mana += round(manaGain * ((manaGainMultiplier - 1) * manaPoolUseTimes + 1));
                }
            }
            if (waves.getCurWave() == 0){
                if (seconds >= waves.getPreWavePause()){
                    seconds = 0;
                    waves.getMonsters(this, monsters);
                    waves.incrementWave();
                    waveNumber += 1;
                    countDown = String.valueOf(parseInt(waves.getPreWavePause() + waves.getPrevWaveDuration()));
                }
            }
            else{
                if (waveNumber == waves.getWaveCount()){
                    countDown = "";
                    if (monsters.size() == 0){
                        winGame = true;
                        towerBuilder = false;
                        
                    }
                }
                else if (seconds >= waves.getPreWavePause() + waves.getPrevWaveDuration()){
                seconds = 0;
                waves.getMonsters(this, monsters);
                waveNumber += 1;
                    if (waves.getCurWave()+1 < waves.getWaveCount()){
                        waves.incrementWave();
                    }
                countDown = String.valueOf(parseInt(waves.getPreWavePause() + waves.getPrevWaveDuration()));
                }
            }
            background(132, 115, 74);
            gameBoard.drawBoard(this);
            
            try{
                for (int i = 0; i < monsters.size(); i++){
                    if (monsters.get(i).health <= 0){
                        if (mana + monsters.get(i).getManaGained() <= round(maxmana * ((manaCapMultiplier - 1) * manaPoolUseTimes + 1))){
                            mana += (monsters.get(i).getManaGained() * ((manaGainMultiplier - 1) * manaPoolUseTimes + 1));
                        }
                        else{
                            mana = round(maxmana * ((manaCapMultiplier - 1) * manaPoolUseTimes + 1));
                        }
                        deadMonsters.add(monsters.get(i));
                        monsters.remove(i);

                    }
                    monsters.get(i).drawMonster(this);
                    monsters.get(i).movePath();
                }
            }
            catch (IndexOutOfBoundsException e){
            }

            if (deadMonsters.size() > 0){
                ongoingFrame += 1;
                monsterDeath();
            }
            for (Tower tower : towers){
                tower.drawTower(this);
                if ((mouseX >= tower.x_pos && mouseX <= tower.x_pos+32) && (mouseY >= tower.y_pos && mouseY <= tower.y_pos+32)){
                    noFill();
                    stroke(255, 255, 0);
                    ellipse(tower.x_pos + 16, tower.y_pos + 16, tower.getTowerRange(), tower.getTowerRange());
                    stroke(0);
                }
                if (tower.getFireRate() <= tower.getTowerTime()){
                    tower.attackEnemy(monsters, fireballs);
                }
            }
            for (int i = 0; i < fireballs.size(); i++){
                fireballs.get(i).drawFireBall(this, loadImage("src/main/resources/WizardTD/fireball.png"));
                fireballs.get(i).trackTarget();
                if (fireballs.get(i).reachedTarged){
                    fireballs.remove(i);
                }

            }
            gameBoard.drawSprite(this, gameBoard.wizHouse, gameBoard.getWizHouseX()-8, gameBoard.getWizHouseY()-8);
            if (towerBuilder){
                drawTower();
            }
            if (mouseX >= 650 && mouseX <= 690 && mouseY >= 200 && mouseY <= 240){
                drawToolTip(true);
            }
            if (mouseX >= 650 && mouseX <= 690 && mouseY >= 400 && mouseY <= 440){
                drawToolTip(false);
            }
            drawGui();
            drawUpgradeCost();
            
            if (mana <= 0){
                mana = 0;
                drawGui();
                gameOver = true;
            }
        }
        else if (pauseGame && !gameOver && !winGame){
            gameBoard.drawBoard(this);
            for (Tower tower : towers){
                tower.drawTower(this);
                if ((mouseX >= tower.x_pos && mouseX <= tower.x_pos+32) && (mouseY >= tower.y_pos && mouseY <= tower.y_pos+32)){
                    noFill();
                    stroke(255, 255, 0);
                    ellipse(tower.x_pos + 16, tower.y_pos + 16, tower.getTowerRange(), tower.getTowerRange());
                    stroke(0);
                }
            }

            for (Monster monster : monsters){
                monster.drawMonster(this);
            }
            for (int i = 0; i < fireballs.size(); i++){
                fireballs.get(i).drawFireBall(this, loadImage("src/main/resources/WizardTD/fireball.png"));
            }
            gameBoard.drawSprite(this, gameBoard.wizHouse, gameBoard.getWizHouseX()-8, gameBoard.getWizHouseY()-8);
            if (towerBuilder){
                drawTower();
            }
            drawGui();
            if (mouseX >= 650 && mouseX <= 690 && mouseY >= 200 && mouseY <= 240){
                drawToolTip(true);
            }
            if (mouseX >= 650 && mouseX <= 690 && mouseY >= 400 && mouseY <= 440){
                drawToolTip(false);
            }
            drawUpgradeCost();
            paused();
        }
        else if (winGame){
            winScreen();
        }
        else{
            gameOver();
        }
    }

    public void drawGui(){
        fill(130, 120, 110);
        noStroke();
        rect(0, 0, 760, 40);
        rect(640, 0, 120, 680);
        fill(255, 255, 255);
        stroke(0, 0, 0);
        rect(400, 10, 330, 20);
        textSize(22);
        fill(0);
        text("MANA:", 320, 29);
        fill(0, 240, 255);
        rect(400, 10, parseFloat(mana)/(maxmana * ((manaCapMultiplier - 1) * manaPoolUseTimes + 1))*330, 20);
        fill(0);
        text("Wave " + waveNumber + " Starts: " + countDown, 10, 29);
        textSize(16);
        text(mana + "/" + round(maxmana * ((manaCapMultiplier - 1) * manaPoolUseTimes + 1)), 638, 26);
        if (fastForward){
            fill(255, 255, 0);
        }  
        else{
            fill(170, 170, 170);
        }
        rect(650, 100, 40, 40);
        fill(0);
        text("FF", 661, 127);
        textSize(10);
        text("2x Game", 700, 117);
        text("Speed", 700, 130);
        textSize(16);
        if (pauseGame){
            fill(255, 255, 0);
        }  
        else{
            fill(170, 170, 170);
        }
        rect(650, 150, 40, 40);
        fill(0);
        text("P", 666, 177);
        textSize(10);
        text("Pause", 700, 167);
        text("Game", 700, 180);
        textSize(16);
        if (towerBuilder){
            fill(255, 255, 0);
        }  
        else{
            fill(170, 170, 170);
        }
        rect(650, 200, 40, 40);
        fill(0);
        text("T", 666, 227);
        textSize(10);
        text("Build", 700, 217);
        text("Tower", 700, 230);
        textSize(16);
        if (upgradeRange){
            fill(255, 255, 0);
        }  
        else{
            fill(170, 170, 170);
        }
        rect(650, 250, 40, 40);
        fill(0);
        text("U1", 660, 277);
        textSize(10);
        text("Upgrade", 700, 267);
        text("Range", 700, 280);
        textSize(16);
        if (upgradeSpeed){
            fill(255, 255, 0);
        }  
        else{
            fill(170, 170, 170);
        }
        rect(650, 300, 40, 40);
        fill(0);
        text("U2", 660, 327);
        textSize(10);
        text("Upgrade", 700, 317);
        text("Speed", 700, 330);
        textSize(16);
        if (upgradeDamage){
            fill(255, 255, 0);
        }  
        else{
            fill(170, 170, 170);
        }
        rect(650, 350, 40, 40);
        fill(0);
        text("U3", 660, 377);
        textSize(10);
        text("Upgrade", 700, 367);
        text("Damage", 700, 380);
        textSize(16);
        if (upgradeShotgun){
            fill(255, 255, 0);
        }  
        else{
            fill(170, 170, 170);
        }
        rect(650, 450, 40, 40);
        fill(0);
        text("S", 666, 477);
        textSize(10);
        text("Upgrade", 700, 467);
        text("Shotgun", 700, 480);
        textSize(16);
        fill(0, 255, 0);
        rect(650, 400, 40, 40);
        fill(0);
        text("M", 664, 427);
        textSize(10);
        text("Mana Pool", 700, 417);
        text("Cost: " + String.valueOf(manaPoolInitialCost + (manaPoolUseTimes * manaPoolCostIncrease)), 700, 430);
    }

    public void drawToolTip(boolean towerbuilder){
        if (towerbuilder){
            fill(255, 255, 255);
            rect(580, 220, 53, 15);
            fill(0);
            textSize(10);
            text("Cost: 100", 583, 231);
        }
        else{
            fill(255, 255, 255);
            rect(580, 420, 53, 15);
            fill(0);
            textSize(10);
            text("Cost: " + String.valueOf(manaPoolInitialCost + (manaPoolUseTimes * manaPoolCostIncrease)), 583, 431);
        }
    }
    

    public void gameOver(){
        fill(255, 0, 0);
        rect(220, 340, 200, 30);
        textSize(16);
        fill(0);
        text("YOU LOST! 'r' to restart", 230, 362);
    }

    public void drawTower(){
        noFill();
        stroke(255, 255, 0);
        ellipse(mouseX, mouseY, 96, 96);
        image(loadImage("src/main/resources/WizardTD/tower0.png"), mouseX - 16, mouseY - 16);
        stroke(0, 0, 0);
    }

    public void paused(){
        fill(255,255, 255);
        rect(310, 340, 85, 30);
        textSize(16);
        fill(0);
        text("PAUSED", 325, 362);
    }

    public void drawUpgradeCost(){

        for (Tower tower : towers){
            if ((mouseX >= tower.x_pos && mouseX <= tower.x_pos+32) && (mouseY >= tower.y_pos && mouseY <= tower.y_pos+32) && (upgradeDamage || upgradeRange || upgradeSpeed)){
                fill(255, 255, 255);
                rect(650, 500, 90, 16);
                fill(0);
                textSize(12);
                text("Upgrade Cost", 655, 512);
                fill(255, 255, 255);
                rect(650, 516, 90, 18*((upgradeSpeed ? 1 : 0) + (upgradeDamage ? 1 : 0) + (upgradeRange ? 1 : 0)));
                fill(0);
                int downCount = 0;
                int total = 0;
                if(upgradeRange){
                    text("Range: " + (20 + (10 * (tower.rangeLevel))), 655, 530 + (downCount * 18));
                    downCount += 1;
                    total += (20 + (10 * (tower.rangeLevel)));
                }
                if(upgradeSpeed){
                    text("Speed: " + (20 + (10 * (tower.fireRateLevel))), 655, 530 + (downCount * 18));
                    downCount += 1;
                    total += (20 + (10 * (tower.fireRateLevel)));
                }
                if(upgradeDamage){
                    text("Damage: " + (20 + (10 * (tower.damageLevel))), 655, 530 + (downCount * 18));
                    downCount += 1;
                    total += (20 + (10 * (tower.damageLevel)));
                }
                fill(255, 255, 255);
                rect(650, 516 + (downCount * 18), 90, 16);
                fill(0);
                text("Total: " + total, 655, 530 + (downCount * 18));
                noFill();            
            }
        }
    }

    public void winScreen(){
        fill(0,255, 0);
        rect(310, 340, 110, 30);
        textSize(16);
        fill(0);
        text("YOU WIN!!!", 325, 362);
    }


    public void monsterDeath(){
        for (int i = 0; i < deadMonsters.size(); i++){
            if (ongoingFrame % 20 <= 3){
                deadMonsters.get(i).monsterDeathAnimation(this, loadImage("src/main/resources/WizardTD/gremlin1.png"));
            }
            else if (ongoingFrame % 20 <= 7 && ongoingFrame % 16 < 3){
                deadMonsters.get(i).monsterDeathAnimation(this, loadImage("src/main/resources/WizardTD/gremlin2.png"));
            }
            else if (ongoingFrame % 20 <= 11 && ongoingFrame % 16 < 7){
                deadMonsters.get(i).monsterDeathAnimation(this, loadImage("src/main/resources/WizardTD/gremlin3.png"));
            }
            else if (ongoingFrame % 20 <= 15 && ongoingFrame % 16 < 11){
                deadMonsters.get(i).monsterDeathAnimation(this, loadImage("src/main/resources/WizardTD/gremlin4.png"));
            }
            else if (ongoingFrame % 20 <= 19 && ongoingFrame % 16 < 15){
                deadMonsters.remove(i);
                ongoingFrame = 0;
            }
        }
    }


    public static void main(String[] args) {
        PApplet.main("WizardTD.App");
    }

    /**
     * Source: https://stackoverflow.com/questions/37758061/rotate-a-buffered-image-in-java
     * @param pimg The image to be rotated
     * @param angle between 0 and 360 degrees
     * @return the new rotated image
     */
    public PImage rotateImageByDegrees(PImage pimg, double angle) {
        BufferedImage img = (BufferedImage) pimg.getNative();
        double rads = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
        int w = img.getWidth();
        int h = img.getHeight();
        int newWidth = (int) Math.floor(w * cos + h * sin);
        int newHeight = (int) Math.floor(h * cos + w * sin);

        PImage result = this.createImage(newWidth, newHeight, ARGB);
        //BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        BufferedImage rotated = (BufferedImage) result.getNative();
        Graphics2D g2d = rotated.createGraphics();
        AffineTransform at = new AffineTransform();
        at.translate((newWidth - w) / 2, (newHeight - h) / 2);

        int x = w / 2;
        int y = h / 2;

        at.rotate(rads, x, y);
        g2d.setTransform(at);
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();
        for (int i = 0; i < newWidth; i++) {
            for (int j = 0; j < newHeight; j++) {
                result.set(i, j, rotated.getRGB(i, j));
            }
        }

        return result;
    }
}