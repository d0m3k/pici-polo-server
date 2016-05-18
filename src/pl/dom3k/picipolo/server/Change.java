package pl.dom3k.picipolo.server;

/**
 * Contains information about last move from game.
 * Created by Januszek on 2016-05-11.
 * @author Kacper Jawoszek
 */
public class Change {

    private long result;
    private long diff;
    private int number;
    private String sign;
    private String playerName;
    private String otherSign;

    public Change(long result, long diff, String sign,String playerName,int number,String otherSign) {
        this.result = result;
        this.diff = diff;
        this.sign = sign;
        this.playerName=playerName;
        this.number=number;
        this.otherSign=otherSign;
    }

    public long getResult() {
        return result;
    }

    public long getDiff() {
        return diff;
    }

    public String getSign() {
        return sign;
    }

    public String getOtherSign() {
        return otherSign;
    }

    public int getNumber() {
        return number;
    }

    public String getPlayerName() {
        return playerName;
    }

}
