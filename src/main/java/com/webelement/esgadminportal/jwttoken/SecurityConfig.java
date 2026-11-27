package com.webelement.esgadminportal.jwttoken;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	@Autowired
	private JwtRequestFilter jwtFilter;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf().disable().cors() // Enable CORS
				.and().authorizeRequests().antMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Allow preflight
				.antMatchers("/auth/**").permitAll() // Login & Refresh token
			//	.antMatchers("/admin/**").permitAll()
				.antMatchers("/authuser/**").permitAll().anyRequest().authenticated() // Protect all other APIs
				.and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

		http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}
