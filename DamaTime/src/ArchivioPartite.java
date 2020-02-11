
import java.sql.*;
import java.util.logging.*;
import javafx.collections.*;
import static javafx.collections.FXCollections.*;

public class ArchivioPartite {
    
    private static final String nomeDB = "jdbc:mysql://localhost:3306/damatimedb";
    private static final String usernameDB = "root";
    private static final String passwordDB = "";

    private static final String QUERY_INSERISCI_PARTITA = "INSERT INTO archivio_partite (vincitore, perdente) VALUES (?, ?)";
    private static final String QUERY_GET_VITTORIE = "SELECT COUNT(*) AS TOT FROM archivio_partite WHERE vincitore = ? AND data >= ?";
    private static final String QUERY_GET_SCONFITTE = "SELECT COUNT(*) AS TOT FROM archivio_partite WHERE perdente = ? AND data >= ?";
    private static final String RISULTATO_QUERY = "TOT";
    private static final String QUERY_GET_STATO_PARTITA = "SELECT * FROM partite_online WHERE bianco = ? AND nero = ?";
    private static final String QUERY_INSERISCI_PARTITA_ONLINE = "INSERT INTO partite_online (bianco, nero, pos_bianchi, pos_neri, minuti_nero, secondi_nero, minuti_bianco, secondi_bianco, giocatore_di_turno) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String QUERY_AGGIORNA_PARTITA_ONLINE = "UPDATE partite_online SET pos_bianchi = ?, pos_neri = ?, minuti_bianco = ?, secondi_bianco = ?, minuti_nero = ?, secondi_nero = ?, giocatore_di_turno = ? WHERE bianco = ? AND nero = ?";
    private static final String QUERY_SALVA_MOSSA = "INSERT INTO mosse (mossaEffettuata, giocatore, bianco, nero) VALUES (?, ?, ?, ?)";
    private static final String QUERY_GET_MOSSE = "SELECT mossaEffettuata, giocatore FROM mosse WHERE bianco = ? AND nero = ? ORDER BY idmossa DESC LIMIT ?";    
    
    public static void salvaRisultatoPartita(String vincitore, String perdente){
        
        try ( 
                Connection conn = DriverManager.getConnection(nomeDB, usernameDB, passwordDB);
                PreparedStatement queryInserisciPartita = conn.prepareStatement(QUERY_INSERISCI_PARTITA);
            ){
            
            queryInserisciPartita.setString(1, vincitore);
            queryInserisciPartita.setString(2, perdente);
            
            queryInserisciPartita.executeUpdate();
            
        } catch (SQLException e) {System.err.println(e.getMessage());}
        
    }
    
    public static int[] caricaInformazioniUtente(String nomeUtente){
        int[] info = new int[2];
        
        try ( 
                Connection conn = DriverManager.getConnection(nomeDB, usernameDB, passwordDB);
                PreparedStatement queryVittorie = conn.prepareStatement(QUERY_GET_VITTORIE);
                PreparedStatement querySconfitte = conn.prepareStatement(QUERY_GET_SCONFITTE);
            ){
            
            queryVittorie.setString(1, nomeUtente);
            querySconfitte.setString(1, nomeUtente);
            
            queryVittorie.setString(2, ParametriConfigurazione.dataPartenza);
            querySconfitte.setString(2, ParametriConfigurazione.dataPartenza);
            
            ResultSet resultVittorie = queryVittorie.executeQuery();
            ResultSet resultSconfitte = querySconfitte.executeQuery();
            
            resultVittorie.next();
            resultSconfitte.next();
            
            info[0] = resultVittorie.getInt(RISULTATO_QUERY);
            info[1] = resultSconfitte.getInt(RISULTATO_QUERY);
            
        } catch (SQLException e) {System.err.println(e.getMessage());}
        
        return info;
    }
    
