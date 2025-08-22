package com.nextory.poc.jlink.service;

import com.nextory.poc.jlink.dto.BookDto;
import com.nextory.poc.jlink.model.Author;
import com.nextory.poc.jlink.model.Book;
import com.nextory.poc.jlink.model.Publisher;
import com.nextory.poc.jlink.repository.AuthorRepository;
import com.nextory.poc.jlink.repository.BookRepository;
import com.nextory.poc.jlink.repository.PublisherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final PublisherRepository publisherRepository;

    public BookDto create(BookDto dto) {
        Author author = authorRepository.findById(dto.authorId())
                .orElseThrow(() -> new IllegalArgumentException("Author not found"));
        Publisher publisher = publisherRepository.findById(dto.publisherId())
                .orElseThrow(() -> new IllegalArgumentException("Publisher not found"));

        Book book = Book.builder()
                .title(dto.title())
                .isbn(dto.isbn())
                .publicationDate(dto.publicationDate())
                .price(dto.price())
                .author(author)
                .publisher(publisher)
                .build();

        Book saved = bookRepository.save(book);
        return toDto(saved);
    }

    public Optional<BookDto> getById(Long id) {
        return bookRepository.findById(id).map(this::toDto);
    }

    public Page<BookDto> getAll(Pageable pageable, String titleFilter, Long authorId, Long publisherId) {
        // Basic filtering strategy:
        // 1) If titleFilter provided, use title query
        // 2) Otherwise return all (pagination)
        // Note: more advanced combined filtering can be added later (specify repository methods or use Specifications)
        if (titleFilter != null && !titleFilter.isBlank()) {
            return bookRepository.findAllByTitleContainingIgnoreCase(titleFilter, pageable)
                    .map(this::toDto);
        }
        // filtering by authorId / publisherId can be applied in service if needed
        if (authorId != null) {
            return bookRepository.findAll((root, query, cb) ->
                    cb.equal(root.get("author").get("id"), authorId), pageable).map(this::toDto);
        }
        if (publisherId != null) {
            return bookRepository.findAll((root, query, cb) ->
                    cb.equal(root.get("publisher").get("id"), publisherId), pageable).map(this::toDto);
        }
        return bookRepository.findAll(pageable).map(this::toDto);
    }

    public Optional<BookDto> update(Long id, BookDto dto) {
        return bookRepository.findById(id).map(existing -> {
            if (dto.title() != null) existing.setTitle(dto.title());
            if (dto.isbn() != null) existing.setIsbn(dto.isbn());
            if (dto.publicationDate() != null) existing.setPublicationDate(dto.publicationDate());
            if (dto.price() != null) existing.setPrice(dto.price());

            if (dto.authorId() != null) {
                Author author = authorRepository.findById(dto.authorId())
                        .orElseThrow(() -> new IllegalArgumentException("Author not found"));
                existing.setAuthor(author);
            }
            if (dto.publisherId() != null) {
                Publisher publisher = publisherRepository.findById(dto.publisherId())
                        .orElseThrow(() -> new IllegalArgumentException("Publisher not found"));
                existing.setPublisher(publisher);
            }

            Book saved = bookRepository.save(existing);
            return toDto(saved);
        });
    }

    public void delete(Long id) {
        bookRepository.deleteById(id);
    }

    private BookDto toDto(Book book) {
        return new BookDto(
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                book.getPublicationDate(),
                book.getPrice(),
                book.getAuthor() != null ? book.getAuthor().getId() : null,
                book.getPublisher() != null ? book.getPublisher().getId() : null
        );
    }
}