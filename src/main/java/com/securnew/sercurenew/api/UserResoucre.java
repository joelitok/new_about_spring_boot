package com.securnew.sercurenew.api;

import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.securnew.sercurenew.domain.AppRole;
import com.securnew.sercurenew.domain.AppUser;
import com.securnew.sercurenew.domain.service.UserService;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserResoucre {

    private final UserService userService;
    public static final String APPLICATION_JSON_VALUE="application/json";
    public static final String AUTHORIZATION="Authorization";

    @GetMapping("/users")
    public ResponseEntity<List<AppUser>> getUsers() {
        return ResponseEntity.ok().body(userService.getUsers());
    }

    @PostMapping("/user/save")
    public ResponseEntity<AppUser> saveUser(@RequestBody AppUser appUser) {
   URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/user/save").toString());
        return ResponseEntity.created(uri).body(userService.saveUser(appUser));
    }

    @PostMapping("/role/save")
    public ResponseEntity<AppRole> saveRole(@RequestBody AppRole appRole) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/role/save").toString());
        return ResponseEntity.created(uri).body(userService.saveRole(appRole));
    }

    @PostMapping("/role/addtouser")
    public ResponseEntity<?> addRoleToUser(@RequestBody RoleToUserForm form) {
        userService.addRoleToUser(form.getUsername(), form.getRoleName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request,HttpServletResponse response) throws JsonGenerationException, JsonMappingException, IOException{
        String authorizationHeader =request.getHeader(AUTHORIZATION);
        if(authorizationHeader!=null && authorizationHeader.startsWith("Bearer ")){

           try {
               String refresh_token = authorizationHeader.substring("Bearer ".length()); 
               Algorithm algorithm =Algorithm.HMAC256("secret".getBytes());
               JWTVerifier verifier =JWT.require(algorithm).build();
               DecodedJWT decodedJWT =verifier.verify(token);
               String username =decodedJWT.getSubject();
               AppUser user = userService.getUser(username);
               String access_token =JWT.create().withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis()+10*60*1000))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles",user.getRoles().stream().map(AppRole::getName).collect(Collectors.toList()))
                .sign(algorithm);
                
                Map<String,String> tokens = new HashMap<>();
                tokens.put("access_token", access_token);
                tokens.put("refresh_token", refresh_token);
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(),tokens);

           } catch (Exception exception) {
               log.error("Error logging in : {}", exception.getMessage());
               response.setHeader("error", exception.getMessage());
               response.setStatus(FORBIDDEN.value());
               //response.sendError(FORBIDDEN.value());
               Map<String,String> error = new HashMap<>();
               error.put("error_message", exception.getMessage());
               response.setContentType(APPLICATION_JSON_VALUE);
               new ObjectMapper().writeValue(response.getOutputStream(), error);
           }
        }else{
           throw new RuntimeException("Refresh token is missing");
        }
    }

}

@Data
class RoleToUserForm{
private String username;
private String roleName;
}
