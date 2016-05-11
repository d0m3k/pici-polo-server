package pl.dom3k.picipolo.server;

/**
 * Created by Januszek on 2016-05-10. Welcome!
 */
public class Game {
    User[] players;
    long[] points;

    Game(User first){
        players = new User[2];
        points = new long[2];
        players[0]=first;
        points[0]=0;
        points[1]=0;
    }

    Game(User first,User second){
        players = new User[2];
        points = new long[2];
        players[0]=first;
        players[1]=second;
        points[0]=0;
        points[1]=0;
    }


}
