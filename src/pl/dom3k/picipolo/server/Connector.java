package pl.dom3k.picipolo.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Januszek on 2016-05-10.
 */
public class Connector {

    private static LinkedList<SingleRequest> readyPool;
    private static LinkedList<SingleRequest> activePool;
    private static Thread mainThread=null;
    private static Object monitor;

    public static void runServerSocket(){
        monitor = new Object();
        mainThread = Thread.currentThread();
        readyPool = new LinkedList<>();
        activePool = new LinkedList<>();
        ServerSocket sS = null;
        try {
            sS = new ServerSocket(5432);
            System.out.println("Server ready.");
            Socket s = null;
            while(true){
                try {
                    s = sS.accept();
                    SingleRequest sR = null;
                    while ((sR=getFreeRequest())==null){
                        try {
                            monitor.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    sR.setRequest(s);
                    sR.start();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if (sS!=null) try {
                sS.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static synchronized void addFreeRequest(SingleRequest sR){
        try {
            readyPool.addLast(sR.resetRequest());
        } catch (IOException e) {
            readyPool.addLast(new SingleRequest());
            e.printStackTrace();
        }
    }

    public static synchronized SingleRequest getFreeRequest(){
        return readyPool.pollFirst();
    }

    public static Thread getMainThread(){
        return mainThread;
    }

    public static void notifyMonitor(){
        synchronized (monitor){
            monitor.notifyAll();
        }
    }
}
