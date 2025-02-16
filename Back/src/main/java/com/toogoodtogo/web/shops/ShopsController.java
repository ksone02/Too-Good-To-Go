package com.toogoodtogo.web.shops;

import com.toogoodtogo.advice.ValidationSequence;
import com.toogoodtogo.application.shop.ShopUseCase;
import com.toogoodtogo.configuration.security.CurrentUser;
import com.toogoodtogo.domain.user.User;
import com.toogoodtogo.web.common.ApiResponse;
import com.toogoodtogo.web.shops.dto.AddShopRequest;
import com.toogoodtogo.web.shops.dto.ShopDto;
import com.toogoodtogo.web.shops.dto.UpdateShopRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Positive;
import java.io.IOException;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ShopsController {
    private final ShopUseCase shopUseCase;

    @GetMapping("/shops")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<ShopDto>> findAllShops() {
        return new ApiResponse<>(shopUseCase.findAllShops());
    }

    @GetMapping("/manager/shops")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<ShopDto>> findShops(@CurrentUser User user) {
        return new ApiResponse<>(shopUseCase.findShops(user.getId()));
    }

    @GetMapping("/shops/{shopId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<ShopDto> findShop(@PathVariable Long shopId) {
        return new ApiResponse<>(shopUseCase.findShop(shopId));
    }

    @PostMapping("/manager/shops")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ShopDto> addShop(
            @CurrentUser User user,
            @RequestPart(required = false) MultipartFile file,
            @RequestPart @Validated(ValidationSequence.class) AddShopRequest request) throws IOException {
        return new ApiResponse<>(shopUseCase.addShop(user.getId(), file, request));
    }
    
    @PutMapping("/manager/shops/{shopId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<ShopDto> updateShop(
            @CurrentUser User user,
            @PathVariable @Positive(message = "path 오류") Long shopId,
            @RequestBody @Validated(ValidationSequence.class) UpdateShopRequest request) {
        return new ApiResponse<>(shopUseCase.updateShop(user.getId(), shopId, request));
    }

    @PostMapping("/manager/shops/{shopId}/image")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<String> updateProductImage(
            @CurrentUser User user,
            @PathVariable @Positive(message = "path 오류") Long shopId,
            @RequestPart MultipartFile file) throws IOException {
        return new ApiResponse<>(shopUseCase.updateShopImage(user.getId(), shopId, file));
    }

    @DeleteMapping("/manager/shops/{shopId}/image")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProductImage(
            @CurrentUser User user,
            @PathVariable @Positive(message = "path 오류") Long shopId) {
        shopUseCase.deleteShopImage(user.getId(), shopId);
    }


    @DeleteMapping("/manager/shops/{shopId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteShop
            (@CurrentUser User user,
             @PathVariable @Positive(message = "path 오류") Long shopId) {
        shopUseCase.deleteShop(user.getId(), shopId);
    }
}
