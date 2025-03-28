package com.isidrosantiago.booknetwork.feedback;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@Builder
public class CreateFeedbackResponse {
    private Long id;
    private Double rating;
    private String comment;
}
