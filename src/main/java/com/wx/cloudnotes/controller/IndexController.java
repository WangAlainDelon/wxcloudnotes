package com.wx.cloudnotes.controller;

import com.wx.cloudnotes.domain.User;
import com.wx.cloudnotes.service.UserService;
import com.wx.cloudnotes.utils.hbase.HbaseTools;
import com.wx.cloudnotes.utils.redis.RedisTools;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
public class IndexController {

    @Autowired
    private RedisTools redisTools;

    @Autowired
    private HbaseTools hbaseTools;

    @Autowired
    private UserService userService;

    @RequestMapping("/index")
    public String index() {
        hbaseTools.inserData("user", "wang_123456", "info1", "name", "svnkdsnvkjs");
        return "index/index";
    }

    @RequestMapping("/get")
    @ResponseBody
    public String get() {
        List<String> list = new ArrayList<String>();
        list.add("wang_123456");
        List<Result> listRowkeyData = hbaseTools.getListRowkeyData("user", list, "info1", "name");
        for (Result listRowkeyDatum : listRowkeyData) {
            byte[] nameBytes = listRowkeyDatum.getValue(Bytes.toBytes("info1"), Bytes.toBytes("name"));
            System.out.println(new String(Bytes.toString(nameBytes)));
        }
        return "";
    }

    @RequestMapping("/getUser")
    @ResponseBody
    public User getUser(@RequestParam(value = "id") Integer id) {
        System.out.println(id);
        return userService.selectOne(id);
    }

}
