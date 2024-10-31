package com.cinema.controllers;

import com.cinema.dto.RequestDto;
import com.cinema.dto.SpaceshipDto;
import com.cinema.exception.*;

import com.cinema.services.DataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
public class Controller {

    private static final String NEGATIVE_ID_NOT_ALLOWED = "Negative ID not allowed" ;

    public Controller(DataService service){
        this.service = service;
    }

    private final DataService service;

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @Operation(
            summary = "Retrieve all spaceship with pagination",
            description = "default page size=10 and page=0.  The parameter page is optional",
            tags = {  "getAll", "getAllWithPagesize" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = SpaceshipDto.class))),
                    description = "List of spaceships"),
            @ApiResponse(responseCode = "204", description = "Spaceships not found",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Book not found",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content)

    })
    @GetMapping("/api/spaceships")
    public  ResponseEntity<?> getAll(@RequestParam(name="page", defaultValue = "0", required = false) Integer page)
    {
        log.info("getAll");
        Optional<List<SpaceshipDto>> result = service.getAll(page);

        if(result.isEmpty())
        {
            log.info("not found");
            return new ResponseEntity<NotFoundException>(new NotFoundException("Not found"), HttpStatus.NO_CONTENT);
        }
        else
        {
            log.info("found "+result.get().size());
            return new ResponseEntity<List<SpaceshipDto>>(result.get() , HttpStatus.OK);
        }

    }

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @Operation(
            summary = "Retrieve a spaceship",
            description = "Retrieve spaceship data with a specific ID",
            tags = {  "getById" })
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = { @Content(mediaType = "application/json",
            schema = @Schema(implementation = SpaceshipDto.class)) }),
            @ApiResponse(responseCode = "404", description = "Spaceship not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Issues with ID", content = @Content),
            @ApiResponse(responseCode = "500", description = "Some internal erroor", content = @Content)})
    @GetMapping(path = "/api/spaceships/id/{id}")
    public  ResponseEntity<?> getById(@PathVariable(name = "id") String id) {
        log.info("get by id " + id);

        try {
            long idParam = Long.parseLong(id);

            if(idParam<0)
                throw new NegativeIDNotAllowedException(NEGATIVE_ID_NOT_ALLOWED);

            Optional<SpaceshipDto> result = service.getById(idParam);

            if (result.isEmpty()) {
                log.info("not found");
                throw new NotFoundException("Not found with id:" + id);
            } else {
                log.info("found");
                return new ResponseEntity<SpaceshipDto>(result.get(), HttpStatus.OK);
            }

        } catch (NumberFormatException e) {
            throw new BadParametersRequestException("Incorrect value format must be a integer number");
        } catch (SpaceshipException e) {
            log.error(e.getMessage());
            throw new SpaceshipException(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @Operation(
            summary = "Retrieve a spaceship",
            description = "Retrieve spaceship data by name",
            tags = {  "getByName" })
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = { @Content(mediaType = "application/json",  schema = @Schema(implementation = SpaceshipDto.class)) }),
            @ApiResponse(responseCode = "404", description = "Spaceship not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Issues with name parameter", content = @Content),
            @ApiResponse(responseCode = "500", description = "Some internal error", content = @Content)})
    @GetMapping(path="/api/spaceships/name/{name}")
    public  ResponseEntity<?> getByName(@PathVariable(required = true) String name)
    {
       log.info("get by name {} ", name);
        try {
            Optional<List<SpaceshipDto>> result = service.getAllByName(name);

            if (result.isEmpty()) {
                log.info("not found");
                throw new NotFoundException("Not found with name:" +name);
            } else {
                log.info("found");
                return new ResponseEntity<List<SpaceshipDto>>(result.get(), HttpStatus.OK);
            }

        } catch (NumberFormatException e) {
            throw new BadParametersRequestException("Incorrect value format must be a integer number");
        } catch (SpaceshipException e) {
            log.error(e.getMessage());
            throw new SpaceshipException(e.getMessage());
        }
    }
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @PostMapping("/api/spaceships")
    @Operation(
            summary = "Create a spaceship",
            description = "Create new register with name and origin(MOVIE or SERIE)",
            tags = {  "registerNewSpaceship" })
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "Issues with input parameter name or origin", content = @Content),
            @ApiResponse(responseCode = "201", description = "Spaceship created ok", content = @Content),
            @ApiResponse(responseCode = "500", description = "Some internal error", content = @Content)})
    public ResponseEntity<?> post(@RequestBody(required = true) RequestDto request) throws BadParametersRequestException, DataExistsException
    {
        log.info("create");
        log.info(request.toString());
        //validate
        String name = request.name();
        String origin = request.origin();

        if(name==null || name.trim().isEmpty())
        {
            throw new BadParametersRequestException("Name cannot be empty");
        }
        if(origin==null || origin.trim().isEmpty()) {
            throw new BadParametersRequestException("Origin cannot be empty");
        }
        //verify if exist
        if(!service.isOriginValid(request.origin()))
            throw new BadParametersRequestException("Origin must be SERIE or MOVIE");
        //create
        service.create(request);
        return new ResponseEntity<>("Created Ok", HttpStatus.CREATED);

    }

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @Operation(
            summary = "Update a spaceship",
            description = "Update a existing register with specific ID, it can change name and origin(MOVIE or SERIE)",
            tags = {  "udpateSpaceship" })
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "Issues with input parameter name or origin", content = @Content),
            @ApiResponse(responseCode = "200", description = "Spaceship updated ok", content = @Content),
            @ApiResponse(responseCode = "500", description = "Some internal error", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)})
    @PutMapping("/api/spaceships/{id}")
    public  ResponseEntity<?> put(@PathVariable String id,   @RequestBody RequestDto request) {

        log.info("update Id:{}",id);
        log.info(request.toString());
        //validate
        String name = request.name();
        String origin = request.origin();

        if (name == null || name.trim().isEmpty()) {
            throw new BadParametersRequestException("Name cannot be empty");
        }
        if (origin == null || origin.trim().isEmpty()) {
            throw new BadParametersRequestException("Origin cannot be empty");
        }

        if (id == null || id.trim().isEmpty()) {
            throw new BadParametersRequestException("Id cannot be null or empty");
        }

        long ID = 0L;
        try {
           ID= Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new BadParametersRequestException("Invalid id number :" + id);
        }

        if(ID<0)
            throw new NegativeIDNotAllowedException(NEGATIVE_ID_NOT_ALLOWED);
        //verify if exist
        if (!service.isOriginValid(request.origin()))
            throw new BadParametersRequestException("Origin must be SERIE or MOVIE");

        if (service.validateIfExistsID(ID) == Boolean.TRUE) {
            //update
            service.update(ID, SpaceshipDto.builder().origin(request.origin()).name(request.name()).build());
            return new ResponseEntity<>("Updated Ok", HttpStatus.OK);
        } else {
            throw new NotFoundException("ID not found id:"+ID);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(
            summary = "Delete a spaceship",
            description = "Delete a spaceship by it ID)",
            tags = {  "deleteSpaceship" })
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "Issues with input parameter ID", content = @Content),
            @ApiResponse(responseCode = "200", description = "Spaceship deleted ok", content = @Content),
            @ApiResponse(responseCode = "500", description = "Some internal error", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)})
    @DeleteMapping("/api/spaceships/{id}")
    public  ResponseEntity<?> delete(@PathVariable String id)
    {
        log.info("Delete Id: {}" , id);

        if (id == null || id.trim().isEmpty()) {
            throw new BadParametersRequestException("Id cannot be null or empty");
        }
        long ID = 0L;
        try {
            ID=Long.valueOf(id);
        } catch (NumberFormatException e) {
            throw new BadParametersRequestException("Invalid id number:" + id);
        }

        if(ID<0){
            throw new NegativeIDNotAllowedException(NEGATIVE_ID_NOT_ALLOWED);
        }

        if (service.validateIfExistsID(ID) == Boolean.TRUE) {
            //delete
            service.delete(ID);
            return new ResponseEntity<>("Deleted Ok", HttpStatus.OK);
        } else {
            throw new NotFoundException("ID not found id:"+ID);
        }
    }
}
