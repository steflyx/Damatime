
import javafx.scene.*;
import javafx.scene.input.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;

public class Pedina {
    
    private final int PEDINE_PER_RIGA = 4;
    private final int OFFSET_X = 35;
    private final int OFFSET_Y = 555;
    private final int DIM_CASELLA = 59;
    private final int DIM_PEDINA = 20;

    private final int indice;
    public static Colore giocatoreDiTurno;
    
    public final static Circle pedinaVisual[] = new Circle[32]; //(1)
    public final static boolean isCliccata[] = new boolean[32];
    public final static boolean isVisibile[] = new boolean[32];
    public final static Colore colorePedina[] = new Colore[32];
       
    public Pedina(Group frameGUI, Colore colorePedinaCreata, Boolean isPedinaVisibile, int indicePedina){
        
        indice = indicePedina;
        
        isVisibile[indice] = isPedinaVisibile;
        colorePedina[indice] = colorePedinaCreata;
        isCliccata[indice] = false;
        
        int posizioneX = calcolaPosizioneX();
        int posizioneY = calcolaPosizioneY();
        
        if(colorePedina[indice] == Colore.NERO)
            pedinaVisual[indice] = new Circle(posizioneX, posizioneY, DIM_PEDINA, Color.BLACK);
        else 
            pedinaVisual[indice] = new Circle(posizioneX, posizioneY, DIM_PEDINA, Color.WHITE);
        
        if(!isVisibile[indice])
            pedinaVisual[indice].setFill(Color.TRANSPARENT);
        
        pedinaVisual[indice].setOnMouseClicked((MouseEvent e) -> {
            gestisciClick();
        });
        
        frameGUI.getChildren().add(pedinaVisual[indice]);      
    }
    
    private int calcolaPosizioneX(){
        int posizione = OFFSET_X + (indice % PEDINE_PER_RIGA)*DIM_CASELLA*2 + DIM_CASELLA/2; //(2)
        if(indice % (PEDINE_PER_RIGA*2) > 3)
            posizione += DIM_CASELLA; //(3)
        return posizione;
    }
    
    private int calcolaPosizioneY(){
        return OFFSET_Y - ((indice / PEDINE_PER_RIGA)*DIM_CASELLA + DIM_CASELLA/2); //(4)
    }
    
    public void gestisciClick(){//(5)
        isCliccata[indice] = true;
        
        if(!isVisibile[indice])
            pedinaVisual[indice].setFill(Color.web(ParametriConfigurazione.colorePedinaInCuiMuovere));
        else{
            if(colorePedina[indice] == giocatoreDiTurno)
                pedinaVisual[indice].setFill(Color.web(ParametriConfigurazione.colorePedinaDaMuovere));
            else pedinaVisual[indice].setFill(Color.web(ParametriConfigurazione.colorePedinaMangiata));
        }
        
        EventoDiNavigazioneGUI e = new EventoDiNavigazioneGUI();
        e.inviaSegnalazione(EventoGUI.MOSSA);
    }
    
    public void aggiornaStatoPedina(){ //(6)
        if(isCliccata[indice]){
            isCliccata[indice] = false;
            
            if(!isVisibile[indice]){
                isVisibile[indice] = true;
                
                if(giocatoreDiTurno == Colore.BIANCO)
                    pedinaVisual[indice].setFill(Color.WHITE);
                else 
                    pedinaVisual[indice].setFill(Color.BLACK);
                
                colorePedina[indice] = giocatoreDiTurno;
            }
            else{
                pedinaVisual[indice].setFill(Color.TRANSPARENT);
                isVisibile[indice] = false;
            }
        }
    }
}

/*
(1) All'inizio avevo usato attributi di classe semplici, ma ho dovuto modificarli
    perché altrimenti non potevano essere modificati all'interno dell'evento causato dal click
(2) La formula si compone così:
        - l'offset è il punto da cui comincia la scacchiera
        - il secondo addendo calcola la colonna dove si deve trovare la pedina e la sposta
        - l'ultimo addendo serve per centrare la pedina all'interno della casella
(3) Le pedine, a righe alternate, vanno posizionate a partire dalla prima o dalla seconda casella
(4) Le pedine sono posizionate a partire dal basso (ecco perché qui c'è il meno)
(5) Quando la pedina viene cliccata, si possono avere i seguenti casi:
        - Pedina invisibile --> il giocatore vuole muovere qui
        - Pedina visibile, giocatore dello stesso colore --> il giocatore vuole muovere questa pedina
        - Pedina visibile, giocatore di colore diverso --> il giocatore vuole mangiare questa pedina
(6) L'aggiornamento della pedina avviene in questo modo:
        - Pedina invisibile --> inseriamo una pedina del colore del giocatore (vuole muovere qui)
        - Pedina visibile --> il giocatore o ha spostato una sua pedina o ne ha mangiata una
                              del suo avversario; in entrambi i casi va resa invisibile
*/