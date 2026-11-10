package com.example.smart_pantry_waste_saver.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    //Recupera la chiave segreta dal file application.properties
    @Value("${app.jwt-secret}")
    private String secretKey;

    // Recupera la durata del token dal file application.properties
    @Value("${app.jwt-expiration-milliseconds}")
    private long jwtExpiration;

    // Converte la stringa segreta in una chiave utilizzabile dall'algoritmo di cifratura
    private Key getSignInKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    //PER GENERARE IL TOKEN
    
    // Prende i dati dell'utente (UserDetails) e crea il token stampando dentro lo username e la scadenza
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername()) // Mette il nome utente dentro il token
                .setIssuedAt(new Date(System.currentTimeMillis())) // Data di creazione
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration)) // Data di scadenza
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // Firma il token con la chiave segreta
                .compact();
    }

    //PER LEGGERE IL NOME DENTRO IL TOKEN
    
    // Prende un token e ci dice a quale utente appartiene
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    //PER VALIDARE IL TOKEN
    
    // Controlla se il token appartiene all'utente giusto e se non è scaduto
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Smonta il token usando la chiave segreta per leggerne il contenuto interno
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}

