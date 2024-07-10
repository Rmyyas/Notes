package com.example.notes.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;


import java.time.LocalDateTime;

/**
 * Data Transfer Object for Note.
 * This class is used to transfer data between different layers of the application.
 * It includes fields for note ID, text, and timestamp.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NoteDTO {

    private Long id;
    private String text;

    @JsonSerialize(using = com.example.notes.config.LocalDateTimeSerializer.class)
    private LocalDateTime time;

    public LocalDateTime getTime() {
        return time;
    }


    public void setTime(LocalDateTime time) {
        this.time = time;
    }


    public String getText() {
        return text;
    }


    public void setText(String text) {
        this.text = text;
    }

    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }
}
