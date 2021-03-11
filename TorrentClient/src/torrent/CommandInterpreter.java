package torrent;

import torrent.model.FileModel;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class CommandInterpreter implements Runnable {

    public CommandInterpreter(){
    }

    @Override
    public void run() {

        Scanner scanner = new Scanner(System.in);
        String command;

        while (true){

            command=scanner.nextLine();


            if(check(command)){
                String request=command.split(" ")[0];

                switch (request) {

                    case "list":
                        Map<String, List<FileModel>> fileMap = new ClientTCPRequestSocket().requestList();
                        Long fileSize = 0L;

                        for (Map.Entry<String, List<FileModel>> entry : fileMap.entrySet()) {
                            System.out.println("Plik: " + entry.getKey().split("\\+")[0] + "  Suma Kontrolna=" + entry.getKey().split("\\+")[1]);
                            System.out.print("  Dostepny na:  ");
                            for (FileModel file : entry.getValue()) {
                                fileSize = file.getFileSize();
                                System.out.print(file.getClientID() + ",");
                            }
                            System.out.print("  File Size=" + fileSize + " B");
                            System.out.println();
                            System.out.println();
                        }
                        break;
                    //////////////////////////////////////////////////////
                    case "push":
                        new ClientTCPRequestSocket().push(command);
                        break;
                    //////////////////////////////////////////////////////
                    case "pull":
                        new ClientTCPRequestSocket().pull(command);
                        break;
                    /////////////////////////////////////////////////////
                    case "":
                        break;
                    ////////////////////////////////////////////////////
                    default:
                        System.out.println("Brak takiego polecenia");
                        break;
                }
            }
        }
    }

    public boolean check(String command){

        String[] commands=command.split(" ");

        boolean t=true;

        if(commands.length==1&&!commands[0].equals("list")){
            t=false;
            return t;
        }
        if(commands.length==2&&!commands[0].equals("list")){
            t=false;
            System.out.println("Nie podano ID hosta");
        }
        for (int i=2;i<commands.length;i++){
            try {
                Integer.parseInt(commands[i]);
            }catch (Exception e){
                t=false;
                System.out.println("Nieprawidlowa nazwa hosta");
            }
        }
        return t;
    }
}
