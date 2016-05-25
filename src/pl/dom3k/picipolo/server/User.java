package pl.dom3k.picipolo.server;

import java.util.HashSet;
import java.util.LinkedList;

/**
 * Class for single user.
 * Created by Januszek on 2016-05-10.
 * @author Kacper Jawoszek
 */
public class User {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User player = (User) o;
        return name.equals(player.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    private String name;
    private String id;
    private HashSet<Game> activeGames;

    public User(String name, String id) {
        this.name = name;
        this.id = id;
        activeGames = new HashSet<>();
    }

    /**
     * Compare given id with this user id.
     * @param id id to check.
     * @return if given id is correct.
     * @throws Exception
     */
    public boolean compareID(String id)throws Exception{
        return this.id.equals(id);
    }

    public boolean addGame(Game game)throws Exception{
        return activeGames.add(game);
    }

    public boolean removeGame(Game game)throws Exception{
        return activeGames.remove(game);
    }

    public boolean compareName(String name)throws Exception{
        return this.name.equals(name);
    }

    public String getName() {
        return name;
    }

    public LinkedList<Game> getGames(){
        return new LinkedList<>(activeGames);
    }

    @Override
    public String toString() {
        return name;
    }
}
