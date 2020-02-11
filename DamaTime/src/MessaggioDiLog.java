
import java.io.*;
import java.util.*;

public class MessaggioDiLog implements Serializable{
    private String nomeApplicazione = "DamaTime!";
    private String evento;
    private String IPClient;
    private Date data;
    
    public MessaggioDiLog(EventoGUI eventoVerificato, String IP, Date dataEvento){
        IPClient = IP;
        data = dataEvento;
        if(eventoVerificato == EventoGUI.AVVIO)
            evento = "AVVIO";
        if(eventoVerificato == EventoGUI.CONFERMA)
            evento = "CONFERMA";
        if(eventoVerificato == EventoGUI.RESET)
            evento = "RESET";
        if(eventoVerificato == EventoGUI.MOSSA)
            evento = "MOSSA";
        if(eventoVerificato == EventoGUI.TERMINE)
            evento = "TERMINE";
    }
}
