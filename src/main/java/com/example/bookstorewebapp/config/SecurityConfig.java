package com.example.bookstorewebapp.config;


import com.example.bookstorewebapp.security.MfaFilter;
import com.example.bookstorewebapp.logging.MdcFilter;
import com.example.bookstorewebapp.service.CustomUserDetailsService;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity



public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }

    private final MdcFilter mdcFilter;
    private final MfaFilter mfaFilter;

    // <-- if you don’t have MFA yet, delete this field & its uses

    // If you DON'T have MfaFilter, use a constructor with only MdcFilter
    public SecurityConfig(MdcFilter mdcFilter, MfaFilter mfaFilter) {
        this.mdcFilter = mdcFilter;
        this.mfaFilter = mfaFilter;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();

    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService())
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // (keep your HTTPS gating)
// inside securityFilterChain(...)
       // http.addFilterAfter(mfaFilter, UsernamePasswordAuthenticationFilter.class);

        // constructor-inject MdcFilter mdcFilter (similar to MfaFilter)
        http.addFilterBefore(mdcFilter, UsernamePasswordAuthenticationFilter.class);

        http
                // make sure this line is present to stop “save request /error”
                .requestCache(rc -> rc.disable())

                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
                .authorizeHttpRequests(auth -> auth
                        // explicitly permit error endpoints
                        .requestMatchers("/error", "/error/**").permitAll()
                        .requestMatchers("/", "/register", "/login", "/books", "/css/**", "/mfa/**", "/h2-console/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/books", true)
                        .permitAll())
                .logout(lo -> lo.logoutSuccessUrl("/login?logout").permitAll());

        return http.build();
    }
}
