package WizardTD;

import processing.core.PApplet;
import processing.data.JSONArray;
import processing.data.JSONObject;
import java.util.*;

public class Waves extends App{
    private int waveCount = 0;
    private int curWave = 0;
    private JSONArray wavesArray;
    public ArrayList<int[]> startPoints;
    Pathfinding path;

    public Waves(JSONArray waves, ArrayList<int[]> startPoints, Pathfinding path){
        this.wavesArray = waves;
        waveCount = waves.size();
        this.startPoints = startPoints;
        this.path = path;
    }

    public void incrementWave(){
        curWave++;
    }

    public int getCurWave(){
        return curWave;
    }

    public int getWaveCount(){
        return waveCount;
    }

    public float getPreWavePause(){
        JSONObject individualWave = parseJSONObject(String.valueOf(wavesArray.get(curWave)));
        float preWavePause = parseFloat(String.valueOf(individualWave.get("pre_wave_pause")));
        return preWavePause;
    }

    public int getWaveDuration(){
        JSONObject individualWave = parseJSONObject(String.valueOf(wavesArray.get(curWave)));
        int waveDuration = parseInt(String.valueOf(individualWave.get("duration")));
        return waveDuration;
    }

    public int getPrevWaveDuration(){
        JSONObject individualWave = parseJSONObject(String.valueOf(wavesArray.get(curWave-1)));
        int waveDuration = parseInt(String.valueOf(individualWave.get("duration")));
        return waveDuration;
    }

    public void getMonsters(PApplet app, ArrayList<Monster> monsters){
        Random random = new Random();
        JSONObject individualWave = parseJSONObject(String.valueOf(wavesArray.get(curWave)));
        JSONArray monstersData = individualWave.getJSONArray("monsters");
        String spritePath = "src/main/resources/WizardTD/gremlin.png";

        for (int i = 0; i < monstersData.size(); i++){
            JSONObject individualMonsterData = parseJSONObject(String.valueOf(monstersData.get(i)));
            float quantity = parseFloat(String.valueOf(individualMonsterData.get("quantity")));
            for (int j = 0; j < quantity; j++){
                int health = parseInt(String.valueOf(individualMonsterData.get("hp")));
                String type = String.valueOf(individualMonsterData.get("type"));
                int manaGain = parseInt(String.valueOf(individualMonsterData.get("mana_gained_on_kill")));
                float speed = parseFloat(String.valueOf(individualMonsterData.get("speed")));
                float armour = parseFloat(String.valueOf(individualMonsterData.get("armour")));
                if (type.equals("gremlin")){
                    spritePath = "src/main/resources/WizardTD/gremlin.png";
                }
                else if (type.equals("beetle")){
                    spritePath = "src/main/resources/WizardTD/beetle.png";
                }
                else {
                    spritePath = "src/main/resources/WizardTD/worm.png";
                }
                int randomSpawn = random.nextInt(startPoints.size());
                float spawnOffset = (getWaveDuration()/quantity) * (speed * FPS);
                if (startPoints.get(randomSpawn)[0] == 0){
                    monsters.add(new Monster(app, health, speed, armour, manaGain, spritePath, (startPoints.get(randomSpawn)[1]*32), ((startPoints.get(randomSpawn)[0]*32)+40)-(j+1)*spawnOffset, path.pathAssembler(new int[] {startPoints.get(randomSpawn)[0], startPoints.get(randomSpawn)[1]})));
                }
                else if (startPoints.get(randomSpawn)[0] == 19){
                    monsters.add(new Monster(app, health, speed, armour, manaGain, spritePath, (startPoints.get(randomSpawn)[1]*32), ((startPoints.get(randomSpawn)[0]*32)+40)+(j+1)*spawnOffset, path.pathAssembler(new int[] {startPoints.get(randomSpawn)[0], startPoints.get(randomSpawn)[1]})));
                }
                else if (startPoints.get(randomSpawn)[1] == 0){
                    monsters.add(new Monster(app, health, speed, armour, manaGain, spritePath, (startPoints.get(randomSpawn)[1]*32)-(j+1)*spawnOffset, ((startPoints.get(randomSpawn)[0]*32)+40), path.pathAssembler(new int[] {startPoints.get(randomSpawn)[0], startPoints.get(randomSpawn)[1]})));
                }
                else{
                    monsters.add(new Monster(app, health, speed, armour, manaGain, spritePath, (startPoints.get(randomSpawn)[1]*32)+(j+1)*spawnOffset, ((startPoints.get(randomSpawn)[0]*32)+40), path.pathAssembler(new int[] {startPoints.get(randomSpawn)[0], startPoints.get(randomSpawn)[1]})));
                }
                
            }
        }

        return;
    }

}
