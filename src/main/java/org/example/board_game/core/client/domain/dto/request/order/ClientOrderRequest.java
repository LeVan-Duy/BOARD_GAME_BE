package org.example.board_game.core.client.domain.dto.request.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.client.domain.dto.request.customer.ClientAddressRequest;
import org.example.board_game.core.client.domain.dto.response.order.ClientTransactionInfoResponse;
import org.example.board_game.infrastructure.enums.OrderStatus;
import org.example.board_game.infrastructure.enums.PaymentMethod;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClientOrderRequest {

    String id;

    String customerId;

    String employeeId;

    String voucherId;

    ClientAddressRequest address;

    List<ClientCartItemRequest> cartItems;

    @NotBlank(message = "Số điện thoại không được để trống.")
    String phoneNumber;

    @NotBlank(message = "Vui lòng nhập đầy đủ họ tên.")
    String fullName;

    @NotBlank(message = "Vui lòng nhập email của bạn.")
    String email;

    @NotNull(message = "Phí ship không được bỏ trống.")
    float shippingMoney;

    String note;

    ClientTransactionInfoResponse transactionInfo;

    PaymentMethod paymentMethod;

    OrderStatus status;

}
