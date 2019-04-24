package com.wx.cloudnotes.dao;

import java.util.List;

import com.wx.cloudnotes.domain.Note;
import com.wx.cloudnotes.domain.NoteBook;

public interface RedisDao {

	public List<NoteBook> getNotebook(String key);

	public List<Note> getNote(String key) ;

	public boolean saveNotebookToRedis(String userName, String string);

	public boolean delNotebookToRedis(String userName, String string);

	public boolean updateNotebookToRedis(String key, String oldValue,
                                         String newValue);

	public boolean saveNoteToRedis(String noteBookRowkey, String string);

	public boolean delNoteToRedis(String noteBookRowkey, String string);

	public boolean updateNoteToRedis(String noteBookRowkey, String string,
                                     String string2);

}
