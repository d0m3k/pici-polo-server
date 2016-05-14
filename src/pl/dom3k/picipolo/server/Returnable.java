package pl.dom3k.picipolo.server;

/**
 * Created by Januszek on 2016-05-14.
 */
public interface Returnable {
    String getOutput();
}

class UserCreated implements Returnable {
    @Override
    public String getOutput() {
        return "created";
    }
}

class UserAccepted implements Returnable {
    @Override
    public String getOutput() {
        return "ok";
    }
}

class Taken implements Returnable {
    @Override
    public String getOutput() {
        return "taken";
    }
}

class MoveResults implements Returnable {
    @Override
    public String getOutput() {
        StringBuilder sB = new StringBuilder().append("results:").append(gameName).append(":").append(sign).append(":").append(secondSign).append(":").append(diff).append(":").append(turnName).append(":");
        for (String name:players) sB.append(name).append(":");
        for (long points:results) sB.append(points).append(":");
        return sB.toString();
    }

    private String gameName;

    public MoveResults(String gameName, String sign, String secondSign, long diff, String turnName, String[] players, long[] results) {
        this.gameName = gameName;
        this.sign = sign;
        this.secondSign = secondSign;
        this.diff = diff;
        this.turnName = turnName;
        this.players = players;
        this.results = results;
    }

    private String sign;
    private String secondSign;
    private long diff;
    private String turnName;
    private String[] players;
    private long[] results;
}

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

class Forbidden implements Returnable {
    @Override
    public String getOutput() {
        return "forbidden";
    }
}

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

class State implements Returnable {
    @Override
    public String getOutput() {
        StringBuilder sB = new StringBuilder().append("state:").append(gameName).append(":").append(turnName).append(":");
        for (String name:players) sB.append(name).append(":");
        for (long points:results) sB.append(points).append(":");
        return sB.toString();
    }

    public State(String gameName, String turnName, String[] players, long[] results) {
        this.gameName = gameName;
        this.turnName = turnName;
        this.players = players;
        this.results = results;
    }

    private String gameName;
    private String turnName;
    private String[] players;
    private long[] results;
}

class OtherTurn implements Returnable {
    @Override
    public String getOutput() {
        StringBuilder sB = new StringBuilder().append("other:").append(gameName).append(":").append(userName).append(":").append(number).append(":").append(sign).append(":").append(diff).append(";").append(turnName).append(":");
        for (String name:players) sB.append(name).append(":");
        for (long points:results) sB.append(points).append(":");
        return sB.toString();
    }

    public OtherTurn(String gameName, String userName, int number, String sign, long diff, String turnName, String[] players, long[] results) {
        this.gameName = gameName;
        this.userName = userName;
        this.number = number;
        this.sign = sign;
        this.diff = diff;
        this.turnName = turnName;
        this.players = players;
        this.results = results;
    }

    private String gameName;
    private String userName;
    private int number;
    private String sign;
    private long diff;
    private String turnName;
    private String[] players;
    private long[] results;
}

class Idle implements Returnable {
    @Override
    public String getOutput() {
        return "idle";
    }
}

class GameBeginning implements Returnable {
    @Override
    public String getOutput() {
        return "beginning";
    }
}

class GameFull implements Returnable {
    @Override
    public String getOutput() {
        return "full";
    }
}

class GameNonexistent implements Returnable {
    @Override
    public String getOutput() {
        return "nonexistent";
    }
}

class GameJoined implements Returnable {
    @Override
    public String getOutput() {
        return "ok";
    }
}

class GameRejoined implements Returnable {
    @Override
    public String getOutput() {
        return "already";
    }
}




