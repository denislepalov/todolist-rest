package lepdv.todolistrest.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lepdv.todolistrest.dto.auth.CredentialsDto;
import lepdv.todolistrest.dto.auth.RegisterDto;
import lepdv.todolistrest.integration.IT;
import lepdv.todolistrest.service.AdminService;
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
import static lepdv.todolistrest.Constants.REGISTER_DTO;
import static lepdv.todolistrest.Constants.USER;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@IT
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@RequiredArgsConstructor
class AuthControllerIT /*extends IntegrationTestBase*/ {

    private final MockMvc mockMvc;
    private final AdminService adminService;
    private final ObjectMapper jsonMapper = JsonMapper.builder()
            .findAndAddModules()
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .build();




    @Test
    void performLogin_shouldGetREWithJwtDto_whenCorrectCredentials() throws Exception {
        final CredentialsDto credentialsDto = new CredentialsDto("Ivan", "Ivan");
        final String jsonCredentialsDto = jsonMapper.writeValueAsString(credentialsDto);
        final RequestBuilder request = post("/api/v2/authenticate/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonCredentialsDto);

        mockMvc.perform(request)
                .andExpectAll(
                        openApi().isValid("static/oldOpenapi.yaml"),
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.jwt").exists()
                );
    }

    @Test
    void performLogin_shouldGetREWithErrorsBody_whenIncorrectCredentials() throws Exception {
        final CredentialsDto credentialsDto = new CredentialsDto("Ivan", "dummy");
        final String jsonCredentialsDto = jsonMapper.writeValueAsString(credentialsDto);
        final RequestBuilder request = post("/api/v2/authenticate/login")
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
                                    "path": "uri=/api/v2/authenticate/login",
                                    "message": "Invalid request"
                                }""")
                );
    }

    @Test
    @WithMockUser(username = "Admin", authorities = "ADMIN")
    void performLogin_shouldGetREWithErrorsBody_whenUserIsLocked() throws Exception {
        final CredentialsDto credentialsDto = new CredentialsDto("Ivan", "Ivan");
        final String jsonCredentialsDto = jsonMapper.writeValueAsString(credentialsDto);
        final RequestBuilder request = post("/api/v2/authenticate/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonCredentialsDto);
        adminService.lockUser(USER.getId());

        mockMvc.perform(request)
                .andExpectAll(
                        openApi().isValid("static/oldOpenapi.yaml"),
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                  "status": 400,
                                    "errors": ["User is locked"],
                                    "type": "UnitedException",
                                    "path": "uri=/api/v2/authenticate/login",
                                    "message": "Invalid request"
                                }""")
                );
    }



    @Test
    void register_shouldGetREWithJwtDto_whenDataIsValid() throws Exception {
        final String jsonRegisterDto = jsonMapper.writeValueAsString(REGISTER_DTO);
        final RequestBuilder request = post("/api/v2/authenticate/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRegisterDto);

        mockMvc.perform(request)
                .andExpectAll(
                        openApi().isValid("static/oldOpenapi.yaml"),
                        status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.jwt").exists()
                );
    }


    @Test
    void register_shouldGetREWithErrorsBody_whenDataIsInvalid() throws Exception {
        final RegisterDto registerDto = RegisterDto.builder()
                .username("Pe")
                .password("")
                .fullName("Petrov Petr")
                .dateOfBirth(LocalDate.of(2100, Month.AUGUST, 15))
                .build();
        final String jsonRegisterDto = jsonMapper.writeValueAsString(registerDto);
        final RequestBuilder request = post("/api/v2/authenticate/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRegisterDto);

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
                                    "path": "uri=/api/v2/authenticate/registration",
                                    "message": "Invalid request"
                                }""")
                );
    }


}














