package org.codealpha.gmsservice.security;

import java.util.ArrayList;
import org.codealpha.gmsservice.entities.Granter;
import org.codealpha.gmsservice.entities.Organization;
import org.codealpha.gmsservice.entities.User;
import org.codealpha.gmsservice.exceptions.InvalidCredentialsException;
import org.codealpha.gmsservice.exceptions.InvalidTenantException;
import org.codealpha.gmsservice.services.OrganizationService;
import org.codealpha.gmsservice.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class AuthProvider implements AuthenticationProvider {

  @Autowired
  private UserService userService;
  @Autowired
  private OrganizationService organizationService;


  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String provider = authentication.getAuthorities().iterator().next().getAuthority();
    String username = authentication.getName();
    String password = authentication.getCredentials().toString();
    String tenantCode = (String) authentication.getDetails();
    authentication.getDetails();
    if(tenantCode == null){
      throw new InvalidCredentialsException("Missing required header X-TENANT-CODE");
    }
    if ("ANUDAN".equalsIgnoreCase(provider.toUpperCase())) {
      Organization org = organizationService.findOrganizationByTenantCode(tenantCode);
      if (org == null) {
        throw new InvalidTenantException("Invalid tenant code " + tenantCode);
      }
      User user = userService.getUserByEmail(username);

      if (user != null) {

        if (user.getOrganization().getOrganizationType().equalsIgnoreCase("GRANTER")
            && !((Granter) user.getOrganization()).getCode().equalsIgnoreCase(tenantCode)) {
          throw new BadCredentialsException("Invalid login for Granter organization.");
        }
        if (password.equalsIgnoreCase(user.getPassword())
            && username.equalsIgnoreCase(user.getEmailId())) {

          return new UsernamePasswordAuthenticationToken(username, password, new ArrayList());
        } else {
          //TODO - Read messages from a resource bundle
          throw new InvalidCredentialsException("Invalid password for " + user.getEmailId());
        }
      }else{
        throw new InvalidCredentialsException("Invalid username " + username);
      }
    } else if ("GOOGLE".equalsIgnoreCase(provider.toUpperCase())) {
      Organization org = organizationService.findOrganizationByTenantCode(tenantCode);
      if (org == null) {
        throw new InvalidTenantException("Invalid tenant code " + tenantCode);
      }
      User user = userService.getUserByEmail(username);

      if (user != null) {

        if (user.getOrganization().getOrganizationType().equalsIgnoreCase("GRANTER")
            && !((Granter) user.getOrganization()).getCode().equalsIgnoreCase(tenantCode)) {
          throw new InvalidCredentialsException("Invalid login for Granter organization.");
        }

        return new UsernamePasswordAuthenticationToken(username, password, new ArrayList());
      } else {
        throw new InvalidCredentialsException("User Name and password does not match.");
      }

    }

    return null;
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
  }

}