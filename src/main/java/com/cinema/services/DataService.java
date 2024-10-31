package com.cinema.services;

import com.cinema.dto.RequestDto;
import com.cinema.dto.SpaceshipDto;
import java.util.List;
import java.util.Optional;

public interface DataService {

    Optional<List<SpaceshipDto>> getAll(Integer size);

    Optional<List<SpaceshipDto>> getAllByName(String name);

    Optional<SpaceshipDto> getById(Long id);

    void update(Long id, SpaceshipDto data);

    void delete(Long id);

    Boolean validateIfExists(String name, String origin);

    void create(RequestDto request);

    Boolean isOriginValid(String origin);

    Boolean validateIfExistsID(Long id);
}
