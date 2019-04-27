package com.wx.cloudnotes.dao.imp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.wx.cloudnotes.common.Constants;
import com.wx.cloudnotes.dao.DataDao;
import com.wx.cloudnotes.domain.Note;
import com.wx.cloudnotes.domain.NoteBook;
import com.wx.cloudnotes.utils.JsonUtil;
import com.wx.cloudnotes.utils.hbase.HbaseTools;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.RegexStringComparator;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.hadoop.hbase.HbaseTemplate;
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
                note.setRowKey(String.valueOf(listRowkeyDatum.getRow()));
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

    /**

     *//**
     * 查询所有数据
     *  tableName：表名
     *  ResultScanner:结果列表
     *  IOException：IO异常
     *//*
	public ResultScanner queryAll(String tableName) throws IOException {
		Table table =  Constants.CONNECTION.getTable(TableName.valueOf(tableName));// 从连接池中获取表
		Scan scan = new Scan();// 创建scan，用于查询
		ResultScanner scanner = table.getScanner(scan);// 通过scan查询结果
		*//*
     * for (Result row: scanner) {//循环出每一条row
     * System.out.println(row.getRow()); for (KeyValue kv : row.raw())
     * {//循环row的不同列 System.out.println(new String(kv.getRow()));//获取rowkey
     * System.out.println(new String(kv.getFamily()));//获取列族
     * System.out.println(new String(kv.getQualifier()));//获取列描述
     * System.out.println(new String(kv.getValue()));//获取值
     * System.out.println(kv.getTimestamp());//获取版本时间 } }
     *//*
		table.close();// 关闭table
		return scanner;
	}

	*//**
     * 通过rowKey查询笔记列表
     * tableName：表名
     * rowKey：rowKey
     * IOExceptionL：IO异常
     *
     *//*
	public List<Note> queryNoteListByRowKey(String rowKey)
			{
		Result queryByRowKey = null;
		List<Note> noteList = null;
		try {
			Table table =  Constants.CONNECTION.getTable(TableName.valueOf(Constants.NOTEBOOK_TABLE_NAME));// 从连接池中获取表
			Get get = new Get(rowKey.getBytes());// 获取get，注入rowKey
			queryByRowKey = table.get(get);
			byte[] value = queryByRowKey.getValue(Constants.NOTEBOOK_FAMLIY_NOTEBOOKINFO.getBytes(), Constants.NOTEBOOK_NOTEBOOKINFO_CLU_NOTELIST.getBytes());
			if (value!=null) {
				String str = new String(value);
				//将list转换为json
				noteList = JsonUtil.changeStringToListNote(str);
			}
			table.close();// 关闭table
		} catch (Exception e) {
			e.printStackTrace();
		}
		return noteList;
	}
	*//**
     * 通过rowKey查询
     * tableName：表名
     * rowKey：rowKey
     * IOExceptionL：IO异常
     *
     *//*
	public Note queryNoteByRowKey(String rowKey)
	{
		Result row = null;
		Note note =null;
		try {
			Table table =  Constants.CONNECTION.getTable(TableName.valueOf(Constants.NOTE_TABLE_NAME));// 从连接池中获取表
			Get get = new Get(rowKey.getBytes());// 获取get，注入rowKey
			row = table.get(get);
			if (row!=null) {
				note = new Note();
				note.setRowKey(new String(row.getRow()));
				note.setName(new String(row.getValue(
						Constants.NOTE_FAMLIY_NOTEINFO.getBytes(),
						Constants.NOTE_NOTEINFO_CLU_NOTENAME.getBytes()), "UTF-8"));
				note.setCreateTime(new String(row.getValue(
						Constants.NOTE_FAMLIY_NOTEINFO.getBytes(),
						Constants.NOTE_NOTEINFO_CLU_CREATETIME.getBytes()), "UTF-8"));
				byte[] content = row.getValue(
						Constants.NOTE_FAMLIY_CONTENTINFO.getBytes(),
						Constants.NOTE_CONTENTINFO_CLU_CONTENT.getBytes());
				if (content != null) {
					String string = new String(content, "UTF-8");
					note.setContent(string);
				} else {
					note.setContent("");
				}
				note.setStatus(new String(row.getValue(
						Constants.NOTE_FAMLIY_NOTEINFO.getBytes(),
						Constants.NOTE_NOTEINFO_CLU_STATUS.getBytes()), "UTF-8"));
				
			}
			table.close();// 关闭table
		} catch (Exception e) {
			e.printStackTrace();
		}
		return note;
	}


	*//**
     * 查询部分rowKey数据
     * tableName：表名
     * rowKeys:rowKey列表
     * IOException:IO异常
     *//*
	public ResultScanner querySome(String tableName, List<String> rowKeys)
			throws IOException {
		Table table =  Constants.CONNECTION.getTable(TableName.valueOf(tableName));// 从连接池中获取表
		Scan scan = new Scan();// 创建scan，用于查询
		FilterList filter = new FilterList(FilterList.Operator.MUST_PASS_ONE);// FilterList.Operator.MUST_PASS_ONE（或）
																				// 的关系
		for (String rowKey : rowKeys) {
			String[] split = rowKey.split(",");
			filter.addFilter(new SingleColumnValueFilter(Bytes.toBytes(split[0]), Bytes.toBytes(split[1]),CompareOp.EQUAL, Bytes.toBytes(split[2])));
		}
		scan.setFilter(filter);
		ResultScanner scanner = table.getScanner(scan);
		*//*
     * for (Result row: scanner) { System.out.println(row.getRow()); for
     * (KeyValue kv : row.raw()) { System.out.println(new
     * String(kv.getRow())); System.out.println(new String(kv.getFamily()));
     * System.out.println(new String(kv.getQualifier()));
     * System.out.println(new String(kv.getValue()));
     * System.out.println(kv.getTimestamp()); } }
     *//*
		table.close();// 关闭table
		return scanner;
	}*/


    /**
     * 从一个表复制笔记到另一个表
     * fromTableName：源表
     * fromRowKey：源表rowKey
     * toTableName：目的表
     * toRowKey：目的表RowKey
     * @throws IOException
     *//*
	@Override
	public void copyData(String fromTableName, String toTableName, String fromRowKey, String toRowKey) throws IOException {
		Table fromTable =  Constants.CONNECTION.getTable(TableName.valueOf(fromTableName));// 从连接池中获取表
		Table toTable =  Constants.CONNECTION.getTable(TableName.valueOf(toTableName));//从连接池中获取表
		Get get = new Get(fromRowKey.getBytes());// 获取get，注入rowKey
		Result row = fromTable.get(get);// 获取单条row
		toTable.setAutoFlushTo(false);//AutoFlush指的是在每次调用HBase的Put操作，是否提交到HBase Server。 默认是true,每次会提交。如果此时是单条插入，就会有更多的IO,从而降低性能
		toTable.setWriteBufferSize(Constants.HBASE_WRITE_BUFFER);//Write Buffer Size在AutoFlush为false的时候起作用，默认是2MB,也就是当插入数据超过2MB,就会自动提交到Server
		List<Put> lp = new ArrayList<Put>();
		 for (KeyValue kv : row.raw()) {//循环数据 
			 Put put = new Put(Bytes.toBytes(toRowKey));//创建put，添加rowKey
			 put.add(kv.getFamily(), kv.getQualifier(),kv.getValue());//放入数据
			 lp.add(put);
			 }
		 toTable.put(lp);//将数据存入table
		 toTable.flushCommits();//提交
		logger.debug("拷贝数据成功!fromTableName：" + fromTableName+";toTableName:"+toTableName+";fromRowKey"+fromRowKey+";toRowKey"+toRowKey);
		toTable.close();//关闭table
		fromTable.close();//关闭table
	}

	@Override
	public Result queryByRowKey(String noteTableName, String rowKey) {

		Result row = null;
		try {
			Table table =  Constants.CONNECTION.getTable(TableName.valueOf(Constants.NOTE_TABLE_NAME));// 从连接池中获取表
			Get get = new Get(rowKey.getBytes());// 获取get，注入rowKey
			row = table.get(get);
			table.close();// 关闭table
		} catch (Exception e) {
			e.printStackTrace();
		}
		return row;
	
	}*/

}
