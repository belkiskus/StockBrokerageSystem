package com.brokagefirm.StockBrokerageSystem.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // ✅ Tüm isteklere izin ver
                )
                //.authorizeHttpRequests(auth -> auth
                        //.requestMatchers("/h2-console/**").permitAll()  // Geliştirme aşamasında H2 Console'a erişim izni
                        //.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").authenticated() // Swagger güvenlik kuralı
                        //.requestMatchers("/api/admin/**").hasRole("ADMIN")  // Admin için özel endpointler
                        //.requestMatchers("/api/orders/**").hasAnyRole("ADMIN", "USER")  // Siparişler için genel erişim
                        //.anyRequest().permitAll()  // Diğer tüm istekler kimlik doğrulaması gerektirir
                //)
                .csrf(csrf -> csrf.disable()) // Eğer form veya frontend kullanmıyorsan kapatabilirsin
                .headers(headers -> headers.frameOptions(frame -> frame.disable())); // H2 Console için

        return http.build();
    }

/*    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll()  // Geliştirme aşamasında H2 Console'a erişim izni
                        //.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").authenticated() // Swagger güvenlik kuralı
                        //.requestMatchers("/api/admin/**").hasRole("ADMIN")  // Admin için özel endpointler
                        //.requestMatchers("/api/orders/**").hasAnyRole("ADMIN", "USER")  // Siparişler için genel erişim
                        .anyRequest().permitAll()  // Diğer tüm istekler kimlik doğrulaması gerektirir
                )
                .httpBasic(Customizer.withDefaults())
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(denemeservice);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
*/
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
