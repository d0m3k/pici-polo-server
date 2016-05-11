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

    private void processUser(String[] line) throws Exception{
        /*
        client: "user:<user-name>:<id>"
        server: "created||ok||taken"
        */
        if(line.length != 3)
            throw new Exception("incomplete request");

        String userName = line[1];
        String id = line[2];

        String toClientRequest;
        //if(!userNameIsTaken) toClientRequest = "created";
        //else if(!isUsed) toClientRequest = "ok";
        //else toClientRequest = "taken";

        //send to client

    }

    private void processCreate(String[] line) throws Exception{
        /*
        client: "create:<user-name>:<id>;public/private:<game-name>"
        server: "ok||taken"
        */
        if(line.length != 5)
            throw new Exception("incomplete request");

        String userName = line[1];
        String id = line[2];
        String visibility = line[3];
        String gameName = line[4];

        String toClientRequest;
        //if(!gameNameIsTaken) toClientRequest = "created";
        //else toClientRequest = "taken";

        //send to client

    }

    private void processMove(String[] line) throws Exception{
        /*
        client: "move:<user-name>:<id>:<game-name>:<number>:<card-number>"
        server: "results:<game-name>:<sign>:<diff>:<whos-turn-name>:
            <first-name>:<second-name>:<first-result>:<second-result>"
        */
        if(line.length != 6)
            throw new Exception("incomplete request");

        String userName = line[1];
        String id = line[2];
        String gameName = line[3];
        String number = line[4];
        String cardNumber = line[5];

        String toClientRequest;

        //send to client

    }

    private void processWaiting(String[] line) throws Exception{
        /*
        client: "waiting:<user-name>:<id>:<game-name>"
        server: "idle" ||  "other:<game-name>:<user-name>:<number>:<sign>:<diff>:<whos-turn-name>:
            <first-name>:<second-name>:<first-result>:<second-result>"
        */

        if(line.length != 4)
            throw new Exception("incomplete request");

        String userName = line[1];
        String id = line[2];
        String gameName = line[3];

        String toClientRequest;
        //if(isIdle) toClientRequest = "idle";
        //else

        //send to client


    }

    private void processState(String[] line) throws Exception{
        /*
        client: "state:<user-name>:<id>:<game-name>"
        server: "state:<game-name>:<whos-turn-name>:<first-name>:<second-name>:<first-result>:<second-result>"
        */

        if(line.length != 4)
            throw new Exception("incomplete request");

        String userName = line[1];
        String id = line[2];
        String gameName = line[3];

        String toClientRequest;
        //send to client

    }
}
