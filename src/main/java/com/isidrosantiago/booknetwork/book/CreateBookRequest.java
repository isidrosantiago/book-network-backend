package com.isidrosantiago.booknetwork.book;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CreateBookRequest(
    Long id,
    @NotNull(message = "Title is required")
    @NotEmpty(message = "Title is required")
    String title,
    String authorName,
    String synopsis,
    String isbn,
    boolean sharable
) {
}
