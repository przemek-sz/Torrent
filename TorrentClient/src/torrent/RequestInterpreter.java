package torrent;

import torrent.model.FileModel;
import torrent.model.RequestResponseTCP;

public class RequestInterpreter {

    ObjectBytesConverter converter = new ObjectBytesConverter();
    RequestResponseTCP request;
    ClientDirectory clientDirectory=new ClientDirectory();

    public Object interpretAndGetResponse(Object request){

        this.request=(RequestResponseTCP)request;
        switch (this.request.getRequest()){
            case "list":
                System.out.println("Wyslanie listy");
                return new ClientDirectory().getLocalFileList();
            /////////////////////////////////////////////////////////////////////////
            case "push":
                System.out.println("Odbieranie pliku");
                FileModel file= (FileModel) converter.BytesToObject(this.request.getData());
                clientDirectory.saveFile(file.getName(),file.getFileBytes());
                return null;
            /////////////////////////////////////////////////////////////////////////
            case "pull":
                System.out.println("Wysylanie danych");
                byte[] fileBytes = clientDirectory.getFile(((FileModel)(converter.BytesToObject(this.request.getData()))).getName());

                int[] sizes=splitSizeArray(fileBytes.length,this.request.getPartsAmount());

                byte[] bytesToSend;
                bytesToSend=splitArray(fileBytes,sizes,this.request.getRequestPartNumber());

                return new RequestResponseTCP(bytesToSend,this.request.getRequestPartNumber());
        }

        return null;
    }

    public byte[] splitArray(byte[] arrayToSplit,int[] sizes,int partToCut){

        byte[] partToSend=new byte[sizes[partToCut-1]];
        int partSize=sizes[partToCut-1];

        int i=0;
        int j=1;

       while (j<partToCut){
           i=i+sizes[j];
           j++;
       }

        int y=0;
        while(i<arrayToSplit.length&&y<partSize){
            partToSend[y]=arrayToSplit[i];
            i++;
            y++;
        }

        return partToSend;
    }

    public int[] splitSizeArray(int size,int numberOfParts){

        int[] partSizes=new int[numberOfParts];
        int assignedElements=0;
        int i=0;

        while (i<numberOfParts){
            partSizes[i]=size/numberOfParts;
            assignedElements=assignedElements+(size/numberOfParts);
            i++;
            if (i+1==numberOfParts){
                i++;
            }
        }
        if(numberOfParts>1){
            i--;
            partSizes[i]=size-assignedElements;
        }
        return partSizes;
    }
}
