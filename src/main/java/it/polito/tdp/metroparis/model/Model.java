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
import org.jgrapht.traverse.GraphIterator;

import it.polito.tdp.metroparis.db.MetroDAO;

public class Model {
	
	private List<Fermata> fermate;
	private Graph<Fermata,DefaultEdge> grafo;
	private Map<Integer, Fermata> fermateIdMap;
	//controller non deve conoscere il grafo ---> creaGrafo() possiamo metterlo private
	public List<Fermata> getFermate(){
		if(this.fermate == null) {
			MetroDAO dao = new MetroDAO();
			this.fermate = dao.getAllFermate();
			fermateIdMap = new HashMap<Integer,Fermata>();
			//popolo la mappa:
			for(Fermata f : this.fermate) {
				fermateIdMap.put(f.getIdFermata(), f);
			}
		}
		return this.fermate;
	}
	
	public List<Fermata> calcolaPercorso(Fermata partenza, Fermata arrivo){
		//bisogna creare il grafo, visitare il grafo e poi stamparlo
		creaGrafo();
		visitaGrafo(partenza);
		
		return null;
	}
	
	public void creaGrafo() {
		// creare la struttura dati grafo
		this.grafo = new SimpleDirectedGraph<Fermata,DefaultEdge>(DefaultEdge.class);
		
		Graphs.addAllVertices(this.grafo, this.getFermate());
		
		MetroDAO dao = new MetroDAO();
		//METODO 1 : itero su ogni coppia di vertici
		/*for(Fermata partenza: fermate) {
			for(Fermata arrivo: fermate) {
				if(dao.isFermateConnesse(partenza, arrivo)) { //se esiste almeno una connessione fra partenza e arrivo, aggiungo l'arco
					this.grafo.addEdge(partenza, arrivo); // molto lento: così sta facendo tante query ogni volta ---> quasi 400000 esecuzioni della
														  // query
					//alternativa: prendo una sola fermata e guardo quali siano le connessioni: query che data una fermata di partenza darà
					//l'elenco delle fermate di arrivo. ---> diminuisce il numero di query, una sola per ogni vertice ---> 619 query
				}
			}
		}*/
		
		//METODO 2 : dato ciascun vertice, trova quelli ad esso adiacenti; il DAO restituisce un elenco di ID numerici
		//Nota: posso iterare su fermate o su this.grafo.vertexSet()
		/*for(Fermata partenza : fermate) {
			List<Integer> idConnesse = dao.getIdFermateConnesse(partenza); //ottengo gli id delle altre fermate connesse
			for(Integer id : idConnesse) {
				Fermata arrivo = null; //fermata che possiede quell'id;
				for(Fermata f : fermate) { 
					if(f.getIdFermata() == id) {
						arrivo = f;
						break;
					}
				}
				this.grafo.addEdge(partenza, arrivo);
			}
		}*/
		//METODO 2b: DAO restituisce elenco di oggetti Fermata
		/*for(Fermata partenza : fermate) {
			List<Fermata> arrivi = dao.getFermateConnesse(partenza); //ottengo gli id delle altre fermate connesse
			for(Fermata a : arrivi)
				this.grafo.addEdge(partenza, a); //primo oggetto è già presente nel grafo; secondo oggetto non esiste nel grafo.
		}*/
		//METODO 2c: DAO restituisce elenco di id numerici che converto in oggetti tramite una Map, che mappa gli Integer sulla Fermata
		//Pattern: IDENTITY MAP.
		/*for(Fermata partenza : fermate) {
			List<Integer> idConnesse = dao.getIdFermateConnesse(partenza);
			for(Integer id : idConnesse) {
				Fermata arrivo = fermateIdMap.get(id); //così non creo mai dei doppioni di oggetti.
				this.grafo.addEdge(partenza, arrivo);
			}
		}*/
		//METODO 3: faccio una sola query che mi restituisca le coppie di fermate da collegare.
		//Può tirare fuori coppie di ID o di OGGETTI interi ---> preferisco usare Identity Map (variante preferita)
		List<CoppiaID> fermateDaCollegare = dao.getAllFermateConnesse();
		for(CoppiaID c : fermateDaCollegare)
			this.grafo.addEdge(fermateIdMap.get(c.getIdPartenza()), fermateIdMap.get(c.getIdArrivo()));
		//System.out.println(this.grafo);
		//System.out.println("Vertici = "+this.grafo.vertexSet().size());
		//System.out.println("Archi = "+this.grafo.edgeSet().size());
		
		//visitaGrafo(fermate.get(0));
	}
	
	public void visitaGrafo(Fermata partenza) {
		GraphIterator<Fermata, DefaultEdge> visita = new BreadthFirstIterator<>(this.grafo,partenza);
		
		Map<Fermata, Fermata> alberoInverso = new HashMap<>();
		alberoInverso.put(partenza, null); //il nodo di partenza non ha "genitore"
		visita.addTraversalListener(new RegistraAlberoDiVisita(alberoInverso,this.grafo));
		while(visita.hasNext()) {
			Fermata f = visita.next();
			//System.out.println(f);
		}
		
		List<Fermata> percorso = new ArrayList<>();
		//da arrivo, con un ciclo while ripeto operazione:
		while(fermata != null) {
			fermata = alberoInverso(fermata);
		}
	}


}
