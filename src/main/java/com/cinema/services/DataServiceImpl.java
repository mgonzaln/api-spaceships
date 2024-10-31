package com.cinema.services;

import com.cinema.dto.RequestDto;
import com.cinema.dto.SpaceshipDto;
import com.cinema.exception.DataExistsException;
import com.cinema.exception.NotFoundException;
import com.cinema.exception.SpaceshipException;
import com.cinema.model.SpaceShipOrigin;
import com.cinema.model.Spaceship;
import com.cinema.repository.SpaceshipRepository;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DataServiceImpl implements DataService{

    @Value("${pagination.size}")
    private Integer PAGINATION_SIZE;

   private final SpaceshipRepository repository;

    public DataServiceImpl(SpaceshipRepository spaceshipRepository)
    {
        this.repository =spaceshipRepository;
    }
    @Override
    @Cacheable("spaceship")
    public Optional<List<SpaceshipDto>> getAll(Integer size) {
        log.info("get all by page size "+size);

        Sort sort = Sort.by(Sort.Direction.ASC, "Id");
        Pageable pageable = PageRequest.of(size, PAGINATION_SIZE).withSort(sort);

        Page<Spaceship> result = null;
        result = repository.findAll(pageable);

        return Optional.of(result.map(data -> SpaceshipDto.builder()
                .name(
                        WordUtils.capitalizeFully(data.getName()).toString())
                .id(data.getId().toString())
                .origin(data.getOrigin().name()).build()
                ).stream().collect(Collectors.toList()));
    }

    @Override
    @Cacheable("spaceship")
    public Optional<List<SpaceshipDto>> getAllByName(String name) {
        log.info("get all by name ");
        List<Spaceship> result = repository.findAllByNameContaining(name.toUpperCase());
        if(result.isEmpty())
            throw  new NotFoundException("Not found with  name "+name);

        return Optional.of(result.stream()
                .map(data -> SpaceshipDto.builder()
                .name(WordUtils.capitalizeFully(data.getName()).toString())
                .id(data.getId().toString())
                .origin(data.getOrigin().name()).build()).collect(Collectors.toList()));
    }

    @Override
    @Cacheable("spaceship")
    public Optional<SpaceshipDto> getById(Long id) {
        log.info("get by id");
        return repository.findById(id)
                .map(data-> SpaceshipDto.builder().name(data.getName())
                .id(data.getId().toString())
                        .origin(data.getOrigin().name()).build());

    }

    @Override
    @CachePut(value = "spaceship")
    public void update(Long id, SpaceshipDto data) {

        try {
            repository.save(Spaceship.builder().id(id)
                    .name(data.name())
                    .origin(data.origin().equalsIgnoreCase("MOVIE")?SpaceShipOrigin.MOVIE:SpaceShipOrigin.SERIE)
                    .build());
        } catch (SpaceshipException e) {
            log.error(e.getMessage());
            throw new SpaceshipException("Update error");
        }
    }

    @Override
    @CacheEvict(value="spaceship", allEntries = true)
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public Boolean validateIfExists(String name, String origin) throws DataExistsException {
        Optional<Spaceship> data = repository.findByNameAndOrigin(name, origin.equalsIgnoreCase("MOVIE")?SpaceShipOrigin.MOVIE:SpaceShipOrigin.SERIE);
        if(data.isPresent()){
            throw new DataExistsException("Error at create, data already exists");
        }
        else return Boolean.FALSE;
    }

    @Override
    public void create(RequestDto request) {
        log.info("create "+request.toString());
        repository.save(Spaceship.builder().name(request.name())
                .origin(
                        request.origin().equalsIgnoreCase("MOVIE")?
                        SpaceShipOrigin.MOVIE:SpaceShipOrigin.SERIE
                ).build());
    }

    @Override
    public Boolean isOriginValid(String origin) {
        if(origin.equalsIgnoreCase(SpaceShipOrigin.MOVIE.toString()) || origin.equalsIgnoreCase(SpaceShipOrigin.SERIE.toString()))
            return Boolean.TRUE;
        return Boolean.FALSE;
    }

    @Override
    public Boolean validateIfExistsID(Long id) {
        Optional<Spaceship> data = repository.findById(id);
        if(data.isPresent()){
            return Boolean.TRUE;
        }
        else return Boolean.FALSE;
    }
}
