package pl.dom3k.picipolo.server;

import java.util.HashMap;

/**
 * Created by Januszek on 2016-05-11.
 */
public class Storage {
    private static Object gamesMonitor;
    private static Object usersMonitor;
    private static HashMap<String,Game> games;
    private static HashMap<String,User> users;

    public void setStorage(){
        gamesMonitor = new Object();
        usersMonitor = new Object();
        games = new HashMap<>();
        users = new HashMap<>();
    }

    public boolean addUser(String name,String id){
        User user = null;
        synchronized(usersMonitor){
            user = users.get(name);
            if(user==null){
                users.put(name,new User(name,id));
            }
        }
        if (user == null) return true;
        return false;
    }

    public boolean checkUser(String name,String id){
        boolean correctID = false;
        User user = null;
        synchronized (usersMonitor){
            user = users.get(name);
            if (user!=null){
                correctID = user.compareID(id);
            }
        }
        return correctID;
    }

    public Game addGame(String name,String userName,boolean priv){
        Game game = null;
        User user = null;
        synchronized (gamesMonitor) {
            synchronized (usersMonitor) {
                game = games.get(name);
                user = users.get(userName);
                if (game != null||user != null) {
                    return null;
                }
                games.put(name, new Game(name, user, priv));

            }
        }
        return game;
    }

    public boolean removeGame(String name){
        boolean removed = false;
        Game game = null;
        synchronized(gamesMonitor){
            synchronized (usersMonitor){
                game = games.get(name);
                if (game == null) {
                    return false;
                }
                User[] users = game.players;
                if (users.length>0&&users[0]!=null){

                }
                if (users.length>1&&users[1]!=null){

                }
            }
        }
        return true;
    }
}
