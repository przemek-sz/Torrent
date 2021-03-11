package torrent.model;

import java.io.Serializable;

public class RequestResponseUDP implements Serializable {

    String request;
    Object data;

    public RequestResponseUDP(String request,Object data) {
        this.request = request;
        this.data=data;
    }

    public String getRequest() {
        return request;
    }

    public Object getData() {
        return data;
    }

}
