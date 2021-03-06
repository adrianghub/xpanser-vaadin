package com.kodilla.project.xpanser.backend.service;

import com.kodilla.project.xpanser.backend.entity.*;
import com.kodilla.project.xpanser.backend.repository.CategoryRepository;
import com.kodilla.project.xpanser.backend.repository.ProductRepository;
import com.kodilla.project.xpanser.backend.repository.ShopRepository;
import com.kodilla.project.xpanser.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class XpanserService {

    private final ProductRepository productRepository;
    private final ShopRepository shopRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public XpanserService(
            ProductRepository productRepository,
            ShopRepository shopRepository,
            CategoryRepository categoryRepository,
            UserRepository userRepository
    ) {
        this.productRepository = productRepository;
        this.shopRepository = shopRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    public List<Product> findAllProducts(String productFilter) {
        if (productFilter == null || productFilter.isEmpty()) {
            return productRepository.findAll();
        } else {
            return productRepository.search(productFilter);
        }
    }

    public long countProducts() {
        return productRepository.count();
    }

    public void deleteProduct(Product product) {
        productRepository.delete(product);
    }

    public void saveProduct(Product product) {
        if (product == null) {
            System.err.println("Cannot find product.");
            return;
        }
        productRepository.save(product);
    }

    public List<Shop> findAllShops() {
        return shopRepository.findAll();
    }

    public List<Category> findAllCategories() {
        return categoryRepository.findAll();
    }

    @PostConstruct
    public void loadData() {

        if (userRepository.count() == 0) {
            userRepository.save(new User("Andrzej", "admin1@secret.co", "admin1", Role.ADMIN, true));
        }

        if (shopRepository.count() == 0) {
            shopRepository.saveAll(
                    Stream.of("Biedronka", "Lidl", "Orlen", "Spoldzienia mieszkaniowa", "Spotify")
                            .map(Shop::new)
                            .collect(Collectors.toList()));
        }

        if (categoryRepository.count() == 0) {
            categoryRepository
                    .saveAll(Stream.of("Groceries", "Gifts", "Holidays", "Car", "Rent", "Subscriptions")
                            .map(Category::new).collect(Collectors.toList()));
        }

        if (productRepository.count() == 0) {
            Random r = new Random(0);
            List<com.kodilla.project.xpanser.backend.entity.Shop> shops = shopRepository.findAll();
            List<Category> categories = categoryRepository.findAll();
            productRepository.saveAll(
                    Stream.of("Vacuum 13.40", "MonthlyPayment 19.99")
                            .map(properties -> {
                                String[] split = properties.split(" ");
                                Product product = new Product();
                                product.setName(split[0]);
                                product.setPrice(Double.parseDouble(split[1]));
                                product.setShop(shops.get(r.nextInt(shops.size())));
                                product.setCategory(categories.get(r.nextInt(categories.size())));
                                product.setDate(LocalDate.now());
                                return product;
                            }).collect(Collectors.toList()));
        }
    }

}