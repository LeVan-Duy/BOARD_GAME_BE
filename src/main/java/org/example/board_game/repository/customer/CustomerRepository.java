package org.example.board_game.repository.customer;

import org.example.board_game.entity.customer.Customer;
import org.example.board_game.entity.voucher.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {

}
