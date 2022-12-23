package com.dtnsbike.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.dtnsbike.dao.interfaces.AccountsDAO;
import com.dtnsbike.dao.interfaces.AuthoritiesDAO;
import com.dtnsbike.entity.Accounts;
import com.dtnsbike.entity.Types;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;



@Service
public class UserService implements UserDetailsService {

	@Autowired
	SessionService sessionService;

	@Autowired
	AccountsDAO accountDAO;

	@Autowired
	AuthoritiesDAO authoritiesDAO;

	@Autowired
	BCryptPasswordEncoder pe;
	
	@Autowired
	SessionService session;
	
	@Autowired
	RestApiService api;
	
	@Autowired
	ObjectMapper mapper;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		BCryptPasswordEncoder pe = new BCryptPasswordEncoder();
		Accounts accounts = null;
		String pass = null;
		String[] roles = null;
		try {
			accounts = accountDAO.findByIdAndActiveTrue(username, true).get();
			pass = accounts.getPassword();
			roles = accounts.getAuthorities().stream().filter(a -> a.getActive()).map(map -> map.getRoleId().getId()).collect(Collectors.toList())
					.toArray(new String[0]);
            UserDetails userDetails = User.withUsername(username).password(pe.encode(pass)).roles(roles).build();
            List<Types> list = new ArrayList<>();
    		TypeReference<List<Types>> type = new TypeReference<List<Types>>() {
    		};
    		list = mapper.readValue(api.get("/DTNsBike/rest/types").toString(), type);
    		session.set("listTypes", list);
    		session.set("account", accountDAO.findById(accounts.getUsername()));
			return userDetails;
		} catch (Exception e) {
			if (accounts == null) {
				System.out.println("[==========| Username: " + username + " not found in database! |==========]");
			}
			if (pass == null) {
				System.out.println("[==========| Passwords: Value is null! |==========]");
			}
			if (roles == null) {
				System.out.println("[==========| Roles: Value is null! |==========]");
			}
			throw new UsernameNotFoundException(null);
		}
	}

}
