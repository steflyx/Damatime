
import javafx.application.*;
import javafx.event.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import javafx.stage.*;

public class CreatoreGUI extends Application {
    
    Partita gestorePartita;
    
    static Boolean isPartitaIniziata = false;
    
    Group root;
    
    Text titolo;
    Text labelTimer;    
    static Text labelUsernameNero; //(1)
    static Text labelUsernameBianco;
    
    static TextField inputUsernameNero;
    static TextField inputUsernameBianco;
    
    Button btnConferma;
    Button btnReset;
    
    @Override
    public void start(Stage stage) {
        
        CaricatoreParametriConfigurazione caricatoreParametriConfigurazione = new CaricatoreParametriConfigurazione();
        
        inizializzaScritte();
        inizializzaTextField();
        inizializzaButton();
        
        root = new Group(titolo, labelUsernameBianco, inputUsernameBianco,
                labelUsernameNero, inputUsernameNero, btnConferma, btnReset, labelTimer);
                
        Scene scene = new Scene(root, 1000, 650);
        scene.setFill(Color.web(ParametriConfigurazione.coloreSfondo));
        stage.setTitle("DamaTime!");
        scene.getStylesheets().addAll("css/button.css", "css/circle.css");
        
        stage.setScene(scene);
        stage.show();
        
        inizializzaGestorePartita();        
        
        stage.setOnCloseRequest((WindowEvent wv) -> {
            if(isPartitaIniziata)
                gestorePartita.terminaPartita();
            
            EventoDiNavigazioneGUI e = new EventoDiNavigazioneGUI();
            e.inviaSegnalazione(EventoGUI.TERMINE);
        });
        
        EventoDiNavigazioneGUI e = new EventoDiNavigazioneGUI();
        e.inviaSegnalazione(EventoGUI.AVVIO);

    }

    public static void main(String[] args) {
        launch(args);
    }
    
    private void inizializzaGestorePartita(){
        gestorePartita = new Partita(root);  
        
        if(ParametriConfigurazione.isPartitaOnline)
            PartitaOnline.gestorePartita = gestorePartita;
    }
    
    private void gestisciBtnConferma(String nomeBianco, String nomeNero){
        if(isPartitaIniziata && !gestorePartita.isPartitaFinita){ //(2)
            gestorePartita.terminaTurno();
            if(gestorePartita.isPartitaFinita)
                isPartitaIniziata = false;
        }
        else{
            gestorePartita.aggiungiUtenti(nomeBianco, nomeNero);
            isPartitaIniziata = true;
            gestorePartita.iniziaPartita();
        }
        
        EventoDiNavigazioneGUI e = new EventoDiNavigazioneGUI();
        e.inviaSegnalazione(EventoGUI.CONFERMA);
    }
    
    private void gestisciBtnReset(){
        isPartitaIniziata = false;
        labelUsernameBianco.setFill(Color.BLACK);
        labelUsernameNero.setFill(Color.BLACK);
        
        gestorePartita.resettaPartita();
        
        EventoDiNavigazioneGUI e = new EventoDiNavigazioneGUI();
        e.inviaSegnalazione(EventoGUI.RESET);
    }
    
    private void inizializzaScritte(){
        titolo = new Text("DamaTime!");
        titolo.setX(430);
        titolo.setY(30);
        titolo.setFont(Font.font (ParametriConfigurazione.fontTitolo, 25));
        titolo.setStyle("-fx-font-weight: bold;");
        
        labelUsernameNero = new Text("User nero");
        labelUsernameNero.setX(140);
        labelUsernameNero.setY(48);
        labelUsernameNero.setFont(Font.font (ParametriConfigurazione.fontScritte, 12));
        
        labelUsernameBianco = new Text("User bianco");
        labelUsernameBianco.setX(140);
        labelUsernameBianco.setY(608);
        labelUsernameBianco.setFont(Font.font (ParametriConfigurazione.fontScritte, 12));
        labelUsernameBianco.setFill(Color.RED);
        
        labelTimer = new Text("Tempo rimanente");
        labelTimer.setX(840);
        labelTimer.setY(400);
        labelTimer.setFont(Font.font (ParametriConfigurazione.fontScritte, 15));
        labelTimer.setStyle("-fx-font-weight: bold;");
    }
    
    private void inizializzaTextField(){
        inputUsernameNero = new TextField();
        inputUsernameNero.setLayoutX(220);
        inputUsernameNero.setLayoutY(30);
        
        inputUsernameBianco = new TextField();
        inputUsernameBianco.setLayoutX(220);
        inputUsernameBianco.setLayoutY(590);
    }
    
    private void inizializzaButton(){
        btnConferma = new Button("Conferma ");
        btnConferma.setLayoutX(625);
        btnConferma.setLayoutY(590);
        
        btnReset = new Button("  Reset  ");
        btnReset.setLayoutX(815);
        btnReset.setLayoutY(590);
        btnReset.setStyle("-fx-padding: 5 42 5 42");
        
        if(ParametriConfigurazione.isPartitaOnline)
            btnReset.setVisible(false); //(3)
        
        btnConferma.setOnAction((ActionEvent ev) -> {
            gestisciBtnConferma(inputUsernameBianco.getText(), inputUsernameNero.getText());
        });
        btnReset.setOnAction((ActionEvent ev) -> {gestisciBtnReset();});
    }
    
    public static void coloraLabelUsername(Colore giocatoreDiTurno){ //(4)
        if(giocatoreDiTurno == Colore.BIANCO){
            labelUsernameBianco.setFill(Color.RED);
            labelUsernameNero.setFill(Color.BLACK);
        }
        else{
            labelUsernameBianco.setFill(Color.BLACK);
            labelUsernameNero.setFill(Color.RED);
        }
    }
}

/*
(1) Questi parametri sono statici per permettere ad altre classi di modificarli
(2) Il pulsante "CONFERMA" viene premuto sia all'inizio per confermare i nomi degli
    utenti sia durante la partita per segnalare la fine del turno
(3) Nelle partite online non è presente la possibilità di resettare la partita
(4) Per individuare il giocatore che si deve muovere viene usato questo aiuto grafico
*/