package torrent.model;

import java.io.Serializable;
import java.util.Arrays;

public class RequestResponseTCP implements Serializable {

    public String request;
    public byte[] data;
    public int partsAmount;
    public int requestPartNumber;
    public int totalSize;

    public RequestResponseTCP(String request){
        this.request=request;
    }

    public RequestResponseTCP(String request, byte[] data) {
        this.request=request;
        this.data=data;
    }

    public RequestResponseTCP(String request, byte[] data, int parts, int requestPartNumber) {
        this.request = request;
        this.data = data;
        this.partsAmount = parts;
        this.requestPartNumber=requestPartNumber;
    }

    public RequestResponseTCP(byte[] data, int parts, int requestPartNumber, int totalSize) {
        this.data = data;
        this.partsAmount = parts;
        this.requestPartNumber=requestPartNumber;
        this.totalSize=totalSize;
    }
    public RequestResponseTCP(byte[] data, int requestPartNumber) {
        this.data = data;
        this.requestPartNumber=requestPartNumber;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getPartsAmount() {
        return partsAmount;
    }

    public int getRequestPartNumber() {
        return requestPartNumber;
    }

    public int getTotalSize() {
        return totalSize;
    }

    @Override
    public String toString() {
        return "RequestResponseTCP{" +
                "request='" + request + '\'' +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
