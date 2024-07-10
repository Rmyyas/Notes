package com.example.notes.controller;

import com.example.notes.dto.NoteDTO;
import com.example.notes.service.NoteServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


import javax.validation.constraints.Min;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;



@RestController
@RequestMapping("/api/${api.version}/notes")
public class NoteController {

    private static final Logger logger = Logger.getLogger(NoteController.class.getName());



    @Autowired
    private NoteServiceImpl noteService;



    private boolean isValidOrder(String order) {
        return "asc".equalsIgnoreCase(order) || "desc".equalsIgnoreCase(order);
    }

    private boolean isValidTimeZone(String tz) {
        return tz != null && !tz.isEmpty() && TimeZone.getTimeZone(tz) != null;
    }


    /**
     * Creates a new note.
     *
     * @param note the note to create
     * @return a response entity containing the created note
     */
    @PostMapping("/createNote")
    public Mono<ResponseEntity<NoteDTO>> saveNote(@RequestBody NoteDTO note) {
        return noteService.saveNote(note)
                .map(savedNote -> ResponseEntity.status(HttpStatus.CREATED).body(savedNote));
    }


    /**
     * Retrieves all notes.
     *
     * with the following options :
     * @param timezone the timezone filter
     * @param order the order of the notes (asc or desc)
     *
     * @return a response entity containing the list of all notes
     */
    @GetMapping("/getAllNotes")
    public Mono<ResponseEntity<List<NoteDTO>>> getNotes(@RequestParam(value = "tz", required = false,defaultValue = "GMT") String timezone,
                                                        @RequestParam(value = "order", required = false, defaultValue = "asc") String order,
                                                        @RequestParam(value = "pageNumber", required = false,defaultValue = "0") @Min(0) int pageNumber,
                                                        @RequestParam(value = "pageSize",required = false,defaultValue = "50") @Min(1) int pageSize) {
        if (!isValidOrder(order)) {
            return Mono.error(new IllegalArgumentException("Order must be 'asc' or 'desc'"));
        }


        return noteService.getAllNotes(timezone, order, pageNumber, pageSize)
                .collectList()
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    logger.log(Level.SEVERE, "Error retrieving notes", e);
                    return Mono.just(ResponseEntity.badRequest().build());
                });
    }




    /**
     * Retrieves a note by its ID.
     *
     * @param id the ID of the note to retrieve
     * @return a response entity containing the requested note
     */

    @GetMapping("/{id}")
    public Mono<ResponseEntity<NoteDTO>> getNoteById(@PathVariable Long id) {
        return noteService.getNoteById(id)
                .map(noteDTO -> ResponseEntity.ok().body(noteDTO));
    }

    /**
     * retrieve all notes that contains "text"
     * @param text
     * @return
     */
    @GetMapping("/search")
    public Mono<ResponseEntity<List<NoteDTO>>> searchNotes(@RequestParam String text) {
        return noteService.searchNotes(text)
                .collectList()
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    logger.log(Level.SEVERE, "Error searching notes", e);
                    return Mono.just(ResponseEntity.badRequest().build());
                });
    }

}