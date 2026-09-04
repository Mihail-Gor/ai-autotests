package org.example.api.database.models;

import lombok.Builder;
import org.example.api.database.ResultSetMapper;

import java.sql.Timestamp;
import java.time.Instant;

@Builder(toBuilder = true)
public record UserRecord(
        Long id,
        String username,
        String email,
        String firstName,
        String lastName,
        String role,
        String status,
        Instant createdAt
) {
    public static final ResultSetMapper<UserRecord> MAPPER = rs -> {
        Timestamp ts = rs.getTimestamp("created_at");
        return UserRecord.builder()
                .id(rs.getLong("id"))
                .username(rs.getString("username"))
                .email(rs.getString("email"))
                .firstName(rs.getString("first_name"))
                .lastName(rs.getString("last_name"))
                .role(rs.getString("role"))
                .status(rs.getString("status"))
                .createdAt(ts != null ? ts.toInstant() : null)
                .build();
    };
}
