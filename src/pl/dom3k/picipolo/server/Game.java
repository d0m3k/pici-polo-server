package pl.dom3k.picipolo.server;

import java.util.Date;
import java.util.Random;

/**
 * Created by Januszek on 2016-05-10. Welcome!
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
    User[] players;
    long[] points;
    Change lastChange;
    int currentPlayer;
    long creatingTime = new Date().getTime();
    boolean priv;

    Game(String name,User first,boolean priv){
        this.name = name;
        players = new User[2];
        points = new long[2];
        lastChange = null;
        players[0]=first;
        points[0]=0;
        points[1]=0;
        this.priv=priv;
        currentPlayer = -1;
    }

    Game(String name,User first,User second,boolean priv){
        this.name = name;
        players = new User[2];
        points = new long[2];
        lastChange = null;
        players[0]=first;
        players[1]=second;
        points[0]=0;
        points[1]=0;
        this.priv=priv;
        currentPlayer = 0;
    }

    public int getUserIndex(String name)throws Exception{
        int result = -1;
        for (int i=0;i<players.length;i++){
            if (players[i]!=null&&players[i].compareName(name)) result = i;
        }
        return result;
    }

    public Returnable makeMove(int playerIndex ,int number,int cardNumber)throws Exception{
        if (playerIndex!= currentPlayer) return new Forbidden();
        Change change;
        int zero = new Random().nextInt()%100;
        int one = new Random().nextInt()%100;
        long old = points[playerIndex];
        String sign;
        String otherSign;
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
        return fillMoveResults();
    }

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

    public Returnable fillState()throws Exception{
        if (currentPlayer<0) return new GameLonely();
        String[] tabP = new String[players.length];
        long[] tabR = new long[points.length];
        for (int i=0;i<players.length;i++){
            tabP[i]=players[i].getName();
        }
        System.arraycopy(points, 0, tabR, 0, points.length);
        return new State(name,players[currentPlayer].getName(),tabP,tabR);
    }

    public Returnable getLastChange(int askingIndex)throws Exception{
        if (currentPlayer<0) return new Forbidden();
        if (lastChange==null) return new GameBeginning();
        if (lastChange.getPlayerName().equals(players[askingIndex].getName())) return new Idle();
        String[] tabP = new String[players.length];
        long[] tabR = new long[points.length];
        for (int i=0;i<players.length;i++){
            tabP[i]=players[i].getName();
        }
        System.arraycopy(points, 0, tabR, 0, points.length);
        return new OtherTurn(name,lastChange.getPlayerName(),lastChange.getNumber(),lastChange.getSign(),lastChange.getDiff(),players[currentPlayer].getName(),tabP,tabR);
    }

    public Returnable addPlayer(User user)throws Exception{
        if (user.equals(players[0])||user.equals(players[1])) return new GameRejoined();
        if (players[1]==null){
            players[1]=user;
            currentPlayer = 0;
            user.addGame(this);
            lastChange = null;
            return new GameJoined();
        }
        return new GameFull();
    }

    private Returnable fillMoveResults(){
        if (lastChange==null)return new Forbidden();
        String[] tabP = new String[players.length];
        long[] tabR = new long[points.length];
        for (int i=0;i<players.length;i++){
            tabP[i]=players[i].getName();
        }
        System.arraycopy(points, 0, tabR, 0, points.length);
        return new MoveResults(name,lastChange.getSign(),lastChange.getOtherSign(),lastChange.getDiff(),players[currentPlayer].getName(),tabP,tabR);
    }
}
