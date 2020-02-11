
import javafx.collections.*;
import javafx.scene.*;
import javafx.scene.chart.*;


public class StatisticheVinciteVisual {
    
    private final int POS_X = 580;
    private final int POS_Y = 320;
    private final int WIDTH = 250;
    private final int HEIGHT = 250;
    
    private static PieChart graficoVisual;
    
    public void mostra(Group frameGUI, int numVittorie, int numSconfitte){
        
        ObservableList<PieChart.Data> pieChartData = //(1)
                FXCollections.observableArrayList(                    
                    new PieChart.Data("Vittorie", numVittorie),
                    new PieChart.Data("Sconfitte", numSconfitte)               
                );
        
        graficoVisual = new PieChart(pieChartData);
        graficoVisual.setTitle("Statistiche vincite/sconfitte");
        
        graficoVisual.setLayoutX(POS_X);
        graficoVisual.setLayoutY(POS_Y);
        graficoVisual.setPrefSize(WIDTH, HEIGHT);
        
        frameGUI.getChildren().add(graficoVisual);
        
    }
    
    public void nascondi(){
        graficoVisual.setVisible(false);        
    }
}

/*
(1) Il codice è tratto da un sito individuato in fase di prototipazione
*/