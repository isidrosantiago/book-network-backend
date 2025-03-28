package com.isidrosantiago.booknetwork.book;

import com.isidrosantiago.booknetwork.common.PageResponse;
import com.isidrosantiago.booknetwork.exception.OperationNotPermittedException;
import com.isidrosantiago.booknetwork.file.FileStorageService;
import com.isidrosantiago.booknetwork.history.BookTransactionHistory;
import com.isidrosantiago.booknetwork.history.BookTransactionRepository;
import com.isidrosantiago.booknetwork.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookMapper bookMapper;
    private final BookRepository bookRepository;
    private final BookTransactionRepository transactionRepository;
    private final FileStorageService fileStorageService;

    public Long save(CreateBookRequest request, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Book book = bookMapper.toBook(request);
        book.setOwner(user);
        var savedBook = bookRepository.save(book);
        return savedBook.getId();
    }

    public BookResponse getBook(Long id) {
        return bookRepository.findById(id)
            .map(bookMapper::toBookResponse)
            .orElseThrow(() -> new EntityNotFoundException("Book with ID: " + id + " not found"));
    }

    public PageResponse<BookResponse> getBooks(int page, int size, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAllDisplayableBooks(pageable, user.getId());
        return pageResponse(books);
    }

    public PageResponse<BookResponse> getBooksByOwner(int page, int size, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAll(BookSpecification.withOwnerId(user.getId()), pageable);
        return pageResponse(books);
    }

    public PageResponse<BorrowedBookResponse> getBorrowedBooks(int page, int size, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> allBorrowedBooks = transactionRepository.findAllBorrowedBooks(pageable, user.getId());
        List<BorrowedBookResponse> borrowedBookResponse = allBorrowedBooks.stream()
            .map(bookMapper::toBorrowedBookResponse)
            .toList();

        return new PageResponse<>(
            borrowedBookResponse,
            allBorrowedBooks.getNumber(),
            allBorrowedBooks.getSize(),
            allBorrowedBooks.getTotalElements(),
            allBorrowedBooks.getTotalPages(),
            allBorrowedBooks.isFirst(),
            allBorrowedBooks.isLast()
        );
    }

    public PageResponse<BorrowedBookResponse> getReturnedBooks(int page, int size, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> allBorrowedBooks = transactionRepository.findAllReturnedBooks(pageable, user.getId());
        List<BorrowedBookResponse> borrowedBookResponse = allBorrowedBooks.stream()
            .map(bookMapper::toBorrowedBookResponse)
            .toList();

        return new PageResponse<>(
            borrowedBookResponse,
            allBorrowedBooks.getNumber(),
            allBorrowedBooks.getSize(),
            allBorrowedBooks.getTotalElements(),
            allBorrowedBooks.getTotalPages(),
            allBorrowedBooks.isFirst(),
            allBorrowedBooks.isLast()
        );
    }

    public Long updateSharableStatus(Long bookId, Authentication connectedUser) {
        Book book = getBookFromDB(bookId);
        User user = (User) connectedUser.getPrincipal();
        if (!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot update other owners book shareable status");
        }
        book.setShareable(!book.isShareable());
        bookRepository.save(book);
        return bookId;
    }

    public Long updateArchiveStatus(Long bookId, Authentication connectedUser) {
        Book book = getBookFromDB(bookId);
        User user = (User) connectedUser.getPrincipal();
        if (!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot update other owners book archive status");
        }
        book.setArchived(!book.isArchived());
        bookRepository.save(book);
        return bookId;
    }

    public Long borrowBook(Long bookId, Authentication connectedUser) {
        Book book = getBookFromDB(bookId);
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException(
                "The requested book cannot be borrowed since it is archive or not sharable"
            );
        }

        User user = (User) connectedUser.getPrincipal();
        if (Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot borrow your own book");
        }

        final boolean isAlreadyBorrowed = transactionRepository.isAlreadyBorrowedByUser(bookId, user.getId());
        if (isAlreadyBorrowed) {
            throw new OperationNotPermittedException("The requested book is already borrowed");
        }

        BookTransactionHistory bookTransactionHistory = BookTransactionHistory.builder()
            .user(user)
            .book(book)
            .returned(false)
            .returned(false)
            .build();

        return transactionRepository.save(bookTransactionHistory).getId();
    }

    public Long returnBorrowedBook(Long bookId, Authentication connectedUser) {
        Book book = getBookFromDB(bookId);
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException(
                "The requested book cannot be borrowed since it is archive or not sharable"
            );
        }

        User user = (User) connectedUser.getPrincipal();
        if (Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot return your own book");
        }

        BookTransactionHistory transaction = transactionRepository.findByBookIdAndUserId(bookId, user.getId())
            .orElseThrow(() -> new OperationNotPermittedException("You did not borrowed this book"));

        transaction.setReturned(true);
        return transactionRepository.save(transaction).getId();
    }

    public Long approveReturnBorrowedBook(Long bookId, Authentication connectedUser) {
        Book book = getBookFromDB(bookId);
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException(
                "The requested book cannot be borrowed since it is archive or not sharable"
            );
        }

        User user = (User) connectedUser.getPrincipal();
        if (!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("Accept other owners book");
        }

        BookTransactionHistory transaction = transactionRepository.findByBookIdAndOwnerId(bookId, book.getOwner().getId())
            .orElseThrow(
                () -> new OperationNotPermittedException("The book is not returned yet. You cannot approve its return")
            );

        transaction.setReturnedApproved(true);
        return transactionRepository.save(transaction).getId();
    }

    public void uploadCoverImage(MultipartFile file, Long bookId, Authentication connectedUser) {
        Book book = getBookFromDB(bookId);
        var bookCover = fileStorageService.saveFile(file, connectedUser.getName());
        book.setBookCover(bookCover);
        bookRepository.save(book);
    }

    private PageResponse<BookResponse> pageResponse(Page<Book> books) {
        List<BookResponse> bookResponse = books.stream()
            .map(bookMapper::toBookResponse)
            .toList();

        return new PageResponse<>(
            bookResponse,
            books.getNumber(),
            books.getSize(),
            books.getTotalElements(),
            books.getTotalPages(),
            books.isFirst(),
            books.isLast()
        );
    }

    private Book getBookFromDB(Long bookId) {
        return bookRepository.findById(bookId)
            .orElseThrow(() -> new EntityNotFoundException("Book with ID: " + bookId + " was not found"));
    }
}
