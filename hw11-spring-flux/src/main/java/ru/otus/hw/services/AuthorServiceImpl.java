package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.AuthorRequestDto;
import ru.otus.hw.dto.AuthorResponseDto;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.mapper.AuthorMapper;
import ru.otus.hw.repositories.AuthorRepository;

@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;

    private final AuthorMapper authorMapper;

    @Override
    public Flux<AuthorResponseDto> findAll() {
        return authorRepository.findAll()
                .map(authorMapper::toDto);
    }

    @Override
    public Mono<AuthorResponseDto> findById(AuthorRequestDto authorRequestDto) {
        Long id = authorRequestDto.id();

       return authorRepository.findById(id)
               .map(authorMapper::toDto)
               .switchIfEmpty(Mono.error(new NotFoundException("Author not found with id: " + id)));
    }
}