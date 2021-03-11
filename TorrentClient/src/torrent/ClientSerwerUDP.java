package torrent;

import torrent.model.HostModel;
import torrent.model.RequestResponseUDP;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ClientSerwerUDP implements Runnable {

    String message;
    DatagramSocket clientSocket;
    byte[] reciveData = new byte[1024];
    byte[] sendData = new byte[1024];
    DatagramPacket sendPacket;
    DatagramPacket recivePacket = new DatagramPacket(reciveData,reciveData.length);
    InetAddress serwerIPAdress;
    int serwerPort;
    ObjectBytesConverter converter = new ObjectBytesConverter();


   public ClientSerwerUDP(){

       serwerIPAdress=Main.serwerIPAdress;
       serwerPort=Main.serwerPort;

       while(Main.TCPport==0){

       }
       //getConfig();
       Main.handle=1+(int)(Math.random()*100000);
       message = new Integer(Main.TCPport).toString()+"+"+ Main.handle;
        try {
            clientSocket = new DatagramSocket();
            sendData= converter.ObjectToBytes(new RequestResponseUDP("new",message));
            sendPacket = new DatagramPacket(sendData,sendData.length, serwerIPAdress,serwerPort);
            clientSocket.send(sendPacket);
            System.out.println("Start...UDP");
        }catch (SocketException e){
            e.printStackTrace();
        }catch (UnknownHostException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
       try {
           while(true) {

               clientSocket.receive(recivePacket);
               RequestResponseUDP request = (RequestResponseUDP)converter.BytesToObject(recivePacket.getData());

               if(request.getRequest().equals("check")){
                   byte[]messageBytes=converter.ObjectToBytes(new RequestResponseUDP(new String("active+"+ Main.handle), Main.handle));
                   sendPacket = new DatagramPacket(messageBytes,messageBytes.length, serwerIPAdress,recivePacket.getPort());
                   clientSocket.send(sendPacket);
               }else {
                    Main.hostModelList = (ArrayList<HostModel>) request.getData();
               }

           }
       }catch (IOException e){
           e.printStackTrace();
       }
    }

    /*public void getConfig(){

       Path path = Paths.get("clientConfig.txt");

       try {
           File file = new File(path.toUri());
           Scanner sc = new Scanner(file);

           serwerIPAdress=InetAddress.getByName(sc.nextLine().split("=")[1]);
           serwerPort=Integer.parseInt(sc.nextLine().split("=")[1]);
           Main.serwerPort=serwerPort;

       }catch (FileNotFoundException e){
           e.printStackTrace();
       }catch (UnknownHostException e){
           e.printStackTrace();
       }
    }*/
}