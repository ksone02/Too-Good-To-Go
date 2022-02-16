package com.toogoodtogo.application.search;

import com.toogoodtogo.application.shop.ShopUseCase;
import com.toogoodtogo.domain.shop.Shop;
import com.toogoodtogo.domain.shop.ShopRepository;
import com.toogoodtogo.domain.shop.product.ChoiceProduct;
import com.toogoodtogo.domain.shop.product.ChoiceProductRepository;
import com.toogoodtogo.domain.shop.product.ProductRepositorySupport;
import com.toogoodtogo.web.shops.dto.ShopDto;
import com.toogoodtogo.web.shops.products.dto.ProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SearchService {
//    @Autowired
    private final StringRedisTemplate redisTemplate;
    private final ShopUseCase shopUseCase;
    private final ShopRepository shopRepository;
    private final ChoiceProductRepository choiceProductRepository;
    private final ProductRepositorySupport productRepositorySupport;

    private ZSetOperations<String, String> redisRecentSearch; //TODO
    private String REDIS_KEY = "recentKeywords:";

    public List<ProductDto> searchProductsByShop(Long userId, String keyword) {
        redisRecentSearch = redisTemplate.opsForZSet();
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSSSSSSSS"));
        redisRecentSearch.add(REDIS_KEY + userId, keyword, Double.parseDouble(time));
        redisRecentSearch.removeRange(REDIS_KEY + userId, -(10 + 1), -(10 + 1));
        //DONE : 해당 Shop의 choice 혹은 가장 할인율 높은 ProductDto를 보여줘야....
        //TODO : 보여준 것들은 뭐가 filter?
        List<Shop> all = shopRepository.findAll(); // 이게 맞나....
        List<ProductDto> data = new ArrayList<>();
        List<ChoiceProduct> choiceProductList = choiceProductRepository.findAll();
        all.forEach(s -> {
            if(s.getName().contains(keyword) || s.getCategory().contains(keyword)) {
                ChoiceProduct byShopId = choiceProductRepository.findByShopId(s.getId());
                if(byShopId.getProduct() != null) { // 추천 상품이 있다면
                    data.add(new ProductDto(byShopId.getProduct()));
                }
                else { // 추천 상품이 없다면
                    data.add(new ProductDto(productRepositorySupport.choiceHighestRateProductPerShop(s.getId())));
                }
            }
        });
        return data;
//        return shopUseCase.findShopsBySearch(keyword);
    }

    public List<String> recentKeywords(Long userId) {
        redisRecentSearch = redisTemplate.opsForZSet();
        return new ArrayList<>(Objects.requireNonNull(
                redisRecentSearch.reverseRange(REDIS_KEY + userId, 0, -1)));
    }

    public void deleteRecentKeyword(Long userId, String keyword) {
        redisRecentSearch = redisTemplate.opsForZSet();
        redisRecentSearch.remove(REDIS_KEY + userId, keyword);
    }

    public void deleteRecentKeywordAll(Long userId) {
        redisRecentSearch = redisTemplate.opsForZSet();
        redisTemplate.delete(REDIS_KEY + userId);
    }
}
