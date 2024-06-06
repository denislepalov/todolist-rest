package lepdv.todolistrest.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lepdv.todolistrest.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;
import java.util.List;


@Component
@PropertySource("classpath:variables.properties")
@RequiredArgsConstructor
public class JWTUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.lifetime}")
    private Long amount;
    private final UserService userService;



    public String generateToken(String username) {

        UserDetails userDetails = userService.loadUserByUsername(username);
        List<String> roleList = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        Date issuedDate = new Date();
        Duration duration = Duration.ofMinutes(amount);
        Date expirationDate = new Date(issuedDate.getTime() + duration.toMillis());

        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roleList)
                .setIssuedAt(issuedDate)
                .setExpiration(expirationDate)
                .setIssuer("todoList_v2_rest")
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }


    public String validateAndRetrieveUsername(String jwt) {
        return getAllClaims(jwt).getSubject();
    }

    public List<String> validateAndRetrieveRoles(String jwt) {
        List<?> roles = getAllClaims(jwt).get("roles", List.class);
        return roles.stream()
                .map(String::valueOf)
                .toList();
    }

    private Claims getAllClaims(String jwt) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(jwt)
                .getBody();
    }


//    public String generateToken(String username) {
//
//        UserDetails userDetails = userService.loadUserByUsername(username);
//        List<String> roleList = userDetails.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .toList();
//        Date issuedDate = new Date();
//        Duration duration = Duration.ofMinutes(countMinutes);
//        Date expirationDate = new Date(issuedDate.getTime() + duration.toMillis());
//        return JWT.create()
//                .withSubject(username)
//                .withClaim("roles", roleList)
//                .withIssuedAt(issuedDate)
//                .withIssuer("todoList_v2_rest")
//                .withExpiresAt(expirationDate)
//                .sign(Algorithm.HMAC256(secret));
//    }


//    public DecodedJWT validateTokenAndRetrieveDecodedJWT(String jwt) throws JWTVerificationException {
//
//        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
//                                  .withIssuer("todoList_v2_rest")
//                                  .build();
//        return verifier.verify(jwt);
//    }


}
