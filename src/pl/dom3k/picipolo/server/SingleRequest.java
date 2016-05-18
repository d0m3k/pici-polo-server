package pl.dom3k.picipolo.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Custom Thread extending class, being a heart of thread pool system.
 * Created by Januszek on 2016-05-11.
 * @author Kacper Jawoszek
 */
@SuppressWarnings("InfiniteLoopStatement")
public class SingleRequest extends Thread {

    private final Object monitor = new Object();
    private Socket s;
    private PrintWriter pW;
    private BufferedReader bR;
    private boolean flag;

    public SingleRequest(){
        this.s = null;
        pW = null;
        bR = null;
        flag = false;
    }

    public SingleRequest(Socket s) throws IOException {
        this.s = s;
        pW = new PrintWriter(s.getOutputStream(), true);
        bR = new BufferedReader(new InputStreamReader(s.getInputStream()));
        flag = true;
    }

    /**
     * Setting thread for new task.
     * @param s Socket which should be handled.
     * @throws IOException thrown from creating writer and reader.
     */
    public void setRequest(Socket s) throws IOException {
        this.s = s;
        pW = new PrintWriter(s.getOutputStream(), true);
        bR = new BufferedReader(new InputStreamReader(s.getInputStream()));
        flag = true;
    }

    /**
     * Clearing thread - closing streams and socket - so it can be assigned again.
     * @return
     * @throws IOException
     */
    public SingleRequest resetRequest() throws IOException {
        if (bR!=null) bR.close();
        if (pW!=null) pW.close();
        if (s!=null) s.close();
        flag = false;
        return this;
    }

    @Override
    public void run() {
        while(true){
            while(!flag){
                synchronized(monitor){
                    try {
                        monitor.wait(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            process();
        }
    }

    public void wake(){
        synchronized(monitor){
            monitor.notifyAll();
        }
    }

    /**
     * Method containing most of SingleRequest logic - interpreting user input, calling right methods and finally freeing this thread.
     */
    private void process(){
        String line;
        try {
            while ((line = bR.readLine()) != null) {
                String[] sublines = line.split(";");
                String[][] tab = new String[sublines.length][];
                for(int i = 0;i<sublines.length;i++){
                    tab[i]=sublines[i].split(":");
                }
                if (line.startsWith("user")){
                    processUser(tab);
                }else if(line.startsWith("create")){
                    processCreate(tab);
                }else if(line.startsWith("move")){
                    processMove(tab);
                }else if(line.startsWith("waiting")){
                    processWaiting(tab);
                }else if(line.startsWith("state")){
                    processState(tab);
                }else if(line.startsWith("join")){
                    processJoin(tab);
                }else if(line.startsWith("public")){
                    processPublic(tab);
                }else if(line.startsWith("games")){
                    processGames(tab);
                }else{
                    throw new IOException("Wrong user input");
                }
            }
        }catch(IOException e){
            e.printStackTrace();
            pW.println("error");
        }
        Connector.addFreeRequest(this);
        Connector.notifyMonitor();
    }

    private void processUser(String[][] line){
        Returnable output = new Error();
        try {
            String name = line[0][1];
            String ID = line[0][2];
            if (Storage.checkUser(name, ID)){
                output = new UserAccepted();
            }else{
                output = Storage.addUser(name,ID);
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            pW.println(output.getOutput());
        }
    }

    private void processCreate(String[][] line){
        Returnable output = new Error();
        try {
            String gameName = null;
            if (line[1].length>1){
                gameName = line[1][1];
            }
            String userName = line[0][1];
            String ID = line[0][2];
            String priv = line[1][0];
            String modes=null;
            if (line[1].length>2) modes = line[1][2];
            boolean ifPriv = false;
            if (priv.equals("private")) ifPriv = true;
            if (Storage.checkUser(userName, ID)) {
                output = Storage.addGame(gameName, userName, ifPriv,modes);
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            pW.println(output.getOutput());
        }
    }

    private void processMove(String[][] line){
        Returnable output = new Error();
        try{
            String playerName=line[0][1];
            String ID=line[0][2];
            String gameName=line[0][3];
            int number=Integer.parseInt(line[0][4]);
            int signNumber=Integer.parseInt(line[0][5]);
            if (Storage.checkUser(playerName,ID)) output = Storage.makeMove(playerName,gameName,number,signNumber);
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            pW.println(output.getOutput());
        }
    }

    private void processWaiting(String[][] line){
        Returnable output = new Error();
        try{
            String playerName=line[0][1];
            String ID=line[0][2];
            String gameName=line[0][3];
            if (Storage.checkUser(playerName,ID)) output = Storage.getLastChange(gameName,playerName);
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            pW.println(output.getOutput());
        }
    }

    private void processState(String[][] line){
        Returnable output = new Error();
        try{
            String playerName=line[0][1];
            String ID=line[0][2];
            String gameName=line[0][3];
            if (Storage.checkUser(playerName,ID)) output = Storage.getResult(gameName,playerName);
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            pW.println(output.getOutput());
        }
    }

    private void processJoin(String[][] line){
        Returnable output = new Error();
        try {
            String gameName = line[0][3];
            String userName = line[0][1];
            String ID = line[0][2];
            if (Storage.checkUser(userName, ID)) {
                output = Storage.addPlayer(gameName, userName);
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            pW.println(output.getOutput());
        }
    }

    private void processPublic(String[][] line){
        Returnable output = new Error();
        try {
            String userName = line[0][1];
            String ID = line[0][2];
            if (Storage.checkUser(userName, ID)) {
                output = Storage.listPublic();
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            pW.println(output.getOutput());
        }
    }

    private void processGames(String[][] line){
        Returnable output = new Error();
        try {
            String userName = line[0][1];
            String ID = line[0][2];
            if (Storage.checkUser(userName, ID)) {
                output = Storage.listGames(userName);
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            pW.println(output.getOutput());
        }
    }
}
