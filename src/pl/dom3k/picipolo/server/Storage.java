package pl.dom3k.picipolo.server;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

/**
 * Created by Januszek on 2016-05-11.
 */
public class Storage {
    private static final Object gamesMonitor = new Object();
    private static final Object usersMonitor = new Object();
    private static HashMap<String,Game> games = new HashMap<>();
    private static LinkedList<Game> publicGames = new LinkedList<>();
    private static HashMap<String,User> users = new HashMap<>();

    public static boolean addUser(String name,String ID)throws Exception{
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

    public static boolean checkUser(String name,String ID)throws Exception{
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

    public static String addGame(String name,String userName,boolean priv)throws Exception{
        Game game = null;
        User user = null;
        String newName = null;
        synchronized (gamesMonitor) {
            synchronized (usersMonitor) {
                if (name!=null) game = games.get(name);
                user = users.get(userName);
                if (game != null||user == null) {
                    return null;
                }
                if (name==null){
                    newName = getRandomName();
                    if (newName==null) return null;
                }else{
                    newName = name;
                }
                game = new Game(name, user, priv);
                games.put(newName, game);
                if (!priv) publicGames.add(game);
                user.addGame(game);
            }
        }
        return newName;
    }

    public static boolean removeGame(String name)throws Exception{
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

    public static Change makeMove(String userName,String gameName,int number,int cardNumber)throws Exception{
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

    public static String[] getResult(String gameName,String playerName)throws Exception{
        String[] tab=new String[6];
        boolean flag = false;
        synchronized(gamesMonitor) {
            Game game = null;
            if ((game = games.get(gameName)) != null) {
                game.setResultInTab(tab);
                flag = true;
            }
        }
        if (flag) return tab; else return null;
    }

    public static Change getLastChange(String gameName,String targetName)throws Exception{
        Change change=null;
        synchronized(gamesMonitor){
            Game game = null;
            if ((game = games.get(gameName)) != null) {
                change = game.getLastChange();
            }
        }
        return change;
    }

    public static String addPlayer(String gameName,String userName)throws Exception{
        Game game = null;
        synchronized (gamesMonitor){
            synchronized (usersMonitor){
                if ((game = games.get(gameName)) != null) {
                    if (game.addPlayer(users.get(userName))){
                        return "ok";
                    }else{
                        return "full";
                    }
                }else{
                    return "nonexisting";
                }
            }
        }
    }

    public static String listPublic()throws Exception{
        StringBuilder sB = new StringBuilder();
        synchronized (gamesMonitor){
            for (Game g:publicGames){
                sB.append(g.getName());
            }
        }
        return sB.toString();
    }

    public static String listGames(String userName)throws Exception{
        StringBuilder sB = new StringBuilder();
        User user=null;
        synchronized (gamesMonitor){
            synchronized (usersMonitor) {
                if ((user = users.get(userName)) != null)
                    for (Game g : user.getGames()) {
                        sB.append(g.getName());
                    }
            }
        }
        return sB.toString();
    }

    private static String getRandomName()throws Exception{
        boolean flag = true;
        synchronized (gamesMonitor){
            String name=null;
            while(flag) {
                int number = 20 + new Random().nextInt() % 10000;
                name = "Burak" + number;
                if (games.get(name)==null)flag = false;
            }
            return name;
        }
    }
}
