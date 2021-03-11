package torrent;

import torrent.model.HostModel;
import torrent.model.RequestResponseUDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class Checker implements Runnable {

    DatagramSocket checkerSocket;
    byte[] sendData = new byte[1024];
    byte[] reciveData = new byte[1024];
    DatagramPacket sendPacket;
    DatagramPacket recivePacket = new DatagramPacket(reciveData, reciveData.length);
    ObjectBytesConverter converter = new ObjectBytesConverter();
    ArrayList<Integer> bufor = new ArrayList<>();
    List<HostModel> sendList = new ArrayList<>(Main.hostList);


    public Checker() {

        try {
            checkerSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        new Thread(new Sender()).start();


        while (true) {
            try {
                checkerSocket.receive(recivePacket);
                RequestResponseUDP responseUDP = (RequestResponseUDP) converter.BytesToObject(recivePacket.getData());
                int handler = Integer.parseInt(responseUDP.getData().toString());
                bufor.add(handler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void compareAndRemove(){

        List<HostModel> hostsToRemove=new ArrayList<>();

        for(HostModel hostToCheck : sendList){
            boolean find=false;
            for (int hostHandle : bufor){
                if(hostToCheck.getHandle()==hostHandle){
                    find=true;
                }
            }
            if(!find){
                hostsToRemove.add(hostToCheck);
            }
        }

        bufor=new ArrayList<>();

        for(HostModel hostTR:hostsToRemove){
            int i=0;
            ArrayList<HostModel> list = new ArrayList<>(Main.hostList);
            for (HostModel host:list){
                if(hostTR.getHandle()==host.getHandle()){
                    System.out.println("Rozlaczono z hostem: "+ Main.hostList.get(i).toString());
                    Main.hostList.remove(i);
                }
                i++;
            }
        }
    }

    class Sender implements Runnable {

        @Override
        public void run() {
            sendRequest();
        }

        public void sendRequest() {

            sendData = converter.ObjectToBytes(new RequestResponseUDP("check", null));

            while (true) {
                sendList = new ArrayList<>();

                for (HostModel host : Main.hostList) {
                    try {
                        sendList.add(host);
                        sendPacket = new DatagramPacket(sendData, sendData.length, host.getIP(), host.getUDPPort());
                        checkerSocket.send(sendPacket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                compareAndRemove();
            }
        }
    }


}
