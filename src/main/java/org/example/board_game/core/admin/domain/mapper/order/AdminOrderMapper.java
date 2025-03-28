package org.example.board_game.core.admin.domain.mapper.order;

import org.example.board_game.core.admin.domain.dto.response.customer.AdminCustomerResponse;
import org.example.board_game.core.admin.domain.dto.response.order.AdminOrderResponse;
import org.example.board_game.core.client.domain.dto.response.order.ClientOrderDetailResponse;
import org.example.board_game.core.client.domain.dto.response.order.ClientOrderHistoryResponse;
import org.example.board_game.core.client.domain.dto.response.order.ClientOrderResponse;
import org.example.board_game.core.client.domain.dto.response.order.ClientPaymentResponse;
import org.example.board_game.core.common.dto.AddressResponse;
import org.example.board_game.core.common.dto.ProductResponse;
import org.example.board_game.entity.customer.Address;
import org.example.board_game.entity.customer.Customer;
import org.example.board_game.entity.order.Order;
import org.example.board_game.entity.order.OrderDetail;
import org.example.board_game.entity.order.OrderHistory;
import org.example.board_game.entity.payment.Payment;
import org.example.board_game.entity.product.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface AdminOrderMapper  {

    AdminOrderMapper INSTANCE = Mappers.getMapper(AdminOrderMapper.class);

    List<ClientOrderResponse> toResponseList(List<Order> orders);

    AdminOrderResponse toRes(Order order);

    AddressResponse toAddressRes(Address address);

    AdminCustomerResponse toCustomerRes(Customer customer);

    ClientPaymentResponse toPaymentRes(Payment payment);

    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "publisher", ignore = true)
    @Mapping(target = "author", ignore = true)
    ProductResponse toProductRes(Product entity);

    ClientOrderDetailResponse toOrderDetailRes(OrderDetail orderDetails);

    List<ClientOrderHistoryResponse> toOrderHistoriesRes(List<OrderHistory> histories);


}
