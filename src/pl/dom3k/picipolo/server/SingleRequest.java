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

    private Object monitor;
    private Socket s;
    private PrintWriter pW;
    private BufferedReader bR;
    private boolean flag;

    public SingleRequest(){
        monitor = new Object();
        this.s = null;
        pW = null;
        bR = null;
        flag = false;
    }

    public SingleRequest(Socket s) throws IOException {
        monitor = new Object();
        this.s = s;
        pW = new PrintWriter(s.getOutputStream(), true);
        bR = new BufferedReader(new InputStreamReader(s.getInputStream()));
        flag = true;
    }

    public void setRequest(Socket s) throws IOException {
        this.s = s;
        pW = new PrintWriter(s.getOutputStream(), true);
        bR = new BufferedReader(new InputStreamReader(s.getInputStream()));
        flag = true;
    }

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
            while(flag==false){
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

    private void process(){
        String line = null;
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
        String output = "error";
        try {
            String name = line[0][1];
            String ID = line[0][2];
            if (Storage.addUser(name, ID)) {
                output = "created";
            } else {
                if (Storage.checkUser(name, ID)) {
                    output = "ok";
                } else {
                    output = "taken";
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            pW.println(output);
        }
    }

    private void processCreate(String[][] line){
        String output = "error";
        try {
            String gameName = line[1][1];
            String userName = line[0][1];
            String ID = line[0][2];
            String priv = line[1][0];
            boolean ifPriv = false;
            if (priv.equals("priv")) ifPriv = true;
            if (Storage.checkUser(userName, ID)) {
                if (Storage.addGame(gameName, userName, ifPriv)) {
                    output = "created";
                } else {
                    output = "taken";
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            pW.println(output);
        }
    }

    private void processMove(String[][] line){
        String output = "error";
        try{
            String playerName=line[0][1];
            String ID=line[0][2];
            String gameName=line[0][3];
            Change change = null;
            int number=Integer.parseInt(line[0][4]);
            int signNumber=Integer.parseInt(line[0][5]);
            if (Storage.checkUser(playerName,ID)) change = Storage.makeMove(playerName,gameName,number,signNumber);
            if (change!=null){
                String[] tab = Storage.getResult(gameName,playerName);
                if (tab!=null){
                    output="results:"+gameName+":"+change.getSign()+":"+change.getDiff()+":"+tab[1]+":"+tab[2]+":"+tab[3]+":"+tab[4]+":"+tab[5];
                }
            }else{
                output="forbidden";
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            pW.println(output);
        }
    }

    private void processWaiting(String[][] line){
        String output = "error";
        try{
            String playerName=line[0][1];
            String ID=line[0][2];
            String gameName=line[0][3];
            Change change = null;
            if (Storage.checkUser(playerName,ID)) change = Storage.getLastChange(gameName,playerName);
            if (change!=null){
                String[] tab = Storage.getResult(gameName,playerName);
                if (tab!=null){
                    output="other:"+gameName+":"+change.getPlayerName()+":"+change.getResult()+":"+change.getSign()+":"+change.getDiff()+";"+tab[1]+":"+tab[2]+":"+tab[3]+":"+tab[4]+":"+tab[5];
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            pW.println(output);
        }
    }

    private void processState(String[][] line){
        String output = "error";
        try{
            String playerName=line[0][1];
            String ID=line[0][2];
            String gameName=line[0][3];
            String[] tab=null;
            if (Storage.checkUser(playerName,ID)) tab = Storage.getResult(gameName,playerName);
            if(tab!=null){
                output="state:"+tab[0]+":"+tab[1]+":"+tab[2]+":"+tab[3]+":"+tab[4]+":"+tab[5];
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            pW.println(output);
        }
    }

    private void processJoin(String[][] line){
        String output = "error";
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
            pW.println(output);
        }
    }

    private void processPublic(String[][] line){
        String output = "error";
        try {
            String userName = line[0][1];
            String ID = line[0][2];
            if (Storage.checkUser(userName, ID)) {
                output = Storage.listPublic();
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            pW.println(output);
        }
    }

    private void processGames(String[][] line){
        String output = "error";
        try {
            String userName = line[0][1];
            String ID = line[0][2];
            if (Storage.checkUser(userName, ID)) {
                output = Storage.listGames(userName);
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            pW.println(output);
        }
    }
}
