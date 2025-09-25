// Pacote onde está localizada a classe de segurança
package com.example.estacionamento.Auth;

// Importações necessárias
import com.example.estacionamento.Auth.JwtUtil;
import com.example.estacionamento.Entity.Usuario;
import com.example.estacionamento.Repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

// Anotação que marca esse filtro como um componente do Spring (será gerenciado e injetado automaticamente)
@Component

// Lombok: gera um construtor com todos os campos finais (jwtUtil e userRepository)
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    // Classe utilitária para operações com JWT (gerar, validar, extrair info)
    private final JwtUtil jwtUtil;

    // Repositório que permite buscar usuários pelo e-mail
    private final UsuarioRepository userRepository;

    // Este método é executado automaticamente a cada requisição
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Extrai o cabeçalho Authorization da requisição
        final String authHeader = request.getHeader("Authorization");

        String username = null; // vai armazenar o e-mail extraído do token
        String jwt = null;      // vai armazenar o token JWT

        // Verifica se o cabeçalho existe e começa com "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7); // Remove "Bearer " para pegar só o token
            username = jwtUtil.extractUsername(jwt); // Extrai o e-mail do token
        }

        // Se o username foi extraído com sucesso e o usuário ainda não está autenticado
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Busca o usuário no banco de dados
            Usuario user = userRepository.findByEmail(username).orElse(null);

            // Se o usuário foi encontrado
            if (user != null) {
                // Cria um objeto User do Spring Security com as credenciais
                User springUser = new User(user.getEmail(), user.getSenha(), new ArrayList<>());

                // Verifica se o token é válido para este usuário
                if (jwtUtil.validateToken(jwt, springUser)) {

                    // Cria um token de autenticação válido com base no usuário
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    springUser, null, springUser.getAuthorities());

                    // Define o usuário autenticado no contexto do Spring Security
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }

        // Continua o fluxo da requisição (chama o próximo filtro ou controlador)
        filterChain.doFilter(request, response);
    }
}
