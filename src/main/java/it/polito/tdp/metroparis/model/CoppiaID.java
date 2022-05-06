package it.polito.tdp.metroparis.model;

public class CoppiaID {

	int idPartenza;
	int idArrivo;
	
	public CoppiaID(int idPartenza, int idArrivo) {
		super();
		this.idPartenza = idPartenza;
		this.idArrivo = idArrivo;
	}

	public int getIdPartenza() {
		return idPartenza;
	}

	public void setIdPartenza(int idPartenza) {
		this.idPartenza = idPartenza;
	}

	public int getIdArrivo() {
		return idArrivo;
	}

	public void setIdArrivo(int idArrivo) {
		this.idArrivo = idArrivo;
	}
	
	
	
}
