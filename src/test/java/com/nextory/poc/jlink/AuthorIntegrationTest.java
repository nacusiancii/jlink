package com.nextory.poc.jlink;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextory.poc.jlink.dto.AuthorDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthorIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void authorCrudFlow() throws Exception {
        AuthorDto dto = AuthorDto.builder().name("Jane Doe").email("jane@example.com").bio("Author bio").build();

        String createJson = objectMapper.writeValueAsString(dto);

        String location = mockMvc.perform(post("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn().getResponse().getHeader("Location");

        assertThat(location).isNotBlank();

        // fetch created
        String body = mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        AuthorDto created = objectMapper.readValue(body, AuthorDto.class);
        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("Jane Doe");

        // update
        created.setBio("Updated bio");
        String updateJson = objectMapper.writeValueAsString(created);

        mockMvc.perform(put("/api/authors/" + created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bio").value("Updated bio"));

        // delete
        mockMvc.perform(delete("/api/authors/" + created.getId()))
                .andExpect(status().isNoContent());

        // not found after delete
        mockMvc.perform(get("/api/authors/" + created.getId()))
                .andExpect(status().isNotFound());
    }
}


