package torrent.model;

import java.io.Serializable;

public class RequestResponseUDP implements Serializable {

    public RequestResponseUDP(String request, Object data) {
        this.request = request;
        this.data=data;
    }

    String request;
    Object data;

    public String getRequest() {
        return request;
    }

    public Object getData() {
        return data;
    }
}
