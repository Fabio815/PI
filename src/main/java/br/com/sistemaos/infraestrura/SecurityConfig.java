package br.com.sistemaos.infraestrura;

import br.com.sistemaos.infraestrura.security.UsuarioDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UsuarioDetailsService usuarioDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authorizeHttpRequests(auth -> auth
                        // Front-end (arquivos estáticos do ExtJS) e login/recuperação de senha ficam livres
                        .requestMatchers(
                                "/", "/index.html", "/app.js", "/bootstrap.js", "/bootstrap.css",
                                "/favicon.ico", "/*.json", "/*.jsonp",
                                "/resources/**", "/ext/**", "/classic/**", "/modern/**", "/app/**", "/build/**"
                        ).permitAll()
                        .requestMatchers("/usuarios/login", "/auth/**").permitAll()
                        .requestMatchers("/usuarios/sessao", "/usuarios/logout").authenticated()
                        // Só ADM mexe em cadastro/listagem de usuários
                        .requestMatchers("/usuarios/**").hasAuthority("ROLE_ADM")
                        // Tudo mais (os, cliente, peca) só exige estar logado;
                        // a regra "só o dono edita" é tratada dentro do OsService.
                        .anyRequest().authenticated()
                )
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint((request, response, ex) ->
                                response.sendError(401, "Não autenticado"))
                        .accessDeniedHandler((request, response, ex) ->
                                response.sendError(403, "Sem permissão"))
                );

        return http.build();
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public AuthenticationManager authenticationManager(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(usuarioDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
