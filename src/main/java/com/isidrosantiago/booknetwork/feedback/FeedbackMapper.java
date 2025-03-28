package com.isidrosantiago.booknetwork.feedback;

import com.isidrosantiago.booknetwork.book.Book;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class FeedbackMapper {

    public Feedback toFeedback(CreateFeedbackRequest request) {
        return Feedback.builder()
            .rating(request.rating())
            .comment(request.comment())
            .book(Book.builder()
                .id(request.bookId())
                .build()
            )
            .build();
    }

    public FeedbackResponse toFeedbackResponse(Feedback feedback, Long id) {
        return FeedbackResponse.builder()
            .rating(feedback.getRating())
            .comment(feedback.getComment())
            .ownFeedback(Objects.equals(feedback.getCreatedBy(), id))
            .build();
    }
}
