package org.example.board_game.core.common.base;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.auth.utils.AuthHelper;
import org.example.board_game.entity.customer.Address;
import org.example.board_game.entity.customer.Customer;
import org.example.board_game.entity.employee.Employee;
import org.example.board_game.entity.product.*;
import org.example.board_game.entity.voucher.Voucher;
import org.example.board_game.infrastructure.constants.MessageConstant;
import org.example.board_game.infrastructure.exception.ResourceNotFoundException;
import org.example.board_game.repository.customer.AddressRepository;
import org.example.board_game.repository.customer.CustomerRepository;
import org.example.board_game.repository.employee.EmployeeRepository;
import org.example.board_game.repository.product.*;
import org.example.board_game.repository.voucher.VoucherRepository;
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
    AddressRepository addressRepository;
    VoucherRepository voucherRepository;

    public Category getCategory(String id) {
        return categoryRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstant.CATEGORY_NOT_FOUND));
    }

    public Author getAuthor(String id) {
        return authorRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstant.AUTHOR_NOT_FOUND));
    }

    public Publisher getPublisher(String id) {
        return publisherRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstant.PUBLISHER_NOT_FOUND));
    }

    public Product getProduct(String id) {
        return productRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstant.PRODUCT_NOT_FOUND));
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

    public Customer getCustomerByAuth() {
        String email = AuthHelper.getUsername();
        return customerRepository.findByEmailAndDeletedFalse(email).orElse(null);
    }

    public Employee getEmployeeByAuth() {
        String email = AuthHelper.getUsername();
        return employeeRepository.findByEmailAndDeletedFalse(email).orElse(null);
    }

    public Employee getEmployee(String id) {
        return employeeRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstant.EMPLOYEE_NOT_FOUND));
    }

    public Customer getCustomer(String id) {
        return customerRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstant.CUSTOMER_NOT_FOUND));
    }

    public Address getAddressByIdAndCustomer(String id,String customerId) {
        return addressRepository
                .findByIdAndCustomer_IdAndDeletedFalse(id,customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Địa chỉ của khách hàng này không tìm thấy."));
    }

    public Address getAddress(String id) {
        return addressRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstant.ADDRESS_NOT_FOUND));
    }

    public Voucher getVoucher(String id) {
        return voucherRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstant.VOUCHER_NOT_FOUND));
    }
}
