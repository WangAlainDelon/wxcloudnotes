package com.wx.cloudnotes.dao.imp;

import com.wx.cloudnotes.common.Constants;
import com.wx.cloudnotes.dao.RedisDao;
import com.wx.cloudnotes.domain.Note;
import com.wx.cloudnotes.domain.NoteBook;
import com.wx.cloudnotes.utils.redis.RedisTools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("redisDaoImpl")
public class RedisDaoImp implements RedisDao {

    @Autowired
    private RedisTools redisTools;

    /**
     * 根据key获取笔记本
     *
     * @param key
     * @return
     */
    @Override
    public List<NoteBook> getNotebook(String key) {
        if (key == null) {
            return null;
        }
        List<String> list = redisTools.getList(key);
        if (list == null) {
            return null;
        }
        List<NoteBook> listNoteBook = new ArrayList<NoteBook>();
        for (String s : list) {
            NoteBook noteBook = new NoteBook();
            String[] split = s.split("\\" + Constants.STRING_SEPARATOR);
            noteBook.setRowKey(split[0]);
            noteBook.setName(split[1]);
            noteBook.setCreateTime(split[2]);
            noteBook.setStatus(split[3]);
            listNoteBook.add(noteBook);
        }
        return listNoteBook;
    }

    @Override
    public List<Note> getNote(String key) {
        return null;
    }

    /***
     * 保存笔记本的信息到redis中
     * @param userName
     * @param string
     * @return
     */
    @Override
    public boolean saveNotebookToRedis(String userName, String string) {
        Long returnSize = redisTools.appendRightList(userName, string);
        if (returnSize == 0) {
            return false;
        }
        return true;
    }

    /**
     * 根据用户名删除redis中笔记本的信息
     *
     * @param userName
     * @param string
     * @return
     */
    @Override
    public boolean delNotebookToRedis(String userName, String string) {
        Long lrem = redisTools.deleteValueOfList(userName, 1, string);
        if (lrem == 0) {
            return false;
        }
        return true;
    }

    /**
     * 跟新笔记本名称的时候跟新到redis中
     *
     * @param username
     * @param oldValue
     * @param newValue
     * @return
     */
    @Override
    public boolean updateNotebookToRedis(String username, String oldValue, String newValue) {
        redisTools.deleteValueOfList(username, 1, oldValue.trim());
        Long returnSize = redisTools.appendRightList(username, newValue);
        if (returnSize == 0) {
            return false;
        }
        return true;
    }

    @Override
    public boolean saveNoteToRedis(String noteBookRowkey, String string) {
        return false;
    }

    @Override
    public boolean delNoteToRedis(String noteBookRowkey, String string) {
        return false;
    }

    @Override
    public boolean updateNoteToRedis(String noteBookRowkey, String string, String string2) {
        return false;
    }
}
