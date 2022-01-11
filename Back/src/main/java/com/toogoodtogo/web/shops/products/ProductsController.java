package com.toogoodtogo.web.shops.products;

import com.toogoodtogo.application.shop.product.ProductUseCase;
import com.toogoodtogo.configuration.security.CurrentUser;
import com.toogoodtogo.domain.shop.ShopRepository;
import com.toogoodtogo.domain.user.User;
import com.toogoodtogo.web.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductsController {
    private final ProductUseCase productUseCase;
    private final ShopRepository shopRepository;

    @GetMapping("/products")
    public ApiResponse<List<ProductDto>> findAllProducts() {
        return new ApiResponse<>(productUseCase.findAllProducts());
    }

    @GetMapping("/shop/{shopId}/products")
    public ApiResponse<List<ProductDto>> findProducts(@PathVariable Long shopId) {
        return new ApiResponse<>(productUseCase.findProducts(shopId));
    }

    @PostMapping("/manager/shop/{shopId}/product")
    public ApiResponse<ProductDto> addProduct(@CurrentUser User user, @PathVariable Long shopId, @RequestBody AddProductRequest request) {
        return new ApiResponse<>(productUseCase.addProduct(user.getId(), shopId, request/*.toServiceDto()*/));
    }

    @PatchMapping("/manager/shop/{shopId}/product/{productId}")
    public ApiResponse<ProductDto> updateProduct(
            @CurrentUser User user, @PathVariable Long productId, @RequestBody UpdateProductRequest request) {
        return new ApiResponse<>(productUseCase.updateProduct(user.getId(), productId, request));
    }

    @DeleteMapping("/manager/shop/{shopId}/product/{productId}")
    public ApiResponse<Long> deleteProduct(@CurrentUser User user, @PathVariable Long productId) {
        productUseCase.deleteProduct(user.getId(), productId);
        return new ApiResponse<>(0L);
    }
}
