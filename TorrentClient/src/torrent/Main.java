package torrent;

import torrent.model.FileModel;
import torrent.model.HostModel;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {

    public static ArrayList<HostModel> hostModelList = new ArrayList<>();
    public static int TCPport=0;
    public static File directory;
    public static int ID;
    public static int handle;
    public static Map<String,List<FileModel>> files;
    public static InetAddress serwerIPAdress;
    public static int serwerPort=10000;

    public static void main(String[] args){

        try {
            serwerIPAdress=InetAddress.getByName("localhost");
            if(args.length>0){
                serwerIPAdress=InetAddress.getByName(args[0]);
            }
            if(args.length>1){
                serwerPort = Integer.parseInt(args[1]);
            }
        }catch (UnknownHostException e){
            e.printStackTrace();
        }

        System.out.println("list - pobieranie listy plikow");
        System.out.println("push - wysylanie                \"push file.txt 3 2 5 ....\"");
        System.out.println("pull - pobieranie               \"pull file.txt 1 4 3 ....\" ");

        Thread clientTCP = new Thread(new ClientTCPListener());
        clientTCP.start();

        Thread serwerConnection = new Thread(new ClientSerwerUDP());
        serwerConnection.start();

        Thread makeDir = new Thread(new ClientDirectory());
        makeDir.start();

        Thread commandInterpreter = new Thread(new CommandInterpreter());
        commandInterpreter.start();
    }
}