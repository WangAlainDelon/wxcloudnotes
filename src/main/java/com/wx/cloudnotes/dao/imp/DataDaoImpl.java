package com.wx.cloudnotes.dao.imp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.wx.cloudnotes.common.Constants;
import com.wx.cloudnotes.dao.DataDao;
import com.wx.cloudnotes.domain.Note;
import com.wx.cloudnotes.utils.JsonUtil;
import com.wx.cloudnotes.utils.hbase.HbaseTools;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;


@Service("dataDaoImpl")
public class DataDaoImpl implements DataDao {
    private static Logger logger = LoggerFactory.getLogger(DataDaoImpl.class);

    @Autowired
    private HbaseTools hbaseTools;

    /***
     * 通过rowKey正则表达式查询
     * @param tableName
     * @param reg
     * @return
     */
    public List<Result> queryByReg(String tableName, String reg) {
        /**这里考虑需不需要对tableName，reg做判断*/
        List<Result> results = hbaseTools.queryByReg(tableName, reg);
        return results;
    }

    /**
     * 插入数据
     * tableName：表名
     * rowKey：rowKey
     * family：列族
     * qualifier：列描述，即列
     * value：插入的值
     * IOException：IO异常
     */
    public boolean insertData(String tableName, String rowKey, String family, String qualifier, String value) {
        return hbaseTools.inserData(tableName, rowKey, family, qualifier, value);
    }

    /**
     * 插入数据
     * tableName：表名
     * rowKey：rowKey
     * famQuaVals：二维数组，[[famliy，qualifier，value],……………………]
     * IOException：IO异常
     */
    public boolean insertData(String tableName, String rowKey, String[][] famQuaVals) {
        return hbaseTools.insertData(tableName, rowKey, famQuaVals);
    }

    /**
     * 根据rowKey删除数据
     * tableName：表名
     * rowKey：rowKey
     * IOException：IO异常
     */
    public boolean deleteData(String tableName, String rowKey, String lieZu) {
        return hbaseTools.deleteData(tableName, rowKey, lieZu);
    }

    /**
     * 根据笔记本的rowKey查询笔记本下的笔记列表
     *
     * @param noteBookRowkey
     * @return
     */
    public List<Note> queryNoteListByRowKey(String noteBookRowkey) {
        List<String> rowkeyList = new ArrayList<String>();
        rowkeyList.add(noteBookRowkey);
        List<Result> reslutList = hbaseTools.getListRowkeyData(Constants.NOTEBOOK_TABLE_NAME, rowkeyList, StringUtils.EMPTY, StringUtils.EMPTY);
        //取出数据封装成List<Note>
        List<Note> noteList = null;
        if (reslutList != null) {
            for (Result result : reslutList) {
                Note note = new Note();
                //从返回来的结果中筛选出存放笔记列表的列
                byte[] value = result.getValue(Bytes.toBytes(Constants.NOTEBOOK_FAMLIY_NOTEBOOKINFO), Bytes.toBytes(Constants.NOTEBOOK_NOTEBOOKINFO_CLU_NOTELIST));
                if (value != null) {
                    String str = new String(value);
                    //将list转换为json
                    noteList = JsonUtil.changeStringToListNote(str);
                }
            }
        }
        return noteList;
    }

    /**
     * 根据笔记本的rowkey 查询笔记列表
     *
     * @param tableName
     * @param noteBookRowkey
     * @return
     */
    public List<String> queryByRowKeyString(String tableName, String noteBookRowkey) {
        List<String> list = null;
        List<String> rowkeyList = new ArrayList<String>();
        rowkeyList.add(noteBookRowkey);
        List<Result> rowkeyData = hbaseTools.getListRowkeyData(tableName, rowkeyList, StringUtils.EMPTY, StringUtils.EMPTY);
        if (rowkeyData != null) {
            for (Result rowkeyDatum : rowkeyData) {
                byte[] value = rowkeyDatum.getValue(Constants.NOTEBOOK_FAMLIY_NOTEBOOKINFO.getBytes(), Constants.NOTEBOOK_NOTEBOOKINFO_CLU_NOTELIST.getBytes());
                if (value != null){
                    String str = new String(value);
                    //将json转化为list
                    list = JsonUtil.changeStringToListString(str);
                }
            }
        }
        return list;
    }

    /**
     * 根据rowKey 查询笔记的详情
     *
     * @param noteRowkey
     * @return
     */
    @Override
    public Note queryNoteByRowKey(String noteRowkey) {
        List<String> list = new ArrayList<String>();
        Note note = new Note();
        try {
            list.add(noteRowkey);
            List<Result> listRowkeyData = hbaseTools.getListRowkeyData(Constants.NOTE_TABLE_NAME, list, StringUtils.EMPTY, StringUtils.EMPTY);
            for (Result listRowkeyDatum : listRowkeyData) {
                note.setRowKey(noteRowkey);
                byte[] valueName = listRowkeyDatum.getValue(Bytes.toBytes(Constants.NOTE_FAMLIY_NOTEINFO), Bytes.toBytes(Constants.NOTE_NOTEINFO_CLU_NOTENAME));
                note.setName(new String(valueName, "UTF-8"));
                byte[] valueCreateTime = listRowkeyDatum.getValue(Bytes.toBytes(Constants.NOTE_FAMLIY_NOTEINFO), Bytes.toBytes(Constants.NOTE_NOTEINFO_CLU_CREATETIME));
                note.setCreateTime(new String(valueCreateTime, "UTF-8"));
                byte[] valueStatu = listRowkeyDatum.getValue(Bytes.toBytes(Constants.NOTE_FAMLIY_NOTEINFO), Bytes.toBytes(Constants.NOTE_NOTEINFO_CLU_STATUS));
                note.setStatus(new String(valueStatu, "UTF-8"));
                byte[] valueContent = listRowkeyDatum.getValue(Bytes.toBytes(Constants.NOTE_FAMLIY_CONTENTINFO), Bytes.toBytes(Constants.NOTE_CONTENTINFO_CLU_CONTENT));
                if (valueContent != null) {
                    note.setContent(new String(valueContent, "UTF-8"));
                } else {
                    note.setContent("");
                }
            }
        } catch (Exception e) {

        }
        return note;
    }


}
