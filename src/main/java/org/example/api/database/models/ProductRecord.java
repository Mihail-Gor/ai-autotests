package org.example.api.database.models;

import lombok.Builder;
import org.example.api.database.ResultSetMapper;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;

@Builder(toBuilder = true)
public record ProductRecord(
        Long id,
        String title,
        String description,
        BigDecimal price,
        String category,
        Integer stock,
        Instant createdAt
) {
    public static final ResultSetMapper<ProductRecord> MAPPER = rs -> {
        Timestamp ts = rs.getTimestamp("created_at");
        return ProductRecord.builder()
                .id(rs.getLong("id"))
                .title(rs.getString("title"))
                .description(rs.getString("description"))
                .price(rs.getBigDecimal("price"))
                .category(rs.getString("category"))
                .stock(rs.getInt("stock"))
                .createdAt(ts != null ? ts.toInstant() : null)
                .build();
    };
}
