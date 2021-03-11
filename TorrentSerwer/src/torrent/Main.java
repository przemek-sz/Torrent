package torrent;

import torrent.model.HostModel;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static List<HostModel> hostList=new ArrayList<>();
    public static List<HostModel> tmpHostList=new ArrayList<>();
    public static int hostCounter=0;
    public static int port=10000;

    public static void main(String[] args) {

        if(args.length>0){
            port=Integer.parseInt(args[0]);
        }


        Thread serwerClientUDP = new Thread(new SerwerUDP());
        serwerClientUDP.start();

        new Thread(new Checker()).start();
    }
}