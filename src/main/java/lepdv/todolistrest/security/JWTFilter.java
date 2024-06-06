package lepdv.todolistrest.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;


@Component
@Slf4j
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;


    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            try {
                String username = jwtUtil.validateAndRetrieveUsername(jwt);
                List<SimpleGrantedAuthority> roles = jwtUtil.validateAndRetrieveRoles(jwt)
                        .stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList();

                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            username, null, roles);
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (ExpiredJwtException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Lifetime of jwt token is expired");
                log.info("Lifetime of jwt token is expired");
            } catch (SignatureException | MalformedJwtException | UnsupportedJwtException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid jwt token");
                log.info("Invalid jwt token");
            }
        }
        filterChain.doFilter(request, response);
    }


//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
//                                    FilterChain filterChain) throws ServletException, IOException {
//
//        String authHeader = request.getHeader("Authorization");
//
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            String jwt = authHeader.substring(7);
//            try {
//                DecodedJWT decodedJWT = jwtUtil.validateTokenAndRetrieveDecodedJWT(jwt);
//                String username = decodedJWT.getSubject();
//                List<SimpleGrantedAuthority> roles = decodedJWT.getClaim("roles")
//                        .asList(String.class)
//                        .stream()
//                        .map(SimpleGrantedAuthority::new)
//                        .toList();
//
//                if (SecurityContextHolder.getContext().getAuthentication() == null) {
//                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
//                            username, null, roles);
//                    SecurityContextHolder.getContext().setAuthentication(authToken);
//                }
//            } catch (JWTVerificationException e) {
//                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid jwt token");
//                log.info("Invalid jwt token");
//            }
//        }
//        filterChain.doFilter(request, response);
//    }


}
