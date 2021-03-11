package torrent;

import java.io.*;

public class ObjectBytesConverter {

    public byte[] ObjectToBytes(Object o){

        byte[] data=null;
        ByteArrayOutputStream bos=null;
        ObjectOutputStream oos=null;

        try {
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(o);
            oos.flush();
            data = bos.toByteArray();
            return data;

        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try {
                if(oos!=null){
                    oos.close();
                }
                if(bos!=null){
                    bos.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }

        }
        return null;
    }

    public Object BytesToObject(byte[] data){

            Object o;
            ByteArrayInputStream bis=null;
            ObjectInputStream ois=null;

            try{
                bis = new ByteArrayInputStream(data);
                ois = new ObjectInputStream(bis);


                o=ois.readObject();
                return o;
            }catch (IOException e){
                e.printStackTrace();
            }catch (ClassNotFoundException e) {
                e.printStackTrace();
            }finally {
                try {
                    if(bis!=null){
                        bis.close();
                    }
                    if(ois!=null){
                        ois.close();
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }

            }

        return null;
   }

}
