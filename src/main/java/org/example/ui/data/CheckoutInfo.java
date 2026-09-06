package org.example.ui.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.datafaker.Faker;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutInfo {

    private String firstName;
    private String lastName;
    private String postalCode;

    private static final Faker FAKER = new Faker();

    public static CheckoutInfo random() {
        return CheckoutInfo.builder()
                .firstName(FAKER.name().firstName())
                .lastName(FAKER.name().lastName())
                .postalCode(FAKER.address().zipCode())
                .build();
    }

    public static CheckoutInfo standard() {
        return CheckoutInfo.builder()
                .firstName("John")
                .lastName("Doe")
                .postalCode("12345")
                .build();
    }
}
