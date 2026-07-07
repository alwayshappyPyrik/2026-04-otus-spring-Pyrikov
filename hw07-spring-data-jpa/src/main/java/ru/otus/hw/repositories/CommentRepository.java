package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @EntityGraph("Comment.withBookAndAuthorAndGenres")
    Optional<Comment> findById(long id);

    @EntityGraph("Comment.withBookAndAuthorAndGenres")
    List<Comment> findAllByBookId(long id);
}