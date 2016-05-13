package pl.dom3k.picipolo.server;

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

    String name;
    User[] players;
    long[] points;
    Change lastChange;
    int turn;
    boolean priv;

    Game(String name,User first,boolean priv){
        this.name = name;
        players = new User[2];
        points = new long[2];
        lastChange = new Change(-2);
        players[0]=first;
        points[0]=0;
        points[1]=0;
        this.priv=priv;
        turn = -1;
    }

    Game(String name,User first,User second,boolean priv){
        this.name = name;
        players = new User[2];
        points = new long[2];
        lastChange = new Change(-1);
        players[0]=first;
        players[1]=second;
        points[0]=0;
        points[1]=0;
        this.priv=priv;
        turn = 0;
    }

    public int getUserIndex(String name)throws Exception{
        int result = -1;
        for (int i=0;i<players.length;i++){
            if (players[i]!=null&&players[i].compareName(name)) result = i;
        }
        return result;
    }

    public Change makeMove(int playerIndex ,int number,int cardNumber)throws Exception{
        if (playerIndex!=turn) return new Change(1);
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
        turn = (turn+1)%2;
        return change;
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

    public String[] setResultInTab(String[] tab,User player)throws Exception{
        if (getUserIndex(player.getName())==-1) return new String[1];
        tab[0]=name;
        if (turn>=0)tab[1]=players[turn].getName();else tab[1]="";
        tab[2]=players[0].getName();
        if (players[1]!=null)tab[3]=players[1].getName(); else tab[3]="";
        tab[4]=Long.toString(points[0]);
        tab[5]=Long.toString(points[1]);
        return tab;
    }

    public Change getLastChange()throws Exception{
        return lastChange;
    }

    public String addPlayer(User user)throws Exception{
        if (user.equals(players[0])||user.equals(players[1])) return "already";
        if (players[1]==null){
            players[1]=user;
            turn = 0;
            user.addGame(this);
            lastChange = new Change(-1);
            return "ok";
        }
        return "full";
    }
}
