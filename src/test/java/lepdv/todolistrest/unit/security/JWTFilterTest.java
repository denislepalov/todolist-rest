package lepdv.todolistrest.unit.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import lepdv.todolistrest.security.JWTFilter;
import lepdv.todolistrest.security.JWTUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.List;

import static lepdv.todolistrest.Constants.USER;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class JWTFilterTest {

    @Mock
    private JWTUtil jwtUtil;
    @InjectMocks
    private JWTFilter jwtFilter;




    @Test
    void doFilterInternal_shouldDoFilter_whenJwtIsValid() throws ServletException, IOException {
        final MockHttpServletRequest requestMock = new MockHttpServletRequest();
        requestMock.addHeader(HttpHeaders.AUTHORIZATION, "Bearer 'some jwt'");
        final HttpServletResponse responseMock = mock(HttpServletResponse.class);
        final FilterChain filterChainMock = mock(FilterChain.class);
        final String jwt = "'some jwt'";
        final String username = USER.getUsername();
        final List<String> roleList = USER.getAuthorities().stream().map(String::valueOf).toList();
        final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                username, null, roleList.stream().map(SimpleGrantedAuthority::new).toList());
        final SecurityContext mockSecurityContext = mock(SecurityContext.class);
        doReturn(username).when(jwtUtil).validateAndRetrieveUsername(jwt);
        doReturn(roleList).when(jwtUtil).validateAndRetrieveRoles(jwt);
        doReturn(null).when(mockSecurityContext).getAuthentication();
        doNothing().when(mockSecurityContext).setAuthentication(authToken);
        doNothing().when(filterChainMock).doFilter(requestMock, responseMock);

        try (MockedStatic<SecurityContextHolder> contextHolder = mockStatic(SecurityContextHolder.class)) {
            contextHolder.when(SecurityContextHolder::getContext).thenReturn(mockSecurityContext);
            jwtFilter.doFilterInternal(requestMock, responseMock, filterChainMock);

            contextHolder.verify(SecurityContextHolder::getContext, times(2));
        }

        verify(jwtUtil).validateAndRetrieveUsername(jwt);
        verify(jwtUtil).validateAndRetrieveRoles(jwt);
        verify(mockSecurityContext).getAuthentication();
        verify(mockSecurityContext).setAuthentication(authToken);
        verify(filterChainMock).doFilter(requestMock, responseMock);
    }

    @Test
    void doFilterInternal_shouldThrowException_whenJwtIsExpired() throws ServletException, IOException {
        final MockHttpServletRequest requestMock = new MockHttpServletRequest();
        requestMock.addHeader(HttpHeaders.AUTHORIZATION, "Bearer 'ExpiredJwtException jwt'");
        final HttpServletResponse responseMock = mock(HttpServletResponse.class);
        final FilterChain filterChainMock = mock(FilterChain.class);
        final String expiredJwt = "'ExpiredJwtException jwt'";
        doThrow(ExpiredJwtException.class).when(jwtUtil).validateAndRetrieveUsername(expiredJwt);
        doNothing().when(responseMock).sendError(HttpServletResponse.SC_BAD_REQUEST, "Lifetime of jwt token is expired");

        jwtFilter.doFilterInternal(requestMock, responseMock, filterChainMock);

        verify(jwtUtil).validateAndRetrieveUsername(expiredJwt);
        verify(responseMock).sendError(HttpServletResponse.SC_BAD_REQUEST, "Lifetime of jwt token is expired");
        verify(filterChainMock).doFilter(requestMock, responseMock);
    }

    @Test
    void doFilterInternal_shouldThrowException_whenJwtIsInvalid() throws ServletException, IOException {
        final MockHttpServletRequest requestMock = new MockHttpServletRequest();
        requestMock.addHeader(HttpHeaders.AUTHORIZATION, "Bearer 'SignatureException jwt'");
        final HttpServletResponse responseMock = mock(HttpServletResponse.class);
        final FilterChain filterChainMock = mock(FilterChain.class);
        final String invalidJwt = "'SignatureException jwt'";
        doThrow(SignatureException.class).when(jwtUtil).validateAndRetrieveUsername(invalidJwt);
        doNothing().when(responseMock).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid jwt token");

        jwtFilter.doFilterInternal(requestMock, responseMock, filterChainMock);

        verify(jwtUtil).validateAndRetrieveUsername(invalidJwt);
        verify(responseMock).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid jwt token");
        verify(filterChainMock).doFilter(requestMock, responseMock);
    }



}
























