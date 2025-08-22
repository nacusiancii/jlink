package com.nextory.poc.jlink;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextory.poc.jlink.dto.BookDto;
import com.nextory.poc.jlink.dto.AuthorDto;
import com.nextory.poc.jlink.dto.PublisherDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class BookIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void bookCrudFlow() throws Exception {
        // create author
        AuthorDto author = AuthorDto.builder().name("Author X").email("authorx@example.com").bio("bio").build();
        String authorLocation = mockMvc.perform(post("/api/authors").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(author)))
                .andExpect(status().isCreated()).andReturn().getResponse().getHeader("Location");

        // create publisher
        PublisherDto publisher = new PublisherDto(null, "Pub X", "https://pubx.example", "addr");
        String publisherLocation = mockMvc.perform(post("/api/publishers").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(publisher)))
                .andExpect(status().isCreated()).andReturn().getResponse().getHeader("Location");

        // read ids
        AuthorDto createdAuthor = objectMapper.readValue(mockMvc.perform(get(authorLocation)).andReturn().getResponse().getContentAsString(), AuthorDto.class);
        PublisherDto createdPublisher = objectMapper.readValue(mockMvc.perform(get(publisherLocation)).andReturn().getResponse().getContentAsString(), PublisherDto.class);

        BookDto book = new BookDto(null, "Book Title", "ISBN-12345", LocalDate.now(), new BigDecimal("9.99"), createdAuthor.getId(), createdPublisher.id());

        String bookLocation = mockMvc.perform(post("/api/books").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");

        assertThat(bookLocation).isNotBlank();

        BookDto createdBook = objectMapper.readValue(mockMvc.perform(get(bookLocation)).andReturn().getResponse().getContentAsString(), BookDto.class);
        assertThat(createdBook.id()).isNotNull();
        assertThat(createdBook.title()).isEqualTo("Book Title");

        // update title
        BookDto update = new BookDto(null, "Updated Title", null, null, null, null, null);
        mockMvc.perform(put("/api/books/" + createdBook.id()).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));

        // delete
        mockMvc.perform(delete("/api/books/" + createdBook.id())).andExpect(status().isNoContent());
        mockMvc.perform(get("/api/books/" + createdBook.id())).andExpect(status().isNotFound());
    }
}


