package pl.dom3k.picipolo.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Januszek on 2016-05-11.
 */
public class SingleRequest extends Thread {

    Socket s;
    PrintWriter pW;
    BufferedReader bR;

    public SingleRequest(){
        this.s = null;
        pW = null;
        bR = null;
    }

    public SingleRequest(Socket s) throws IOException {
        this.s = s;
        pW = new PrintWriter(s.getOutputStream(), true);
        bR = new BufferedReader(new InputStreamReader(s.getInputStream()));
    }

    public void setRequest(Socket s) throws IOException {
        this.s = s;
        pW = new PrintWriter(s.getOutputStream(), true);
        bR = new BufferedReader(new InputStreamReader(s.getInputStream()));
    }

    public SingleRequest resetRequest() throws IOException {
        if (bR!=null) bR.close();
        if (pW!=null) pW.close();
        if (s!=null) s.close();
        return this;
    }

    @Override
    public void run() {
        String line = null;
        try {
            while ((line = bR.readLine()) != null) {
                if (line.startsWith("user")){

                }else if(line.startsWith("create")){

                }else if(line.startsWith("move")){

                }else if(line.startsWith("waiting")){

                }else if(line.startsWith("state")){

                }else{
                    throw new IOException("Wrong user input");

                }
            }
        }catch(IOException e){
            e.printStackTrace();
            try{
                if (bR!=null) bR.close();
                if (pW!=null) pW.close();
                if (s!=null) s.close();
            }catch(IOException f){
                e.printStackTrace();
            }
        }
    }

    private void processUser(String line){

    }

    private void processCreate(String line){

    }

    private void processMove(String line){

    }

    private void processWaiting(String line){

    }

    private void processState(String line){

    }
}
