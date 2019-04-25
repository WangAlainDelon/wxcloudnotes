package com.wx.cloudnotes.service.imp;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.wx.cloudnotes.common.Constants;
import com.wx.cloudnotes.dao.DataDao;
import com.wx.cloudnotes.dao.RedisDao;
import com.wx.cloudnotes.domain.Note;
import com.wx.cloudnotes.domain.NoteBook;
import com.wx.cloudnotes.service.NoteService;
import net.sf.json.JSONArray;

import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.lucene.index.CorruptIndexException;
/*import org.apache.lucene.queryParser.ParseException;*/
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/*import com.itcast.tsc.note.bean.Article;
import com.itcast.tsc.note.bean.Note;
import com.itcast.tsc.note.bean.NoteBook;
import com.itcast.tsc.note.bean.SearchBean;
import com.itcast.tsc.note.dao.CreateIndexDao;
import com.itcast.tsc.note.dao.DataDao;
import com.itcast.tsc.note.dao.RedisDao;
import com.itcast.tsc.note.dao.SearchIndexDao;
import com.itcast.tsc.note.service.NoteService;
import com.itcast.tsc.util.JsonUtil;
import com.itcast.tsc.util.constants.Constants;*/

@Service
public class NoteServiceImpl implements NoteService {
    /**
     * @resource和@autowired的区别: http://www.cnblogs.com/think-in-java/p/5474740.html
     */
    @Resource(name = "redisDaoImpl")
    private RedisDao redisDao;

    @Resource(name = "dataDaoImpl")
    private DataDao dataDao;
    /*
    @Resource(name = "searchIndexDaoImpl")
    private SearchIndexDao searchIndexDao;
    @Resource(name = "createIndexDaoIpml")
    private CreateIndexDao createIndexDao;
    private static Logger logger = LoggerFactory.getLogger(NoteServiceImpl.class);*/

    /**
     * 获取指定用户的所有笔记本 userIdAndName:userId_loginName IOException：IO异常
     *
     * @throws IOException
     */
    @Override
    public List<NoteBook> getAllNoteBook(String userIdAndName) throws IOException {
        List<NoteBook> notebookNames = null;
        try {
            notebookNames = redisDao.getNotebook(userIdAndName);// 从redis中获取笔记本列表
        } catch (Exception e) {
            // 如果在redis中出现异常，在hbase中获取
            List<Result> resultList = dataDao.queryByReg(Constants.NOTEBOOK_TABLE_NAME, userIdAndName + "*");
            // 循环结果集
            for (Result row : resultList) {
                NoteBook noteBook = new NoteBook();
                String rowKey = new String(row.getRow());// rowKey
                noteBook.setRowKey(rowKey);
                String notebookName = new String(row.getValue(Bytes.toBytes(Constants.NOTEBOOK_FAMLIY_NOTEBOOKINFO), Bytes.toBytes(Constants.NOTEBOOK_NOTEBOOKINFO_CLU_NOTEBOOKNAME)));
                noteBook.setName(notebookName);
                notebookNames.add(noteBook);
            }
        }
        if (notebookNames == null || notebookNames.size() <= 0) {
            // 如果从redis中查不到值，去hbase中查询
            List<Result> resultList = dataDao.queryByReg(Constants.NOTEBOOK_TABLE_NAME, userIdAndName + "*");
            // 循环结果集
            for (Result row : resultList) {
                NoteBook noteBook = new NoteBook();
                String rowKey = new String(row.getRow());// rowKey
                noteBook.setRowKey(rowKey);
                String notebookName = new String(row.getValue(Bytes.toBytes(Constants.NOTEBOOK_FAMLIY_NOTEBOOKINFO), Bytes.toBytes(Constants.NOTEBOOK_NOTEBOOKINFO_CLU_NOTEBOOKNAME)));
                noteBook.setName(notebookName);
                notebookNames.add(noteBook);
            }
        }
        return notebookNames;
    }

    @Override
    public boolean addNoteBook(String noteBookName, String userName, String createTime, int status) {
        // 事务的成功
        boolean ifsuccess = false;
        // redis是否成功
        ifsuccess = addNoteBookToRedis(noteBookName, userName, createTime, status);
        // 如果redis成功，保存hbase
        if (ifsuccess) {
            try {
                // 保存hbase是否成功
                ifsuccess = addNoteBookToHbase(noteBookName, userName, createTime, status);
                // 如果不成功，删除redis
                if (!ifsuccess) {
                    deleteNoteBookFromRedis(noteBookName, userName, createTime, status);
                }
            } catch (Exception e) {
                // 报异常，删除redis，返回false
                deleteNoteBookFromRedis(noteBookName, userName, createTime, status);
                e.printStackTrace();
                return false;
            }
        }
        return ifsuccess;
    }

