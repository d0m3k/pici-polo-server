package pl.dom3k.picipolo.server;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

/**
 * Contains structures with games and players, methods interacting with them - all static.
 * Created by Januszek on 2016-05-11.
 * @author Kacper Jawoszek
 */
public class Storage {
    private static final Object gamesMonitor = new Object();
    private static final Object usersMonitor = new Object();
    private static HashMap<String,Game> games = new HashMap<>();
    private static LinkedList<Game> publicGames = new LinkedList<>();
    private static HashMap<String,User> users = new HashMap<>();

    /**
     * Checks if user with given name exists, if not - add him;
     * @param name user name.
     * @param ID user android id.
     * @return Returnable - userCreated or Taken.
     * @throws Exception
     */
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

    /**
     * Checks if given user exists and if his id is correct.
     * @param name user name.
     * @param ID user android id.
     * @return if user with given name and id exists.
     * @throws Exception
     */
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

    /**
     * Add game with given parameters to server.
     * @param name game name. If empty or null, then random name is generated.
     * @param userName name of user who wants to create game.
     * @param priv if created game should be private.
     * @param modes string with modes and theirs parameters. See <a href=https://github.com/kawiory-studio/pici-polo-server/blob/master/src/pl/dom3k/picipolo/server/PICIProcotol>PICIProtocol</a> for details.
     * @return Returnable - GameCreated, Taken or Error.
     * @throws Exception
     */
    public static Returnable addGame(String name,String userName,boolean priv,String modes)throws Exception{
        Game game = null;
        User user;
        String newName;
        synchronized (gamesMonitor) {
            synchronized (usersMonitor) {
                if (name!=null) game = games.get(name);
                user = users.get(userName);
                if (game != null) return new Taken();
                if (user == null) return new Error();
                if (name==null||name.trim().equals("")){
                    newName = getRandomName();
                    if (newName==null) return new Error();
                }else{
                    newName = name;
                }
                game = new Game(newName, user, priv,modes);
                games.put(newName, game);
                if (!priv) publicGames.add(game);
                user.addGame(game);
            }
        }
        return new GameCreated(newName);
    }

    /**
     * Currently unused.
     * @param name name of game to remove.
     * @return Returnable - currently null.
     * @throws Exception
     */
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

    /**
     * Make a move in given game as given player.
     * @param userName name of player.
     * @param gameName name of game.
     * @param number number chosen by player.
     * @param cardNumber card number chosen by player.
     * @return Returnable - Results, Forbidden or Done.
     * @throws Exception
     */
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

    /**
     * Returns state for given game.
     * @param gameName game name.
     * @param playerName name of player who asks about game.
     * @return Returnable - State, Forbidden or Error.
     * @throws Exception
     */
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

    /**
     * Get last move from game.
     * @param gameName name of game.
     * @param askingName name of player who asks about game.
     * @return
     * @throws Exception
     */
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
            int[] tabNum = new int[publicGames.size()];
            int[] tabMax = new int[publicGames.size()];
            int i = 0;
            for(Game game:publicGames){
                tab[i]=game.getName();
                tabNum[i] = game.getPlayersCount();
                tabMax[i++] = game.getMaxPlayers();
            }
            output = new PublicGames(tab,tabNum,tabMax);
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
                    int[] tabNum = new int[publicGames.size()];
                    int[] tabMax = new int[publicGames.size()];
                    String[] currentPlayers = new String[publicGames.size()];
                    int i =0;
                    for (Game game : user.getGames()) {
                        tab[i]=game.toString();
                        tabNum[i] = game.getPlayersCount();
                        tabMax[i] = game.getMaxPlayers();
                        currentPlayers[i++] = game.getCurrentName();
                    }
                    output = new PrivateGames(tab,tabNum,tabMax,currentPlayers);
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
