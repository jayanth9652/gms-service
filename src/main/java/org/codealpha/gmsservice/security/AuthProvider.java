package org.codealpha.gmsservice.security;

import java.util.ArrayList;
import org.codealpha.gmsservice.entities.Organization;
import org.codealpha.gmsservice.entities.User;
import org.codealpha.gmsservice.exceptions.InvalidTenantException;
import org.codealpha.gmsservice.services.OrganizationService;
import org.codealpha.gmsservice.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class AuthProvider implements AuthenticationProvider {

  @Autowired
  private UserService userService;
  @Autowired
  private OrganizationService organizationService;


  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String role = authentication.getAuthorities().iterator().next().getAuthority();
    String username = authentication.getName();
    String password = authentication.getCredentials().toString();
    String tenantCode = (String)authentication.getDetails();
    authentication.getDetails();
    if ("USER".equalsIgnoreCase(role.toUpperCase())) {
      Organization org = organizationService.fingOrganizationByCode(tenantCode);
      if(org==null){
        throw new BadCredentialsException("Invalid tenant code "+ tenantCode);
      }
      User user = userService.getUserByEmail(username);

      if (password.equalsIgnoreCase(user.getPassword())
          && username.equalsIgnoreCase(user.getEmailId())) {

        return new UsernamePasswordAuthenticationToken(username, password, new ArrayList());
      } else {
        //TODO - Read messages from a resource bundle
        throw new BadCredentialsException("User Name and password does not match.");
      }
    }

    return null;
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
  }

}
