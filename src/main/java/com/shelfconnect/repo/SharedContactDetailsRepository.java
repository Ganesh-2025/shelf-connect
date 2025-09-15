package com.shelfconnect.repo;

import com.shelfconnect.model.SharedContactDetails;
import com.shelfconnect.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SharedContactDetailsRepository extends JpaRepository<SharedContactDetails,Long > {
    Page<SharedContactDetails> findAllByFrom(User from, Pageable pageable);
    Page<SharedContactDetails> findAllByTo(User from, Pageable pageable);
}
