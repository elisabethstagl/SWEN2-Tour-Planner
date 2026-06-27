package at.fhtw.tourplanner_backend.security;

import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;

public class JwtKeyGenerator {

    public static void main(String[] args) {
        String secret = Encoders.BASE64.encode(
                Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256).getEncoded()
        );

        System.out.println(secret);
    }
}