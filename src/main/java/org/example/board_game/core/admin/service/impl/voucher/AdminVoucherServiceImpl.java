package org.example.board_game.core.admin.service.impl.voucher;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.admin.domain.dto.request.voucher.AdminVoucherRequest;
import org.example.board_game.core.admin.domain.dto.response.employee.AdminEmployeeResponse;
import org.example.board_game.core.admin.domain.dto.response.voucher.AdminVoucherResponse;
import org.example.board_game.core.admin.domain.mapper.voucher.AdminVoucherMapper;
import org.example.board_game.core.admin.service.voucher.AdminVoucherService;
import org.example.board_game.core.common.PageableObject;
import org.example.board_game.core.common.base.EntityService;
import org.example.board_game.entity.employee.Employee;
import org.example.board_game.entity.voucher.Voucher;
import org.example.board_game.infrastructure.constants.EntityProperties;
import org.example.board_game.infrastructure.enums.VoucherStatus;
import org.example.board_game.infrastructure.enums.VoucherType;
import org.example.board_game.infrastructure.exception.ApiException;
import org.example.board_game.repository.voucher.VoucherRepository;
import org.example.board_game.utils.ConvertUtil;
import org.example.board_game.utils.PaginationUtil;
import org.example.board_game.utils.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminVoucherServiceImpl implements AdminVoucherService {

    EntityService entityService;
    VoucherRepository voucherRepository;
    AdminVoucherMapper voucherMapper = AdminVoucherMapper.INSTANCE;

    @Override
    public Response<PageableObject<AdminVoucherResponse>> findAll(AdminVoucherRequest request) {
        Pageable pageable = PaginationUtil.pageable(request);
        Page<Voucher> page = voucherRepository.findAllVoucher(request, pageable, request.getStatus(), request.getType());
        Page<AdminVoucherResponse> resPage = page.map(voucherMapper::toResponse);
        PageableObject<AdminVoucherResponse> pageableObject = new PageableObject<>(resPage);
        return Response.of(pageableObject).success(EntityProperties.SUCCESS, EntityProperties.CODE_GET);
    }

    @Override
    public Response<Object> create(AdminVoucherRequest request) {

        boolean isExistCode = voucherRepository.existsByCodeAndDeletedFalse(request.getCode());
        if (isExistCode) {
            throw new ApiException("Mã voucher này đã tồn tại.");
        }
        validConstraint(request);
        Voucher voucher = voucherMapper.toEntity(request);
        Long startDate = request.getStartDate();
        Long endDate = request.getEndDate();
        Long currentDate = ConvertUtil.convertDateToLong(new Date());
        VoucherStatus voucherStatus = voucherStatus(currentDate, startDate, endDate);
        voucher.setStatus(voucherStatus);
        voucherRepository.save(voucher);
        return Response.ok().success(EntityProperties.SUCCESS, EntityProperties.CODE_POST);
    }

    @Override
    public Response<Object> update(AdminVoucherRequest request, String id) {

        boolean isExistCode = voucherRepository.existsByCodeAndDeletedFalseAndIdNotLike(request.getCode(), id);
        if (isExistCode) {
            throw new ApiException("Mã voucher này đã tồn tại.");
        }
        validConstraint(request);
        Voucher voucher = entityService.getVoucher(id);
        Long endDate = request.getEndDate();
        Long startDate = request.getStartDate();
        Long currentDate = ConvertUtil.convertDateToLong(new Date());
        VoucherStatus voucherStatus = voucherStatus(currentDate, startDate, endDate);
        voucher.setStatus(voucherStatus);
        voucherRepository.save(voucher);
        return Response.ok().success(EntityProperties.SUCCESS, EntityProperties.CODE_POST);
    }

    @Override
    public Response<AdminVoucherResponse> findById(String id) {
        Voucher voucher = entityService.getVoucher(id);
        AdminVoucherResponse response = voucherMapper.toResponse(voucher);
        return Response.of(response);
    }

    @Override
    public Response<Object> delete(String id) {
        Voucher voucher = entityService.getVoucher(id);
        voucher.setDeleted(true);
        voucherRepository.save(voucher);
        return Response.ok().success(EntityProperties.SUCCESS, EntityProperties.CODE_POST);
    }

    private VoucherStatus voucherStatus(Long currentDate, Long startDate, Long endDate) {

        if (currentDate >= startDate && currentDate <= endDate) {
            return VoucherStatus.ACTIVE;
        } else if (currentDate < startDate) {
            return VoucherStatus.UP_COMING;
        } else {
            return VoucherStatus.EXPIRED;
        }
    }

    private void validConstraint(AdminVoucherRequest request) {
        if (request.getType() == VoucherType.PERCENTAGE) {
            if (request.getValue() > 70) {
                throw new ApiException("Loại voucher theo phần trăm giá trị không được vượt quá 70% của đơn hàng.");
            }
        } else {
            if (request.getValue() >= request.getConstraint()) {
                throw new ApiException("Số tiền được giảm không được vượt quá điều kiện.");
            }
        }
        if (request.getEndDate() < request.getStartDate()) {
            throw new ApiException("Ngày kết thúc phải sau ngày bắt đầu.");
        }
    }

    @Override
    public Response<Object> deactivateVoucher(String voucherId) {

        Voucher voucher = entityService.getVoucher(voucherId);
        if (voucher.getStatus() == VoucherStatus.EXPIRED || voucher.getStatus() == VoucherStatus.IN_ACTIVE) {
            throw new ApiException("Voucher đã hết hạn hoặc ngừng hoạt động.");
        }
        if (voucher.getStatus() == VoucherStatus.ACTIVE || voucher.getStatus() == VoucherStatus.UP_COMING) {
            voucher.setStatus(VoucherStatus.CANCELLED);
        } else {
            voucher.setStatus(VoucherStatus.ACTIVE);
        }
        voucherRepository.save(voucher);
        return Response.ok().success(EntityProperties.SUCCESS, EntityProperties.CODE_POST);
    }
}
