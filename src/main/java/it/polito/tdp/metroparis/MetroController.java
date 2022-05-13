package it.polito.tdp.metroparis;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.metroparis.model.Fermata;
import it.polito.tdp.metroparis.model.Model;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;

public class MetroController {
	
	private Model model;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ComboBox<Fermata> cmbArrivo;

    @FXML
    private ComboBox<Fermata> cmbPartenza;

    @FXML
    private TextArea txtResult;
    

    @FXML
    private TableColumn<Fermata, String> Fermata; // primo generic = quello della tabella, mentre il secondo è il tipo del dato 
    //che metto nella colonna. OClonna ha bisogno di sapere come fare ad estrarre la stringa dalla fermata.
    
    @FXML
    private TableView<Fermata> tablePercorso;

    @FXML
    void handleRicerca(ActionEvent event) {
    	Fermata partenza = this.cmbPartenza.getValue();
    	Fermata arrivo = this.cmbArrivo.getValue();
    	if(partenza != null && arrivo!=null && !partenza.equals(arrivo)) {
    		List<Fermata> percorso = this.model.calcolaPercorso(partenza, arrivo);
    		this.tablePercorso.setItems(FXCollections.observableArrayList(percorso));
    		this.txtResult.setText("Percorso trovato con "+percorso.size()+" stazioni\n");
    	}else {
    		this.txtResult.setText("Devi selezionare due stazioni diverse tra loro\n");
    	}
    }
    
    public void setModel(Model m) {
    	this.model = m;
    	List<Fermata> fermate = this.model.getFermate();
    	this.cmbPartenza.getItems().addAll(fermate);
    	this.cmbArrivo.getItems().addAll(fermate);
    }

    @FXML
    void initialize() {
        assert cmbArrivo != null : "fx:id=\"cmbArrivo\" was not injected: check your FXML file 'Metro.fxml'.";
        assert cmbPartenza != null : "fx:id=\"cmbPartenza\" was not injected: check your FXML file 'Metro.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Metro.fxml'.";
        //ogni colonna riceve l'info sull'intero oggetto ed estrae la stringa da visualizzare:
        //uso PropertyValueFactory con gli stessi generics della colonna, passando come parametro il nome della colonna da visualizzare,
        //ovvero il nome dell'attributo che ci interessa
        Fermata.setCellValueFactory(new PropertyValueFactory<Fermata, String>("nome"));
    }

}
