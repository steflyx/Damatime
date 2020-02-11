
import javafx.scene.*;

public class Utente {
    
    public String nome;
    
    private final Group frameGUI;
    
    private StatisticheVinciteVisual graficoPartite = new StatisticheVinciteVisual();
    private TempoPartita timer;
    
    private int numVittorie;
    private int numSconfitte;
    
    public Utente(Group frameGUI){
        this.frameGUI = frameGUI;
    }
    
    public void settaInformazioni(String nomeScelto){
        nome = nomeScelto;
        
        int statistiche[] = ArchivioPartite.caricaInformazioniUtente(nomeScelto);
        numVittorie = statistiche[0];
        numSconfitte = statistiche[1];
        
        timer = new TempoPartita();
    }
    
    public void iniziaTurno(){
        timer.avvia();
        timer.mostra(frameGUI);
        
        graficoPartite.mostra(frameGUI, numVittorie, numSconfitte);
    }
    
    public void terminaTurno(){
        timer.ferma();
        timer.nascondi(frameGUI);
        
        graficoPartite.nascondi();
    }
    
    public Boolean isTempoFinito(){
        return timer.minutiRimanenti == 0 && timer.secondiRimanenti == 0;
    }
    
    public int[] getTempo(){
        int tempo[] = new int[2];
        tempo[0] = timer.minutiRimanenti;
        tempo[1] = timer.secondiRimanenti;
        return tempo;
    }
    
    public void setTempo(int tempo[]){
        timer.minutiRimanenti = tempo[0];
        timer.secondiRimanenti = tempo[1];
    }
}