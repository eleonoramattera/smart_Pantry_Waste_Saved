package com.example.smart_pantry_waste_saver.entity;

public enum StatoIngrediente {

    CONSUMATO("Consumato"),
    IN_SCADENZA("In scadenza"),// lo calcolo in automatica
    SCADUTO("Scaduto"), // lo calcolo in automatico
    CONGELATO("In freezer"),
	FRESCO("fresco"); 
	
	private final String descrizione;
	
	StatoIngrediente(String descrizione) {
		this.descrizione = descrizione;
	}
	
	public String getDescrizione() {
		return descrizione;
	}
}
