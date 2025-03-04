package org.example.board_game.core.common.base;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.board_game.entity.customer.Customer;
import org.example.board_game.entity.employee.Employee;
import org.example.board_game.entity.product.*;
import org.example.board_game.infrastructure.exception.ResourceNotFoundException;
import org.example.board_game.repository.customer.CustomerRepository;
import org.example.board_game.repository.employee.EmployeeRepository;
import org.example.board_game.repository.product.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EntityService {

    CategoryRepository categoryRepository;
    AuthorRepository authorRepository;
    PublisherRepository publisherRepository;
    ProductRepository productRepository;
    ProductMediaRepository productMediaRepository;
    CustomerRepository customerRepository;
    EmployeeRepository employeeRepository;

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

    public Product getProduct(String id) {
        return productRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found."));
    }

    public ProductMedia getMediaByIdAndProductId(String id, String productId) {
        return productMediaRepository
                .findByIdAndProduct_Id(id, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Image for Product not found."));
    }


    public void resetMainImage(String productId) {
        productMediaRepository
                .getProductMediaByProduct_IdAndMainImgTrue(productId)
                .ifPresent(currentMainMedia -> {
                    currentMainMedia.setMainImg(false);
                    productMediaRepository.save(currentMainMedia);
                });
    }

    public Customer getCurrentCustomer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return customerRepository.findByEmailAndDeletedFalse(email).orElse(null);
    }

    public Employee getCurrentEmployee() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return employeeRepository.findByEmailAndDeletedFalse(email).orElse(null);
    }
}
