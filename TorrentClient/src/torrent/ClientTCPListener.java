package torrent;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientTCPListener implements Runnable{

    ServerSocket clientSocket;
    Object request;
    Object response=null;

    public ClientTCPListener(){

        clientSocket=getListeningSocket();
        System.out.println("Start...TCP");
    }

    @Override
    public void run() {

        while (true){
            try {
                Socket connectionSocket = clientSocket.accept();
                Thread responseThread = new Thread(new ClientTCPObjectSocket(connectionSocket));
                responseThread.start();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }


    public ServerSocket getListeningSocket() {
        for ( int port = Main.serwerPort+1 ; port <= 20000+Main.serwerPort ; port++ )
        {
            try {
                ServerSocket s = new ServerSocket( port );
                Main.TCPport=port;
                return s;
            } catch (IOException e) {

            }
        }return null;
    }

    class ClientTCPObjectSocket implements Runnable{

        Socket connectionSocket;
        RequestInterpreter interpreter = new RequestInterpreter();

        public ClientTCPObjectSocket(Socket connectionSocket) {
            this.connectionSocket=connectionSocket;
        }

        @Override
        public void run() {
            try {
                ObjectInputStream objectInputStream = new ObjectInputStream(connectionSocket.getInputStream());
                ObjectOutputStream objectOutputStream= new ObjectOutputStream(connectionSocket.getOutputStream());

                request=objectInputStream.readObject();
                response =interpreter.interpretAndGetResponse(request);
                objectOutputStream.writeObject(response);

                objectOutputStream.flush();
                objectOutputStream.close();
                objectInputStream.close();
                connectionSocket.close();

            }catch (IOException e){
                e.printStackTrace();
            }catch (ClassNotFoundException e){
                e.printStackTrace();
            }
        }
    }
}
