package com.isidrosantiago.booknetwork.history;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookTransactionRepository extends JpaRepository<BookTransactionHistory, Long> {
    @Query("""
        SELECT transaction
        FROM BookTransactionHistory transaction
        WHERE transaction.user.id = :userId
        """)
    Page<BookTransactionHistory> findAllBorrowedBooks(Pageable pageable, Long userId);

    @Query("""
        SELECT transaction
        FROM BookTransactionHistory transaction
        WHERE transaction.book.owner.id = :userId
        """)
    Page<BookTransactionHistory> findAllReturnedBooks(Pageable pageable, Long userId);

    @Query("""
        SELECT
        (COUNT(*) > 0) AS isBorrowed
         FROM BookTransactionHistory transaction
         WHERE transaction.user.id = :userId
         AND transaction.book.id = :bookId
         AND transaction.returnedApproved = false
        """)
    boolean isAlreadyBorrowedByUser(Long bookId, Long userId);

    @Query("""
            SELECT transaction
            FROM BookTransactionHistory transaction
            WHERE transaction.user.id = :userId
            AND transaction.book.id = :bookId
            AND transaction.returned = false
            AND transaction.returnedApproved = false
        """)
    Optional<BookTransactionHistory> findByBookIdAndUserId(Long bookId, Long userId);

    @Query("""
          SELECT transaction
            FROM BookTransactionHistory transaction
            WHERE transaction.book.owner.id = :userId
            AND transaction.book.id = :bookId
            AND transaction.returned = true
            AND transaction.returnedApproved = false
        """)
    Optional<BookTransactionHistory> findByBookIdAndOwnerId(Long bookId, Long userId);
}
