package it.polito.tdp.metroparis.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;
import org.jgrapht.traverse.GraphIterator;

import it.polito.tdp.metroparis.db.MetroDAO;

public class Model {

	private List<Fermata> fermate;
	Map<Integer, Fermata> fermateIdMap ;
	
	private Graph<Fermata, DefaultEdge> grafo;

	public List<Fermata> getFermate() {
		if (this.fermate == null) {
			MetroDAO dao = new MetroDAO();
			this.fermate = dao.getAllFermate();
			
			this.fermateIdMap = new HashMap<Integer, Fermata>();
			for (Fermata f : this.fermate)
				this.fermateIdMap.put(f.getIdFermata(), f);

		}
		return this.fermate;
	}

	public void creaGrafo() {
		this.grafo = new SimpleDirectedGraph<Fermata, DefaultEdge>(DefaultEdge.class);
//		Graphs.addAllVertices(this.grafo, this.fermate);
		Graphs.addAllVertices(this.grafo, getFermate());
		
		MetroDAO dao = new MetroDAO();

		List<CoppiaID> fermateDaCollegare = dao.getAllFermateConnesse();
		for (CoppiaID coppia : fermateDaCollegare) {
			this.grafo.addEdge(fermateIdMap.get(coppia.getIdPartenza()), fermateIdMap.get(coppia.getIdArrivo()));
		}

//		System.out.println(this.grafo);
//		System.out.println("Vertici = " + this.grafo.vertexSet().size());
//		System.out.println("Archi   = " + this.grafo.edgeSet().size());
	}

	public List<Fermata> calcolaPercorso(Fermata partenza, Fermata arrivo) {
		creaGrafo() ;
		Map<Fermata, Fermata> alberoInverso = visitaGrafo(partenza);
		//creo lista che contiene il solo vertice di arrivo, che ogni volta prende il vertice precedente
		Fermata corrente = arrivo;
		List<Fermata> percorso = new ArrayList<>();
		
		//iterazione: finchè non sono arrivato alla radice (null = predecessore messo nella mappa del nodo di partenza)
		//ogni volta che trovo un vertice corrente lo aggiungo alla lista, poi passo al nodo precedente e così via.
		while( corrente!=null ) {
			percorso.add(corrente);
			corrente = alberoInverso.get(corrente);
			//corrente = getParent(corrente); ---> non devo implementare il Listener
		}
		return percorso ;
	}
	
	public Map<Fermata, Fermata> visitaGrafo(Fermata partenza) {
		//uso numero minimo di archi a partire dall'insieme di partenza:
		GraphIterator<Fermata, DefaultEdge> visita = new BreadthFirstIterator<>(this.grafo, partenza); 
		
		Map<Fermata,Fermata> alberoInverso = new HashMap<>() ;
		alberoInverso.put(partenza, null) ;
		
		visita.addTraversalListener(new RegistraAlberoDiVisita(alberoInverso, this.grafo));
		//albero inverso: dato un vertice il suo predecessore è uno solo (rappre univoca) + non ho bisogno di algoritmo ricorsivo per trovare
		//il percorso, come farei con un albero normale, che ha tanti successori e ad ogni bivio avrei più possibilità da scegliere.
		while (visita.hasNext()) {
			Fermata f = visita.next();
//			System.out.println(f);
		}
		return alberoInverso;
		// Ricostruiamo il percorso a partire dall'albero inverso (pseudo-code)
//		List<Fermata> percorso = new ArrayList<>() ;
//		fermata = arrivo
//		while(fermata != null)
//			fermata = alberoInverso.get(fermata)
//			percorso.add(fermata)
		
		//problema di questo algoritmo: non conosce arrivo ---> procedura di visita fatta da calcolaPercorso
	}

}
