
import javafx.scene.*;

public class Partita {
    
    public Scacchiera campoDiGioco;
    
    public Colore giocatoreDiTurno;
    public Boolean isPartitaFinita;
    
    public Utente giocatoreBianco;
    public Utente giocatoreNero;
    
    private final Group frameGUI;
    
    public Colore giocatoreLocale;
    
    public Partita(Group frameGUI){
        
        isPartitaFinita = false;
        giocatoreDiTurno = Colore.BIANCO;
        this.frameGUI = frameGUI;
        
        campoDiGioco = new Scacchiera(frameGUI);
        giocatoreBianco = new Utente(frameGUI);
        giocatoreNero = new Utente(frameGUI);
        
        if(!ParametriConfigurazione.isPartitaOnline && caricaPartita(SalvataggioLocalePartita.caricaInformazioniPartita())){
            CreatoreGUI.isPartitaIniziata = true;
            CreatoreGUI.inputUsernameBianco.setText(giocatoreBianco.nome);
            CreatoreGUI.inputUsernameNero.setText(giocatoreNero.nome);
            iniziaPartita();
        }
        
        if(ParametriConfigurazione.isPartitaOnline)
            giocatoreLocale = ParametriConfigurazione.coloreGiocatoreLocale;
    }
    
    public void iniziaPartita(){        
        isPartitaFinita = false;
        
        if(ParametriConfigurazione.isPartitaOnline){
            caricaPartita(ArchivioPartite.caricaStatoPartita(giocatoreBianco.nome, giocatoreNero.nome));
            if(isPartitaFinita()){ //(1)
                isPartitaFinita = true;
                terminaPartita();
                return;
            }
        }
        
        if(giocatoreDiTurno == Colore.BIANCO)
            giocatoreBianco.iniziaTurno();
        else giocatoreNero.iniziaTurno();
        
        if(ParametriConfigurazione.isPartitaOnline && giocatoreDiTurno != giocatoreLocale)
            PartitaOnline.riceviMosse(getStatoPartita());
        
        CreatoreGUI.coloraLabelUsername(giocatoreDiTurno);
        
        if(ParametriConfigurazione.isPartitaOnline) //(2)
            campoDiGioco.gestoreTabella.aggiornaListaMosse();
    }
    
    public void terminaTurno(){
        if(!isPartitaFinita){
            campoDiGioco.aggiorna();
            
            if(isPartitaFinita()){
                isPartitaFinita = true;
                if(ParametriConfigurazione.isPartitaOnline){
                    
                    InfoPartita partitaDaTerminare = getStatoPartita();
                    if(partitaDaTerminare.giocatoreDiTurno == Colore.BIANCO) //(3)
                        partitaDaTerminare.giocatoreDiTurno = Colore.NERO;
                    else partitaDaTerminare.giocatoreDiTurno = Colore.BIANCO;    
                    PartitaOnline.inviaMossa(partitaDaTerminare);
                }
                terminaPartita();
                return;
            }
           
            if(giocatoreDiTurno == Colore.BIANCO){
                giocatoreBianco.terminaTurno();
                giocatoreDiTurno = Colore.NERO;
                giocatoreNero.iniziaTurno();   
            }
            else{
                giocatoreNero.terminaTurno();
                giocatoreDiTurno = Colore.BIANCO;
                giocatoreBianco.iniziaTurno();
            }
            
            CreatoreGUI.coloraLabelUsername(giocatoreDiTurno);
         
            if(ParametriConfigurazione.isPartitaOnline){
                PartitaOnline.inviaMossa(getStatoPartita()); //(4)
                PartitaOnline.riceviMosse(getStatoPartita());
            }
            
        }
    }
    
    public boolean isPartitaFinita(){
        return campoDiGioco.isFinita() || giocatoreBianco.isTempoFinito() || giocatoreNero.isTempoFinito(); //(5)
    }
    
