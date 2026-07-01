package ru.otus.hw.repositories;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Optional;

import static org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.FETCH;

@Repository
@RequiredArgsConstructor
public class JpaBookRepository implements BookRepository {

    @PersistenceContext
    private final EntityManager em;

    @Override
    public Optional<Book> findById(long id) {
        EntityGraph<?> entityGraph = em.getEntityGraph("Book.withAuthorAndGenres");
        TypedQuery<Book> query = em.createQuery(
                "SELECT b FROM Book b " +
                    "WHERE b.id = :id",
                Book.class);
        query.setParameter("id", id);
        query.setHint(FETCH.getKey(), entityGraph);
        return query.getResultStream().findFirst();
    }

    @Override
    public List<Book> findAll() {
        EntityGraph<?> entityGraph = em.getEntityGraph("Book.withAuthorAndGenres");
        TypedQuery<Book> query = em.createQuery(
                   "SELECT b FROM Book b",
                   Book.class);
        query.setHint(FETCH.getKey(), entityGraph);
        return query.getResultList();
    }

    @Override
    public Book save(Book book) {
        return em.merge(book);
    }

    @Override
    public void deleteById(long id) {
        Optional.ofNullable(em.find(Book.class, id))
                .ifPresent(em::remove);
    }
}