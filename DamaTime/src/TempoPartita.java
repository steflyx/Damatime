
import java.util.*;
import javafx.application.*;
import javafx.scene.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;

public class TempoPartita {
    
    private final int POS_X = 870;
    private final int POS_Y = 440;
    
    private final int DELAY = 0;
    private final int PERIOD = 1000;
    
    private Timer timer;
    private Text timerVisual;
    
    public int minutiRimanenti;
    public int secondiRimanenti;
    
    private String minutiRimanentiVisual; //(1)
    private String secondiRimanentiVisual;
    
    public TempoPartita(){
        int secondiTotali = ParametriConfigurazione.valoreInizialeTimer; //(2)
        minutiRimanenti = secondiTotali / 60;
        secondiRimanenti = secondiTotali % 60;
    }
    
    public void avvia(){
        timer = new Timer();
                     
        timer.scheduleAtFixedRate(new TimerTask() { //(3)
                      
            @Override
            public void run() {
                
                if(minutiRimanenti != 0 || secondiRimanenti != 0){
                    secondiRimanenti--;
                    if(secondiRimanenti == -1){
                        minutiRimanenti--;
                        secondiRimanenti = 59;
                    }
                }
                else timerVisual.setFill(Color.RED);
                
                minutiRimanentiVisual = ""; //(1)
                secondiRimanentiVisual = "";
                
                if(minutiRimanenti < 10) //(1)
                    minutiRimanentiVisual = "0";
                minutiRimanentiVisual += minutiRimanenti;
                
                if(secondiRimanenti < 10) //(1)
                    secondiRimanentiVisual = "0";
                secondiRimanentiVisual += secondiRimanenti;
                
                Platform.runLater(() -> { //(4)
                    timerVisual.setText(minutiRimanentiVisual + ":" + secondiRimanentiVisual);
                });
            }
        }, DELAY, PERIOD);
    }
    
    public void ferma(){
        timer.cancel();
    }
    
    public void mostra(Group frameGUI){
        timerVisual = new Text();
        timerVisual.setX(POS_X);
        timerVisual.setY(POS_Y);
        timerVisual.setStyle("-fx-font-weight: bold; -fx-font-size: 25px;");
        frameGUI.getChildren().add(timerVisual);
    }
    
    public void nascondi(Group frameGUI){
        frameGUI.getChildren().remove(timerVisual);
    }
}

/*
(1) Le due stringhe sono utilizzate per far apparire gli 0 davanti ai numeri dell'orologio
    quando questi sono minori di 10
(2) Nei parametri di configurazione per semplicità salviamo solo il numero di secondi totale
(3) Il codice è tratto da un sito visitato durante la fase di prototipazione
(4) Queste righe vanno aggiunte altrimenti viene lanciata l'eccezione "Not on FX
    application thread" (soluzione trovata in varie risposte su stackoverflow)
*/