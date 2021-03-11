package torrent;

import torrent.model.HostModel;
import torrent.model.RequestResponseUDP;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

public class SerwerUDP implements Runnable {

    DatagramSocket serverSocket;
    DatagramPacket recivePacket;
    DatagramPacket sendPacket;
    byte[] reciveData = new byte[1024];
    byte[] sendData = new byte[1024];

    ObjectBytesConverter converter = new ObjectBytesConverter();
    RequestResponseUDP requestResponseUDP;

    public SerwerUDP() {
        try {
            serverSocket = new DatagramSocket(Main.port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {

        new Thread(new Updater()).start();

        while (true) {
            try {
                recivePacket = new DatagramPacket(reciveData, reciveData.length);
                serverSocket.receive(recivePacket);

                requestResponseUDP = (RequestResponseUDP) converter.BytesToObject(recivePacket.getData());

                ++Main.hostCounter;

                HostModel host = new HostModel(
                        Main.hostCounter,
                        InetAddress.getByName(recivePacket.getAddress().getHostName()),
                        recivePacket.getPort(),
                        Integer.parseInt(requestResponseUDP.getData().toString().split("\\+")[0]),
                        Integer.parseInt(requestResponseUDP.getData().toString().split("\\+")[1])
                );

                Main.hostList.add(host);

                System.out.println("Polaczono z nowym hostem: " + host.toString());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class Updater implements Runnable {

        ArrayList<HostModel> hostList;

        @Override
        public void run() {

            while (true) {
                hostList = new ArrayList<>(Main.hostList);
                                
                try {
                   Thread.sleep(500);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }

                for (HostModel hostModel : hostList) {

                    try {
                        sendData = converter.ObjectToBytes(new RequestResponseUDP("hostlist", hostList));
                        sendPacket = new DatagramPacket(sendData, sendData.length, hostModel.getIP(), hostModel.getUDPPort());
                        serverSocket.send(sendPacket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
