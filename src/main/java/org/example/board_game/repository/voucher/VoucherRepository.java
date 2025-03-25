package org.example.board_game.repository.voucher;

import jakarta.transaction.Transactional;
import org.example.board_game.core.admin.domain.dto.request.product.AdminCategoryRequest;
import org.example.board_game.core.admin.domain.dto.request.voucher.AdminVoucherRequest;
import org.example.board_game.core.client.domain.dto.request.voucher.ClientVoucherRequest;
import org.example.board_game.entity.product.Category;
import org.example.board_game.entity.voucher.Voucher;
import org.example.board_game.infrastructure.enums.VoucherStatus;
import org.example.board_game.infrastructure.enums.VoucherType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, String> {

    @Query("""
                SELECT x FROM Voucher x
                WHERE
                    (:#{#request.q} IS NULL OR :#{#request.q} ILIKE '' OR x.name ILIKE CONCAT('%', :#{#request.q}, '%') OR x.code ILIKE CONCAT('%', :#{#request.q}, '%'))
                    AND
                    (:status IS NULL OR x.status = :status) AND (:type IS NULL OR x.type = :type)
                    AND
                    (:#{#request.quantityMin} IS NULL OR :#{#request.quantityMin} <= x.quantity)
                    AND
                    (:#{#request.quantityMax} IS NULL OR :#{#request.quantityMax} >= x.quantity)
                    AND
                    (:#{#request.valueMin} IS NULL OR x.value >= :#{#request.valueMin})
                    AND
                    (:#{#request.valueMax} IS NULL OR x.value <= :#{#request.valueMax})
                    AND
                    (:#{#request.constraintMin} IS NULL OR :#{#request.constraintMin} <= x.constraint)
                    AND
                    (:#{#request.constraintMax} IS NULL OR :#{#request.constraintMax} >= x.constraint)
                    AND
                    (:#{#request.startDate} IS NULL OR x.startDate >= :#{#request.startDate})
                    AND
                    (:#{#request.endDate} IS NULL OR x.endDate >= :#{#request.endDate})
                    AND
                    (x.deleted = FALSE)
            """)
    Page<Voucher> findAllVoucher(@Param("request") AdminVoucherRequest request, Pageable pageable,
                                 @Param("status") VoucherStatus voucherStatus,
                                 @Param("type") VoucherType voucherType);


    @Query("""
                SELECT x FROM Voucher x
                WHERE
                    (:#{#request.q} IS NULL OR :#{#request.q} ILIKE '' OR x.name ILIKE CONCAT('%', :#{#request.q}, '%') OR x.code ILIKE CONCAT('%', :#{#request.q}, '%'))
                    AND
                    (:type IS NULL OR x.type = :type)
                    AND
                    (:#{#request.quantityMin} IS NULL OR :#{#request.quantityMin} <= x.quantity)
                    AND
                    (:#{#request.quantityMax} IS NULL OR :#{#request.quantityMax} >= x.quantity)
                    AND
                    (:#{#request.valueMin} IS NULL OR x.value >= :#{#request.valueMin})
                    AND
                    (:#{#request.valueMax} IS NULL OR x.value <= :#{#request.valueMax})
                    AND
                    (:#{#request.constraintMin} IS NULL OR :#{#request.constraintMin} <= x.constraint)
                    AND
                    (:#{#request.constraintMax} IS NULL OR :#{#request.constraintMax} >= x.constraint)
                    AND
                    (:#{#request.startDate} IS NULL OR x.startDate >= :#{#request.startDate})
                    AND
                    (:#{#request.endDate} IS NULL OR x.endDate >= :#{#request.endDate})
                    AND
                    (x.deleted = FALSE) AND (x.status = 0) AND (x.quantity > 0)
            """)
    Page<Voucher> findAllVoucherForClient(@Param("request") ClientVoucherRequest request, Pageable pageable,
                                          @Param("type") VoucherType voucherType);

    boolean existsByCodeAndDeletedFalse(String code);

    boolean existsByCodeAndDeletedFalseAndIdNotLike(String code, String id);


    @Modifying
    @Query("UPDATE Voucher e " +
            "SET e.status = " +
            "   CASE " +
            "       WHEN :currentTime < e.startDate THEN 3 " +
            "       WHEN :currentTime BETWEEN e.startDate AND e.endDate THEN 0 " +
            "       ELSE 2 " +
            "   END " +
            "WHERE e.deleted = FALSE AND e.status != 4 AND e.status != 1")
    @Transactional
    void updateStatusAutomatically(@Param("currentTime") Long currentTime);
}
