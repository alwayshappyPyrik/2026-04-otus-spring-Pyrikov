package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Author;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Collections;

@RequiredArgsConstructor
@Repository
public class JdbcAuthorRepository implements AuthorRepository {

    private final NamedParameterJdbcOperations jdbc;

    @Override
    public List<Author> findAll() {
        String sql = "SELECT id, full_name FROM authors";
        return jdbc.query(sql, new AuthorRowMapper());
    }

    @Override
    public Optional<Author> findById(long id) {
        String sql = "SELECT id, full_name FROM authors where id = :id";
        return jdbc.query(sql,
                        Collections.singletonMap("id", id),
                        new AuthorRowMapper()).stream()
                .findFirst();
    }

    private static class AuthorRowMapper implements RowMapper<Author> {

        @Override
        public Author mapRow(ResultSet rs, int i) throws SQLException {
            long id = rs.getLong("id");
            String fullName = rs.getString("full_name");
            return new Author(id, fullName);
        }
    }
}