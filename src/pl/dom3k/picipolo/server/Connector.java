package pl.dom3k.picipolo.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Januszek on 2016-05-10.
 */
public class Connector {

    public static void runServerSocket(){
        ServerSocket sS = null;
        try {
            sS = new ServerSocket(5432);
            System.out.println("Server ready.");
            Socket s = null;
            while(true){
                try {
                    s = sS.accept();

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

}
