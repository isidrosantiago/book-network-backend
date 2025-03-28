package com.isidrosantiago.booknetwork.feedback;

import com.isidrosantiago.booknetwork.book.Book;
import com.isidrosantiago.booknetwork.book.BookRepository;
import com.isidrosantiago.booknetwork.common.PageResponse;
import com.isidrosantiago.booknetwork.exception.OperationNotPermittedException;
import com.isidrosantiago.booknetwork.user.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final BookRepository bookRepository;
    private final FeedbackMapper feedbackMapper;

    public CreateFeedbackResponse createFeedback(@Valid CreateFeedbackRequest request, Authentication connectedUser) {
        Book book = bookRepository.findById(request.bookId())
            .orElseThrow(() -> new EntityNotFoundException("Book with ID: " + request.bookId() + " was not found"));

        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException(
                "You cannot give feedback for an archived or not shareable book"
            );
        }

        User user = (User) connectedUser.getPrincipal();
        if (Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot give feedback to your own book");
        }

        Feedback savedFeedback = feedbackMapper.toFeedback(request);

        savedFeedback = feedbackRepository.save(savedFeedback);

        return CreateFeedbackResponse.builder()
            .id(savedFeedback.getId())
            .rating(savedFeedback.getRating())
            .comment(savedFeedback.getComment())
            .build();
    }

    public PageResponse<FeedbackResponse> findAllFeedbacksByBook(Integer bookId, int page, int size, Authentication connectedUser) {
        Pageable pageable = PageRequest.of(page, size);

        User user = (User) connectedUser.getPrincipal();
        Page<Feedback> feedbacks = feedbackRepository.findAllByBookId(pageable, bookId);

        List<FeedbackResponse> feedbackResponses = feedbacks.stream()
            .map(f -> feedbackMapper.toFeedbackResponse(f, user.getId()))
            .toList();

        return new PageResponse<>(
            feedbackResponses,
            feedbacks.getNumber(),
            feedbacks.getSize(),
            feedbacks.getTotalElements(),
            feedbacks.getTotalPages(),
            feedbacks.isFirst(),
            feedbacks.isLast()
        );
    }
}
