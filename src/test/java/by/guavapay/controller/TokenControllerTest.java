package by.guavapay.controller;

import by.guavapay.AbstractWebTestConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.User;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TokenControllerTest extends AbstractWebTestConfiguration {

    @Test
    void rootWhenUnauthenticatedThen401() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void tokenWhenBadCredentialsThen401() throws Exception {
        mockMvc.perform(post("/token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void tokenByBasicAuthentication() throws Exception {
        when(userDetailsService.loadUserByUsername("user1"))
                .thenReturn(User
                        .withUsername("user@test1.cm")
                        .password("{noop}password2")
                        .roles("USER")
                        .build());

        mockMvc.perform(post("/token")
                        .with(httpBasic("user1", "password2")))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, "text/plain;charset=UTF-8"))
                .andExpect(content().string(hasLength(484)))
                .andReturn();
    }
}