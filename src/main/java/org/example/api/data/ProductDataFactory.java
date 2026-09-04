package org.example.api.data;

import net.datafaker.Faker;
import org.example.api.models.product.DimensionsDto;
import org.example.api.models.product.ProductDto;

import java.util.List;

public final class ProductDataFactory {

    private static final Faker FAKER = new Faker();

    private ProductDataFactory() {
    }

    public static ProductDto createValidProduct() {
        return ProductDto.builder()
                .title(FAKER.commerce().productName())
                .description(FAKER.lorem().sentence(10))
                .category(FAKER.commerce().department())
                .price(FAKER.number().randomDouble(2, 5, 1000))
                .discountPercentage(FAKER.number().randomDouble(2, 1, 30))
                .rating(FAKER.number().randomDouble(2, 1, 5))
                .stock(FAKER.number().numberBetween(1, 100))
                .brand(FAKER.company().name())
                .sku("SKU-" + FAKER.random().hex(8).toUpperCase())
                .weight(FAKER.number().randomDouble(1, 1, 10))
                .dimensions(DimensionsDto.builder()
                        .width(FAKER.number().randomDouble(1, 5, 50))
                        .height(FAKER.number().randomDouble(1, 5, 50))
                        .depth(FAKER.number().randomDouble(1, 5, 50))
                        .build())
                .tags(List.of(FAKER.commerce().department().toLowerCase(), "featured"))
                .warrantyInformation("1 year warranty")
                .shippingInformation("Ships in 2-3 days")
                .availabilityStatus("In Stock")
                .returnPolicy("30 days return policy")
                .minimumOrderQuantity(1)
                .build();
    }

    public static ProductDto createMinimalProduct(String title, Double price) {
        return ProductDto.builder()
                .title(title)
                .price(price)
                .build();
    }

    public static ProductDto createProductWithCustomData(String title, Double price, String category) {
        return ProductDto.builder()
                .title(title)
                .price(price)
                .category(category)
                .stock(FAKER.number().numberBetween(10, 50))
                .brand(FAKER.company().name())
                .build();
    }
}
