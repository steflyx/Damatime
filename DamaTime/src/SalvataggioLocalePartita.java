
import java.io.*;
import java.util.logging.*;

public class SalvataggioLocalePartita {
    
    private static final String pathFileCache = "partitaSalvata.bin";
    
    public static void salvaInformazioniPartita(InfoPartita partitaBloccata){
        
        try ( 
                FileOutputStream fout = new FileOutputStream( pathFileCache );
                ObjectOutputStream oout = new ObjectOutputStream(fout);
            ){
            
            oout.writeObject( partitaBloccata );          
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SalvataggioLocalePartita.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SalvataggioLocalePartita.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static InfoPartita caricaInformazioniPartita(){
        InfoPartita partitaDaCaricare = new InfoPartita();
        
        try (
                FileInputStream fin = new FileInputStream( pathFileCache );
                ObjectInputStream oin = new ObjectInputStream(fin);
            ){
            
            partitaDaCaricare = (InfoPartita) oin.readObject();
            
        }catch (ClassNotFoundException | IOException ex) {
            Logger.getLogger(SalvataggioLocalePartita.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return partitaDaCaricare;
    }
   
}
