package com.isidrosantiago.booknetwork.feedback;

import com.isidrosantiago.booknetwork.common.PageResponse;
import com.isidrosantiago.booknetwork.response.CustomResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("feedback")
@RequiredArgsConstructor
@Tag(name = "Feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<?> createFeedback(
        @Valid @RequestBody CreateFeedbackRequest request,
        Authentication connectedUser
    ) {
        CreateFeedbackResponse feedbackResponse = feedbackService.createFeedback(request, connectedUser);
        var response = CustomResponse.builder()
            .status("success")
            .data(feedbackResponse)
            .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/book/{book-id}")
    public ResponseEntity<?> findAllFeedbacksByBook(
        @PathVariable("book-id") Integer bookId,
        @RequestParam(name = "page", defaultValue = "0", required = false) int page,
        @RequestParam(name = "size", defaultValue = "10", required = false) int size,
        Authentication connectedUser
    ) {
        PageResponse<FeedbackResponse> feedbackPage = feedbackService.findAllFeedbacksByBook(bookId, page, size, connectedUser);
        var response = CustomResponse.<PageResponse<FeedbackResponse>>builder()
            .status("success")
            .data(feedbackPage)
            .build();
        return ResponseEntity.ok(response);
    }
}
