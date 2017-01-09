package com.pallette.web.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import com.pallette.service.UserService;

import com.pallette.domain.AuthenticationRequest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class CustomAuthenticationProvider implements AuthenticationProvider {

	@Autowired
	private UserService service;
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String name = authentication.getName();
		String password = authentication.getCredentials().toString();
		AuthenticationRequest request = new AuthenticationRequest();
		request.setUsername(name);
		request.setPassword(password);
		try {
			Map<String, Object> params = service.login(request);
			if (params != null) {
				List<GrantedAuthority> grantedAuths = new ArrayList<>();
				grantedAuths.add(new SimpleGrantedAuthority("USER"));
				Authentication auth = new UsernamePasswordAuthenticationToken(
						name, password, grantedAuths);
				return auth;
			} else {
				throw new BadCredentialsException("Username not found");
			}
		} catch (HttpServerErrorException e) {
			throw new BadCredentialsException("Login failed!");
		}
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}
	
}
