package torrent;

import torrent.model.FileModel;
import torrent.model.HostModel;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClientDirectory implements Runnable {

    File dir = null;
    int hostNumber;
    Path path = Paths.get("D:\\TORrent_" + hostNumber);

    public ClientDirectory() {

    }

    @Override
    public void run() {
        while (Main.hostModelList.size() == 0) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for (HostModel hostModel : Main.hostModelList) {

            if (hostModel.getHandle() == Main.handle) {
                hostNumber = hostModel.getID();
                Main.ID = hostModel.getID();
                createDirectory();
            }
        }
    }

    public void createDirectory() {
        path = Paths.get("D:\\TORrent_" + hostNumber);
        dir = new File(path.toUri());
        dir.mkdirs();
        Main.directory = dir;
        System.out.println("Katalog klienta..."+dir.getPath());
        System.out.println("HostID = "+ Main.ID);
        System.out.println();
    }

    public void saveFile(String fileName,byte[] fileBytes){
        try {
            Files.write(Paths.get(Main.directory.toPath().toString()+"\\"+fileName),fileBytes);
            System.out.println("Zapis");
        }catch (IOException e){
            e.printStackTrace();
         }
    }

    public List<FileModel> getLocalFileList(){

        List<FileModel> fileModelList=new ArrayList<>();

        File directory= Main.directory;
        for (File f:directory.listFiles()){
            f.length();
            fileModelList.add(
                    new FileModel(f.getName(),
                    getCheckSum(f.getPath()),
                    Integer.toString(Main.ID),
                            f.length())
            );
        }

        return fileModelList;
    }

    public String getCheckSum(String path){

        try {
            byte[] b = Files.readAllBytes(Paths.get(path));
            byte[] hash = MessageDigest.getInstance("MD5").digest(b);
            return DatatypeConverter.printHexBinary(hash);

        }catch (IOException e){
            e.printStackTrace();
        }catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }

         return null;
    }

    public byte[] getFile(String fileName){

        try {
            byte[] fileBytes=Files.readAllBytes(Paths.get(Main.directory.getPath()+"\\"+fileName));
            return fileBytes;
        }catch (IOException e){
        }
            return null;
    }

    public FileModel checkIsFileOnHost(String fileName, String[] hostID) {

        //new ClientTCPRequestSocket().requestList();

        for (Map.Entry<String, List<FileModel>> entry : Main.files.entrySet()) {
            String key = entry.getKey();
            if (key.split("\\+")[0].equals(fileName)) {

                int mach = 0;
                for (String h : hostID) {
                    for (FileModel file : entry.getValue()) {

                        if (file.getClientID().equals(h)) {
                            mach++;
                        }
                        if (mach == hostID.length) {
                            return file;
                        }
                    }
                }
            }
        }
        System.out.println("Nie znaleziono pliku lub hosta");
        return null;
    }

}

