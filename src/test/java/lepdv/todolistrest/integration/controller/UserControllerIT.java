package lepdv.todolistrest.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lepdv.todolistrest.dto.auth.CredentialsDto;
import lepdv.todolistrest.dto.user.EditPasswordDto;
import lepdv.todolistrest.dto.user.UserDto;
import lepdv.todolistrest.integration.IT;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import java.time.LocalDate;
import java.time.Month;

import static com.atlassian.oai.validator.OpenApiInteractionValidator.createFor;
import static com.atlassian.oai.validator.mockmvc.OpenApiValidationMatchers.openApi;
import static com.atlassian.oai.validator.whitelist.ValidationErrorsWhitelist.create;
import static com.atlassian.oai.validator.whitelist.rule.WhitelistRules.messageHasKey;
import static lepdv.todolistrest.Constants.USER_DTO;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@IT
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@WithMockUser(username = "Ivan", authorities = "USER")
@RequiredArgsConstructor
class UserControllerIT /*extends IntegrationTestBase*/ {

    private final MockMvc mockMvc;
    private final ObjectMapper jsonMapper = JsonMapper.builder()
            .findAndAddModules()
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .build();




    @Test
    void getUser_shouldGetREWithUserDto() throws Exception {
        final String jsonUserDto = jsonMapper.writeValueAsString(USER_DTO);
        final RequestBuilder request = get("/api/v2/user");

        mockMvc.perform(request)
                .andExpectAll(
                        openApi().isValid("static/oldOpenapi.yaml"),
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(jsonUserDto)
                );
    }



    @Test
    void updateUser_shouldGetREWithUpdatedUserDto_whenDataIsValid() throws Exception {
        final UserDto userDto = UserDto.builder()
                .username("Ivan")
                .fullName("Updated full name")
                .dateOfBirth(LocalDate.of(2000, Month.JANUARY, 1))
                .build();
        final String jsonUserDto = jsonMapper.writeValueAsString(userDto);
        final RequestBuilder request = put("/api/v2/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUserDto);

        mockMvc.perform(request)
                .andExpectAll(
                        openApi().isValid("static/oldOpenapi.yaml"),
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(jsonUserDto)
                );
    }

    @Test
    void updateUser_shouldGetREWithErrorsBody_whenDataIsInvalid() throws Exception {
        final UserDto userDto = UserDto.builder()
                .username("")
                .fullName("Updated full name")
                .dateOfBirth(LocalDate.of(2100, Month.JANUARY, 1))
                .build();
        final String jsonUserDto = jsonMapper.writeValueAsString(userDto);
        final RequestBuilder request = put("/api/v2/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUserDto);

        mockMvc.perform(request)
                .andExpectAll(
                        openApi().isValid(createFor("static/oldOpenapi.yaml")
                                .withWhitelist(create().withRule("Ignoring validation minLength",
                                        messageHasKey("validation.request.body.schema.minLength")))
                                .build()),
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                  "status": 400,
                                  "type": "UnitedException",
                                  "path": "uri=/api/v2/user",
                                  "message": "Invalid request"
                                }
                                """)
                );
    }



    @Test
    void editPassword_shouldGetREWithEditPasswordString_whenDataIsValid() throws Exception {
        final EditPasswordDto editPasswordDto = EditPasswordDto.builder()
                .username("Ivan")
                .oldPassword("Ivan")
                .newPassword("newIvan")
                .build();
        final String expectedString = "Password of %s was edited".formatted(editPasswordDto.getUsername());
        final String jsonEditPasswordDto = jsonMapper.writeValueAsString(editPasswordDto);
        final RequestBuilder request = put("/api/v2/user/edit-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonEditPasswordDto);

        mockMvc.perform(request)
                .andExpectAll(
                        openApi().isValid("static/oldOpenapi.yaml"),
                        status().isOk(),
                        content().contentType(MediaType.TEXT_PLAIN),
                        content().bytes(expectedString.getBytes())
                );
    }

    @Test
    void editPassword_shouldGetREWithErrorsBody_whenDataIsInvalid() throws Exception {
        final EditPasswordDto editPasswordDto = EditPasswordDto.builder()
                .username("Ivan")
                .oldPassword("dummy")
                .newPassword("newIvan")
                .build();
        final String jsonEditPasswordDto = jsonMapper.writeValueAsString(editPasswordDto);
        final RequestBuilder request = put("/api/v2/user/edit-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonEditPasswordDto);

        mockMvc.perform(request)
                .andExpectAll(
                        openApi().isValid("static/oldOpenapi.yaml"),
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                  "status": 400,
                                  "errors": ["Incorrect credentials"],
                                  "type": "UnitedException",
                                  "path": "uri=/api/v2/user/edit-password",
                                  "message": "Invalid request"
                                }
                                """)
                );
    }



    @Test
    void deleteAccount_shouldGetREWithDeleteUserString_whenDataIsValid() throws Exception {
        final CredentialsDto credentialsDto = CredentialsDto.builder()
                .username("Ivan")
                .password("Ivan")
                .build();
        final String expectedString = "Account of %s was deleted".formatted(credentialsDto.getUsername());
        final String jsonCredentialsDto = jsonMapper.writeValueAsString(credentialsDto);
        final RequestBuilder request = delete("/api/v2/user/delete-account")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonCredentialsDto);

        mockMvc.perform(request)
                .andExpectAll(
                        openApi().isValid("static/oldOpenapi.yaml"),
                        status().isOk(),
                        content().contentType(MediaType.TEXT_PLAIN),
                        content().bytes(expectedString.getBytes())
                );
    }

    @Test
    void deleteAccount_shouldGetREWithErrorsBody_whenDataIsInvalid() throws Exception {
        final CredentialsDto credentialsDto = CredentialsDto.builder()
                .username("Ivan")
                .password("dummy")
                .build();
        final String jsonCredentialsDto = jsonMapper.writeValueAsString(credentialsDto);
        final RequestBuilder request = delete("/api/v2/user/delete-account")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonCredentialsDto);

        mockMvc.perform(request)
                .andExpectAll(
                        openApi().isValid("static/oldOpenapi.yaml"),
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                  "status": 400,
                                  "errors": ["Incorrect credentials"],
                                  "type": "UnitedException",
                                  "path": "uri=/api/v2/user/delete-account",
                                  "message": "Invalid request"
                                }
                                """)
                );
    }

    

}
