    /**
     * 添加笔记本的时候保存笔记本到redis
     *
     * @param noteBookName
     * @param userName
     * @param createTime
     * @param status
     * @return 返回成功与否
     */
    public boolean addNoteBookToRedis(String noteBookName, String userName, String createTime, int status) {
        StringBuffer noteBookToString = new StringBuffer();
        // wx@163.com_312312|java基础|123213|0
        noteBookToString
                .append(userName + Constants.ROWKEY_SEPARATOR
                        + createTime.trim()).append(Constants.STRING_SEPARATOR)
                .append(noteBookName).append(Constants.STRING_SEPARATOR)
                .append(createTime).append(Constants.STRING_SEPARATOR)
                .append(status);
        // 保存redis，用戶名為key，笔记本信息为value
        boolean flag = redisDao.saveNotebookToRedis(userName, noteBookToString.toString());// 将笔记本存放到redis中
        return flag;
    }

    /**
     * 保存笔记本到hbase中
     *
     * @param noteBookName
     * @param userName
     * @param createTime
     * @param status
     * @return
     */
    public boolean addNoteBookToHbase(String noteBookName, String userName, String createTime, int status) {
        // 创建rowkey  wx@163.com_312312_123213
        String rowKey = userName.trim() + Constants.ROWKEY_SEPARATOR + createTime.trim();
        // 创建笔记列表
        List<String> noteList = new ArrayList<String>();
        // list转json
        String noteListToJson = JSONArray.fromObject(noteList).toString();
        // 封装二维数组，[[famliy，qualifier，value],……………………]，调用dao的公共方法
        /*
         *       nbi(列族1，笔记本信息)  nbn（列1，笔记本名字）         noteBookName
         *       nbi(列族1，笔记本信息)  ct(列2：创建笔记本时间)        createTime
         *       nbi(列族1，笔记本信息)  st（列3：笔记本状态）          status
         *       nbi(列族1，笔记本信息)  nl（列4：笔记本下笔记信息列表）noteListToJson
         *
         * */
        String famQuaVals[][] = new String[4][3];
        famQuaVals[0][0] = Constants.NOTEBOOK_FAMLIY_NOTEBOOKINFO;
        famQuaVals[0][1] = Constants.NOTEBOOK_NOTEBOOKINFO_CLU_NOTEBOOKNAME;
        famQuaVals[0][2] = noteBookName;
        famQuaVals[1][0] = Constants.NOTEBOOK_FAMLIY_NOTEBOOKINFO;
        famQuaVals[1][1] = Constants.NOTEBOOK_NOTEBOOKINFO_CLU_CREATETIME;
        famQuaVals[1][2] = createTime;
        famQuaVals[2][0] = Constants.NOTEBOOK_FAMLIY_NOTEBOOKINFO;
        famQuaVals[2][1] = Constants.NOTEBOOK_NOTEBOOKINFO_CLU_STATUS;
        famQuaVals[2][2] = status + "";// 状态：未设置
        famQuaVals[3][0] = Constants.NOTEBOOK_FAMLIY_NOTEBOOKINFO;
        famQuaVals[3][1] = Constants.NOTEBOOK_NOTEBOOKINFO_CLU_NOTELIST;
        famQuaVals[3][2] = noteListToJson;
        // 调用dao的公共方法
        boolean insertData = dataDao.insertData(Constants.NOTEBOOK_TABLE_NAME, rowKey, famQuaVals);
        return insertData;
    }

    /**
     * 根据用户名删除redis中笔记本的信息
     *
     * @param oldNoteBookName
     * @param userName
     * @param createTime
     * @param status
     * @return
     */
    public boolean deleteNoteBookFromRedis(String oldNoteBookName, String userName, String createTime, int status) {
        StringBuffer oldNoteBookToString = new StringBuffer();
        // 拼笔记本信息
        oldNoteBookToString
                .append(userName + Constants.ROWKEY_SEPARATOR
                        + createTime.trim()).append(Constants.STRING_SEPARATOR)
                .append(oldNoteBookName).append(Constants.STRING_SEPARATOR)
                .append(createTime).append(Constants.STRING_SEPARATOR)
                .append(status);
        // 从redis中删除list中的笔记本
        return redisDao.delNotebookToRedis(userName, oldNoteBookToString.toString());
    }

