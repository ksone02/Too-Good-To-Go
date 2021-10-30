package com.toogoodtogo.application.shop;

import com.toogoodtogo.domain.shop.Shop;
import com.toogoodtogo.domain.shop.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopService implements ShopUseCase {
    private final ShopRepository shopRepository;

    @Override
    public List<Shop> findAllShops() {
        return shopRepository.findAll();
    }

    @Override
    @Transactional
    public Shop addShop(String name, String category, String image) {
        Shop shop = new Shop();
        shop.setName(name);
        shop.setImage(image);
        shop.setCategory(category);
        shopRepository.save(shop);
        return shop;
    }
}
