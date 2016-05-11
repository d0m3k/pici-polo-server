package pl.dom3k.picipolo.server;

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
    boolean priv;

    Game(String name,User first,boolean priv){
        this.name = name;
        players = new User[2];
        points = new long[2];
        players[0]=first;
        points[0]=0;
        points[1]=0;
        this.priv=priv;
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
    }


}
