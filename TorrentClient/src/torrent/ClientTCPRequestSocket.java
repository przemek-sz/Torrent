package torrent;

import torrent.model.FileModel;
import torrent.model.HostModel;
import torrent.model.RequestResponseTCP;

import java.io.*;
import java.net.Socket;
import java.util.*;


public class ClientTCPRequestSocket {

    class ClientTCPObjectSocket implements Runnable {

        HostModel hostModel;
        boolean isResponse = false;
        RequestResponseTCP request;
        Object response = null;

        public ClientTCPObjectSocket(HostModel hostModel, RequestResponseTCP request) {
            this.hostModel = hostModel;
            this.request = request;
        }


        @Override
        public void run() {

            ObjectOutputStream objectOutputStream = null;
            ObjectInputStream objectInputStream = null;
            Socket socket = null;

            try {

                if (hostModel != null) {
                    socket = new Socket(hostModel.getIP(), hostModel.getTCPport());

                    objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                    objectInputStream = new ObjectInputStream(socket.getInputStream());

                    objectOutputStream.writeObject(request);
                    objectOutputStream.flush();

                    response = objectInputStream.readObject();
                    isResponse = true;

                    objectOutputStream.close();
                    objectInputStream.close();
                    socket.close();
                } else {
                    System.out.println("Nie ma takiego hosta");
                    response = new String("Break");
                    isResponse = true;

                }
            } catch (IOException e) {
                System.out.println("Utracono polaczenie z hostem " + hostModel.getID());
                response = new String("Break");
                isResponse = true;

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
            }
        }

        public Object getResponse() {

            while (!isResponse) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return response;
        }

        public RequestResponseTCP getRequest() {
            return request;
        }

        public HostModel getHost() {
            return hostModel;
        }
    }

    public Map<String, List<FileModel>> requestList() {


        RequestResponseTCP request = new RequestResponseTCP("list");
        List<FileModel> fileList;
        Map<String, List<FileModel>> fileMap = new HashMap<>();

        System.out.println("Pobieranie Listy");
        for (HostModel hostModel : Main.hostModelList) {

            ClientTCPObjectSocket clientTCPSocket = new ClientTCPObjectSocket(hostModel, request);

            new Thread(clientTCPSocket).start();
            //fileList = (ArrayList<FileModel>) (clientTCPSocket.getResponse());

            if (!clientTCPSocket.getResponse().equals("Break")) {
                fileList = (ArrayList<FileModel>) (clientTCPSocket.getResponse());
                for (FileModel file : fileList) {
                    String key = file.getName() + "+" + file.getChecksum();
                    if (fileMap.get(key) == null) {
                        List<FileModel> fL = new ArrayList<>();
                        fL.add(file);
                        fileMap.put(key, fL);
                    } else {
                        List<FileModel> fL;
                        fL = fileMap.get(key);
                        fL.add(file);
                        fileMap.put(key, fL);
                    }
                }
            }
        }
        Main.files = fileMap;
        return fileMap;
    }

    public void push(String command) {

        String fileName = command.split(" ")[1];
        byte[] file = new ClientDirectory().getFile(fileName);
        if (file != null) {


            System.out.println("Wysylanie pliku");
            FileModel fileModel = new FileModel(fileName, file);

            int numberOfCommands = command.split(" ").length;

            for (int i = 2; i < numberOfCommands; i++) {
                HostModel host = getHost(Integer.parseInt(command.split(" ")[i]));
                new Thread(
                        new ClientTCPObjectSocket(
                                host,
                                new RequestResponseTCP(
                                        "push",
                                        new ObjectBytesConverter().ObjectToBytes(fileModel)
                                )
                        )
                ).start();
            }
        } else System.out.println("Brak pliku o podanej nazwie");
    }

    public void pull(String command) {

        ObjectBytesConverter converter = new ObjectBytesConverter();

        String fileName = command.split(" ")[1];
        int numberOfComands = command.split(" ").length;
        int numberOfRequests = numberOfComands - 2;

        String[] hostTab = new String[numberOfRequests];
        int i = 2;
        int j = 0;

        while (i < numberOfComands) {
            hostTab[j] = command.split(" ")[i];
            i++;
            j++;
        }

        HostModel[] hosts = new HostModel[numberOfRequests];

        new ClientTCPRequestSocket().requestList();
        FileModel fileToSend = new ClientDirectory().checkIsFileOnHost(fileName, hostTab);

        for (int k = 0; k < numberOfRequests; k++) {
            hosts[k] = getHost(Integer.parseInt(command.split(" ")[k + 2]));
        }


        if (fileToSend != null) {


            Synchronizer synchronizer = new Synchronizer(fileName);

            int partNumber = 1;

            for (int k = 2; k < numberOfComands; k++) {

                RequestResponseTCP request = new RequestResponseTCP("pull", converter.ObjectToBytes(fileToSend), numberOfRequests, partNumber);
                synchronizer.addHost(hosts[k - 2]);
                synchronizer.addRequest(request);
                partNumber++;
            }
            new Thread(synchronizer).start();

            System.out.println("Pobieranie pliku");

        }


    }

