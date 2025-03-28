package com.isidrosantiago.booknetwork.feedback;

import jakarta.validation.constraints.*;

public record CreateFeedbackRequest(
    @NotNull(message = "Rating cannot be null")
    @DecimalMin(value = "0.0", message = "Rating must be greater than or equal to 0")
    @DecimalMax(value = "5.0", message = "Rating must be less than or equal to 5")
    Double rating,

    @NotNull(message = "Comment is required")
    @NotEmpty(message = "Comment is required")
    String comment,

    @NotNull(message = "Book id is required")
    Long bookId
) {
}
