package org.example.api.tests;

import io.qameta.allure.AllureId;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Issue;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.qameta.allure.TmsLink;
import io.restassured.response.Response;
import org.example.api.data.ProductDataFactory;
import org.example.api.models.common.ErrorResponseDto;
import org.example.api.models.product.ProductDto;
import org.example.api.models.product.ProductsResponseDto;
import org.example.api.specifications.ResponseSpecs;
import org.example.common.annotations.Component;
import org.example.common.annotations.JiraIssue;
import org.example.common.annotations.Layer;
import org.example.common.annotations.Layers;
import org.example.common.annotations.Microservice;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.api.assertions.ProductAssert.assertThatProduct;

@Epic("DummyJSON E-Commerce API")
@Feature("Products Management")
@Layer(Layers.API)
@Microservice("product-catalog-service")
@Component("Products")
@Owner("QA Automation Engineer")
@Tag("regression")
@Tag("products")
@DisplayName("Products CRUD Test Suite")
public class ProductCrudTest extends BaseTest {

    @Nested
    @Story("Create Product")
    @DisplayName("Create (POST) Operations")
    class CreateOperations {

        @Test
        @AllureId("1001")
        @TmsLink("TMS-1001")
        @JiraIssue("SHOP-201")
        @Severity(SeverityLevel.BLOCKER)
        @DisplayName("POST /products/add - Successfully create product with full payload")
        @Description("Verifies that a new product can be created with all valid attributes and returns 201 Created.")
        void shouldCreateProductWithFullPayload() {
            ProductDto newProduct = ProductDataFactory.createValidProduct();

            ProductDto createdProduct = productClient.createProduct(newProduct);

            assertThatProduct(createdProduct)
                    .isNotNull()
                    .hasTitle(newProduct.getTitle())
                    .hasPrice(newProduct.getPrice())
                    .hasCategory(newProduct.getCategory());

            assertThat(createdProduct.getId())
                    .as("Product ID should be generated upon creation")
                    .isNotNull()
                    .isPositive();
        }

        @ParameterizedTest(name = "POST /products/add - Title: {0}, Price: {1}")
        @CsvSource({
                "Wireless Gaming Mouse, 49.99",
                "Mechanical RGB Keyboard, 129.50"
        })
        @AllureId("1002")
        @TmsLink("TMS-1002")
        @JiraIssue("SHOP-201")
        @Severity(SeverityLevel.CRITICAL)
        @DisplayName("POST /products/add - Successfully create product with minimal payload (Parameterized)")
        @Description("Verifies product creation with different minimal payloads.")
        void shouldCreateProductWithMinimalPayload(String title, Double price) {
            ProductDto minimalProduct = ProductDataFactory.createMinimalProduct(title, price);

            ProductDto createdProduct = productClient.createProduct(minimalProduct);

            assertThatProduct(createdProduct)
                    .isNotNull()
                    .hasTitle(title)
                    .hasPrice(price);

            assertThat(createdProduct.getId())
                    .as("Generated ID should be present")
                    .isNotNull();
        }
    }

    @Nested
    @Story("Read Product")
    @DisplayName("Read (GET) Operations")
    class ReadOperations {

        @Test
        @AllureId("1003")
        @TmsLink("TMS-1003")
        @JiraIssue("SHOP-202")
        @Severity(SeverityLevel.BLOCKER)
        @DisplayName("GET /products/{id} - Successfully get product by ID")
        @Description("Verifies that an existing product can be retrieved by its ID with correct attributes.")
        void shouldGetProductByIdSuccessfully() {
            int targetId = 1;

            ProductDto product = productClient.getProductById(targetId);

            assertThatProduct(product)
                    .isNotNull()
                    .hasId(targetId);

            assertThat(product.getTitle()).isNotBlank();
            assertThat(product.getPrice()).isPositive();
            assertThat(product.getCategory()).isNotBlank();
        }

        @Test
        @AllureId("1004")
        @TmsLink("TMS-1004")
        @JiraIssue("SHOP-202")
        @Severity(SeverityLevel.CRITICAL)
        @DisplayName("GET /products - Get paginated list of products")
        @Description("Verifies retrieval of products list with limit and skip parameters.")
        void shouldGetProductsListWithPagination() {
            int limit = 5;
            int skip = 0;

            ProductsResponseDto response = productClient.getProducts(limit, skip);

            assertThat(response).isNotNull();
            assertThat(response.getProducts())
                    .as("Products list should have exact size equal to limit")
                    .hasSize(limit);
            assertThat(response.getLimit()).isEqualTo(limit);
            assertThat(response.getSkip()).isEqualTo(skip);
            assertThat(response.getTotal()).isPositive();
        }

        @Test
        @AllureId("1005")
        @TmsLink("TMS-1005")
        @JiraIssue("SHOP-202")
        @Severity(SeverityLevel.NORMAL)
        @DisplayName("GET /products/search - Search products by query")
        @Description("Verifies searching products by keyword query.")
        void shouldSearchProductsByQuery() {
            String query = "phone";

            ProductsResponseDto response = productClient.searchProducts(query);

            assertThat(response).isNotNull();
            assertThat(response.getProducts())
                    .as("Search results should not be empty")
                    .isNotEmpty();

            assertThat(response.getProducts())
                    .allMatch(p -> p.getTitle().toLowerCase().contains(query)
                            || p.getDescription().toLowerCase().contains(query)
                            || p.getCategory().toLowerCase().contains(query),
                            "Each item should contain the search query in title, description, or category");
        }

