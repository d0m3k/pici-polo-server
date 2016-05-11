package pl.dom3k.picipolo.server;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Januszek on 2016-05-10.
 */
public class StoredSocket {

    public Socket getSocket() {
        return s;
    }

    public PrintWriter getPrintWriter() {
        return pW;
    }

    public BufferedReader getBufferedReader() {
        return bR;
    }

    Socket s;
    PrintWriter pW;
    BufferedReader bR;

    public StoredSocket(Socket s, PrintWriter pW, BufferedReader bR) {
        this.s = s;
        this.pW = pW;
        this.bR = bR;
    }


}
