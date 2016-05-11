package pl.dom3k.picipolo.server;

/**
 * Created by Januszek on 2016-05-11.
 */
public class Change {

    public Change(long result, long diff, String sign) {
        this.result = result;
        this.diff = diff;
        this.sign = sign;
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

    private long result;
    private long diff;
    private String sign;

}
