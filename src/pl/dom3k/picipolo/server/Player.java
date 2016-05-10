package pl.dom3k.picipolo.server;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Januszek on 2016-05-10.
 */
public class Player {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Player player = (Player) o;

        return name.equals(player.name);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    private String name;
    private String id;
    private List<Game> games;

    public Player(String name, String id) {
        this.name = name;
        this.id = id;
        games = new ArrayList<>();
    }



}
