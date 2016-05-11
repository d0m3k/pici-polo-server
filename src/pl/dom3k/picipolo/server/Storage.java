package pl.dom3k.picipolo.server;

import java.util.HashMap;

/**
 * Created by Januszek on 2016-05-11.
 */
public class Storage {
    private static final Object gamesMonitor = new Object();
    private static final Object usersMonitor = new Object();
    private static HashMap<String,Game> games = new HashMap<>();
    private static HashMap<String,User> users = new HashMap<>();

    public boolean addUser(String name,String ID){
        User user = null;
        synchronized(usersMonitor){
            user = users.get(name);
            if(user==null){
                users.put(name,new User(name,ID));
            }
        }
        if (user == null) return true;
        return false;
    }

    public boolean checkUser(String name,String ID){
        boolean correctID = false;
        User user = null;
        synchronized (usersMonitor){
            user = users.get(name);
            if (user!=null){
                correctID = user.compareID(ID);
            }
        }
        return correctID;
    }

    public boolean addGame(String name,String userName,boolean priv){
        Game game = null;
        User user = null;
        synchronized (gamesMonitor) {
            synchronized (usersMonitor) {
                game = games.get(name);
                user = users.get(userName);
                if (game != null||user == null) {
                    return false;
                }
                game = games.put(name, new Game(name, user, priv));
                user.addGame(game);
            }
        }
        if (game != null) return true;
        return false;
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
                    users[0].removeGame(game);
                }
                if (users.length>1&&users[1]!=null){
                    users[1].removeGame(game);
                }
                games.remove(name);
                removed = true;
            }
        }
        return removed;
    }

    public Change makeMove(String userName,String gameName,int number,int cardNumber){
        Game game = null;
        Change change = null;
        int playerIndex = -1;
        synchronized(gamesMonitor){
            synchronized (usersMonitor){
                if((game = games.get(gameName))==null)return null;
                if((playerIndex = game.getUserIndex(userName))==-1)return null;
                change = game.makeMove(playerIndex,number,cardNumber);
            }
        }
        return change;
    }
}
