package torrent.model;

import java.io.Serializable;

public class FileModel implements Serializable {

    String name;
    String checksum;
    String clientID;
    byte[] fileBytes=null;
    Long fileSize;

    public FileModel(String name,String checksum,String clientID,Long fileSize){
        this.name=name;
        this.checksum=checksum;
        this.clientID=clientID;
        this.fileSize=fileSize;
    }

    public FileModel(String name,byte[] fileBytes){
        this.name=name;
        this.fileBytes=fileBytes;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public byte[] getFileBytes() {
        return fileBytes;
    }

    public void setFileBytes(byte[] fileBytes) {
        this.fileBytes = fileBytes;
    }

    public Long getFileSize(){
        return fileSize;
    }

    @Override
    public String toString() {
        return "FileModel{" +
                "name='" + name + '\'' +
                ", checksum='" + checksum + '\'' +
                ", clientID='" + clientID + '\'' +
                '}';
    }
}