    public static InfoPartita caricaStatoPartita(String nomeBianco, String nomeNero){
        
        InfoPartita partitaCaricata = new InfoPartita();
        
        try(
                Connection conn = DriverManager.getConnection(nomeDB, usernameDB, passwordDB);
                PreparedStatement queryStatoPartitaOnline = conn.prepareStatement(QUERY_GET_STATO_PARTITA);
                PreparedStatement queryNuovaPartitaOnline = conn.prepareStatement(QUERY_INSERISCI_PARTITA_ONLINE);
            ){
            
            queryStatoPartitaOnline.setString(1, nomeBianco);
            queryStatoPartitaOnline.setString(2, nomeNero);
            
            ResultSet resultPartitaOnline = queryStatoPartitaOnline.executeQuery();
            
            if(resultPartitaOnline.next()) //(1)    
                return convertiResultSetInInfoPartita(resultPartitaOnline);
            
            queryNuovaPartitaOnline.setString(1, nomeBianco);
            queryNuovaPartitaOnline.setString(2, nomeNero);
            queryNuovaPartitaOnline.setInt(3, Scacchiera.DEFAULT_POS_BIANCHI);
            queryNuovaPartitaOnline.setInt(4, Scacchiera.DEFAULT_POS_NERI);
            queryNuovaPartitaOnline.setInt(5, ParametriConfigurazione.valoreInizialeTimer/60);
            queryNuovaPartitaOnline.setInt(6, ParametriConfigurazione.valoreInizialeTimer%60);
            queryNuovaPartitaOnline.setInt(7, ParametriConfigurazione.valoreInizialeTimer/60);
            queryNuovaPartitaOnline.setInt(8, ParametriConfigurazione.valoreInizialeTimer%60);
            queryNuovaPartitaOnline.setString(9, "bianco");
            
            queryNuovaPartitaOnline.executeUpdate();
            
            partitaCaricata = new InfoPartita();
            partitaCaricata.nomeBianco = nomeBianco;
            partitaCaricata.nomeNero = nomeNero;
            partitaCaricata.isValida = true;
            
        } catch (SQLException ex) {
            Logger.getLogger(ArchivioPartite.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return partitaCaricata;
    }
    
    private static InfoPartita convertiResultSetInInfoPartita(ResultSet rs) throws SQLException{
        
        InfoPartita partitaCaricata = new InfoPartita();
        
        partitaCaricata.isValida = true;
        partitaCaricata.nomeBianco = rs.getString("bianco");
        partitaCaricata.nomeNero = rs.getString("nero");
        partitaCaricata.posizioneBianco = rs.getInt("pos_bianchi");
        partitaCaricata.posizioneNero = rs.getInt("pos_neri");
        partitaCaricata.tempoBianco[0] = rs.getInt("minuti_bianco");
        partitaCaricata.tempoBianco[1] = rs.getInt("secondi_bianco");
        partitaCaricata.tempoNero[0] = rs.getInt("minuti_nero");
        partitaCaricata.tempoNero[1] = rs.getInt("secondi_nero");
        String GiocatoreDiTurnoString = rs.getString("giocatore_di_turno");
        if(GiocatoreDiTurnoString.equals("bianco"))
            partitaCaricata.giocatoreDiTurno = Colore.BIANCO;
        else partitaCaricata.giocatoreDiTurno = Colore.NERO;
        
        return partitaCaricata;
    }
    
    public static void segnalaFineTurno(InfoPartita partitaDaAggiornare){
        
        try(
                Connection conn = DriverManager.getConnection(nomeDB, usernameDB, passwordDB);
                PreparedStatement queryAggiornaPartitaOnline = conn.prepareStatement(QUERY_AGGIORNA_PARTITA_ONLINE);
            ){
            
            queryAggiornaPartitaOnline.setInt(1, partitaDaAggiornare.posizioneBianco);
            queryAggiornaPartitaOnline.setInt(2, partitaDaAggiornare.posizioneNero);
            queryAggiornaPartitaOnline.setInt(3, partitaDaAggiornare.tempoBianco[0]);
            queryAggiornaPartitaOnline.setInt(4, partitaDaAggiornare.tempoBianco[1]);
            queryAggiornaPartitaOnline.setInt(5, partitaDaAggiornare.tempoNero[0]);
            queryAggiornaPartitaOnline.setInt(6, partitaDaAggiornare.tempoNero[1]);
            if(partitaDaAggiornare.giocatoreDiTurno == Colore.BIANCO)
                queryAggiornaPartitaOnline.setString(7, "bianco");
            else queryAggiornaPartitaOnline.setString(7, "nero");
            queryAggiornaPartitaOnline.setString(8, partitaDaAggiornare.nomeBianco);
            queryAggiornaPartitaOnline.setString(9, partitaDaAggiornare.nomeNero);
            
            queryAggiornaPartitaOnline.executeUpdate();
            
        } catch (SQLException ex) {
            Logger.getLogger(ArchivioPartite.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public static void salvaMossa(String mossaEffettuata, InfoPartita partitaInCorso){
        
        try(
                Connection conn = DriverManager.getConnection(nomeDB, usernameDB, passwordDB);
                PreparedStatement query_mossa = conn.prepareStatement(QUERY_SALVA_MOSSA);
            ){
            
            query_mossa.setString(1, mossaEffettuata);
            if(partitaInCorso.giocatoreDiTurno == Colore.BIANCO)
                query_mossa.setString(2, "Bianco");
            else query_mossa.setString(2, "Nero");
            query_mossa.setString(3, partitaInCorso.nomeBianco);
            query_mossa.setString(4, partitaInCorso.nomeNero);
            
            query_mossa.executeUpdate();
            
        } catch (SQLException ex) {
            Logger.getLogger(ArchivioPartite.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public static ObservableList<Mossa> getMosseEffettuate(InfoPartita partitaInCorso){
        
        ObservableList<Mossa> ol = FXCollections.observableArrayList();
        
        try(
                Connection conn = DriverManager.getConnection(nomeDB, usernameDB, passwordDB);
                PreparedStatement queryMosseEffettuate = conn.prepareStatement(QUERY_GET_MOSSE);
            ){
            
            queryMosseEffettuate.setString(1, partitaInCorso.nomeBianco);
            queryMosseEffettuate.setString(2, partitaInCorso.nomeNero);
            queryMosseEffettuate.setInt(3, ParametriConfigurazione.numeroRigheTabella);
            
            ResultSet resultMosse = queryMosseEffettuate.executeQuery();
            
            while(resultMosse.next())
                ol.add(new Mossa(resultMosse.getString("giocatore"), resultMosse.getString("mossaEffettuata")));
            
        } catch (SQLException ex) {
            Logger.getLogger(ArchivioPartite.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        reverse(ol); //(2)
        
        return ol;
    }
    
}

/*
(1) I due utenti potrebbero non aver mai fatto una partita insieme, quindi il risultato della 
    query potrebbe essere vuoto. In questo caso, dovremo caricare nel database una nuova
    partita online
(2) La query restituisce le mosse a partire dalla più recente, noi invece vogliamo 
    mostrare prima quelle più vecchie
*/