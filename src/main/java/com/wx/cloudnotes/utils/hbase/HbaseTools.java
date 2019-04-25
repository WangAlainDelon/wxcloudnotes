package com.wx.cloudnotes.utils.hbase;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.hadoop.hbase.HbaseTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class HbaseTools {

    //参考博客：https://blog.csdn.net/u010775025/article/details/80773679

    @Autowired
    private HbaseTemplate hbaseTemplate;

    /***
     * 通过rowKey正则表达式查询
     * @param tableName
     * @param reg
     * @return
     */
    public List<Result> queryByReg(String tableName, String reg) {
        Scan scan = new Scan();// 创建scan，用于查询
        RowFilter filter = new RowFilter(CompareFilter.CompareOp.EQUAL, new RegexStringComparator(reg));// 创建正则表达式filter
        scan.setFilter(filter);// 设置filter
        return hbaseTemplate.find(tableName, scan, (rowMapper, rowNum) -> rowMapper);
    }


    /**
     * 添加数据,单条添加
     *
     * @param tableName
     * @param rowKey
     * @param lieZu
     * @param lie
     * @param value
     * @return
     */
    public boolean inserData(String tableName, String rowKey, String lieZu, String lie, String value) {
        try {
            hbaseTemplate.put(tableName, rowKey, lieZu, lie, Bytes.toBytes(value));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 添加笔记本的时插入hbase的操作
     *
     * @param tableName
     * @param rowKey
     * @param famQuaVals
     * @return
     */
    public boolean insertData(String tableName, String rowKey, String[][] famQuaVals) {
        try {
            for (int i = 0; i < famQuaVals.length; i++) {
                /*
                 *       nbi(列族1，笔记本信息)  nbn（列1，笔记本名字）         noteBookName
                 *       nbi(列族1，笔记本信息)  ct(列2：创建笔记本时间)        createTime
                 *       nbi(列族1，笔记本信息)  st（列3：笔记本状态）          status
                 *       nbi(列族1，笔记本信息)  nl（列4：笔记本下笔记信息列表）noteListToJson
                 *
                 * */
                //famQuaVals[i][2]就是value
                if (famQuaVals[i][2] != null) {
                    hbaseTemplate.put(tableName, rowKey, famQuaVals[i][0], famQuaVals[i][1], Bytes.toBytes(famQuaVals[i][2]));
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除hbase中的数据
     *
     * @param tableName
     * @param rowKey
     * @param lieZu
     * @return
     */
    public boolean deleteData(String tableName, String rowKey, String lieZu) {
        try {
            hbaseTemplate.delete(tableName, rowKey, lieZu);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 查询数据
     */
    public List<Result> getData(String tableName, String startRowkey, String stopRowkey, String column, String qualifier) {
        FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL);
        if (StringUtils.isNotBlank(column)) {
            filterList.addFilter(new FamilyFilter(CompareFilter.CompareOp.EQUAL, new BinaryComparator(Bytes.toBytes(column))));
        }
        if (StringUtils.isNotBlank(qualifier)) {
            filterList.addFilter(new QualifierFilter(CompareFilter.CompareOp.EQUAL, new BinaryComparator(Bytes.toBytes(qualifier))));
        }
        Scan scan = new Scan();
        if (filterList.getFilters().size() > 0) {
            scan.setFilter(filterList);
        }
        scan.setStartRow(Bytes.toBytes(startRowkey));
        scan.setStopRow(Bytes.toBytes(stopRowkey));

        return hbaseTemplate.find(tableName, scan, (rowMapper, rowNum) -> rowMapper);

    }

    public List<Result> getListRowkeyData(String tableName, List<String> rowKeys, String familyColumn, String column) {
        return rowKeys.stream().map(rk -> {
            if (StringUtils.isNotBlank(familyColumn)) {
                if (StringUtils.isNotBlank(column)) {
                    return hbaseTemplate.get(tableName, rk, familyColumn, column, (rowMapper, rowNum) -> rowMapper);
                } else {
                    return hbaseTemplate.get(tableName, rk, familyColumn, (rowMapper, rowNum) -> rowMapper);
                }
            }
            return hbaseTemplate.get(tableName, rk, (rowMapper, rowNum) -> rowMapper);
        }).collect(Collectors.toList());
    }
    /***/


}
