package com.example.notes.repository;

import com.example.notes.model.Note;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;



/**
 * Repository interface for Note entities.
 * This interface extends JpaRepository, providing CRUD operations for Note entities.
 * It is annotated with @Repository to indicate that it's a Spring Data repository.
 */

@Repository
public interface NoteRepository extends R2dbcRepository<Note, Long> {

    Flux<Note> findAllBy(Pageable page);

    Flux<Note> findByTextContainsIgnoreCase(String text);

}