    /**
     * 重命名笔记本
     *
     * @param noteBookName
     * @param oldNoteBookName
     * @param userName
     * @param createTime
     * @param status
     * @return
     */
    @Override
    public boolean updateNoteBook(String noteBookName, String oldNoteBookName, String userName, String createTime, int status) {
        boolean ifSucess = false;
        ifSucess = renameNoteBookToRedis(noteBookName, oldNoteBookName, userName, createTime, status);
        if (ifSucess) {
            try {
                ifSucess = renameNoteBookToHbase(noteBookName, oldNoteBookName, userName, createTime, status);
                if (!ifSucess) {
                    renameNoteBookToRedis(oldNoteBookName, noteBookName, userName, createTime, status);
                }
            } catch (Exception e) {
                renameNoteBookToRedis(oldNoteBookName, noteBookName, userName, createTime, status);
                e.printStackTrace();
                return false;
            }
        }
        return ifSucess;
    }

    /**
     * 重命名redis中的笔记本名字
     *
     * @param noteBookName
     * @param oldNoteBookName
     * @param userName
     * @param createTime
     * @param status
     * @return
     */
    public boolean renameNoteBookToRedis(String noteBookName, String oldNoteBookName, String userName, String createTime, int status) {
        StringBuffer noteBookToString = new StringBuffer();
        // 需要新增的信息
        noteBookToString
                .append(userName + Constants.ROWKEY_SEPARATOR
                        + createTime.trim()).append(Constants.STRING_SEPARATOR)
                .append(noteBookName).append(Constants.STRING_SEPARATOR)
                .append(createTime).append(Constants.STRING_SEPARATOR)
                .append(status);
        // 需要删除的信息
        StringBuffer oldNoteBookToString = new StringBuffer();
        oldNoteBookToString
                .append(userName + Constants.ROWKEY_SEPARATOR
                        + createTime.trim()).append(Constants.STRING_SEPARATOR)
                .append(oldNoteBookName).append(Constants.STRING_SEPARATOR)
                .append(createTime).append(Constants.STRING_SEPARATOR)
                .append(status);
        // 先删后加
        return redisDao.updateNotebookToRedis(userName, oldNoteBookToString.toString(), noteBookToString.toString());
    }

    /**
     * 重命名hbase中的笔记本名字
     *
     * @param newNoteBookName
     * @param oldNoteBookName
     * @param userName
     * @param createTime
     * @param status
     * @return
     */
    public boolean renameNoteBookToHbase(String newNoteBookName, String oldNoteBookName, String userName, String createTime, int status) {
        // pingrowkey
        String rowKey = userName.trim() + Constants.ROWKEY_SEPARATOR + createTime.trim();
        return dataDao.insertData(Constants.NOTEBOOK_TABLE_NAME, rowKey, Constants.NOTEBOOK_FAMLIY_NOTEBOOKINFO, Constants.NOTEBOOK_NOTEBOOKINFO_CLU_NOTEBOOKNAME, newNoteBookName);
    }

    /**
     * 删除笔记本
     *
     * @param noteBookName
     * @param userName
     * @param createTime
     * @param i
     * @return
     */
    @Override
    public boolean deleteNoteBook(String noteBookName, String userName, String createTime, int i) {
        boolean ifSuccess = false;
        boolean delRedis = deleteNoteBookFromRedis(noteBookName, userName, createTime, i);
        if (delRedis) {
            try {
                boolean delHbase = deleteNoteBookFromHbase(noteBookName, userName, createTime, i);
                if (delHbase) {
                    ifSuccess = true;
                } else {
                    //如果删除hbase失败了，应该把redis的数据还原回去,如果这里添加失败了，redis的数据就和hbase数据不一致
                    boolean b = addNoteBookToRedis(noteBookName, userName, createTime, i);
                }

            } catch (Exception e) {
                boolean b = addNoteBookToRedis(noteBookName, userName, createTime, i);
                e.printStackTrace();
                return false;
            }
        }
        return ifSuccess;
    }

