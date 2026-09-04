package org.example.api.clients;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.example.api.models.product.ProductDto;
import org.example.api.models.product.ProductsResponseDto;
import org.example.api.specifications.ResponseSpecs;

public class ProductClient extends BaseClient {

    private static final String PRODUCTS_ENDPOINT = "/products";
    private static final String PRODUCT_BY_ID_ENDPOINT = "/products/{id}";
    private static final String PRODUCT_SEARCH_ENDPOINT = "/products/search";
    private static final String PRODUCT_ADD_ENDPOINT = "/products/add";

    @Step("Get list of all products")
    public ProductsResponseDto getAllProducts() {
        return getAllProductsRaw()
                .then()
                .spec(ResponseSpecs.statusOk())
                .extract()
                .as(ProductsResponseDto.class);
    }

    @Step("Get products with limit: {limit}, skip: {skip}")
    public ProductsResponseDto getProducts(int limit, int skip) {
        return getRequestSpec()
                .queryParam("limit", limit)
                .queryParam("skip", skip)
                .when()
                .get(PRODUCTS_ENDPOINT)
                .then()
                .spec(ResponseSpecs.statusOk())
                .extract()
                .as(ProductsResponseDto.class);
    }

    @Step("Search products by query: '{query}'")
    public ProductsResponseDto searchProducts(String query) {
        return getRequestSpec()
                .queryParam("q", query)
                .when()
                .get(PRODUCT_SEARCH_ENDPOINT)
                .then()
                .spec(ResponseSpecs.statusOk())
                .extract()
                .as(ProductsResponseDto.class);
    }

    @Step("Send raw GET request for products list")
    public Response getAllProductsRaw() {
        return getRequestSpec()
                .when()
                .get(PRODUCTS_ENDPOINT);
    }

    @Step("Get product by ID: {id}")
    public ProductDto getProductById(int id) {
        return getProductByIdRaw(id)
                .then()
                .spec(ResponseSpecs.statusOk())
                .extract()
                .as(ProductDto.class);
    }

    @Step("Send raw GET request for product with ID: {id}")
    public Response getProductByIdRaw(Object id) {
        return getRequestSpec()
                .pathParam("id", id)
                .when()
                .get(PRODUCT_BY_ID_ENDPOINT);
    }

    @Step("Create new product: {product.title}")
    public ProductDto createProduct(ProductDto product) {
        return createProductRaw(product)
                .then()
                .spec(ResponseSpecs.statusCreated())
                .extract()
                .as(ProductDto.class);
    }

    @Step("Send raw POST request to add product")
    public Response createProductRaw(Object body) {
        return getRequestSpec()
                .body(body)
                .when()
                .post(PRODUCT_ADD_ENDPOINT);
    }

    @Step("Update product with ID: {id} via PUT")
    public ProductDto updateProduct(int id, ProductDto product) {
        return updateProductRaw(id, product)
                .then()
                .spec(ResponseSpecs.statusOk())
                .extract()
                .as(ProductDto.class);
    }

    @Step("Send raw PUT request to update product with ID: {id}")
    public Response updateProductRaw(Object id, Object body) {
        return getRequestSpec()
                .pathParam("id", id)
                .body(body)
                .when()
                .put(PRODUCT_BY_ID_ENDPOINT);
    }

    @Step("Partially update product with ID: {id} via PATCH")
    public ProductDto patchProduct(int id, ProductDto product) {
        return patchProductRaw(id, product)
                .then()
                .spec(ResponseSpecs.statusOk())
                .extract()
                .as(ProductDto.class);
    }

    @Step("Send raw PATCH request to update product with ID: {id}")
    public Response patchProductRaw(Object id, Object body) {
        return getRequestSpec()
                .pathParam("id", id)
                .body(body)
                .when()
                .patch(PRODUCT_BY_ID_ENDPOINT);
    }

    @Step("Delete product with ID: {id}")
    public ProductDto deleteProduct(int id) {
        return deleteProductRaw(id)
                .then()
                .spec(ResponseSpecs.entityDeleted())
                .extract()
                .as(ProductDto.class);
    }

    @Step("Send raw DELETE request for product with ID: {id}")
    public Response deleteProductRaw(Object id) {
        return getRequestSpec()
                .pathParam("id", id)
                .when()
                .delete(PRODUCT_BY_ID_ENDPOINT);
    }
}
