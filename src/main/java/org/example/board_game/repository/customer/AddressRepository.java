package org.example.board_game.repository.customer;

import jakarta.persistence.Tuple;
import org.example.board_game.entity.customer.Address;
import org.example.board_game.entity.customer.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, String> {


    @Query("""
            SELECT x.id as id, x.phoneNumber as phoneNumber, x.detailAddress as detailAddress, x.districtId as districtId,
                   x.isDefault as isDefault, x.provinceId as provinceId, x.wardCode as wardCode, x.customer.id as customerId
            FROM Address x
            WHERE x.customer.id IN :customerIds AND x.deleted = FALSE
            """)
    List<Tuple> getAllByCustomerIds(List<String> customerIds);

    @Query("""
        SELECT COUNT(x.id)
        FROM Address x
        WHERE x.customer.id = :customerId AND x.deleted = FALSE
        """)
    Optional<Long> countAddressesByCustomerId(String customerId);

    @Query("""
            SELECT CASE WHEN COUNT(x) > 0 THEN TRUE ELSE FALSE END
            FROM Address x WHERE x.customer = :customer AND x.deleted = FALSE AND x.isDefault = TRUE
            """)
    boolean existsIsDefaultByCustomer(@Param("customer") Customer customer);

    Optional<Address> findByIdAndCustomer_IdAndDeletedFalse(String id, String customerId);

    Optional<Address> findByCustomer_IdAndDeletedFalseAndIsDefaultTrue(String customerId);


}
