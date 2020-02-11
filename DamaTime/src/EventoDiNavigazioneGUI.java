
import com.thoughtworks.xstream.*;
import com.thoughtworks.xstream.converters.basic.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class EventoDiNavigazioneGUI {
    
    private static final String IPClient = ParametriConfigurazione.IPClient;
    private static final String IPServer = ParametriConfigurazione.IPServer;
    private static final int portaServer = ParametriConfigurazione.portaServer;
    
    public void inviaSegnalazione(EventoGUI eventoVerificato){
        
        Date oraAttuale = new Date();        
        MessaggioDiLog messaggioDaInviare = new MessaggioDiLog(eventoVerificato, IPClient, oraAttuale);
        
        XStream xs = new XStream();    
        xs.registerConverter(new DateConverter("yyyy-MM-dd HH:mm:ss", null));
        String x = xs.toXML(messaggioDaInviare);
        
        try( 
                DataOutputStream dout = new DataOutputStream( (new Socket(IPServer, portaServer) ).getOutputStream())
            ){ 
            dout.writeUTF(x);
        } catch (Exception e) {e.printStackTrace();}
        
    }
}
