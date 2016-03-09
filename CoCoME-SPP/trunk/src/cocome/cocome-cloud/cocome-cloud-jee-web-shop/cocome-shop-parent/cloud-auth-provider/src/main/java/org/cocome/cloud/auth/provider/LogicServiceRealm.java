package org.cocome.cloud.auth.provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.xml.ws.WebServiceRef;

import org.cocome.logic.stub.ILoginManager;
import org.cocome.logic.stub.LoginManagerService;
import org.cocome.logic.stub.NotInDatabaseException_Exception;
import org.cocome.logic.stub.Role;

import com.sun.appserv.security.AppservRealm;
import com.sun.enterprise.security.auth.realm.BadRealmException;
import com.sun.enterprise.security.auth.realm.InvalidOperationException;
import com.sun.enterprise.security.auth.realm.NoSuchRealmException;
import com.sun.enterprise.security.auth.realm.NoSuchUserException;

/**
 * Implements a realm for the authentication of users with the CoCoME login service.
 * This class is needed by GlassFish.
 * 
 * @author Tobias Pöppke
 * @author Robert Heinrich
 */
public class LogicServiceRealm extends AppservRealm {
	private String jaasCtxName;
	
	LoginManagerService managerService = new LoginManagerService();
	ILoginManager loginManager = managerService.getLoginManagerPort();
	
	@Override
	public void init(Properties properties) throws BadRealmException, NoSuchRealmException {
		jaasCtxName = properties.getProperty("jaas-context", "cocomeLogicRealm");
		// TODO add credentials to access the service backend 
	}
	
	@Override
	public String getJAASContext() {
		return jaasCtxName;
	}
	
	@Override
	public String getAuthType() {
		return "CoCoME LogicServiceRealm";
	}

	@Override
	public Enumeration<String> getGroupNames(String username) throws InvalidOperationException, NoSuchUserException {
		List<Role> roles;
		try {
			roles = loginManager.getUserRoles(username);
		} catch (NotInDatabaseException_Exception e) {
			throw new NoSuchUserException(e.getMessage());
		}
		
		List<String> rolesList = new ArrayList<>(roles.size());
		
		for (Role role : roles) {
			rolesList.add(role.value());
		}
		
		return Collections.enumeration(rolesList);
		
		/*
		 * This is only possible with Java 8:
		 * 
		 * return Collections.enumeration(
		 * 	roles.stream().map(r -> r.value()).collect(Collectors.toList()));
		 */
	}

}
