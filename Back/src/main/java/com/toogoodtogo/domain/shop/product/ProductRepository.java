package com.toogoodtogo.domain.shop.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByShopId(Long ShopId);
    List<Product> findAll();

    @Modifying
    @Query("delete from Product p where p.shop.id = ?1")
    void deleteByShopId(Long shopId);
}
