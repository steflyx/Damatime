
import java.util.*;
import javafx.application.*;
import javafx.collections.*;

public class PartitaOnline {
    
    public static Partita gestorePartita;
    
    private static final int DELAY = 0;
    private static final int PERIOD = 500;
    
    public static void riceviMosse(InfoPartita partitaAttuale){//(1)        
       
        Timer timer = new Timer();
        
        timer.scheduleAtFixedRate(new TimerTask(){
            
            @Override
            public void run(){
                Platform.runLater(() ->{
                    InfoPartita partitaCaricata = ArchivioPartite.caricaStatoPartita(partitaAttuale.nomeBianco, partitaAttuale.nomeNero);
                    if(partitaCaricata.giocatoreDiTurno != partitaAttuale.giocatoreDiTurno){ //(2)
                        gestorePartita.terminaPartita(); //(3)
                        gestorePartita.iniziaPartita();
                        timer.cancel();
                    }
                });
            }
            
        }, DELAY, PERIOD);
    }
    
    public static void inviaMossa(InfoPartita statoPartitaInCorso){
        ArchivioPartite.segnalaFineTurno(statoPartitaInCorso);        
    }
    
    public static void salvaMossa(String mossaEffettuata){
        ArchivioPartite.salvaMossa(mossaEffettuata, gestorePartita.getStatoPartita());
    }
    
    public static ObservableList<Mossa> getMosseEffettuate(){
         return ArchivioPartite.getMosseEffettuate(gestorePartita.getStatoPartita());
    }
}

/*
(1) La ricezione delle mosse viene fatta tramite polling sul database
(2) Controlliamo se l'altro giocatore ha passato il turno
(3) Si utilizza questo trucco di terminare e reiniziare la partita, così
    da evitare noie con i timer dei giocatori e in modo da evitare che si complichi
    eccessivamente il passaggio del turno (altrimenti andrebbero aggiornati singolarmente
    tutti i campi della partita, provocando probabilmente effetti collaterali indesiderati)
*/