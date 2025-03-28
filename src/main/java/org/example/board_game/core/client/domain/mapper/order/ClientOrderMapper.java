package org.example.board_game.core.client.domain.mapper.order;


import org.example.board_game.core.client.domain.dto.request.customer.ClientAddressRequest;
import org.example.board_game.core.client.domain.dto.request.order.ClientOrderRequest;
import org.example.board_game.core.client.domain.dto.response.order.ClientOrderDetailResponse;
import org.example.board_game.core.client.domain.dto.response.order.ClientOrderHistoryResponse;
import org.example.board_game.core.client.domain.dto.response.order.ClientOrderResponse;
import org.example.board_game.core.client.domain.dto.response.order.ClientPaymentResponse;
import org.example.board_game.entity.customer.Address;
import org.example.board_game.entity.order.Order;
import org.example.board_game.entity.order.OrderDetail;
import org.example.board_game.entity.order.OrderHistory;
import org.example.board_game.entity.payment.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ClientOrderMapper {

    ClientOrderMapper INSTANCE = Mappers.getMapper(ClientOrderMapper.class);

    @Mapping(target = "address", ignore = true)
    Order toEntity(ClientOrderRequest request);

    @Mapping(target = "voucher", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "payment", ignore = true)
    @Mapping(target = "orderDetails", ignore = true)
    @Mapping(target = "orderHistories", ignore = true)
    ClientOrderResponse toResponse(Order order);

    ClientPaymentResponse toPaymentRes(Payment payment);

    List<ClientOrderHistoryResponse> toOrderHistoriesRes(List<OrderHistory> histories);

    ClientOrderDetailResponse toOrderDetailRes(OrderDetail orderDetails);

    @Mapping(target = "id", ignore = true)
    void updateOrder(ClientOrderRequest request, @MappingTarget Order order);
}
