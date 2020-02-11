
import javafx.beans.property.*;

public class Mossa {
    private SimpleStringProperty giocatore;
    private SimpleStringProperty mossaEffettuata;
    
    public Mossa(String giocatoreParametro, String mossaEffettuataParametro){
        giocatore = new SimpleStringProperty(giocatoreParametro);
        mossaEffettuata = new SimpleStringProperty(mossaEffettuataParametro);
    }
    
    public String getGiocatore(){
        return giocatore.get();
    }
    
    public String getMossaEffettuata(){
        return mossaEffettuata.get();
    }
}
