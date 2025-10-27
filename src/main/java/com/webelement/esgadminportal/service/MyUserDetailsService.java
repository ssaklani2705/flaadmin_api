package com.webelement.esgadminportal.service;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.webelement.esgadminportal.common.LoginRequest;


@Service
public class MyUserDetailsService implements UserDetailsService {

	LoginRequest loginReq = new LoginRequest();

	HashMap<String, String> users = new HashMap<String, String>();

	public void setUser(LoginRequest req) {
		this.loginReq = req;

	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		users.put(username, loginReq.getPassword());
		System.out.println("User requesting: " + loginReq.getUsername());
		return new User(username, users.get(username), new ArrayList<>());
	}

}