package com.pallette.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.pallette.web.security.CustomAuthenticationProvider;
import com.pallette.web.security.CustomCredentialsService;
import com.pallette.web.security.LogoutSuccessHandler;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private CustomCredentialsService customCredentialsService;
	
	@Autowired
	private CustomAuthenticationProvider customAuthenticationProvider;
	
	@Autowired
	private LogoutSuccessHandler logoutSuccessHandler;
	
    @Bean
	public PasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder();
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// TODO Auto-generated method stub
        http    
        .csrf()
        	.ignoringAntMatchers("/rest/**")
        	.and()
        .authorizeRequests()
            .antMatchers("/", "/registration","/rest/**").permitAll()
            .anyRequest().authenticated()
            .and()
        .formLogin()
            .loginPage("/login")
            .loginProcessingUrl("/login")
            .permitAll()
            .and()
        .logout()
        .logoutSuccessHandler(logoutSuccessHandler)
            .permitAll();
	}
	
	@Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers("/webjars/**")
                .antMatchers("/images/**").antMatchers("/css/**").antMatchers("/js/**");
    }
	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(customAuthenticationProvider);
		auth.userDetailsService(customCredentialsService);
	}
}
