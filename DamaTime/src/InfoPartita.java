
import java.io.*;

public class InfoPartita implements Serializable{
    
    public int posizioneNero;
    public int posizioneBianco;
    public String nomeNero;
    public String nomeBianco;
    public Colore giocatoreDiTurno;
    public int[] tempoNero = new int[2]; //(1)
    public int[] tempoBianco = new int[2];
    public boolean isValida;
    
    public InfoPartita(){
        posizioneNero = Scacchiera.DEFAULT_POS_NERI;
        posizioneBianco = Scacchiera.DEFAULT_POS_BIANCHI;
        giocatoreDiTurno = Colore.BIANCO;
        tempoNero[0] = ParametriConfigurazione.valoreInizialeTimer/60;
        tempoNero[1] = ParametriConfigurazione.valoreInizialeTimer%60;
        tempoBianco[0] = ParametriConfigurazione.valoreInizialeTimer/60;
        tempoBianco[1] = ParametriConfigurazione.valoreInizialeTimer%60;
        isValida = false; //(2)
    }
    
}

/*
(1) int[0] contiene i minuti, int[1] contiene i secondi
(2) Deve essere esplicitamente validata da chi la sta usando per evitare problemi
*/
