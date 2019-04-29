package com.wx.cloudnotes.dao;

import java.io.IOException;
import java.util.List;

import com.wx.cloudnotes.domain.Note;
import org.apache.hadoop.hbase.client.Result;


public interface DataDao {

    boolean insertData(String tableName, String rowKey, String family, String qualifier, String value);

    boolean insertData(String tableName, String rowKey, String[][] famQuaVals);

    List<Result> queryByReg(String tableName, String reg) throws IOException;

    boolean deleteData(String tableName, String rowKey, String family);

    List<Note> queryNoteListByRowKey(String noteBookRowkey);

    List<String> queryByRowKeyString(String notebookTableName, String noteBookRowkey);

    Note queryNoteByRowKey(String noteRowkey);


}
