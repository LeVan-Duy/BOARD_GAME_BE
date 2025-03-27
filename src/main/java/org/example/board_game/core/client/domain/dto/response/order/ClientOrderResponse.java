package org.example.board_game.core.client.domain.dto.response.order;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.client.domain.dto.response.customer.ClientAddressResponse;
import org.example.board_game.core.client.domain.dto.response.customer.ClientCustomerResponse;
import org.example.board_game.core.client.domain.dto.response.voucher.ClientVoucherResponse;
import org.example.board_game.infrastructure.enums.OrderStatus;
import org.example.board_game.infrastructure.enums.OrderType;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClientOrderResponse {

    String id;

    ClientCustomerResponse customer;

    ClientVoucherResponse voucher;

    ClientAddressResponse address;

    ClientPaymentResponse payment;

    List<ClientOrderDetailResponse> orderDetails;

    List<ClientOrderHistoryResponse> orderHistories;

    String phoneNumber;

    String fullName;

    String email;

    Float originMoney;

    Float reduceMoney;

    Float totalMoney;

    Float shippingMoney;

    Long confirmationDate;

    Long expectedDeliveryDate;

    Long deliveryStartDate;

    Long receivedDate;

    OrderType type;

    String note;

    OrderStatus status;
}
