package com.isidrosantiago.booknetwork.book;

import com.isidrosantiago.booknetwork.file.FileUtils;
import com.isidrosantiago.booknetwork.history.BookTransactionHistory;
import org.springframework.stereotype.Service;

@Service
public class BookMapper {

    public Book toBook(CreateBookRequest request) {
        return Book.builder()
            .id(request.id())
            .title(request.title())
            .authorName(request.authorName())
            .synopsis(request.synopsis())
            .isbn(request.isbn())
            .archived(false)
            .shareable(request.sharable())
            .build();
    }

    public BookResponse toBookResponse(Book book) {
        return BookResponse.builder()
            .id(book.getId())
            .title(book.getTitle())
            .authorName(book.getAuthorName())
            .isbn(book.getIsbn())
            .synopsis(book.getSynopsis())
            .rate(book.getRate())
            .archived(book.isArchived())
            .sharable(book.isShareable())
            .owner(book.getOwner().fullName())
            .cover(FileUtils.readFileFromLocation(book.getBookCover()))
            .build();
    }

    public BorrowedBookResponse toBorrowedBookResponse(BookTransactionHistory transaction) {
        return BorrowedBookResponse.builder()
            .id(transaction.getId())
            .title(transaction.getBook().getTitle())
            .authorName(transaction.getBook().getAuthorName())
            .isbn(transaction.getBook().getIsbn())
            .rate(transaction.getBook().getRate())
            .returned(transaction.isReturned())
            .returnApproved(transaction.isReturnedApproved())
            .build();
    }
}
