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

    String name;
    User[] players;
    long[] points;
    short turn;
    boolean priv;

    Game(String name,User first,boolean priv){
        this.name = name;
        players = new User[2];
        points = new long[2];
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
        players[0]=first;
        players[1]=second;
        points[0]=0;
        points[1]=0;
        this.priv=priv;
        turn = 0;
    }

    public int getUserIndex(String name){
        int result = -1;
        for (int i=0;i<players.length;i++){
            if (players[i].compareName(name)) result = i;
        }
        return result;
    }

    public Change makeMove(int playerIndex ,int number,int cardNumber){
        if (playerIndex!=turn) return null;
        Change change = null;
        int zero = new Random().nextInt()%100;
        int one = new Random().nextInt()%100;
        long old = points[playerIndex];
        String sign = null;
        if (number<1){
            sign = useSign(playerIndex,number,zero);
        }else{
            sign = useSign(playerIndex,number,one);
        }
        change = new Change(points[playerIndex],points[playerIndex]-old,sign);
        return change;
    }

    private String useSign(int playerIndex,int number, int signNumber){
        String sign = null;
        if (signNumber<35){
            sign = "+";
            points[playerIndex]+=number;
        }else if (signNumber<65){
            sign = "-";
            points[playerIndex]-=number;
        }else if (signNumber<80){
            sign = "*";
            points[playerIndex]*=number;
        }else if(signNumber<95){
            sign = "/";
            points[playerIndex]/=number;
        }else{
            sign = "=";
            points[playerIndex]=number;
        }
        return sign;
    }
}
