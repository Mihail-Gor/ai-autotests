package org.example.api.data;

import net.datafaker.Faker;
import org.example.api.models.user.AddressDto;
import org.example.api.models.user.CompanyDto;
import org.example.api.models.user.UserDto;

public final class UserDataFactory {

    private static final Faker FAKER = new Faker();

    private UserDataFactory() {
    }

    public static UserDto createValidUser() {
        return UserDto.builder()
                .firstName(FAKER.name().firstName())
                .lastName(FAKER.name().lastName())
                .maidenName(FAKER.name().lastName())
                .age(FAKER.number().numberBetween(18, 65))
                .gender(FAKER.gender().binaryTypes())
                .email(FAKER.internet().emailAddress())
                .phone(FAKER.phoneNumber().cellPhone())
                .username(FAKER.internet().username())
                .password(FAKER.internet().password(8, 16, true, true, true))
                .birthDate("1995-06-15")
                .bloodGroup("A+")
                .height(FAKER.number().randomDouble(2, 150, 200))
                .weight(FAKER.number().randomDouble(2, 50, 100))
                .eyeColor(FAKER.color().name())
                .role("user")
                .address(AddressDto.builder()
                        .address(FAKER.address().streetAddress())
                        .city(FAKER.address().city())
                        .state(FAKER.address().state())
                        .stateCode(FAKER.address().stateAbbr())
                        .postalCode(FAKER.address().zipCode())
                        .country("United States")
                        .build())
                .company(CompanyDto.builder()
                        .name(FAKER.company().name())
                        .department(FAKER.company().profession())
                        .title(FAKER.job().title())
                        .build())
                .build();
    }

    public static UserDto createMinimalUser(String firstName, String lastName, String email) {
        return UserDto.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .age(25)
                .build();
    }
}
