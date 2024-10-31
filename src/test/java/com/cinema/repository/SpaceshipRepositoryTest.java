package com.cinema.repository;

import com.cinema.model.Spaceship;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.Assert;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureTestDatabase
@DataJpaTest
@Slf4j
class SpaceshipRepositoryTest {


    @Autowired
    private SpaceshipRepository repository;

    @Test
    @DisplayName("Test 1 - findAll with pagination 2")
    @Order(1)
    public void test1()
    {
        log.info("Test 1 ");
        Sort sort = Sort.by(Sort.Direction.ASC, "Id");
        Pageable pageable = PageRequest.of(1, 5).withSort(sort);

        Page<Spaceship> data = repository.findAll(pageable);

        /** Validacion not null */
        Assertions.assertNotNull(data);
        Assertions.assertEquals(data.getContent().size(),5);
    }

    @Test
    @DisplayName("Test 2 - get by id 1")
    @Order(2)
    public void test2()
    {
        log.info("Test 2 -get by id 1 ");
        Optional<Spaceship> data = repository.findById(1L);
        Assertions.assertNotNull(data);
        Assertions.assertEquals(data.get().getId(), 1L);
    }

    @Test
    @DisplayName("Test 3: Search by name like %")
    @Order(3)
    public void test3()
    {
        log.info("Test 3: Search by name like % ");
        Optional<Spaceship> data = repository.findByNameContaining("WINGS");
        Assertions.assertNotNull(data);
        Assertions.assertEquals(data.get().getName(), "X-WINGS");
    }

    @Test
    @DisplayName("Test 4: Search by name with no result")
    @Order(4)
    public void test4()
    {
        log.info("Test 4: Search by name with no result ");
        Optional<Spaceship> data = repository.findByNameContaining("NON-CONTENT");
        Assertions.assertTrue(data.isEmpty());
    }

    @Test
    @DisplayName("Test 5: Delete by id")
    @Order(5)
    public void test5()
    {
        log.info("Test 5: Delete by id ");
        repository.deleteById(1L);
        Optional<Spaceship> data = repository.findById(1L);
        Assertions.assertTrue(data.isEmpty());

    }

    @Test
    @DisplayName("Test 6: Update")
    @Order(6)
    public void test6()
    {
        log.info("Test 6: Update");

        Optional<Spaceship> data = repository.findById(1L);

        Assertions.assertTrue(data.isPresent());
        Spaceship req = data.get();
        req.setName("TEST");
        repository.save(req);
        Optional<Spaceship> data2 = repository.findById(1L);

        Assertions.assertTrue(data2.isPresent());
        Assertions.assertEquals("TEST", data2.get().getName());


    }

}