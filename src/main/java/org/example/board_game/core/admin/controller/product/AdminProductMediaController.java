package org.example.board_game.core.admin.controller.product;


import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.admin.domain.dto.request.product.AdminProductMediaRequest;
import org.example.board_game.core.admin.domain.dto.request.product.AdminUploadMainImageRequest;
import org.example.board_game.core.admin.service.product.AdminProductMediaService;
import org.example.board_game.utils.Response;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/media")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminProductMediaController {

    AdminProductMediaService productMediaService;

    @PostMapping("/add")
    public Response<Object> addImagesToProduct(@RequestBody @Valid AdminProductMediaRequest request) {
        return productMediaService.addImages(request);
    }

    @PostMapping("/delete")
    public Response<Object> deleted(@RequestBody @Valid AdminProductMediaRequest request) {
        return productMediaService.delete(request);
    }

    @PostMapping("/update-main")
    public Response<Object> updateMainMediaForProduct(@RequestBody @Valid AdminUploadMainImageRequest request) {
        return productMediaService.updateMainImage(request);
    }
}
