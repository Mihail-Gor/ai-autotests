package org.example.ui.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SortOption {
    NAME_AZ("az", "Name (A to Z)"),
    NAME_ZA("za", "Name (Z to A)"),
    PRICE_LOW_HIGH("lohi", "Price (low to high)"),
    PRICE_HIGH_LOW("hilo", "Price (high to low)");

    private final String value;
    private final String label;
}
