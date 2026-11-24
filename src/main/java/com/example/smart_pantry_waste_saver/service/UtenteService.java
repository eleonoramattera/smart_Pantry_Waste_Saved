package com.example.smart_pantry_waste_saver.service;

import java.util.List;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.example.smart_pantry_waste_saver.entity.Utente;
import com.example.smart_pantry_waste_saver.exception.ResourceNotFoundException;
import com.example.smart_pantry_waste_saver.exception.UserAlreadyExistsException;
import com.example.smart_pantry_waste_saver.repository.UtenteRepository;
import com.example.smart_pantry_waste_saver.dto.PasswordUpdateRequest;
import com.example.smart_pantry_waste_saver.dto.UtenteCreateRequest;
import com.example.smart_pantry_waste_saver.dto.UtenteResponse;
import com.example.smart_pantry_waste_saver.dto.UtenteUpdateRequest;

import org.springframework.security.crypto.password.PasswordEncoder;

@Service 
public class UtenteService {

	private final UtenteRepository repo;
	private final PasswordEncoder passwordEncoder;

	@Autowired @Qualifier("FakeUtenteBean") ObjectProvider<Utente> utenteFakeProvider;
	

    // Costruttore: Spring inietta automaticamente le dipendenze
    public UtenteService(UtenteRepository repo, PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }
	
	public void createFakeUtente() {
		Utente u = utenteFakeProvider.getObject();
		createUtenteByUsername(u);
	}
	
	public Utente getUtenteRandom() {
		return repo.findByUtenteRandom();
	}
	//JPA METHODS
	
	// getAll - recupera tutti gli utenti
	public List<UtenteResponse> findAllUtenti() {
	    return repo.findAll()
	            .stream()
	            .map(this::toResponse) //converte ogni Utente nel DTO corrispondente
	            .toList();
	}
	
	//getByID - recupera un utente tramite ID
	public UtenteResponse findUtenteById(Long id) {
	    Utente utente = repo.findById(id)
	        .orElseThrow(() -> new ResourceNotFoundException("Utente con ID " + id + " non trovato" ));
	    return toResponse(utente);
	}
	
	//create
	public Utente createUtenteByEmail(Utente utente) {
	 // Gestione di un errore | email già presente
	 if(repo.existsByEmail(utente.getEmail())) {
		throw new UserAlreadyExistsException("email già presente " + utente.getEmail());
	    }
	 
	 System.out.println("Utente " + utente.getNome() + " " + utente.getCognome() + " inserito nel DB");
		return repo.save(utente);
	}
	
	public Utente createUtenteByUsername(Utente utente) {
		// Gestione di un errore | email già presente
		if(repo.existsByUsername(utente.getUsername())) {
			throw new UserAlreadyExistsException("username già presente " + utente.getUsername());
		}
		
		System.out.println("Utente " + utente.getNome() + " " + utente.getCognome() + " inserito nel DB");
		return repo.save(utente);
	}
	
	public UtenteResponse updateUtente(Long id, UtenteCreateRequest request) {
	    Utente utente = repo.findById(id)
	            .orElseThrow(() -> new ResourceNotFoundException("Utente con ID " + id + " non trovato" ));
	
	    if (!utente.getEmail().equals(request.email()) && repo.existsByEmail(request.email())) {
	        throw new UserAlreadyExistsException("Email già registrata: " + request.email());
	    }
	    if (!utente.getUsername().equals(request.username()) && repo.existsByUsername(request.username())) {
	        throw new UserAlreadyExistsException("Username già utilizzato: " + request.username() );
	    }
	    
	    utente.setNome(request.nome());
	    utente.setCognome(request.cognome());
	    utente.setEmail(request.email());
	    utente.setUsername(request.username());
	
	    Utente utenteModificato = repo.save(utente);

	    return toResponse(utenteModificato);
	}


	public UtenteResponse partiallyUpdateUtente(Long id, UtenteUpdateRequest request) {
	
	    Utente utente = repo.findById(id)
	            .orElseThrow(() -> new ResourceNotFoundException("Utente con ID " + id + " non trovato"));
	
	    if (request.email() != null
	            && !utente.getEmail().equals(request.email())
	            && repo.existsByEmail(request.email())) {

	        throw new UserAlreadyExistsException("Email già registrata: " + request.email() );
	    }

	    if (request.username() != null
	            && !utente.getUsername().equals(request.username())
	            && repo.existsByUsername(request.username())) {

	        throw new UserAlreadyExistsException("Username già utilizzato: " + request.username());
	    }

	    if (request.nome() != null) {
	        utente.setNome(request.nome());
	    }
	
	    if (request.cognome() != null) {
	        utente.setCognome(request.cognome());
	    }
	
	    if (request.email() != null) {
	        utente.setEmail(request.email());
	    }
	
	    if (request.username() != null) {
	        utente.setUsername(request.username());
	    }
	
	    Utente utenteModificato = repo.save(utente);
	
	    return toResponse(utenteModificato);
	}


	public void updatePassword(Long id, PasswordUpdateRequest request) {
	    Utente utente = repo.findById(id)
	            .orElseThrow(() -> new ResourceNotFoundException("Utente con ID " + id + " non trovato"));
	
	    utente.setPassword(passwordEncoder.encode(request.password()));
	
	    repo.save(utente);
	}

	
	//remove
	public void eliminaUtente(Long id) { 
		Utente utente = repo.findById(id)
	            .orElseThrow(() -> new ResourceNotFoundException("Utente con ID " + id + " non trovato" ));
	
	    repo.delete(utente);
	}

	//CUSTOM METHODS

	public Utente getByEmail(String email) {
		return repo.findByEmail(email);
	}
  
	//il Service che recupera l'Entity dal database e la converte nel DTO da restituire al Controller.
	// Converte l'Entity in DTO
	private UtenteResponse toResponse(Utente utente) {
	    return new UtenteResponse(
	        utente.getId(),
	        utente.getNome(),
	        utente.getCognome(),
	        utente.getEmail(),
	        utente.getUsername()
	    );
	}
		

	public UtenteResponse creaUtente(UtenteCreateRequest request) {
	    if (repo.existsByEmail(request.email())) {
	        throw new UserAlreadyExistsException("Email già registrata: " + request.email());
	    }

	    if (repo.existsByUsername(request.username())) {
	        throw new UserAlreadyExistsException("Username già utilizzato: " + request.username() );
	    }
		
	    Utente utente = new Utente();
	
	    utente.setNome(request.nome());
	    utente.setCognome(request.cognome());
	    utente.setEmail(request.email());
	    utente.setUsername(request.username());
	    utente.setPassword(passwordEncoder.encode(request.password()));
	    Utente utenteSalvato = repo.save(utente);

    return toResponse(utenteSalvato);
}

	
}
