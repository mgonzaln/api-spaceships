package com.cinema.controllers;

import com.cinema.config.SecurityConfig;
import com.cinema.dto.RequestDto;
import com.cinema.dto.SpaceshipDto;
import com.cinema.exception.BadParametersRequestException;
import com.cinema.exception.DataExistsException;
import com.cinema.exception.NoAuthorizedException;
import com.cinema.repository.SpaceshipRepository;
import com.cinema.services.DataService;
import com.cinema.services.DataServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.annotations.Filter;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Nested;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.awaitility.Awaitility.given;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(roles = {"USER","ADMIN"})
class ControllerTest{

   @Autowired
   private MockMvc mock;

   @Autowired
   private SpaceshipRepository repository;

    @MockBean
    private DataService dataService;

    //@Mock
    @InjectMocks
    private Controller controller;

    @Autowired
    private ObjectMapper objectMapper;

    private final ObjectMapper mapper = new ObjectMapper();

    SpaceshipDto spaceshipDto;
    RequestDto req, req2;
    ResponseEntity<?> responseCreated;

    @BeforeEach()

    public void setup(){
        spaceshipDto = SpaceshipDto.builder().id("1").name("TEST").origin("SERIE").build();
        req = RequestDto.builder().name("TEST").origin("SERIE").build();
        req2 = RequestDto.builder().name("").origin("SERIE").build();
        responseCreated = new ResponseEntity<>("",HttpStatus.CREATED);
        when(dataService.isOriginValid(any(String.class))).thenReturn(Boolean.TRUE);
        when(dataService.validateIfExists(any(String.class), any(String.class))).thenReturn(Boolean.TRUE);
        when(dataService.validateIfExistsID(any(Long.class))).thenReturn(Boolean.FALSE);
    }

    @Test
    @DisplayName("Test 1:save ok")
    @WithMockUser(roles = {"USER","ADMIN"})
    public void saveOk() throws Exception {
        when(dataService.isOriginValid(any(String.class))).thenReturn(Boolean.TRUE);
        when(dataService.validateIfExistsID(any(Long.class))).thenReturn(Boolean.FALSE);

     mock.perform(MockMvcRequestBuilders.post("/api/spaceships")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(req)))
             .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    @DisplayName("Test 2:save nok badParamOrigin")
    @WithMockUser(roles = {"USER","ADMIN"})
    public void saveBadParamOrigin() throws Exception {
        when(dataService.isOriginValid(any(String.class))).thenReturn(Boolean.FALSE);
        //when(dataService.validateIfExistsID(any(Long.class))).thenReturn(Boolean.FALSE);

        mock.perform(MockMvcRequestBuilders.post("/api/spaceships")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("Test 3:save not allowed")
    @WithMockUser(authorities = {"USER"})
    public void saveNok() throws Exception {
        when(dataService.isOriginValid(any(String.class))).thenReturn(Boolean.TRUE);
        when(dataService.isOriginValid(any(String.class))).thenReturn(Boolean.FALSE);

        mock.perform(MockMvcRequestBuilders.post("/api/spaceships")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }


}