package org.example.board_game.core.admin.domain.dto.response.order;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.admin.domain.dto.response.customer.AdminCustomerResponse;
import org.example.board_game.core.client.domain.dto.response.order.ClientOrderDetailResponse;
import org.example.board_game.core.client.domain.dto.response.order.ClientOrderHistoryResponse;
import org.example.board_game.core.client.domain.dto.response.order.ClientPaymentResponse;
import org.example.board_game.core.common.base.BaseResponse;
import org.example.board_game.core.common.dto.AddressResponse;
import org.example.board_game.infrastructure.enums.OrderStatus;
import org.example.board_game.infrastructure.enums.OrderType;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminOrderResponse {

    String id;

    AdminCustomerResponse customerRes;

    BaseResponse voucher;

    AddressResponse address;

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

    Long createdAt;
}
