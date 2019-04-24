package com.wx.cloudnotes.dao;

import java.io.IOException;
import java.util.List;

import com.wx.cloudnotes.domain.Note;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;

public interface DataDao {
   /*


    public boolean deleteData(String tableName, String rowKey);

    public ResultScanner queryAll(String tableName) throws IOException;

    public ResultScanner querySome(String tableName, List<String> rowKeys)
            throws IOException;*/

    public boolean insertData(String tableName, String rowKey, String family, String qualifier, String value);
    public boolean insertData(String tableName, String rowKey, String[][] famQuaVals);
    public  List<Result> queryByReg(String tableName, String reg) throws IOException;

   /* public void copyData(String fromTableName, String toTableName, String fromRowKey, String toRowKey) throws IOException;

    public List<String> queryByRowKeyString(String notebookTableName,
                                            String noteBookRowkey);

    public Note queryNoteByRowKey(String noteRowkey);

    public List<Note> queryNoteListByRowKey(String rowkey);

    public Result queryByRowKey(String noteTableName, String rowKey);*/
}
