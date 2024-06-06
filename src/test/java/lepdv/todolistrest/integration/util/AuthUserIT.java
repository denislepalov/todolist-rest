package lepdv.todolistrest.integration.util;

import lepdv.todolistrest.util.AuthUser;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import static lepdv.todolistrest.Constants.USER;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@WithMockUser(username = "Ivan", authorities = "USER")
@SpringBootTest()
class AuthUserIT {



    @Test
    void test_cannot_instantiate() {
        assertThrows(InvocationTargetException.class, () -> {
            Constructor<AuthUser> constructor = AuthUser.class.getDeclaredConstructor();
            assertTrue(Modifier.isPrivate(constructor.getModifiers()));
            constructor.setAccessible(true);
            constructor.newInstance();
        });
    }



    @Test
    void getAuthUsername_shouldGetUsername() {
        final String expectedResult = USER.getUsername();

        String actualResult = AuthUser.getAuthUsername();

        assertEquals(expectedResult, actualResult);
    }


}