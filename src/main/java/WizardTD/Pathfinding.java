 package WizardTD;

 import java.util.*;

 public class Pathfinding{
     int[] endPos;
     int maxmoves;
     String[][] simplePath;
     int[][][] shortPath;
     Boolean[][] reached;
     ArrayList<Integer> rowQ;
     ArrayList<Integer> columnQ;
     int next;
     int movecount;
     int left;
     


    public Pathfinding(String[][] pathList){
        String[][] simplePath = new String[20][20];
        for (int y = 0; y < 20; y++){
            for (int x = 0; x < 20; x++){
                if (pathList[y][x].equals("X")){
                    simplePath[y][x] = "X";
                }
                else if (pathList[y][x].equals("W")){
                    simplePath[y][x] = "W";
                    this.endPos = new int [] {y, x};
                }
                else{
                    simplePath[y][x] = " ";
                }
            }
        }
        this.simplePath = simplePath;
    }

    public String[][] getSimplePath(){
        return simplePath;
    }

    public ArrayList<Integer> enqueue(ArrayList<Integer> queue, int value){
        queue.add(value);
        return queue;
    }

    public ArrayList<Integer> dequeue(ArrayList<Integer> queue){
        queue.remove(0);
        return queue;
    }

    public void allFalse(){
        for (int y = 0; y < 20; y++){
            for (int x = 0; x < 20; x++){
                reached[y][x] = false;
            }
        }
    }

    public int BFS(int[] startPos){
        next = 0;
        movecount = 0;
        left = 1;
        maxmoves = 0;
        shortPath = new int[20][20][];
        reached = new Boolean[20][20];
        rowQ = new ArrayList<Integer>();
        columnQ = new ArrayList<Integer>();
        boolean endFound = false;
        allFalse();

        rowQ = enqueue(rowQ, startPos[0]);
        columnQ = enqueue(columnQ, startPos[1]);
        reached[startPos[0]][startPos[1]] = true;
        
        while (rowQ.size() > 0){
            int x = rowQ.get(0);
            int y = columnQ.get(0);
            rowQ = dequeue(rowQ);
            columnQ = dequeue(columnQ);
            if (simplePath[x][y].equals("W")){
                endFound = true;
                break;
            }
            evaluateAdjacent(x, y);
            left -= 1;
            if (left == 0){
                left = next;
                next = 0;
                maxmoves++;
            }
        }
        if (endFound){
            return maxmoves;
        }
        return -1;
    }

    public void evaluateAdjacent(int x, int y){
        for (int i = 0; i < 4; i++){
            int tempX = x;
            int tempY = y;
            if (i == 0){
                if (y - 1 < 0){
                    continue;
                }
                tempY = y - 1;
                
            }
            else if (i == 1){
                if (y + 1 > 19){
                    continue;
                }
                tempY = y + 1;
            }
            else if (i == 2){
                if (x - 1 < 0){
                    continue;
                }
                tempX = x - 1;
            }
            else{
                if (x + 1 > 19){
                    continue;
                }
                tempX = x + 1;
            }
            if (reached[tempY][tempX] || !simplePath[x][y].equals("X")){
                continue;
            }
            rowQ = enqueue(rowQ, tempX);
            columnQ = enqueue(columnQ, tempY);
            reached[tempY][tempX] = true;
            shortPath[tempX][tempY] = new int[] {x, y};
            next++;
        }

        
    }

    public ArrayList<int[]> pathAssembler(int[] startPos){
        int stepCount = BFS(startPos);
        int[] end = endPos;
        ArrayList<int[]> path = new ArrayList<int[]>();
        while (end[0] != startPos[0] || end[1] != startPos[1]){
            path.add(new int[] {(end[0]), (end[1])});
            end = shortPath[end[0]][end[1]];
        }
        path.add(startPos);
        Collections.reverse(path);
        for (int i = 0; i < path.size(); i++){
            int temp = path.get(i)[0];
            path.get(i)[0] = path.get(i)[1];
            path.get(i)[1] = temp;

        }
        for (int i = 0; i < path.size(); i++){
            path.get(i)[0] = (path.get(i)[0] * 32);
            path.get(i)[1] = (path.get(i)[1] * 32) + 40;
        }
        return path;
        }
 }