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

    public static Returnable addUser(String name,String ID)throws Exception{
        User user;
        Returnable output;
        synchronized(usersMonitor){
            user = users.get(name);
            if(user==null){
                users.put(name,new User(name,ID));
                output = new UserCreated();
            }else{
                output = new Taken();
            }
        }
        return output;
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

    public static Returnable addGame(String name,String userName,boolean priv)throws Exception{
        Game game = null;
        User user;
        String newName;
        synchronized (gamesMonitor) {
            synchronized (usersMonitor) {
                if (name!=null) game = games.get(name);
                user = users.get(userName);
                if (game != null) return new Taken();
                if (user == null) return new Error();
                if (name==null){
                    newName = getRandomName();
                    if (newName==null) return new Error();
                }else{
                    newName = name;
                }
                game = new Game(newName, user, priv);
                games.put(newName, game);
                if (!priv) publicGames.add(game);
                user.addGame(game);
            }
        }
        return new GameCreated(newName);
    }

    public static Returnable removeGame(String name)throws Exception{
        Game game;
        synchronized(gamesMonitor){
            synchronized (usersMonitor){
                game = games.get(name);
                if (game == null) {
                    return null;
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
        return null;
    }

    public static Returnable makeMove(String userName,String gameName,int number,int cardNumber)throws Exception{
        Game game;
        Returnable output;
        int playerIndex;
        synchronized(gamesMonitor){
            synchronized (usersMonitor){
                if((game = games.get(gameName))==null)return new Forbidden();
                if((playerIndex = game.getUserIndex(userName))==-1)return new Forbidden();
                output = game.makeMove(playerIndex,number,cardNumber);
            }
        }
        return output;
    }

    public static Returnable getResult(String gameName,String playerName)throws Exception{
        Returnable output;
        synchronized(gamesMonitor) {
            Game game;
            if ((game = games.get(gameName)) == null) return new Error();
            if ((game.getUserIndex(playerName))==-1) return new Forbidden();
            output = game.fillState();
        }
        return output;
    }

    public static Returnable getLastChange(String gameName,String askingName)throws Exception{
        Returnable output = new Error();
        synchronized(gamesMonitor){
            Game game;
            if ((game = games.get(gameName)) != null) {
                int askingIndex;
                if ((askingIndex=game.getUserIndex(askingName))!=-1) {
                    output = game.getLastChange(askingIndex);
                }else{
                    output = new Forbidden();
                }
            }
        }
        return output;
    }

    public static Returnable addPlayer(String gameName,String userName)throws Exception{
        Game game;
        Returnable output;
        synchronized (gamesMonitor){
            synchronized (usersMonitor){
                if ((game = games.get(gameName)) == null) return new GameNonexistent();
                output = game.addPlayer(users.get(userName));

            }
        }
        return output;
    }

    public static Returnable listPublic()throws Exception{
        Returnable output;
        synchronized (gamesMonitor){
            String[] tab = new String[publicGames.size()];
            int i = 0;
            for(Game game:publicGames){
                tab[i++]=game.getName();
            }
            output = new PublicGames(tab);
        }
        return output;
    }

    public static Returnable listGames(String userName)throws Exception{
        Returnable output=new Error();
        User user;
        synchronized (gamesMonitor){
            synchronized (usersMonitor) {
                if ((user = users.get(userName)) != null){
                    String[] tab = new String[user.getGames().size()];
                    int i =0;
                    for (Game game : user.getGames()) {
                        tab[i++]=game.toString();
                    }
                    output = new PrivateGames(tab);
                }
            }
        }
        return output;
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
