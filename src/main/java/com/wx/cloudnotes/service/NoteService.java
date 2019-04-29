package com.wx.cloudnotes.service;

import java.io.IOException;
import java.util.List;

import com.wx.cloudnotes.domain.Note;
import com.wx.cloudnotes.domain.NoteBook;


public interface NoteService {
    List<NoteBook> getAllNoteBook(String userIdAndName) throws IOException;

    boolean addNoteBook(String noteBookName, String userName, String createTime, int status) throws IOException;

    boolean updateNoteBook(String newNoteBookName, String oldNoteBookName, String userName, String createTime, int status);

    boolean deleteNoteBook(String noteBookName, String userName, String createTime, int i);

    List<Note> getNoteListByNotebook(String noteBookRowkey) throws IOException;

    boolean addNote(String noteRowKey, String noteName, String createTime, String status, String noteBookRowkey);

    Note getNoteByRowKey(String noteRowkey) throws Exception;

    boolean updateNote(String noteRowKey, String noteName, String createTime, String content, String status, String oldNoteName, String noteBookRowkey);

    boolean moveAndDeleteNote(String noteRowKey, String oldNoteBookRowkey, String newNoteBookRowkey, String noteName);

    boolean deleteNote(String noteRowKey, String createTime, String status, String oldNoteName, String noteBookRowkey);

    boolean  activeMyNote(String noteRowKey, String activityBtRowKey,String userName);

    boolean starOtherNote(String noteRowKey, String starBtRowKey,String userName);


}
