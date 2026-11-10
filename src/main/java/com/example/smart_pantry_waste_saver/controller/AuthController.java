package com.example.smart_pantry_waste_saver.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.example.smart_pantry_waste_saver.dto.LoginRequest;
import com.example.smart_pantry_waste_saver.dto.RegisterRequest;
import com.example.smart_pantry_waste_saver.entity.RuoloUtente;
import com.example.smart_pantry_waste_saver.entity.Utente;
import com.example.smart_pantry_waste_saver.repository.UtenteRepository;
import com.example.smart_pantry_waste_saver.security.jwt.JwtService;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final UtenteRepository utenteRepository;
    private final PasswordEncoder passwordEncoder; 
    
    public AuthController(AuthenticationManager authenticationManager, UserDetailsService userDetailsService, JwtService jwtService, UtenteRepository utenteRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
        this.utenteRepository = utenteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // verifica se l'email e la password inserite sono corrette
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        // qui controlla quello che inserisco su postman
        //request.getEmail() quindi nel json su postman devo scrivere nel body
//        {
//        "email": "admin",
//        "password": "1111"
//   	   }
        
        // Se le credenziali sono corrette, recuperiamo i dati dell'utente dal db
        var userDetails = userDetailsService.loadUserByUsername(request.getUsername());

        //Genaer il token JWT personalizzato per questo utente
        String token = jwtService.generateToken(userDetails);

        //Restituisce il token al client dentro un oggetto JSON
        return ResponseEntity.ok(Map.of("token", token));
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register( @RequestBody RegisterRequest request) {

        if (request.getUsername() == null
            || request.getUsername().isBlank()
            || request.getPassword() == null
            || request.getPassword().isBlank()) {

            return ResponseEntity.badRequest().body(Map.of("message", "Username e password sono obbligatori"));
            }

        String username = request.getUsername().trim();

		if (utenteRepository.existsByUsername(username)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Username già utilizzato"));
        }

		Utente utente = new Utente();

		utente.setNome(request.getNome());
		utente.setCognome(request.getCognome());
		utente.setEmail(request.getEmail());
		utente.setUsername(request.getUsername().trim());
		utente.setPassword(passwordEncoder.encode(request.getPassword()));

		utente.setRuolo(RuoloUtente.USER); 
        utenteRepository.save(utente);

        return ResponseEntity.status(HttpStatus.CREATED) .body(Map.of("message", "Utente registrato con successo"));
    }
}
