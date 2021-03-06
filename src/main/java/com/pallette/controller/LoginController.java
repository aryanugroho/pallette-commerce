package com.pallette.controller;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.pallette.domain.Account;
import com.pallette.domain.AuthenticationRequest;
import com.pallette.exception.SymbolNotFoundException;
import com.pallette.service.UserService;

@Controller
public class LoginController {
	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
	
	@Autowired
	private UserService accountService;
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String showHome(Model model) throws SymbolNotFoundException {
		if (!model.containsAttribute("login")) {
			model.addAttribute("login", new AuthenticationRequest());
		}
		
		//check if user is logged in!
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!(authentication instanceof AnonymousAuthenticationToken)) {
		    String currentUserName = authentication.getName();
		    logger.debug("User logged in: " + currentUserName);
		    model.addAttribute("account",accountService.getAccount(currentUserName));
		}
		
		return "index";
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	//@RequestMapping(value = "/login")
	public String login(Model model, @ModelAttribute(value="loginForm")AuthenticationRequest login) {
		logger.info("Logging in, user: " + login.getUsername());
		//need to add account object to session?
		//CustomDetails userDetails = (CustomDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		logger.debug("Principal: " + SecurityContextHolder.getContext().
				   getAuthentication().getPrincipal());
		Map<String,Object> params = accountService.login(login);
		model.addAllAttributes(params);
		//logger.info("got user details, token: " + userDetails.getToken());
		return "index";
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String getLogin(Model model, @ModelAttribute(value="loginForm") AuthenticationRequest login) {
		logger.info("Logging in GET, user: " + login.getUsername());
		return "login";
	}
	
	@RequestMapping(value="/logout", method = RequestMethod.POST)
	public String postLogout(Model model, @ModelAttribute(value="login") AuthenticationRequest login) {
		logger.info("Logout, user: " + login.getUsername());
		logger.info(model.asMap().toString());
		return "index";
	}
	
	@RequestMapping(value = "/registration", method = RequestMethod.GET)
	public String registration(Model model) {
		Account account = new Account();
		model.addAttribute("account", account);
		return "registration";
	}

	@RequestMapping(value = "/registration", method = RequestMethod.POST)
	public String register(Model model, @ModelAttribute(value="account") Account account) {
		logger.info("register: user:" + account.getUsername());
		
		//need to set some stuff on account...
		
		account.setCreationdate(new Date());
		
		AuthenticationRequest login = new AuthenticationRequest();
		login.setUsername(account.getUsername());
		model.addAttribute("login", login);
		accountService.createAccount(account);
		return "index";
	}
	@ExceptionHandler({ Exception.class })
	public ModelAndView error(HttpServletRequest req, Exception exception) {
		logger.debug("Handling error: " + exception);
		logger.warn("Exception:", exception);
		ModelAndView model = new ModelAndView();
		model.addObject("errorCode", exception.getMessage());
		model.addObject("errorMessage", exception);
		model.setViewName("error");
		return model;
	}
}
