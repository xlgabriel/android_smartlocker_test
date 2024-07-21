package android_serialport_api;

public class SerialServer {

    private static SerialControl instance = null;
    public static SerialControl getInstance(){

        if(instance == null)
        {
            instance =  new SerialControl();
        }
        return instance;
    }

    public SerialServer(){
    }

}
