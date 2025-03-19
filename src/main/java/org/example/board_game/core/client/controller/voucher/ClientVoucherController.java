//package org.example.board_game.core.client.controller.voucher;
//
//import lombok.AccessLevel;
//import lombok.experimental.FieldDefaults;
//import org.example.board_game.core.admin.domain.dto.request.voucher.AdminVoucherRequest;
//import org.example.board_game.core.admin.domain.dto.response.voucher.AdminVoucherResponse;
//import org.example.board_game.core.admin.service.voucher.AdminVoucherService;
//import org.example.board_game.core.common.base.BaseController;
//import org.example.board_game.utils.Response;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/admin/voucher")
//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//public class ClientVoucherController extends BaseController<AdminVoucherResponse, String, AdminVoucherRequest> {
//
//    AdminVoucherService adminVoucherService;
//
//    public ClientVoucherController(AdminVoucherService service) {
//        super(service);
//        this.adminVoucherService = service;
//    }
//
//    @PostMapping("/deactivate/{id}")
//    public Response<Object> deactivate(@PathVariable("id") String id) {
//        return adminVoucherService.deactivateVoucher(id);
//    }
//}
