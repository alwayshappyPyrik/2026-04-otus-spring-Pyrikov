package ru.otus.hw.mongock.changelog;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.ArrayList;
import java.util.List;

@ChangeUnit(
        id = "v1-initial-schema",
        order = "001",
        author = "alwayshappypyrik",
        systemVersion = "1.0"
)
@RequiredArgsConstructor
@Slf4j
public class ChangelogV1Shema {

    private final MongoTemplate mongoTemplate;

    private final List<String> createdCollections = new ArrayList<>();

    @Execution
    public void migrate() {
        log.info("Executing migration: v1-initial-schema");

        createCollectionIfNotExists("authors");
        createCollectionIfNotExists("genres");
        createCollectionIfNotExists("books");
        createCollectionIfNotExists("comments");

        log.info("Migration v1-initial-schema completed!");
    }

    @RollbackExecution
    public void rollback() {
        for (String collectionName : createdCollections) {
            if (mongoTemplate.collectionExists(collectionName)) {
                long count = mongoTemplate.getCollection(collectionName).countDocuments();
                if (count == 0) {
                    mongoTemplate.dropCollection(collectionName);
                    log.info("Dropped collection: {}", collectionName);
                } else {
                    log.info("Collection not empty, keeping: {}", collectionName);
                }
            }
        }
    }

    private void createCollectionIfNotExists(String collectionName) {
        if (!mongoTemplate.collectionExists(collectionName)) {
            mongoTemplate.createCollection(collectionName);
            createdCollections.add(collectionName);
            log.info("Created collection: {}", collectionName);
        } else {
            log.info("Collection already exists: {}", collectionName);
        }
    }
}
