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
        User user;
        synchronized(usersMonitor){
            user = users.get(name);
            if(user==null){
                users.put(name,new User(name,ID));
            }
        }
        return user == null;
    }

    public static boolean checkUser(String name,String ID)throws Exception{
        boolean correctID = false;
        User user;
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
        User user;
        String newName;
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
                game = new Game(newName, user, priv);
                games.put(newName, game);
                if (!priv) publicGames.add(game);
                user.addGame(game);
            }
        }
        return newName;
    }

    public static boolean removeGame(String name)throws Exception{
        Game game;
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
            }
        }
        return true;
    }

    public static Change makeMove(String userName,String gameName,int number,int cardNumber)throws Exception{
        Game game;
        Change change;
        int playerIndex;
        synchronized(gamesMonitor){
            synchronized (usersMonitor){
                if((game = games.get(gameName))==null)return null;
                if((playerIndex = game.getUserIndex(userName))==-1)return new Change(-1);
                change = game.makeMove(playerIndex,number,cardNumber);
            }
        }
        return change;
    }

    public static String[] getResult(String gameName,String playerName)throws Exception{
        String[] tab=new String[6];
        boolean flag = false;
        synchronized(gamesMonitor) {
            Game game;
            User user;
            if ((game = games.get(gameName)) != null && (user = users.get(playerName))!=null ) {
                tab = game.setResultInTab(tab,user);
                flag = true;
            }
        }
        if (flag) return tab; else return null;
    }

    public static Change getLastChange(String gameName,String targetName)throws Exception{
        Change change=null;
        synchronized(gamesMonitor){
            Game game;
            if ((game = games.get(gameName)) != null) {
                if ((game.getUserIndex(targetName))!=-1) {
                    change = game.getLastChange();
                }else{
                    change=new Change(1);
                }
            }
        }
        return change;
    }

    public static String addPlayer(String gameName,String userName)throws Exception{
        Game game;
        String out;
        synchronized (gamesMonitor){
            synchronized (usersMonitor){
                if ((game = games.get(gameName)) != null) {
                    out = game.addPlayer(users.get(userName));
                }else{
                    out = "nonexisting";
                }
            }
        }
        return out;
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
        User user;
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
