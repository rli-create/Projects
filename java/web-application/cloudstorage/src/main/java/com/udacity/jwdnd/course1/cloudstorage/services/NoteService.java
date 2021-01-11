package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.mapper.NoteMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.Note;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteService {

    private NoteMapper noteMapper;

    public NoteService(NoteMapper noteMapper) {
        this.noteMapper = noteMapper;
    }

    public List<Note> getAllNotesForUser(Integer userid) {
        return this.noteMapper.getUserNotes(userid);
    }

    public Integer createNote(Note note) {
        return this.noteMapper.insert(note);
    }

    public Integer updateNote(Note note) { return this.noteMapper.update(note); }

    public Integer deleteNote(Integer noteid) {
        return this.noteMapper.delete(noteid);
    }
}
