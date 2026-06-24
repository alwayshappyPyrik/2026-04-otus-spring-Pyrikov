package ru.otus.hw.repositories;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;

import static org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.FETCH;

@RequiredArgsConstructor
@Repository
public class JpaCommentRepository implements CommentRepository {

    @PersistenceContext
    private final EntityManager em;

    @Override
    public Optional<Comment> findById(long id) {
        EntityGraph<Comment> entityGraph = em.createEntityGraph(Comment.class);
        entityGraph.addSubgraph("book").addAttributeNodes("author");
        TypedQuery<Comment> query = em.createQuery(
                "SELECT c FROM Comment c " +
                    "WHERE c.id = :id",
                Comment.class);
        query.setParameter("id", id);
        query.setHint(FETCH.getKey(), entityGraph);
        return query.getResultStream().findFirst();
    }

    @Override
    public List<Comment> findAllByBookId(long bookId) {
        EntityGraph<Comment> entityGraph = em.createEntityGraph(Comment.class);
        entityGraph.addSubgraph("book").addAttributeNodes("author");
        TypedQuery<Comment> query = em.createQuery(
                "select c from Comment c " +
                    "where c.book.id = :bookId",
                Comment.class);
        query.setParameter("bookId", bookId);
        query.setHint(FETCH.getKey(), entityGraph);
        return query.getResultList();
    }

    @Override
    public Comment save(Comment comment) {
        return em.merge(comment);
    }

    @Override
    public void deleteById(long id) {
        Optional.ofNullable(em.find(Comment.class, id))
                .ifPresent(em::remove);
    }
}