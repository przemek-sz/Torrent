package torrent.model;

import java.io.Serializable;
import java.net.InetAddress;

public class HostModel implements Serializable {


    private int ID;
    private InetAddress IP;
    private int UDPport;
    private int TCPport;
    private int handle;

    public HostModel(int ID,InetAddress IP,int UDPport,int TCPport,int handle){
        this.ID=ID;
        this.IP=IP;
        this.UDPport=UDPport;
        this.TCPport=TCPport;
        this.handle=handle;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public InetAddress getIP() {
        return IP;
    }

    public void setIP(InetAddress IP) {
        this.IP = IP;
    }

    public int getUDPPort() {
        return UDPport;
    }

    public void setUDPPort(int port) {
        this.UDPport = port;
    }

    public int getTCPport() {
        return TCPport;
    }

    public void setTCPport(int TCPport) {
        this.TCPport = TCPport;
    }

    public int getHandle() {
        return handle;
    }

    public void setHandle(int handle) {
        this.handle = handle;
    }

    @Override
    public String toString() {
        return "HostModel{" +
                "ID=" + ID +
                ", IP=" + IP +
                ", UDPport=" + UDPport +
                ", TCPport=" + TCPport +
                ", handle=" + handle +
                '}';
    }
}
