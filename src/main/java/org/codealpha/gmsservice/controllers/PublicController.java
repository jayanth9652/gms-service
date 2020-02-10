package org.codealpha.gmsservice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.codealpha.gmsservice.entities.Grant;
import org.codealpha.gmsservice.entities.Organization;
import org.codealpha.gmsservice.exceptions.ResourceNotFoundException;
import org.codealpha.gmsservice.services.OrganizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;

@RestController
@RequestMapping("/public")
public class PublicController {

    private static Logger logger = LoggerFactory.getLogger(ApplicationController.class);

    @Autowired
    private ResourceLoader resourceLoader;
    @Value("${spring.upload-file-location}")
    private String uploadLocation;
    @Autowired
    private OrganizationService organizationService;

    @GetMapping("/images/{tenant}/logo")
    @ApiOperation(value = "Get tenant logo image for <img> tag 'src' property")
    public void getLogoImage(HttpServletResponse servletResponse, @ApiParam(name="tenant",value="Tenant code")@PathVariable("tenant") String tenant) {

        Resource image = resourceLoader.getResource("file:" + uploadLocation + "/" + tenant + "/logo/logo.png");

        servletResponse.setContentType(MediaType.IMAGE_PNG_VALUE);
        servletResponse.setHeader("org-name",organizationService.findOrganizationByTenantCode(tenant).getName());
        try {
            StreamUtils.copy(image.getInputStream(), servletResponse.getOutputStream());

        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    /*@GetMapping("/tenant/{domain}")
    public String getTenantCode(@PathVariable("domain") String domain){
        Organization org = organizationService.findOrganizationByTenantCode(domain);
        if(org!=null){
            return org.getCode();
        }else{
            throw new ResourceNotFoundException("Invalid request to access Anudan");
        }

    }*/

    @GetMapping("/tenant/{domain}")
    public String getTenantName(@PathVariable("domain") String domain){
        Organization org = organizationService.findOrganizationByTenantCode(domain.toUpperCase());
        if(org!=null){
            return org.getName();
        }else{
            throw new ResourceNotFoundException("Invalid request to access Anudan");
        }

    }

    /*@GetMapping("/grant/invite")
    public void processInvite(@RequestParam("t") String type,@RequestParam("g")String grantCode,){

        String grantJSON = Base64.getDecoder().decode(grantCode).toString();
        ObjectMapper mapper = new ObjectMapper();
        Grant grant = null;
        try {
            grant = mapper.readValue(grantJSON, Grant.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return
    }*/
}