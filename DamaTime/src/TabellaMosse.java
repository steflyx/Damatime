
import javafx.collections.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;


public class TabellaMosse {
    
    private final int POS_X = 600;
    private final int POS_Y = 70;
    private final int WIDTH = 363;
    private final int HEIGHT = 250;
    
    private final int numeroRighe = ParametriConfigurazione.numeroRigheTabella;
    
    private final TableView<Mossa> tabellaVisual = new TableView();
    private static ObservableList<Mossa> listaMosse;
    
    public TabellaMosse(Group frameGUI){
        listaMosse = FXCollections.observableArrayList();    
            
        TableColumn colonnaGiocatore = new TableColumn("GIOCATORE");
        colonnaGiocatore.setCellValueFactory(new PropertyValueFactory<>("giocatore"));

        TableColumn colonnaMossa = new TableColumn("MOSSA EFFETTUATA");
        colonnaMossa.setCellValueFactory(new PropertyValueFactory<>("mossaEffettuata"));
        
        tabellaVisual.getColumns().addAll(colonnaGiocatore, colonnaMossa);
        tabellaVisual.setLayoutX(POS_X);
        tabellaVisual.setLayoutY(POS_Y);
        tabellaVisual.setPrefSize(WIDTH, HEIGHT);
        tabellaVisual.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabellaVisual.setItems(listaMosse);
                
        frameGUI.getChildren().add(tabellaVisual);
    }
    
    public void aggiungiMossa(Colore giocatore, String mossa){
        Mossa m;
        
        if(giocatore == Colore.BIANCO)
            m = new Mossa("Bianco", mossa);
        else m = new Mossa("Nero", mossa);
        
        listaMosse.add(m);
        
        if(listaMosse.size()>numeroRighe)
            listaMosse.remove(0);
    }
    
    public void aggiornaListaMosse(){
        listaMosse.addAll(PartitaOnline.getMosseEffettuate());
    }
    
    public void svuota(){
        listaMosse.clear();
    }
}
