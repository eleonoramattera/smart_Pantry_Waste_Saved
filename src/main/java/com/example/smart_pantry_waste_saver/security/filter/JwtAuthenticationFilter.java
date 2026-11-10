package com.example.smart_pantry_waste_saver.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.smart_pantry_waste_saver.security.jwt.JwtService;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    // Connettiamo JwtService e il gestore degli utenti di Spring
    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        
        //Prende l'header Authorization dalla richiesta HTTP
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        //Se l'header non c'è o non inizia con Bearer, ignora questo filtro e va avanti
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Estrae il token (taglia i primi 7 caratteri di Bearer)
        jwt = authHeader.substring(7).trim();

        try {
            username = jwtService.extractUsername(jwt);
        } catch (Exception e) {
            System.out.println("JWT ERROR: " + e.getMessage());
            System.out.println("TOKEN PARTS: " + jwt.split("\\.", -1).length);
            throw e;
        }
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        	System.out.println("USERNAME EXTRACTED: " + username);
        	System.out.println("JWT FILTER: extraction completed");
        }

        // 5. Se c'è un username e l'utente non è ancora stato autenticato nel sistema
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            // Cerca l'utente nel nel db
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            
            // 6. Se il token è valido, crea il "pass di accesso" per Spring Security
            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                //da ora in poi l'utente è loggato per questa richiesta
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        //la richiesta prosegue verso l'API finale o verso il filtro successivo
        filterChain.doFilter(request, response);
    }
}

