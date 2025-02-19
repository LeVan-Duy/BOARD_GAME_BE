package org.example.board_game.core.common.base;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.board_game.entity.product.Author;
import org.example.board_game.entity.product.Category;
import org.example.board_game.entity.product.Publisher;
import org.example.board_game.infrastructure.exception.ResourceNotFoundException;
import org.example.board_game.repository.product.AuthorRepository;
import org.example.board_game.repository.product.CategoryRepository;
import org.example.board_game.repository.product.PublisherRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EntityService {

    CategoryRepository categoryRepository;
    AuthorRepository authorRepository;
    PublisherRepository publisherRepository;

    public Category getCategory(String id) {
        return categoryRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found."));
    }

    public Author getAuthor(String id) {
        return authorRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found."));
    }

    public Publisher getPublisher(String id) {
        return publisherRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Publisher not found."));
    }

}
