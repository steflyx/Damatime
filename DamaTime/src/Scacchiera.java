
import javafx.scene.*;
import javafx.scene.image.*;
import javafx.scene.paint.*;

public class Scacchiera {
    
    private final int POS_X = 20;
    private final int POS_Y = 70;
    private final int HEIGHT = 500;
    private final String PATH_IMG = "immagini/scacchiera.png";
    
    private final int NUM_PEDINE = 32;
    private final int NUM_PEDINE_PER_RIGA = 4;
    private final int NUM_PEDINE_PER_COLONNA = 4;
    
    public static int DEFAULT_POS_NERI = -1<<20; //(1)
    public static int DEFAULT_POS_BIANCHI = -1>>>20; //(1)
        
    private final ImageView scacchieraVisual;
    public TabellaMosse gestoreTabella;
    private Pedina[] pedine = new Pedina[32];
    
    public Scacchiera(Group frameGUI, int posizioneNeri, int posizioneBianchi, Colore giocatoreIniziale){
        
        Image imgScacchiera = new Image(PATH_IMG);
        scacchieraVisual = new ImageView(imgScacchiera);
        scacchieraVisual.setLayoutX(POS_X);
        scacchieraVisual.setLayoutY(POS_Y);
        scacchieraVisual.setFitHeight(HEIGHT);
        scacchieraVisual.setPreserveRatio(true);
        frameGUI.getChildren().add(scacchieraVisual);
                
        for(int i=0; i<NUM_PEDINE; i++){ //(1)
            if(posizioneNeri % 2 != 0)
                pedine[i] = new Pedina(frameGUI, Colore.NERO, true, i); 
            if(posizioneBianchi % 2 != 0)
                pedine[i] = new Pedina(frameGUI, Colore.BIANCO, true, i); 
            if(posizioneNeri % 2 == 0 && posizioneBianchi % 2 == 0)
                pedine[i] = new Pedina(frameGUI, Colore.BIANCO, false, i);
            posizioneNeri >>= 1;
            posizioneBianchi >>= 1;       
        }
        Pedina.giocatoreDiTurno = giocatoreIniziale;
        
        gestoreTabella = new TabellaMosse(frameGUI);
                
    }
    
    public Scacchiera(Group frameGUI){     
        this(frameGUI, DEFAULT_POS_NERI, DEFAULT_POS_BIANCHI, Colore.BIANCO);
    }

    public int[] calcolaStato(){
        int stato[] = new int[2];
        
        stato[0] = 0;
        stato[1] = 0;
        
        for (int i=31; i>=0; i--) { //(2)
            stato[0] <<= 1;
            stato[1] <<= 1;
            if(Pedina.isVisibile[i]){
                if(Pedina.colorePedina[i] == Colore.BIANCO)
                    stato[0] += 1;
                if(Pedina.colorePedina[i] == Colore.NERO)
                    stato[1] += 1;
            }
        }
        
        return stato;
    } 
    
    public void aggiorna(){
        
        String pedinaMossa = " ", casellaScelta = " ";
        
        for (int i=0; i<pedine.length; i++) { //(3)
            if(Pedina.isCliccata[i] && Pedina.colorePedina[i] == Pedina.giocatoreDiTurno && Pedina.isVisibile[i])
                pedinaMossa = getCasella(i);
            
            if(Pedina.isCliccata[i] && !Pedina.isVisibile[i])
                casellaScelta = getCasella(i);
            
            pedine[i].aggiornaStatoPedina();            
        }
                
        if(!ParametriConfigurazione.isPartitaOnline) //(4)
            gestoreTabella.aggiungiMossa(Pedina.giocatoreDiTurno, pedinaMossa + "-" + casellaScelta);
        else{
            PartitaOnline.salvaMossa(pedinaMossa + "-" + casellaScelta);
            gestoreTabella.aggiungiMossa(Pedina.giocatoreDiTurno, pedinaMossa + "-" + casellaScelta);
        }
        
        if(Pedina.giocatoreDiTurno == Colore.BIANCO)
            Pedina.giocatoreDiTurno = Colore.NERO;
        else Pedina.giocatoreDiTurno = Colore.BIANCO;
    }
    
    private String getCasella(int indicePedina){
        int riga, colonna;
        String casella = "";
        
        riga = indicePedina/NUM_PEDINE_PER_RIGA + 1;
                
        colonna = (indicePedina%NUM_PEDINE_PER_COLONNA)*2;
        if(indicePedina%8 > 3) //(5)
            colonna += 1;
                               
        switch(colonna){
            case 0: casella = "a"; break;
            case 1: casella = "b"; break;
            case 2: casella = "c"; break;
            case 3: casella = "d"; break;
            case 4: casella = "e"; break;
            case 5: casella = "f"; break;
            case 6: casella = "g"; break;
            case 7: casella = "h"; break;
        }
        casella += riga;
        
        return casella;
    }
    
    public boolean isFinita(){ //(6) 
        int num_neri = 0, num_bianchi = 0;
        
        for (int i=0; i<pedine.length; i++) {
            if(Pedina.isVisibile[i] && Pedina.colorePedina[i] == Colore.NERO)
                num_neri++;
            if(Pedina.isVisibile[i] && Pedina.colorePedina[i] == Colore.BIANCO)
                num_bianchi++;
        }
        
        return (num_neri == 0 || num_bianchi == 0);
    }
    
    public void resettaScacchiera(){
        for(int i=0; i<pedine.length; i++){
            Pedina.isCliccata[i] = false;            
            
            if(i<12){
                Pedina.isVisibile[i] = true;
                Pedina.colorePedina[i] = Colore.BIANCO;
                Pedina.pedinaVisual[i].setFill(Color.WHITE);
            }
            if(i>=12 && i <20){
                Pedina.isVisibile[i] = false;
                Pedina.pedinaVisual[i].setFill(Color.TRANSPARENT);
            }
            if(i>=20){
                Pedina.isVisibile[i] = true;
                Pedina.colorePedina[i] = Colore.NERO;
                Pedina.pedinaVisual[i].setFill(Color.BLACK);
            }
            
            Pedina.giocatoreDiTurno = Colore.BIANCO;
        }
        
        gestoreTabella.svuota();
    }
}

/*
(1) Due interi a 32 bit sono in grado di rappresentare la posizione delle pedine sulla scacchiera:
    i bit uguali a 1 ci indicano che la pedina è presente, i bit a 0 ci indicano che non lo è.
    Gli interi default sono quindi 11111111111100000000000000000000000 per i neri e 
    0000000000000000000000001111111111111111 per i bianchi. Partendo da -1 che ha tutti i bit a 1
    è facile ottenere i due valori.
(2) Per costruire correttamente i due interi bisogna cominciare dal fondo (altrimenti vengono invertiti)
(3) Fra tutte le pedine si cerca quali sono state cliccate. Confrontandone il colore con
    quello del giocatore di turno possiamo determinare quale pedina è stata mossa e dove.
(4) Se la partita è online, la gestione della tabella avviene diversamente: ogni volta
    che la scacchiera viene ricreata, vengono ricaricate le mosse effettuate.
(5) Bisogna considerare il fatto che le pedine si posizionano, a seconda della riga, a partire dalla prima 
    o dalla seconda colonna
(6) La partita è finita, dal punto di vista della scacchiera, se uno dei due giocatori non ha più pedine
*/