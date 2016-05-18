package pl.dom3k.picipolo.server;

/**
 * Interface for possible outputs(returns) from server.
 * See <a href=https://github.com/kawiory-studio/pici-polo-server/blob/master/src/pl/dom3k/picipolo/server/PICIProcotol>PICIProtocol</a> for details.
 * Created by Januszek on 2016-05-14.
 * @author Kacper Jawoszek
 */
public interface Returnable {
    /***
     * @return string which should be sent to asking user.
     */
    String getOutput();
}
/**
 * Response for successfully created user.
 */
class UserCreated implements Returnable {
    @Override
    public String getOutput() {
        return "created";
    }
}
/**
 * Response for accepting existing user login.
 */
class UserAccepted implements Returnable {
    @Override
    public String getOutput() {
        return "ok";
    }
}

/**
 * Response for refusing user login - name already taken.
 */
class Taken implements Returnable {
    @Override
    public String getOutput() {
        return "taken";
    }
}

/**
 * Response for successful move - containing all information about current state of game.
 */
class MoveResults implements Returnable {
    @Override
    public String getOutput() {
        StringBuilder sB = new StringBuilder().append("results:").append(gameName).append(":").append(sign).append(":").append(secondSign).append(":").append(diff).append(":").append(turnName).append(":");
        for (String name:players) sB.append(name).append(":");
        for (long points:results) sB.append(points).append(":");
        sB.append(currTurn);
        if (leftTurn>-1) sB.append("|").append(leftTurn);
        sB.append(":");
        sB.append(currTime);
        if (leftTime>-1) sB.append("|").append(leftTime);
        sB.append(":");
        return sB.toString();
    }

    private String gameName;

    private String sign;
    private String secondSign;
    private long diff;
    private String turnName;
    private String[] players;
    private long[] results;
    private int currTurn;
    private int leftTurn;
    private long currTime;
    private long leftTime;

    public MoveResults(String gameName, String sign, String secondSign, long diff, String turnName, String[] players, long[] results, int currTurn, int leftTurn, long currTime, long leftTime) {
        this.gameName = gameName;
        this.sign = sign;
        this.secondSign = secondSign;
        this.diff = diff;
        this.turnName = turnName;
        this.players = players;
        this.results = results;
        this.currTurn = currTurn;
        this.leftTurn = leftTurn;
        this.currTime = currTime;
        this.leftTime = leftTime;
    }

}
/**
 * Response for ending of a game - containing results.
 */
class GameEnded implements Returnable{
    @Override
    public String getOutput() {
        StringBuilder sB = new StringBuilder().append("done:").append(gameName).append(":").append(winnerName).append(":").append(winnerResult).append(":").append(turnsNumber).append(":").append(lastingTime).append(":");
        for (String name:players) sB.append(name).append(":");
        for (long points:results) sB.append(points).append(":");
        return sB.toString();
    }

    public GameEnded(String gameName, String winnerName, long winnerResult, int turnsNumber, long lastingTime, String[] players, long[] results) {
        this.gameName = gameName;
        this.winnerName = winnerName;
        this.winnerResult = winnerResult;
        this.turnsNumber = turnsNumber;
        this.lastingTime = lastingTime;
        this.players = players;
        this.results = results;
    }

    private String gameName;
    private String winnerName;
    private long winnerResult;
    private int turnsNumber;
    private long lastingTime;
    private String[] players;
    private long[] results;
}
/**
 * Response for forbidden action - trying to get data without permission.
 */
class Forbidden implements Returnable {
    @Override
    public String getOutput() {
        return "forbidden";
    }
}

/**
 * Response for successfully created game.
 */
class GameCreated implements Returnable {
    @Override
    public String getOutput() {
        return "create:"+gameName+":";
    }

    public GameCreated(String gameName) {
        this.gameName = gameName;
    }

    private String gameName;
}

/**
 * Response with whole state about game.
 */
class State implements Returnable {
    @Override
    public String getOutput() {
        StringBuilder sB = new StringBuilder().append("state:").append(gameName).append(":").append(turnName).append(":");
        for (String name:players) sB.append(name).append(":");
        for (long points:results) sB.append(points).append(":");
        sB.append(currTurn);
        if (leftTurn>-1) sB.append("|").append(leftTurn);
        sB.append(":");
        sB.append(currTime);
        if (leftTime>-1) sB.append("|").append(leftTime);
        sB.append(":");
        return sB.toString();
    }

    private String gameName;
    private String turnName;
    private String[] players;
    private long[] results;
    private int currTurn;
    private int leftTurn;
    private long currTime;
    private long leftTime;

