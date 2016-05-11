package pl.dom3k.picipolo.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Januszek on 2016-05-11.
 */
public class SingleRequest implements Runnable {

    Socket s;
    PrintWriter pW;
    BufferedReader bR;

    public SingleRequest(Socket s) throws IOException {
        this.s = s;
        pW = new PrintWriter(s.getOutputStream(), true);
        bR = new BufferedReader(new InputStreamReader(s.getInputStream()));
    }

    @Override
    public void run() {
        String line = null;
        try {
            while ((line = bR.readLine()) != null) {

            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
