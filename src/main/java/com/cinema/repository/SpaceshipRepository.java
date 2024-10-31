package com.cinema.repository;

import com.cinema.model.SpaceShipOrigin;
import com.cinema.model.Spaceship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpaceshipRepository extends JpaRepository<Spaceship, Long> {

    Optional<Spaceship> findByNameContaining(String param);

    List<Spaceship> findAllByNameContaining(String param);

    Optional<Spaceship> findByNameAndOrigin(String name, SpaceShipOrigin origin);
}
