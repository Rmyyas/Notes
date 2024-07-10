package com.example.notes.service;

import com.example.notes.dto.NoteDTO;
import com.example.notes.model.Note;
import com.example.notes.repository.NoteRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


@Service
public class NoteServiceImpl {

    private final NoteRepository noteRepository;

    @Autowired
    public NoteServiceImpl(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
        logger.log(Level.INFO, "NoteServiceImpl instantiated.");
    }

    @Autowired
    private ObjectMapper objectMapper;

    private static final String SLACK_WEBHOOK_URL = "https://hooks.slack.com/services/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";

    private static final Logger logger = Logger.getLogger(NoteServiceImpl.class.getName());



    /**
     * Saves a new note and pushes it to a Slack channel.
     *
     * @param noteDTO the note to be saved
     * @return
     */
    public Mono<NoteDTO> saveNote(NoteDTO noteDTO) {
        Note note = new Note();
        note.setText(noteDTO.getText());
        note.setTimestamp(LocalDateTime.now());

        pushNoteToSlack(note);
        return noteRepository.save(note)
                .flatMap(savedNote -> {
                    NoteDTO savedNoteDTO = convertToNoteDTO(savedNote);
                    logger.log(Level.INFO, "Note saved successfully: {0}", savedNoteDTO);
                    return Mono.just(savedNoteDTO);
                });
    }


    private NoteDTO convertToNoteDTO(Note note) {
        NoteDTO noteDTO = new NoteDTO();
        noteDTO.setText(note.getText());
        noteDTO.setId(note.getId());
        noteDTO.setTime(note.getTimestamp());
        logger.log(Level.FINE, "Converted Note to NoteDTO: {0}", noteDTO);
        return noteDTO;
    }


    /**
     * Retrieves all notes, optionally filtered by timezone and order.
     *
     * @param tz    the timezone to apply to the notes' timestamps
     * @param order the order in which to sort the notes (asc or desc)
     * @return a list of all notes
     */
    public Flux<NoteDTO> getAllNotes(String tz, String order, int pageNumber, int pageSize) {
        logger.log(Level.INFO, "Retrieving all notes with timezone: {0}, order: {1}, pageNumber: {2}, pageSize: {3}", new Object[]{tz, order, pageNumber, pageSize});

        Sort sort = order.equalsIgnoreCase("desc") ? Sort.by("timestamp").descending() : Sort.by("timestamp").ascending();
        Pageable page = PageRequest.of(pageNumber, pageSize, sort);

        return noteRepository.findAllBy(page)
                .map(note -> applyTimeZone(note, tz))
                .map(this::convertToNoteDTO)
                .doOnComplete(() -> logger.log(Level.INFO, "All notes retrieved successfully."))
                .switchIfEmpty(Mono.error(new NoSuchElementException("No Notes available at page number " + pageNumber)));
    }

    private Note applyTimeZone(Note note, String tz) {
        if (tz != null && !tz.isEmpty()) {
            TimeZone timeZone = TimeZone.getTimeZone(tz);
            note.setTimestamp(note.getTimestamp().atZone(ZoneId.systemDefault())
                    .withZoneSameInstant(timeZone.toZoneId()).toLocalDateTime());
        }
        return note;
    }


    /**
     * Retrieves a note by its ID.
     *
     * @param id the ID of the note to retrieve
     * @return the note with the specified ID
     */
    public Mono<NoteDTO> getNoteById(Long id) {
        logger.log(Level.INFO, "Retrieving note by ID: {0}", id);

        return noteRepository.findById(id)
                .map(note -> {
                    NoteDTO noteDTO = new NoteDTO();
                    noteDTO.setText(note.getText());
                    noteDTO.setId(note.getId());
                    noteDTO.setTime(note.getTimestamp());
                    logger.log(Level.INFO, "Note retrieved successfully: {0}", noteDTO);
                    return noteDTO;
                })
                .switchIfEmpty(Mono.error(new NoSuchElementException("Note with id " + id + " not found")))
                .doOnError(error -> logger.log(Level.WARNING, "Failed to retrieve note with ID {0}: {1}", new Object[]{id, error.getMessage()}));
    }

    /**
     * Pushes a note to a Slack channel.
     *
     * @param note the note to push to Slack
     */
    private void pushNoteToSlack(Note note) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String payload = String.format("{\"text\":\"%s\"}", note.getText());
            restTemplate.postForEntity(NoteServiceImpl.SLACK_WEBHOOK_URL, payload, String.class);
            logger.log(Level.INFO, "Note pushed to Slack: {0}", note.getText());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error pushing note to Slack: {0}", e.getMessage());
            throw new RuntimeException("Error pushing note to Slack", e);
        }
    }

    /**
     * search all the notes contain the text provided
     *
     * @param text
     * @return
     */
    public Flux<NoteDTO> searchNotes(String text) {
        return noteRepository.findByTextContainsIgnoreCase(text)
                .map(this::convertToNoteDTO)
                .doOnComplete(() -> logger.log(Level.INFO, "Search completed successfully."))
                .doOnError(error -> logger.log(Level.SEVERE, "Error during search", error));
    }

}


