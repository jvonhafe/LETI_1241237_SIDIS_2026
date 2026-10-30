//package com.aisafe.core.security;

//import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

//@Service
//public class JwtService {

    //private static final String SECRET = "aisafe-secret-key-2026";
    //private static final long EXPIRATION = 1000 * 60 * 60 * 24;

    //public String generateToken(UserDetails userDetails) {
        //long expirationTime = System.currentTimeMillis() + EXPIRATION;
        // username = userDetails.getUsername();

        //String role = userDetails.getAuthorities().iterator().next().getAuthority();

        //String payload = username + ":" + role + ":" + expirationTime;
        //String signature = sign(payload);

        //return Base64.getEncoder().encodeToString((payload + ":" + signature).getBytes(StandardCharsets.UTF_8));
    //}

    //public String extractUsername(String token) {
        //String decoded = decode(token);
        //return decoded.split(":")[0];
    //}

    //public String extractRole(String token) {
        //String decoded = decode(token);
        //return decoded.split(":")[1];
    //}

    //public boolean isTokenValid(String token) {
        //try {
            //String decoded = decode(token);
            //String[] parts = decoded.split(":");

            //String username = parts[0];
            //String role = parts[1];
            //long expiration = Long.parseLong(parts[2]);
            //String signature = parts[3];

            //String expectedSignature = sign(username + ":" + role + ":" + expiration);

            //return signature.equals(expectedSignature) && expiration > System.currentTimeMillis();

        //} catch (Exception e) {
            //return false;
        //}
   // }

   // private String decode(String token) {
     //   return new String(Base64.getDecoder().decode(token), StandardCharsets.UTF_8);
   // }

   // private String sign(String data) {
      //  try {
        //    Mac mac = Mac.getInstance("HmacSHA256");
           // SecretKeySpec key = new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
           // mac.init(key);

          //  byte[] signature = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

           // return Base64.getEncoder().encodeToString(signature);

      //  } catch (Exception e) {
       //     throw new RuntimeException("Could not sign JWT token");
      //  }
  //  }
//}