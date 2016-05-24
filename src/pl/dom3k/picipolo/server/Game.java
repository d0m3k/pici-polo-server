package pl.dom3k.picipolo.server;

import java.util.Date;
import java.util.Random;

/**
 * Represents single game state and logic in server memory.
 * Created by Januszek on 2016-05-10.
 * @author Kacper Jawoszek
 */
public class Game {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return name.equals(game.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    String name;
    boolean priv;
    User[] players;
    long[] points;
    Change lastChange=null;
    long startTime = 0;
    long timeLimit = -1;
    boolean timeMode=false;
    int currentPlayer;
    int turnCount = 0;
    int turnLimit = -1;
    boolean turnMode=false;

    Game(String name,User first,boolean priv,String modes)throws Exception{
        this.name = name;
        players = new User[2];
        points = new long[2];
        players[0]=first;
        points[0]=0;
        points[1]=0;
        this.priv=priv;
        currentPlayer = -1;
        parseModes(modes);
    }

    Game(String name,User first,User second,boolean priv,String modes)throws Exception{
        this.name = name;
        players = new User[2];
        points = new long[2];
        players[0]=first;
        players[1]=second;
        points[0]=0;
        points[1]=0;
        this.priv=priv;
        currentPlayer = 0;
        parseModes(modes);
        startTime = new Date().getTime();
    }

    /**
     * Handle given modes and set parameters for game.
     * @param modes string with modes. See <a href=https://github.com/kawiory-studio/pici-polo-server/blob/master/src/pl/dom3k/picipolo/server/PICIProcotol>PICIProtocol</a> for details.
     * @throws Exception if string is in wrong format.
     */
    public void parseModes(String modes)throws Exception{
        String[] modesT = modes.split(",");
        for(String mode: modesT){
            String[] innerMode = mode.split("|");
            if(innerMode.length>0){
                if(innerMode[0].startsWith("turns")){
                    turnMode=true;
                    turnLimit=Integer.parseInt(innerMode[1]);
                }else if(innerMode[0].startsWith("time")){
                    timeMode=true;
                    timeLimit=Integer.parseInt(innerMode[1])*1000;
                }
            }
        }
    }

    /**
     * Return index for user with given index.
     * @param name user name.
     * @return given user index or -1 if there is no such user.
     * @throws Exception
     */
    public int getUserIndex(String name)throws Exception{
        int result = -1;
        for (int i=0;i<players.length;i++){
            if (players[i]!=null&&players[i].compareName(name)) result = i;
        }
        return result;
    }

    /**
     * Make move in game.
     * @param playerIndex index of moving player.
     * @param number number chosen for current move.
     * @param cardNumber card chosen by user (currently fixed 1).
     * @return {@link Returnable} - based on game logic.
     * @throws Exception
     */
    public Returnable makeMove(int playerIndex ,int number,int cardNumber)throws Exception{
        long currTime = new Date().getTime();
        long lastingTime = currTime-startTime;
        if (playerIndex!= currentPlayer) return new Forbidden();
        if ((turnMode&&turnCount>=turnLimit)||(timeMode&&(timeLimit<lastingTime))){
            return fillEndGame();
        }
        Change change;
        int zero = new Random().nextInt()%100;
        int one = new Random().nextInt()%100;
        long old = points[playerIndex];
        String sign;
        String otherSign;
        turnCount++;
        if (cardNumber<1){
            sign = useSign(playerIndex,number,zero,true);
            otherSign = useSign(playerIndex,number,one,false);
        }else{
            sign = useSign(playerIndex,number,one,true);
            otherSign = useSign(playerIndex,number,zero,false);
        }
        change = new Change(points[playerIndex],points[playerIndex]-old,sign,players[playerIndex].getName(),number,otherSign);
        lastChange=change;
        currentPlayer = (currentPlayer +1)%2;

        return fillMoveResults(lastingTime);
    }

    /**
     * Interpret given sign and number.
     * @param playerIndex index of current player.
     * @param number number chosen by player.
     * @param signNumber randomized number for choosing sign.
     * @param flag if current player's points should change based on sign and number.
     * @return string containing used sign.
     * @throws Exception
     */
    private String useSign(int playerIndex,int number, int signNumber, boolean flag)throws Exception{
        String sign;
        if (signNumber<35){
            sign = "+";
            if (flag) points[playerIndex]+=number;
        }else if (signNumber<65){
            sign = "-";
            if (flag) points[playerIndex]-=number;
        }else if (signNumber<80){
            sign = "*";
            if (flag) points[playerIndex]*=number;
        }else if(signNumber<95&&number!=0){
            sign = "/";
            if (flag) points[playerIndex]/=number;
        }else{
            sign = "=";
            if (flag) points[playerIndex]=number;
        }
        return sign;
    }

    /**
     * Return current state of game.
     * @return Returnable - State, GameLonely (if there is still not enough players) or Done.
     * @throws Exception
     */
    public Returnable fillState()throws Exception{
        long currTime = new Date().getTime();
        long lastingTime = currTime-startTime;
        if (currentPlayer<0) return new GameLonely();
        if ((turnMode&&turnCount>=turnLimit)||(timeMode&&(timeLimit<lastingTime))){
            return fillEndGame();
        }
        String[] tabP = new String[players.length];
        long[] tabR = new long[points.length];
        for (int i=0;i<players.length;i++){
            tabP[i]=players[i].getName();
        }
        System.arraycopy(points, 0, tabR, 0, points.length);
        return new State(name,players[currentPlayer].getName(),tabP,tabR,turnCount,turnLimit,lastingTime/1000,timeLimit/1000);
    }

    /**
     * Return last change from the game.
     * @param askingIndex
     * @return Returnable - OtherTurn, Forbidden, GameBeginning, Idle or Done.
     * @throws Exception
     */
    public Returnable getLastChange(int askingIndex)throws Exception{
        long currTime = new Date().getTime();
        long lastingTime = currTime-startTime;
        if (currentPlayer<0) return new Forbidden();
        if (lastChange==null) return new GameBeginning();
        if ((turnMode&&turnCount>=turnLimit)||(timeMode&&(timeLimit<lastingTime))){
            return fillEndGame();
        }
        if (lastChange.getPlayerName().equals(players[askingIndex].getName())) return new Idle();
        String[] tabP = new String[players.length];
        long[] tabR = new long[points.length];
        for (int i=0;i<players.length;i++){
            tabP[i]=players[i].getName();
        }
        System.arraycopy(points, 0, tabR, 0, points.length);
        return new OtherTurn(name,lastChange.getPlayerName(),lastChange.getNumber(),lastChange.getSign(),lastChange.getDiff(),players[currentPlayer].getName(),tabP,tabR,turnCount,turnLimit,lastingTime/1000,timeLimit/1000);
    }

    /**
     * Adds given player to game or inform him, that he is aleady in.
     * @param user player to add into the game.
     * @return GameJoined, GameRejoined or GameFull.
     * @throws Exception
     */
    public Returnable addPlayer(User user)throws Exception{
        if (user.equals(players[0])||user.equals(players[1])) return new GameRejoined();
        if (players[1]==null){
            players[1]=user;
            currentPlayer = 0;
            user.addGame(this);
            lastChange = null;
            startTime = new Date().getTime();
            return new GameJoined();
        }
        return new GameFull();
    }

    /**
     * Returns information about current move.
     * @param lastingTime
     * @return MoveResults or Forbidden.
     */
    private Returnable fillMoveResults(long lastingTime){
        if (lastChange==null)return new Forbidden();
        String[] tabP = new String[players.length];
        long[] tabR = new long[points.length];
        for (int i=0;i<players.length;i++){
            tabP[i]=players[i].getName();
        }
        System.arraycopy(points, 0, tabR, 0, points.length);
        return new MoveResults(name,lastChange.getSign(),lastChange.getOtherSign(),lastChange.getDiff(),players[currentPlayer].getName(),tabP,tabR,turnCount,turnLimit,lastingTime/1000,timeLimit/1000);
    }

    /**
     * Returns information about ended game.
     * @return GameEnded.
     */
    private Returnable fillEndGame(){
        int victorIndex=0;
        for(int i =0;i<points.length;i++) if (points[i]>points[victorIndex]) victorIndex = i;
        String[] tabP = new String[players.length];
        long[] tabR = new long[points.length];
        for (int i=0;i<players.length;i++){
            tabP[i]=players[i].getName();
        }
        System.arraycopy(points, 0, tabR, 0, points.length);
        return new GameEnded(name,players[victorIndex].getName(),points[victorIndex],turnCount, startTime,tabP,tabR);
    }

    /**
     * Gives number of players inside game.
     * @return players count.
     */
    public int getPlayersCount(){
        int out =0;
        for(User user:players) if (user!=null) out++;
        return out;
    }

    /**
     * Gives maximum number of players that game can contain.
     * @return maximum number of players.
     */
    public int getMaxPlayers(){
        return players.length;
    }

    public String getCurrentName(){
        return players[currentPlayer].getName();
    }
}