    public State(String gameName, String turnName, String[] players, long[] results, int currTurn, int leftTurn, long currTime, long leftTime) {
        this.gameName = gameName;
        this.turnName = turnName;
        this.players = players;
        this.results = results;
        this.currTurn = currTurn;
        this.leftTurn = leftTurn;
        this.currTime = currTime;
        this.leftTime = leftTime;
    }

}

/**
 * Response containing information about last move of other players.
 */
class OtherTurn implements Returnable {
    @Override
    public String getOutput() {
        StringBuilder sB = new StringBuilder().append("other:").append(gameName).append(":").append(userName).append(":").append(number).append(":").append(sign).append(":").append(diff).append(";").append(turnName).append(":");
        for (String name:players) sB.append(name).append(":");
        for (long points:results) sB.append(points).append(":");
        sB.append(currTurn);
        if (leftTurn>-1) sB.append("|").append(leftTurn);
        sB.append(":");
        sB.append(currTime);
        if (leftTime>-1) sB.append("|").append(leftTime);
        sB.append(":");
        return sB.toString();
    }

    private String gameName;
    private String userName;
    private int number;
    private String sign;
    private long diff;
    private String turnName;
    private String[] players;
    private long[] results;
    private int currTurn;
    private int leftTurn;
    private long currTime;
    private long leftTime;

    public OtherTurn(String gameName, String userName, int number, String sign, long diff, String turnName, String[] players, long[] results, int currTurn, int leftTurn, long currTime, long leftTime) {
        this.gameName = gameName;
        this.userName = userName;
        this.number = number;
        this.sign = sign;
        this.diff = diff;
        this.turnName = turnName;
        this.players = players;
        this.results = results;
        this.currTurn = currTurn;
        this.leftTurn = leftTurn;
        this.currTime = currTime;
        this.leftTime = leftTime;
    }
}
/**
 * Response from game which didn't change since player's last move.
 */
class Idle implements Returnable {
    @Override
    public String getOutput() {
        return "idle";
    }
}
/**
 * Response from game which didn't change since its beginning.
 */
class GameBeginning implements Returnable {
    @Override
    public String getOutput() {
        return "beginning";
    }
}
/**
 * Response from full game.
 */
class GameFull implements Returnable {
    @Override
    public String getOutput() {
        return "full";
    }
}
/**
 * Response for accessing nonexistent game.
 */
class GameNonexistent implements Returnable {
    @Override
    public String getOutput() {
        return "nonexistent";
    }
}
/**
 * Response for joining game for the first time.
 */
class GameJoined implements Returnable {
    @Override
    public String getOutput() {
        return "ok";
    }
}
/**
 * Response for joining game after its beginning.
 */
class GameRejoined implements Returnable {
    @Override
    public String getOutput() {
        return "already";
    }
}
/**
 * Response with list of public games.
 */
class PublicGames implements Returnable {
    @Override
    public String getOutput() {
        StringBuilder sB = new StringBuilder().append("public:");
        for (int i=0;i<gamesNames.length;i++){
            sB.append(gamesNames[i]).append(",").append(playersCount[i]).append("|").append(playersMax[i]).append(":");
        }
        return sB.toString();
    }

    public PublicGames(String[] gamesNames,int[] playersCount,int[] playersMax) {
        this.gamesNames = gamesNames;
        this.playersCount=playersCount;
        this.playersMax=playersMax;
    }

    private String[] gamesNames;
    private int[] playersCount;
    private int[] playersMax;
}
/**
 * Response with list of player's games.
 */
class PrivateGames implements Returnable {
    @Override
    public String getOutput() {
        StringBuilder sB = new StringBuilder().append("private:");
        for (int i=0;i<gamesNames.length;i++){
            sB.append(gamesNames[i]).append(",").append(playersCount[i]).append("|").append(playersMax[i]).append(",").append(currentPlayer[i]).append(":");
        }
        return sB.toString();
    }

    public PrivateGames(String[] gamesNames,int[]playersCount,int[]playersMax,String[] currentPlayer) {
        this.gamesNames = gamesNames;
        this.playersCount=playersCount;
        this.playersMax=playersMax;
        this.currentPlayer=currentPlayer;
    }

    private String[] gamesNames;
    private int[] playersCount;
    private int[] playersMax;
    private String[] currentPlayer;
}
/**
 * Response from game with missing players.
 */
class GameLonely implements Returnable {
    @Override
    public String getOutput() {
        return "lonely";
    }
}
/**
 * Response for multiple kinds of errors.
 */
class Error implements Returnable {
    @Override
    public String getOutput() {
        return "error";
    }
}

