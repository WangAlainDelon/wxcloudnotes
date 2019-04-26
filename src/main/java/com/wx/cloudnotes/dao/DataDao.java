package com.wx.cloudnotes.dao;

import java.io.IOException;
import java.util.List;

import com.wx.cloudnotes.domain.Note;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;

public interface DataDao {
   /*
    public ResultScanner queryAll(String tableName) throws IOException;

    public ResultScanner querySome(String tableName, List<String> rowKeys)
            throws IOException;*/

    boolean insertData(String tableName, String rowKey, String family, String qualifier, String value);

    boolean insertData(String tableName, String rowKey, String[][] famQuaVals);

    List<Result> queryByReg(String tableName, String reg) throws IOException;

    boolean deleteData(String tableName, String rowKey, String family);

    List<Note> queryNoteListByRowKey(String noteBookRowkey);

    List<String> queryByRowKeyString(String notebookTableName, String noteBookRowkey);

    Note queryNoteByRowKey(String noteRowkey);

   /* public void copyData(String fromTableName, String toTableName, String fromRowKey, String toRowKey) throws IOException;





    public Result queryByRowKey(String noteTableName, String rowKey);*/
}