    /**
     * 删除hbase中的笔记本信息
     *
     * @param noteBookName
     * @param userName
     * @param createTime
     * @param status
     * @return
     */
    private boolean deleteNoteBookFromHbase(String noteBookName, String userName, String createTime, int status) {
        //拼接rowKey
        StringBuffer rowKey = new StringBuffer();
        rowKey.append(userName.trim() + Constants.ROWKEY_SEPARATOR)
                .append(createTime.trim());
        return dataDao.deleteData(Constants.NOTEBOOK_TABLE_NAME, rowKey.toString(), Constants.NOTEBOOK_FAMLIY_NOTEBOOKINFO);
    }

    /***
     * 查询一个笔记本下的所有笔记
     * @param rowkey
     * @return
     * @throws IOException
     */
    @Override
    public List<Note> getNoteListByNotebook(String rowkey) throws IOException {
        return null;
    }


    /*@Override
    public List<Note> getNoteListByNotebook(String rowkey) throws IOException {
        // 从hbase获取笔记列表
        return dataDao.queryNoteListByRowKey(rowkey);
    }





    public boolean deleteNoteBookFromHbase(String oldNoteBookName,
                                           String userName, String createTime, int status) {
        // 拼接rowkey
        String rowKey = userName.trim() + Constants.ROWKEY_SEPARATOR
                + createTime.trim();
        // 删除笔记本
        return dataDao.deleteData(Constants.NOTEBOOK_TABLE_NAME, rowKey);
    }







    @Override
    public boolean addNote(String noteRowKey, String noteName,
                           String createTime, String status, String noteBookRowkey) {
        boolean ifSucess = false;
        // 查询旧的笔记列表
        List<String> noteList = dataDao.queryByRowKeyString(
                Constants.NOTEBOOK_TABLE_NAME, noteBookRowkey);
        ifSucess = addNoteToNoteList(noteRowKey, noteName, createTime, status,
                noteBookRowkey, noteList);
        if (ifSucess) {
            try {
                ifSucess = addNoteToOrderTable(noteRowKey, noteName,
                        createTime, status, noteBookRowkey);
                if (!ifSucess) {
                    dataDao.insertData(Constants.NOTEBOOK_TABLE_NAME,
                            noteBookRowkey,
                            Constants.NOTEBOOK_FAMLIY_NOTEBOOKINFO,
                            Constants.NOTEBOOK_NOTEBOOKINFO_CLU_NOTELIST,
                            JSONArray.fromObject(noteList).toString());
                }
            } catch (Exception e) {
                dataDao.insertData(Constants.NOTEBOOK_TABLE_NAME,
                        noteBookRowkey, Constants.NOTEBOOK_FAMLIY_NOTEBOOKINFO,
                        Constants.NOTEBOOK_NOTEBOOKINFO_CLU_NOTELIST, JSONArray
                                .fromObject(noteList).toString());
                e.printStackTrace();
                return false;
            }
        }
        return ifSucess;
    }

    public boolean addNoteToNoteList(String noteRowKey, String noteName,
                                     String createTime, String status, String noteBookRowkey,
                                     List<String> noteList) {
        if (noteList == null) {
            noteList = new ArrayList<String>();
        }
        // 拼装新的笔记信息
        StringBuffer noteBookToString = new StringBuffer();
        noteBookToString.append(noteRowKey).append(Constants.STRING_SEPARATOR)
                .append(noteName).append(Constants.STRING_SEPARATOR)
                .append(createTime).append(Constants.STRING_SEPARATOR)
                .append(status);
        // 添加到笔记列表
        noteList.add(noteBookToString.toString());
        JSONArray jsonarray = JSONArray.fromObject(noteList);// list转json
        String noteListToJson = jsonarray.toString();// list转json
        // 修改笔记本中的笔记list信息
        return dataDao.insertData(Constants.NOTEBOOK_TABLE_NAME,
                noteBookRowkey, Constants.NOTEBOOK_FAMLIY_NOTEBOOKINFO,
                Constants.NOTEBOOK_NOTEBOOKINFO_CLU_NOTELIST, noteListToJson);
    }

    public boolean addNoteToOrderTable(String noteRowKey, String noteName,
                                       String createTime, String status, String noteBookRowkey) {
        // 封装笔记信息
        String noteFamQuaVals[][] = new String[4][3];
        noteFamQuaVals[0][0] = Constants.NOTE_FAMLIY_NOTEINFO;
        noteFamQuaVals[0][1] = Constants.NOTE_NOTEINFO_CLU_NOTENAME;
        noteFamQuaVals[0][2] = noteName;
        noteFamQuaVals[1][0] = Constants.NOTE_FAMLIY_NOTEINFO;
        noteFamQuaVals[1][1] = Constants.NOTE_NOTEINFO_CLU_STATUS;
        noteFamQuaVals[1][2] = status;
        noteFamQuaVals[2][0] = Constants.NOTE_FAMLIY_NOTEINFO;
        noteFamQuaVals[2][1] = Constants.NOTE_NOTEINFO_CLU_CREATETIME;
        noteFamQuaVals[2][2] = createTime;
        noteFamQuaVals[3][0] = Constants.NOTE_FAMLIY_CONTENTINFO;
        noteFamQuaVals[3][1] = Constants.NOTE_CONTENTINFO_CLU_CONTENT;
        noteFamQuaVals[3][2] = "";
        return dataDao.insertData(Constants.NOTE_TABLE_NAME, noteRowKey,
                noteFamQuaVals);
    }

    @Override
    public boolean deleteNote(String noteRowKey, String createTime,
                              String status, String oldNoteName, String noteBookRowkey) {
        boolean ifSuccess = false;
        // 查询旧笔记信息
        List<String> noteList = dataDao.queryByRowKeyString(
                Constants.NOTEBOOK_TABLE_NAME, noteBookRowkey);
        ifSuccess = deleteNoteFromNoteBookTable(noteRowKey, createTime, status,
                oldNoteName, noteBookRowkey, noteList);
        if (ifSuccess) {
            try {
                ifSuccess = deleteNoteFromNoteTable(noteRowKey);
                if (!ifSuccess) {
                    dataDao.insertData(Constants.NOTEBOOK_TABLE_NAME,
                            noteBookRowkey,
                            Constants.NOTEBOOK_FAMLIY_NOTEBOOKINFO,
                            Constants.NOTEBOOK_NOTEBOOKINFO_CLU_NOTELIST,
                            JSONArray.fromObject(noteList).toString());
                }
            } catch (Exception e) {
                dataDao.insertData(Constants.NOTEBOOK_TABLE_NAME,
                        noteBookRowkey, Constants.NOTEBOOK_FAMLIY_NOTEBOOKINFO,
                        Constants.NOTEBOOK_NOTEBOOKINFO_CLU_NOTELIST, JSONArray
                                .fromObject(noteList).toString());
                e.printStackTrace();
                return false;
            }
        }
        return ifSuccess;
    }

    public boolean deleteNoteFromNoteBookTable(String noteRowKey,
                                               String createTime, String status, String oldNoteName,
                                               String noteBookRowkey, List<String> noteList) {
        // 修改对应笔记本信息
        StringBuffer oldNoteBookToString = new StringBuffer();
        oldNoteBookToString.append(noteRowKey)
                .append(Constants.STRING_SEPARATOR).append(oldNoteName)
                .append(Constants.STRING_SEPARATOR).append(createTime)
                .append(Constants.STRING_SEPARATOR).append(status);
        noteList.remove(oldNoteBookToString.toString());
        String noteListToJson = JSONArray.fromObject(noteList).toString();// list转json

        return dataDao.insertData(Constants.NOTEBOOK_TABLE_NAME,
                noteBookRowkey, Constants.NOTEBOOK_FAMLIY_NOTEBOOKINFO,
                Constants.NOTEBOOK_NOTEBOOKINFO_CLU_NOTELIST, noteListToJson);
    }

    public boolean deleteNoteFromNoteTable(String noteRowKey) {
        // 删除笔记信息
        return dataDao.deleteData(Constants.NOTE_TABLE_NAME, noteRowKey);
    }

    @Override
    public boolean updateNote(String noteRowKey, String noteName,
                              String createTime, String content, String status,
                              String oldNoteName, String noteBookRowkey) {

        // 获取旧的笔记列表
        List<String> noteList = dataDao.queryByRowKeyString(
                Constants.NOTEBOOK_TABLE_NAME, noteBookRowkey);
        boolean ifSuccess = false;
        ifSuccess = updateNoteListFromNoteBookTanle(noteRowKey, noteName,
                createTime, content, status, oldNoteName, noteBookRowkey,
                noteList);
        if (ifSuccess) {
            try {
                ifSuccess = updateNoteFromNoteTable(noteRowKey, noteName,
                        createTime, content, status, oldNoteName,
                        noteBookRowkey);
                if (!ifSuccess) {
                    dataDao.insertData(Constants.NOTEBOOK_TABLE_NAME,
                            noteBookRowkey,
                            Constants.NOTEBOOK_FAMLIY_NOTEBOOKINFO,
                            Constants.NOTEBOOK_NOTEBOOKINFO_CLU_NOTELIST,
                            JSONArray.fromObject(noteList).toString());
                }
            } catch (Exception e) {
                dataDao.insertData(Constants.NOTEBOOK_TABLE_NAME,
                        noteBookRowkey, Constants.NOTEBOOK_FAMLIY_NOTEBOOKINFO,
                        Constants.NOTEBOOK_NOTEBOOKINFO_CLU_NOTELIST, JSONArray
                                .fromObject(noteList).toString());
                e.printStackTrace();
                return false;
            }
        }

        return ifSuccess;
    }

    public boolean updateNoteListFromNoteBookTanle(String noteRowKey,
                                                   String noteName, String createTime, String content, String status,
                                                   String oldNoteName, String noteBookRowkey, List<String> noteList) {
        StringBuffer noteBookToString = new StringBuffer();
        noteBookToString.append(noteRowKey).append(Constants.STRING_SEPARATOR)
                .append(noteName).append(Constants.STRING_SEPARATOR)
                .append(createTime).append(Constants.STRING_SEPARATOR)
                .append(status);
        StringBuffer oldNoteBookToString = new StringBuffer();
        oldNoteBookToString.append(noteRowKey)
                .append(Constants.STRING_SEPARATOR).append(oldNoteName)
                .append(Constants.STRING_SEPARATOR).append(createTime)
                .append(Constants.STRING_SEPARATOR).append(status);
        noteList.remove(oldNoteBookToString.toString());
        noteList.add(noteBookToString.toString());
        JSONArray jsonarray = JSONArray.fromObject(noteList);// list转json
        String noteListToJson = jsonarray.toString();// list转json
        return dataDao.insertData(Constants.NOTEBOOK_TABLE_NAME,
                noteBookRowkey, Constants.NOTEBOOK_FAMLIY_NOTEBOOKINFO,
                Constants.NOTEBOOK_NOTEBOOKINFO_CLU_NOTELIST, noteListToJson);
    }

    public boolean updateNoteFromNoteTable(String noteRowKey, String noteName,
                                           String createTime, String content, String status,
                                           String oldNoteName, String noteBookRowkey) {
        // 笔记信息存hbase
        String noteFamQuaVals[][] = new String[4][3];
        noteFamQuaVals[0][0] = Constants.NOTE_FAMLIY_NOTEINFO;
        noteFamQuaVals[0][1] = Constants.NOTE_NOTEINFO_CLU_NOTENAME;
        noteFamQuaVals[0][2] = noteName;
        noteFamQuaVals[1][0] = Constants.NOTE_FAMLIY_NOTEINFO;
        noteFamQuaVals[1][1] = Constants.NOTE_NOTEINFO_CLU_STATUS;
        noteFamQuaVals[1][2] = status;
        noteFamQuaVals[2][0] = Constants.NOTE_FAMLIY_NOTEINFO;
        noteFamQuaVals[2][1] = Constants.NOTE_NOTEINFO_CLU_CREATETIME;
        noteFamQuaVals[2][2] = createTime;
        noteFamQuaVals[3][0] = Constants.NOTE_FAMLIY_CONTENTINFO;
        noteFamQuaVals[3][1] = Constants.NOTE_CONTENTINFO_CLU_CONTENT;
        noteFamQuaVals[3][2] = content;
        return dataDao.insertData(Constants.NOTE_TABLE_NAME, noteRowKey,
                noteFamQuaVals);
    }

    @Override
    public boolean moveAndDeleteNote(String noteRowKey,
                                     String oldNoteBookRowkey, String newNoteBookRowkey, String noteName) {
        // 查询旧笔记本下的笔记信息
        List<String> oldNoteBookNoteList = dataDao.queryByRowKeyString(
                Constants.NOTEBOOK_TABLE_NAME, oldNoteBookRowkey);
        // 查询新笔记本下的笔记信息
        List<String> newNoteBookNoteList = dataDao.queryByRowKeyString(
                Constants.NOTEBOOK_TABLE_NAME, newNoteBookRowkey);
        // 创建时间
        String createTime = noteRowKey.split(Constants.ROWKEY_SEPARATOR)[1];
        boolean ifSuccess = false;
        // 删除旧笔记本下的笔记
        ifSuccess = deleteNoteFromNoteBookTable(noteRowKey, createTime, "0",
                noteName, oldNoteBookRowkey, oldNoteBookNoteList);
        if (ifSuccess) {
            try {
                // 添加新笔记本下的笔记
                ifSuccess = addNoteToNoteList(noteRowKey, noteName, createTime,
                        "0", newNoteBookRowkey, newNoteBookNoteList);
                if (!ifSuccess) {
                    dataDao.insertData(Constants.NOTEBOOK_TABLE_NAME,
                            oldNoteBookRowkey, Constants.NOTEBOOK_FAMLIY_NOTEBOOKINFO,
                            Constants.NOTEBOOK_NOTEBOOKINFO_CLU_NOTELIST, JSONArray
                                    .fromObject(oldNoteBookNoteList).toString());
                }
            } catch (Exception e) {
                dataDao.insertData(Constants.NOTEBOOK_TABLE_NAME,
                        oldNoteBookRowkey, Constants.NOTEBOOK_FAMLIY_NOTEBOOKINFO,
                        Constants.NOTEBOOK_NOTEBOOKINFO_CLU_NOTELIST, JSONArray
                                .fromObject(oldNoteBookNoteList).toString());
                e.printStackTrace();
                return false;
            }
        }
        return ifSuccess;
    }

    *//**
     * 获取笔记
     *//*
    @Override
    public Note getNoteByRowKey(String noteRowkey) {
        Note note = dataDao.queryNoteByRowKey(noteRowkey);
        return note;
    }

    *//**
     * 分享笔记 rowKey：rowKey
     *
     * @throws IOException
     * @throws CorruptIndexException
     *//*
    @Override
    public boolean shareNote(String rowKey) throws CorruptIndexException,
            IOException {
        Result queryByRowKey = dataDao.queryByRowKey(Constants.NOTE_TABLE_NAME,
                rowKey);// 查询笔记
        // 封装参数
        String noteName = new String(queryByRowKey.getValue(
                Bytes.toBytes(Constants.NOTE_FAMLIY_NOTEINFO),
                Bytes.toBytes(Constants.NOTE_NOTEINFO_CLU_NOTENAME)));
        String content = new String(queryByRowKey.getValue(
                Bytes.toBytes(Constants.NOTE_FAMLIY_CONTENTINFO),
                Bytes.toBytes(Constants.NOTE_CONTENTINFO_CLU_CONTENT)));
        String time = new String(queryByRowKey.getValue(
                Bytes.toBytes(Constants.NOTE_FAMLIY_NOTEINFO),
                Bytes.toBytes(Constants.NOTE_NOTEINFO_CLU_CREATETIME)));
        Article article = new Article();
        article.setId(rowKey);
        article.setTitle(noteName);
        article.setTime(time);
        article.setContent(content);
        boolean saveNoteToLucene = createIndexDao.saveNoteToLucene(article);// 创建索引
        return saveNoteToLucene;
    }

    *//**
     * 根据关键字获取技术问答列表 key：关键字 page：页码
     *//*
    @Override
    public List<Article> search(String key, int page)
            throws InterruptedException, ParseException, IOException,
            InvalidTokenOffsetsException {
        SearchBean searchBean = new SearchBean();// 封装参数
        searchBean.setKey(key);
        searchBean.setPage(page);
        List<Article> articles = searchIndexDao.searchIndex(searchBean);// 查询文章
        return articles;
    }

    *//**
     * 收藏笔记
     *//*
    @Override
    public boolean starOtherNote(String noteRowKey, String starBtRowKey) {
        Note note = getNoteByRowKey(noteRowKey);// 获取笔记信息
        boolean addNote = addNote(noteRowKey, note.getName(),
                note.getCreateTime(), note.getStatus(), starBtRowKey);
        return addNote;
    }

    *//**
     * 活动笔记
     *//*
    @Override
    public boolean activeMyNote(String noteRowKey, String activityBtRowKey) {
        Note note = getNoteByRowKey(noteRowKey);// 获取笔记信息
        boolean addNote = addNote(noteRowKey, note.getName(),
                note.getCreateTime(), note.getStatus(), activityBtRowKey);
        return addNote;
    }*/
}
