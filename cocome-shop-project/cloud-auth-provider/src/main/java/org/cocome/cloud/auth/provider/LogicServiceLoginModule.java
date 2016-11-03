package org.cocome.cloud.auth.provider;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.security.auth.login.LoginException;
import org.cocome.cloud.logic.stub.ILoginManager;
import org.cocome.cloud.logic.stub.ILoginManagerService;
import org.cocome.cloud.logic.stub.NotInDatabaseException_Exception;
import org.cocome.tradingsystem.inventory.application.usermanager.CredentialTO;
import org.cocome.tradingsystem.inventory.application.usermanager.CredentialType;
import org.cocome.tradingsystem.inventory.application.usermanager.Role;
import org.cocome.tradingsystem.inventory.application.usermanager.UserTO;
import org.glassfish.security.common.PrincipalImpl;

import com.sun.enterprise.security.BasePasswordLoginModule;

/**
 * Implements the authentication of users with the CoCoME login service.
 * This class is an extension of GlassFish's login capabilities.
 * 
 * @author Tobias Pöppke
 * @author Robert Heinrich
 */
public class LogicServiceLoginModule extends BasePasswordLoginModule {
	ILoginManagerService managerService = new ILoginManagerService();
	ILoginManager loginManager = managerService.getILoginManagerPort();
	
	@Override
	protected void authenticateUser() throws LoginException {
		_logger.info("CoCoME LoginModule: authenticateUser for " +
				new String(_passwd));

		UserTO user = initUserTO();
		UserTO authUser;
		
		try {
			if(loginManager.checkCredentials(user)) {
				_logger.info("User authenticated, getting user information");
				authUser = loginManager.getUserTO(user, user.getUsername());
			} else {
				throw new LoginException("Username or Password incorrect.");
			}
		} catch (NotInDatabaseException_Exception e) {
			throw new LoginException(e.getMessage());
		}
		
		Set<Principal> principals = _subject.getPrincipals();
		principals.add(new PrincipalImpl(_username));
		
		String[] grpList = extractRoleStrings(authUser.getRoles());
		_logger.info("User roles: " + Arrays.toString(grpList));
		commitUserAuthentication(grpList);
	}

	private UserTO initUserTO() {
		UserTO user = new UserTO();
		user.setUsername(_username);
		
		CredentialTO credential = new CredentialTO();
		credential.setType(CredentialType.PASSWORD);
		credential.setCredentialString(new String(_passwd));
		
		user.getCredentials().add(credential);
		return user;
	}

	private String[] extractRoleStrings(List<Role> roles) {
		String[] grpList = new String[roles.size()];
		
		for (int i = 0; i < roles.size(); i++) {
			grpList[i] = roles.get(i).label();
		}
		return grpList;
	}

}
