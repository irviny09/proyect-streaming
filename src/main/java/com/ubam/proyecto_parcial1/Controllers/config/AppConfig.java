package com.ubam.proyecto_parcial1.Controllers.config;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;import com.ubam.proyecto_parcial1.Models.Usuario;
import com.ubam.proyecto_parcial1.Repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class AppConfig {

    private final UsuarioRepository repository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public UserDetailsService userDetailsService(){
        return username -> {
            final Usuario usuario = repository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado")); 
            return org.springframework.security.core.userdetails.User.builder()
                    .username(usuario.getEmail())
                    .password(usuario.getPassword())
                    .roles(usuario.getRol().getNombre())
                    .build();
        };
    }

    

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) 
            .authorizeHttpRequests(auth -> auth
                // ASEGÚRATE DE QUE ESTA LÍNEA ESTÉ CORRECTA
                .requestMatchers("/auth/**").permitAll() 
                .anyRequest().authenticated()
            );

        return http.build();
    }
}
