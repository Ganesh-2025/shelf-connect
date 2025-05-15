package com.shelfconnect.repo;

import com.shelfconnect.model.Book;
import com.shelfconnect.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIdAndOwnerId(Long id, Long id1);

    List<Book> findAllByOwner(User owner, Pageable pageable);
}

