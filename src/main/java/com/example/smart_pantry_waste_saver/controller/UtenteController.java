package com.example.smart_pantry_waste_saver.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.smart_pantry_waste_saver.entity.Utente;
import com.example.smart_pantry_waste_saver.service.UtenteService;
import com.example.smart_pantry_waste_saver.dto.PasswordUpdateRequest;
import com.example.smart_pantry_waste_saver.dto.UtenteCreateRequest;
import com.example.smart_pantry_waste_saver.dto.UtenteResponse;
import com.example.smart_pantry_waste_saver.dto.UtenteUpdateRequest;

@RestController
@RequestMapping("/api/utenti")
public class UtenteController {

    @Autowired
    private UtenteService utenteService;

    @GetMapping
    public ResponseEntity<List<UtenteResponse>> getAllUtenti() {
        List<UtenteResponse> utenti = utenteService.findAllUtenti();
        return ResponseEntity.ok(utenti);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UtenteResponse> getUtenteById(@PathVariable Long id) {
        UtenteResponse utente =utenteService.findUtenteById(id);
        return ResponseEntity.ok(utente);
    }

    @PostMapping
    public ResponseEntity<UtenteResponse> createUtente(@RequestBody UtenteCreateRequest request) {
    	UtenteResponse nuovoUtente = utenteService.creaUtente(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuovoUtente); //stato 201
    }

    @PutMapping("/{id}")
    public ResponseEntity<UtenteResponse> updateUtente(@PathVariable Long id, @RequestBody UtenteCreateRequest request) {
    	UtenteResponse utenteAggiornato = utenteService.updateUtente(id, request);
        return ResponseEntity.ok(utenteAggiornato);
    }
   
	@PatchMapping("/{id}")
	public ResponseEntity<UtenteResponse> aggiornaParzialmenteUtente(@PathVariable Long id, @RequestBody UtenteUpdateRequest request) {
	    UtenteResponse utenteModificato = utenteService.partiallyUpdateUtente(id, request);
	    return ResponseEntity.ok(utenteModificato);
	}

	@PatchMapping("/{id}/password")
	public ResponseEntity<Void> updatePassword(@PathVariable Long id, @RequestBody PasswordUpdateRequest request) {
	    utenteService.updatePassword(id, request);
	    return ResponseEntity.noContent().build();
	}

    @DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminaUtente(@PathVariable Long id) {
	    utenteService.eliminaUtente(id);
	    return ResponseEntity.noContent().build();
	}


}
