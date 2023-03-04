package com.example.examservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.security.RolesAllowed;
import java.security.Principal;

/**
 * @author Le Hoang Nhat a.k.a Rei202
 * @Date 2/27/2023
 */

@Controller
@CrossOrigin
@RequestMapping("/admin")
@RolesAllowed("admin")
public class TestController2 {
    @GetMapping()
    ResponseEntity<String> getTestString(Principal principal){
        String name = principal.getName();
        return ResponseEntity.ok(name + "  admin");
    }
}
