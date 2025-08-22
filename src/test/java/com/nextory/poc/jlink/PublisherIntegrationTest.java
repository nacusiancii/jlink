package com.nextory.poc.jlink;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextory.poc.jlink.dto.PublisherDto;
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
public class PublisherIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void publisherCrudFlow() throws Exception {
        PublisherDto dto = new PublisherDto(null, "Acme Publishing", "https://acme.example", "123 Street");

        String createJson = objectMapper.writeValueAsString(dto);

        String location = mockMvc.perform(post("/api/publishers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn().getResponse().getHeader("Location");

        assertThat(location).isNotBlank();

        String body = mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        PublisherDto created = objectMapper.readValue(body, PublisherDto.class);
        assertThat(created.id()).isNotNull();
        assertThat(created.name()).isEqualTo("Acme Publishing");

        // update
        PublisherDto updated = new PublisherDto(created.id(), "Acme Updated", created.website(), created.address());

        mockMvc.perform(put("/api/publishers/" + created.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Acme Updated"));

        // delete
        mockMvc.perform(delete("/api/publishers/" + created.id()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/publishers/" + created.id()))
                .andExpect(status().isNotFound());
    }
}


