package pl.dom3k.picipolo.server;

/**
 * Contains only main method which starts {@link Connector}.
 * Created by Januszek on 2016-05-10.
 * @author Kacper Jawoszek
 */
public class Server {
    public static void main(String[] args){
        Connector.runServerSocket();
    }
}
