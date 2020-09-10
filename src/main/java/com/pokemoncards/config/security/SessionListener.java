package com.pokemoncards.config.security;

import java.util.Map;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;

@WebListener
public class SessionListener implements HttpSessionListener {

	@Autowired
	private Map<UserDetails, Object> usersLockers;

	@Autowired
	private SessionRegistry sessionRegistry;

	@Override
	public void sessionDestroyed(HttpSessionEvent sessionEvent) {
		HttpSession session = sessionEvent.getSession();
		SecurityContext context = ((SecurityContext) session.getAttribute("SPRING_SECURITY_CONTEXT"));
		if (context != null) {
			Object principal = context.getAuthentication().getPrincipal();
			sessionRegistry.removeSessionInformation(session.getId());
			synchronized (principal) {
				if (sessionRegistry.getAllSessions(principal, true).size() == 0)
					usersLockers.remove(principal);
			}
		}
	}
}