    public HostModel getHost(int ID) {

        List<HostModel> tmp = new ArrayList<>(Main.hostModelList);

        for (HostModel host : tmp) {
            if (ID == host.getID()) {
                return host;
            }
        }
        return null;
    }


    class Synchronizer implements Runnable {

        List<ClientTCPObjectSocket> clientTCPObjectSocketList = new ArrayList<>();
        List<HostModel> hostList = new ArrayList<>();
        List<RequestResponseTCP> requestList = new ArrayList<>();
        String synchronizedFileName;
        int responseToGet;


        Synchronizer(String synchronizedFileName) {
            this.synchronizedFileName = synchronizedFileName;
        }


        @Override
        public void run() {

            for (int i = 0; i < hostList.size(); i++) {
                clientTCPObjectSocketList.add(new ClientTCPObjectSocket(hostList.get(i), requestList.get(i)));
            }

            responseToGet = clientTCPObjectSocketList.size();

            for (ClientTCPObjectSocket socket : clientTCPObjectSocketList) {
                new Thread(socket).start();
            }

            mergeRespons(checkResponse());
        }

        public List<RequestResponseTCP> checkResponse() {

            int responsesCounter = 0;
            List<RequestResponseTCP> responseList = new ArrayList<>();

            while (responseToGet != responseList.size()) {

                System.out.println("Pobieranie");

                while (responsesCounter != clientTCPObjectSocketList.size()) {
                    for (ClientTCPObjectSocket socket : clientTCPObjectSocketList) {
                        if (socket.getResponse() != null) {
                            responsesCounter++;
                        }
                    }
                    if (responsesCounter < clientTCPObjectSocketList.size()) {
                        responsesCounter = 0;
                    }
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                ClientTCPObjectSocket socket = clientTCPObjectSocketList.get(0);
                Collections.shuffle(hostList);

                if (responseList.size() < responseToGet) {
                    if (socket.getHost() != null) {
                        if (socket.getResponse().equals("Break")) {
                            RequestResponseTCP req = socket.getRequest();
                            for (int i = 0; i < hostList.size(); i++) {
                                if (hostList.get(i).getHandle() == socket.getHost().getHandle()) {
                                    hostList.remove(i);
                                    clientTCPObjectSocketList.remove(0);
                                    if (hostList.size() == 0) {
                                        responseList.add(new RequestResponseTCP(null, null));
                                        return responseList;
                                    }
                                }
                            }
                            System.out.println("Wznawianie pobierania z hosta " + hostList.get(0).getID());
                            ClientTCPObjectSocket newScket = new ClientTCPObjectSocket(hostList.get(0), req);
                            new Thread(newScket).start();
                            clientTCPObjectSocketList.add(newScket);
                        } else {
                            responseList.add((RequestResponseTCP) socket.getResponse());
                            clientTCPObjectSocketList.remove(0);
                        }
                    } else {
                        if (socket.getResponse().equals("Break")) {
                            clientTCPObjectSocketList.remove(0);
                            ClientTCPObjectSocket newSocket = new ClientTCPObjectSocket(hostList.get(0), socket.getRequest());
                            new Thread(newSocket).start();
                            clientTCPObjectSocketList.add(newSocket);
                            System.out.println("Utracono polaczenie z hostem, pobieranie fragmentu z innego hosta");
                        } else {
                            ClientTCPObjectSocket newScket = new ClientTCPObjectSocket(hostList.get(0), socket.getRequest());
                            new Thread(newScket).start();
                            clientTCPObjectSocketList.remove(0);
                            clientTCPObjectSocketList.add(newScket);
                            System.out.println("Utracono polaczenie z hostem pobieranie fragmentu z innego hosta");
                        }
                    }
                }
                responsesCounter = 0;
            }
            return responseList;
        }

        public void mergeRespons(List<RequestResponseTCP> responses) {

            RequestResponseTCP response = null;
            boolean allResponses = true;


            for (RequestResponseTCP r : responses) {
                if (r.getData() == null) {
                    allResponses = false;
                }
            }

            if (allResponses) {
                byte[] data = new byte[0];


                for (int i = 0; i < responses.size(); i++) {

                    byte[] result = null;

                    for (RequestResponseTCP r : responses) {


                        if (r.getRequestPartNumber() == i + 1) {
                            response = r;
                            int length = data.length + response.getData().length;
                            result = new byte[length];
                            System.arraycopy(data, 0, result, 0, data.length);
                            System.arraycopy(response.getData(), 0, result, data.length, response.getData().length);
                        }
                    }
                    data = result;
                }
                new ClientDirectory().saveFile(synchronizedFileName, data);
            }
        }


        public void addRequest(RequestResponseTCP request) {
            requestList.add(request);
        }

        public void addHost(HostModel host) {
            hostList.add(host);
        }
    }


}