    public void terminaPartita(){
        if(!isPartitaFinita()){            
            if(!ParametriConfigurazione.isPartitaOnline)
                SalvataggioLocalePartita.salvaInformazioniPartita(getStatoPartita());
            
            if(giocatoreDiTurno == Colore.BIANCO) //(6)
                giocatoreBianco.terminaTurno();
            else giocatoreNero.terminaTurno();
        }
        else{ //(7)
            String vincitore = "", perdente = "";
            
            if(campoDiGioco.isFinita()){
                if(campoDiGioco.calcolaStato()[0] == 0){
                    vincitore = giocatoreNero.nome;
                    perdente = giocatoreBianco.nome;
                }
                else{
                    vincitore = giocatoreBianco.nome;
                    perdente = giocatoreNero.nome;
                }
            }
            
            if(giocatoreBianco.isTempoFinito()){
                vincitore = giocatoreNero.nome;
                perdente = giocatoreBianco.nome;
            }
            
            if(giocatoreNero.isTempoFinito()){
                vincitore = giocatoreBianco.nome;
                perdente = giocatoreNero.nome;
            }
            
            if(!ParametriConfigurazione.isPartitaOnline || giocatoreLocale == Colore.BIANCO) //(8)
                ArchivioPartite.salvaRisultatoPartita(vincitore, perdente);
            
            resettaPartita();
        }
        
    }
    
    public InfoPartita getStatoPartita(){
        InfoPartita statoPartita = new InfoPartita();
            
        statoPartita.posizioneBianco = campoDiGioco.calcolaStato()[0];
        statoPartita.posizioneNero = campoDiGioco.calcolaStato()[1];
        statoPartita.nomeBianco = giocatoreBianco.nome;
        statoPartita.nomeNero = giocatoreNero.nome;
        statoPartita.giocatoreDiTurno = giocatoreDiTurno;
        statoPartita.tempoBianco = giocatoreBianco.getTempo();
        statoPartita.tempoNero = giocatoreNero.getTempo();
        statoPartita.isValida = true;
            
        return statoPartita;
    }
    
    public void resettaPartita(){
        campoDiGioco.resettaScacchiera();
        
        giocatoreBianco.terminaTurno();//(6)
        giocatoreNero.terminaTurno();
        
        giocatoreDiTurno = Colore.BIANCO;
    }
    
    public void aggiungiUtenti(String bianco, String nero){
        giocatoreBianco.settaInformazioni(bianco);
        giocatoreNero.settaInformazioni(nero);
    }  
    
    public boolean caricaPartita(InfoPartita partitaDaCaricare){
                
        if(partitaDaCaricare.isValida){ //(9)
            giocatoreBianco.settaInformazioni(partitaDaCaricare.nomeBianco);
            giocatoreNero.settaInformazioni(partitaDaCaricare.nomeNero);
            giocatoreDiTurno = partitaDaCaricare.giocatoreDiTurno;
            campoDiGioco = new Scacchiera(frameGUI, partitaDaCaricare.posizioneNero, 
                    partitaDaCaricare.posizioneBianco, giocatoreDiTurno);
            giocatoreDiTurno = partitaDaCaricare.giocatoreDiTurno;
            giocatoreBianco.setTempo(partitaDaCaricare.tempoBianco);
            giocatoreNero.setTempo(partitaDaCaricare.tempoNero);            
            return true;
        }
        
        return false;
    }
    
}

/*
(1) Controlliamo se la partita caricata dal database è già finita
(2) Nel caso online, la tabella va ricaricata ad ogni passaggio di turno
(3) Poiché il polling sul database avviene controllando di chi è il turno, dobbiamo
    assicurarci di aggiornare quest'informazione prima di inviare la segnalazione
(4) Dopo aver passato il turno sull'host loacale, viene inviata la mossa effettuata.
    A questo punto, il controllo è lasciato alla classe "PartitaOnline"
(5) La partita finisce se uno dei due giocatori ha vinto sul campo o se ha finito il tempo.
(6) Il turno in corso viene terminato per fermare il timer (altrimenti rimarrebbe
    attivo il thread) e per nascondere il grafico
(7) La partita può terminare se uno dei due giocatori ha mangiato tutte le pedine 
    dell'avversario o se uno dei due ha terminato il tempo
(8) Per evitare che nelle partite online il risultato venga salvato due volte
(9) Se la partita che stiamo caricando non è valida, la funzione ritorna false
*/