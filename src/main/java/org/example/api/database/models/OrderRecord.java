package org.example.api.database.models;

import lombok.Builder;
import org.example.api.database.ResultSetMapper;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;

@Builder(toBuilder = true)
public record OrderRecord(
        Long id,
        Long userId,
        Long productId,
        Integer quantity,
        BigDecimal totalAmount,
        String status,
        Instant createdAt
) {
    public static final ResultSetMapper<OrderRecord> MAPPER = rs -> {
        Timestamp ts = rs.getTimestamp("created_at");
        return OrderRecord.builder()
                .id(rs.getLong("id"))
                .userId(rs.getLong("user_id"))
                .productId(rs.getLong("product_id"))
                .quantity(rs.getInt("quantity"))
                .totalAmount(rs.getBigDecimal("total_amount"))
                .status(rs.getString("status"))
                .createdAt(ts != null ? ts.toInstant() : null)
                .build();
    };
}
