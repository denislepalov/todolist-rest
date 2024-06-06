package lepdv.todolistrest.integration.security;

import io.jsonwebtoken.MalformedJwtException;
import lepdv.todolistrest.integration.IT;
import lepdv.todolistrest.security.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;

import static lepdv.todolistrest.Constants.USER;
import static org.junit.jupiter.api.Assertions.*;


@IT
@WithMockUser(username = "Ivan", authorities = "USER")
@RequiredArgsConstructor
class JWTUtilIT {

    private final JWTUtil jwtUtil;




    @Test
    void generateToken_shouldGetJwt_whenDataIsValid() {
        String actualResult = jwtUtil.generateToken(USER.getUsername());

        assertNotNull(actualResult);
    }

    @Test
    void generateToken_shouldThrowException_whenDataIsInvalid() {
        assertThrows(UsernameNotFoundException.class, () -> jwtUtil.generateToken("dummy"));
    }



    @Test
    void validateAndRetrieveUsername_shouldGetUsername_whenJwtIsValid() {
        final String jwt = jwtUtil.generateToken(USER.getUsername());

        String actualResult = jwtUtil.validateAndRetrieveUsername(jwt);

        assertEquals(USER.getUsername(), actualResult);
    }

    @Test
    void validateAndRetrieveUsername_shouldThrowException_whenJwtIsInvalid() {
        final String jwt = "dummy";

        assertThrows(MalformedJwtException.class, () -> jwtUtil.validateAndRetrieveUsername(jwt));
    }



    @Test
    void validateAndRetrieveRoles_shouldGetRoleList_whenJwtIsValid() {
        final String jwt = jwtUtil.generateToken(USER.getUsername());
        final List<String> expectedResult = USER.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        List<String> actualResult = jwtUtil.validateAndRetrieveRoles(jwt);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void validateAndRetrieveRoles_shouldGetRoleList_whenJwtIsInvalid() {
        final String jwt = "dummy";

        assertThrows(MalformedJwtException.class, () -> jwtUtil.validateAndRetrieveRoles(jwt));
    }



}












