package com.isidrosantiago.booknetwork.book;

import com.isidrosantiago.booknetwork.book.response.SaveBookResponse;
import com.isidrosantiago.booknetwork.common.PageResponse;
import com.isidrosantiago.booknetwork.response.CustomResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("books")
@RequiredArgsConstructor
@Tag(name = "Book")
public class BookController {

    private final BookService service;

    @PostMapping
    public ResponseEntity<?> saveBook(@Valid @RequestBody CreateBookRequest request, Authentication connectedUser) {
        var bookId = service.save(request, connectedUser);
        var response = CustomResponse.<SaveBookResponse>builder()
            .status("success")
            .message("Book has been saved")
            .data(new SaveBookResponse(bookId))
            .build();

        return ResponseEntity.status(CREATED).body(response);
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getBookById(@PathVariable("id") Long id) {
        BookResponse book = service.getBook(id);
        var response = CustomResponse.<BookResponse>builder()
            .status("success")
            .data(book)
            .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<?> getBooks(
        @RequestParam(name = "page", defaultValue = "0", required = false) int page,
        @RequestParam(name = "size", defaultValue = "10", required = false) int size,
        Authentication connectedUser
    ) {
        PageResponse<BookResponse> books = service.getBooks(page, size, connectedUser);
        var response = CustomResponse.<PageResponse<BookResponse>>builder()
            .status("success")
            .data(books)
            .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/owner")
    public ResponseEntity<?> getBooksByOwner(
        @RequestParam(name = "page", defaultValue = "0", required = false) int page,
        @RequestParam(name = "size", defaultValue = "10", required = false) int size,
        Authentication connectedUser
    ) {
        PageResponse<BookResponse> books = service.getBooksByOwner(page, size, connectedUser);
        var response = CustomResponse.<PageResponse<BookResponse>>builder()
            .status("success")
            .data(books)
            .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/borrowed")
    public ResponseEntity<?> getBorrowedBooks(
        @RequestParam(name = "page", defaultValue = "0", required = false) int page,
        @RequestParam(name = "size", defaultValue = "10", required = false) int size,
        Authentication connectedUser
    ) {
        PageResponse<BorrowedBookResponse> books = service.getBorrowedBooks(page, size, connectedUser);
        var response = CustomResponse.<PageResponse<BorrowedBookResponse>>builder()
            .status("success")
            .data(books)
            .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/returned")
    public ResponseEntity<?> getReturnedBooks(
        @RequestParam(name = "page", defaultValue = "0", required = false) int page,
        @RequestParam(name = "size", defaultValue = "10", required = false) int size,
        Authentication connectedUser
    ) {
        PageResponse<BorrowedBookResponse> books = service.getReturnedBooks(page, size, connectedUser);
        var response = CustomResponse.<PageResponse<BorrowedBookResponse>>builder()
            .status("success")
            .data(books)
            .build();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/shareable/{book-id}")
    public ResponseEntity<?> updateSharableStatus(
        @PathVariable("book-id") Long bookId,
        Authentication connectedUser
    ) {
        Long id = service.updateSharableStatus(bookId, connectedUser);
        var response = CustomResponse.<String>builder()
            .status("success")
            .message("Book sharable status with ID: " + id + " has been updated")
            .build();

        return ResponseEntity.ok(response);
    }


    @PatchMapping("/archive/{book-id}")
    public ResponseEntity<?> updateArchiveStatus(
        @PathVariable("book-id") Long bookId,
        Authentication connectedUser
    ) {
        Long id = service.updateArchiveStatus(bookId, connectedUser);
        var response = CustomResponse.<String>builder()
            .status("success")
            .message("Book archive status with ID: " + id + " has been updated")
            .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/borrowed/{book-id}")
    public ResponseEntity<?> borrowBook(
        @PathVariable("book-id") Long bookId,
        Authentication connectedUser
    ) {
        Long id = service.borrowBook(bookId, connectedUser);
        var response = CustomResponse.<String>builder()
            .status("success")
            .message("You have borrowed the book with ID: " + id)
            .build();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/borrow/return/{book-id}")
    public ResponseEntity<?> returnBorrowedBook(
        @PathVariable("book-id") Long bookId,
        Authentication connectedUser
    ) {
        Long id = service.returnBorrowedBook(bookId, connectedUser);
        var response = CustomResponse.<String>builder()
            .status("success")
            .message("You have returned the book with ID: " + id)
            .build();
        return ResponseEntity.ok(response);
    }


    @PatchMapping("/borrow/return/approve/{book-id}")
    public ResponseEntity<?> approveReturnBorrowedBook(
        @PathVariable("book-id") Long bookId,
        Authentication connectedUser
    ) {
        Long id = service.approveReturnBorrowedBook(bookId, connectedUser);
        var response = CustomResponse.<String>builder()
            .status("success")
            .message("You have approve the return of the book with ID: " + id)
            .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/cover/{book-id}", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadBookCoverImage(
        @PathVariable("book-id") Long bookId,
        @Parameter()
        @RequestPart("file") MultipartFile file,
        Authentication connectedUser
    ) {
        service.uploadCoverImage(file, bookId, connectedUser);
        var response = CustomResponse.<String>builder()
            .status("success")
            .message("Your cover image for book with ID: " + bookId + " has been uploaded")
            .build();
        return ResponseEntity.ok(response);
    }
}
