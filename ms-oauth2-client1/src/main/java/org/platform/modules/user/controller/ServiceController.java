package org.platform.modules.user.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class ServiceController {

    Logger logger = LoggerFactory.getLogger(ServiceController.class);

    @GetMapping("/service1/method1/{id}")
    public String readService1Method1(@PathVariable String id) {
        return "service1 method1 id : " + id;
    }

    @GetMapping("/service2/method1/{id}")
    public String readService2Method1(@PathVariable String id) {
        return "service2 method1 id : " + id;
    }

    @GetMapping("/principle")
    public OAuth2Authentication readPrinciple(OAuth2Authentication oAuth2Authentication, Principal principal, Authentication authentication) {
        logger.info(oAuth2Authentication.getUserAuthentication().getAuthorities().toString());
        logger.info(oAuth2Authentication.toString());
        logger.info("principal.toString() " + principal.toString());
        logger.info("principal.getName() " + principal.getName());
        logger.info("authentication: " + authentication.getAuthorities().toString());
        return oAuth2Authentication;
    }
}
