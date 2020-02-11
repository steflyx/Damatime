
import java.io.*;
import javax.xml.*;
import javax.xml.parsers.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import javax.xml.validation.*;
import org.w3c.dom.*;
import org.xml.sax.*;

public class CaricatoreParametriConfigurazione {
    
    private static final String pathFileXML = "src/ParametriConfigurazione.xml";
    private static final String pathFileXSD = "src/validazione.xsd";
    
    public CaricatoreParametriConfigurazione(){
        
        if(!valida()) //(1)
            return;
                
        try {
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document documentXML = db.parse(new File(pathFileXML));   
            
            ParametriConfigurazione.coloreSfondo = documentXML.getElementsByTagName("coloreSfondo").item(0).getTextContent();
            ParametriConfigurazione.fontTitolo = documentXML.getElementsByTagName("fontTitolo").item(0).getTextContent();
            ParametriConfigurazione.fontScritte = documentXML.getElementsByTagName("fontScritte").item(0).getTextContent();
            ParametriConfigurazione.IPServer = documentXML.getElementsByTagName("IPServer").item(0).getTextContent();
            ParametriConfigurazione.portaServer = Integer.parseInt(documentXML.getElementsByTagName("portaServer").item(0).getTextContent());
            ParametriConfigurazione.IPClient = documentXML.getElementsByTagName("IPClient").item(0).getTextContent();
            ParametriConfigurazione.numeroRigheTabella = Integer.parseInt(documentXML.getElementsByTagName("numeroRigheTabella").item(0).getTextContent());
            ParametriConfigurazione.valoreInizialeTimer = Integer.parseInt(documentXML.getElementsByTagName("tempo").item(0).getTextContent());
            ParametriConfigurazione.dataPartenza = documentXML.getElementsByTagName("dataPartenza").item(0).getTextContent(); //(2)
            ParametriConfigurazione.colorePedinaDaMuovere = documentXML.getElementsByTagName("colorePedinaDaMuovere").item(0).getTextContent();
            ParametriConfigurazione.colorePedinaInCuiMuovere = documentXML.getElementsByTagName("colorePedinaInCuiMuovere").item(0).getTextContent();
            ParametriConfigurazione.colorePedinaMangiata = documentXML.getElementsByTagName("colorePedinaMangiata").item(0).getTextContent();
            ParametriConfigurazione.isPartitaOnline = Boolean.valueOf(documentXML.getElementsByTagName("isPartitaOnline").item(0).getTextContent());
            
            String coloreGiocatoreLocaleString = documentXML.getElementsByTagName("coloreGiocatoreLocale").item(0).getTextContent();
            if(coloreGiocatoreLocaleString.equals("bianco"))
                ParametriConfigurazione.coloreGiocatoreLocale = Colore.BIANCO;
            else ParametriConfigurazione.coloreGiocatoreLocale = Colore.NERO;
            
            String unitaMisura = documentXML.getElementsByTagName("valoreInizialeTimer").item(0).getAttributes().getNamedItem("unitaMisura").getNodeValue();
            if(unitaMisura.equals("min")) //(3)
                ParametriConfigurazione.valoreInizialeTimer *= 60;
            if(unitaMisura.equals("ore"))
                ParametriConfigurazione.valoreInizialeTimer *= 3600;
            
            
        } catch (ParserConfigurationException | SAXException | IOException | DOMException | NumberFormatException e) {System.out.println(e);}
    }
    
    private boolean valida() {
        try {
            
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document documentXML = db.parse(new File(pathFileXML));
            
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schemaXML = sf.newSchema(new StreamSource(new File(pathFileXSD)));
            
            schemaXML.newValidator().validate(new DOMSource(documentXML));
            
            System.out.println("File di configurazione valido");
            
            return true;
            
        } catch (ParserConfigurationException | SAXException | IOException e) {
            
            if (e instanceof SAXException)
                System.out.println("Errore di validazione: " + e.getMessage());
            else System.out.println(e.getMessage());
            
            return false;
        }
    }
    
}

/*
(1) Se il file di configurazione non è valido verranno utilizzati i valori di default
(2) La data viene già preparata come stringa in modo da poter essere subito usata nel DB
(3) Nei parametri di configurazione per semplicità salviamo solo il numero di secondi totale
*/
