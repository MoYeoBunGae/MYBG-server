package com.midasdev.mybg.group.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.midasdev.mybg.config.security.filter.JwtAuthenticationFilter;
import com.midasdev.mybg.group.service.GroupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@TestPropertySource(properties = "logging.level.org.springframework.security=DEBUG")
@ExtendWith(SpringExtension.class)
@WebMvcTest(GroupController.class)
class GroupControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockBean
    private GroupService groupService;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Request Body에 그룹 이름이 없는 경우 400 Bad Request 반환")
    void 그룹_이름이_없는_경우_400_Bad_Request_반환() throws Exception {
        // given
        String requestBody = """
                {
                    "profileImageUrl": "https://example.com/image.jpg"
                }
                """;

        // when
        // then
        mockMvc.perform(post("/api/v1/groups")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .with(SecurityMockMvcRequestPostProcessors.oidcLogin())
                                .content(requestBody))
               .andExpect(status().isBadRequest());
    }

}