        @Test
        @AllureId("1006")
        @TmsLink("TMS-1006")
        @JiraIssue("SHOP-202")
        @Severity(SeverityLevel.NORMAL)
        @DisplayName("GET /products/{id} - Return 404 for non-existing product")
        @Description("Verifies that requesting a non-existent product ID returns HTTP 404 Not Found.")
        void shouldReturn404ForNonExistingProduct() {
            int nonExistentId = 999999;

            Response response = productClient.getProductByIdRaw(nonExistentId);

            response.then().spec(ResponseSpecs.statusNotFound());

            ErrorResponseDto error = response.as(ErrorResponseDto.class);
            assertThat(error.getMessage())
                    .containsIgnoringCase("not found")
                    .contains(String.valueOf(nonExistentId));
        }
    }

    @Nested
    @Story("Update Product")
    @DisplayName("Update (PUT/PATCH) Operations")
    class UpdateOperations {

        @Test
        @AllureId("1007")
        @TmsLink("TMS-1007")
        @JiraIssue("SHOP-203")
        @Severity(SeverityLevel.CRITICAL)
        @DisplayName("PUT /products/{id} - Fully update product")
        @Description("Verifies full update of an existing product via PUT request.")
        void shouldFullyUpdateProductViaPut() {
            int productId = 1;
            ProductDto updateData = ProductDto.builder()
                    .title("Brand New Updated Phone")
                    .price(599.99)
                    .description("Upgraded technical specifications and features")
                    .category("smartphones")
                    .build();

            ProductDto updatedProduct = productClient.updateProduct(productId, updateData);

            assertThatProduct(updatedProduct)
                    .isNotNull()
                    .hasId(productId)
                    .hasTitle(updateData.getTitle())
                    .hasPrice(updateData.getPrice());
        }

        @Test
        @AllureId("1008")
        @TmsLink("TMS-1008")
        @JiraIssue("SHOP-203")
        @Severity(SeverityLevel.NORMAL)
        @DisplayName("PATCH /products/{id} - Partially update product title")
        @Description("Verifies partial update of a product attribute via PATCH request.")
        void shouldPartiallyUpdateProductViaPatch() {
            int productId = 2;
            String newTitle = "Patched Special Edition Title";
            ProductDto patchData = ProductDto.builder()
                    .title(newTitle)
                    .build();

            ProductDto patchedProduct = productClient.patchProduct(productId, patchData);

            assertThatProduct(patchedProduct)
                    .isNotNull()
                    .hasId(productId)
                    .hasTitle(newTitle);

            assertThat(patchedProduct.getPrice())
                    .as("Existing price should still be present after patching title")
                    .isNotNull()
                    .isPositive();
        }

        @Test
        @AllureId("1009")
        @TmsLink("TMS-1009")
        @JiraIssue("SHOP-203")
        @Severity(SeverityLevel.NORMAL)
        @DisplayName("PUT /products/{id} - Return 404 when updating non-existing product")
        @Description("Verifies that updating a non-existent product ID returns 404 Not Found.")
        void shouldReturn404WhenUpdatingNonExistingProduct() {
            int nonExistentId = 999999;
            ProductDto updateData = ProductDto.builder().title("Non existing").build();

            Response response = productClient.updateProductRaw(nonExistentId, updateData);

            response.then().spec(ResponseSpecs.statusNotFound());
        }
    }

    @Nested
    @Story("Delete Product")
    @DisplayName("Delete (DELETE) Operations")
    class DeleteOperations {

        @Test
        @AllureId("1010")
        @TmsLink("TMS-1010")
        @JiraIssue("SHOP-204")
        @Severity(SeverityLevel.CRITICAL)
        @DisplayName("DELETE /products/{id} - Successfully delete product")
        @Description("Verifies that a product can be deleted and response indicates deletion with timestamp.")
        void shouldDeleteProductByIdSuccessfully() {
            int productId = 1;

            ProductDto deletedProduct = productClient.deleteProduct(productId);

            assertThatProduct(deletedProduct)
                    .isNotNull()
                    .hasId(productId)
                    .isDeleted();
        }

        @Test
        @AllureId("1011")
        @TmsLink("TMS-1011")
        @JiraIssue("SHOP-204")
        @Severity(SeverityLevel.NORMAL)
        @DisplayName("DELETE /products/{id} - Return 404 when deleting non-existing product")
        @Description("Verifies that attempting to delete a non-existent product ID returns 404 Not Found.")
        void shouldReturn404WhenDeletingNonExistingProduct() {
            int nonExistentId = 999999;

            Response response = productClient.deleteProductRaw(nonExistentId);

            response.then().spec(ResponseSpecs.statusNotFound());
        }
    }
}
