package com.example.mh_3.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    @Autowired
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // Временно отключаем CSRF для отладки
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/home", "/about", "/css/**", "/js/**", 
                               "/register", "/register/**", "/login", "/login/**", "/images/**").permitAll()
                .requestMatchers("/admin/users/add").hasRole("ADMIN")
                .requestMatchers("/admin/patient/my-doctors").hasRole("PATIENT")
                .requestMatchers("/admin/doctor/patients").hasRole("DOCTOR")
                .requestMatchers(HttpMethod.POST, "/admin/appointments/create").hasRole("DOCTOR")
                .requestMatchers(HttpMethod.POST, "/admin/appointments/{id}/delete").hasRole("DOCTOR")
                .requestMatchers(HttpMethod.POST, "/admin/appointments/{id}/complete").hasRole("DOCTOR")
                .requestMatchers("/admin/doctor/my-appointments").hasRole("DOCTOR")
                .requestMatchers("/admin/doctor/completed-appointments").hasRole("DOCTOR")
                .requestMatchers("/admin/patient/my-appointments").hasRole("PATIENT")
                .requestMatchers("/admin/patient").hasRole("PATIENT")
                .requestMatchers("/admin/patient/doctors").hasRole("PATIENT")
                .requestMatchers("/admin/patient/completed-appointments").hasRole("PATIENT")
                .requestMatchers("/admin/newborns/add").hasAnyRole("ADMIN", "DOCTOR")
                .requestMatchers("/admin/newborns").hasAnyRole("ADMIN", "DOCTOR")
                .requestMatchers("/admin/newborns/{id}/delete").hasAnyRole("ADMIN", "DOCTOR")
                .requestMatchers("/admin/patient/my-newborns").hasRole("PATIENT")
                .requestMatchers(HttpMethod.POST, "/admin/patient/appointments/create-with-doctor/{doctorId}").hasRole("PATIENT")
                .requestMatchers(HttpMethod.POST, "/admin/patient/appointments/{id}/delete").hasRole("PATIENT")
                .requestMatchers("/admin").authenticated()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/doctor/**").hasRole("DOCTOR")
                .requestMatchers("/patient/**").hasRole("PATIENT")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(customAuthenticationSuccessHandler)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
} 