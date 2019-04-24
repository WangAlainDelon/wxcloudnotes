package com.wx.cloudnotes.controller;

import com.wx.cloudnotes.common.Constants;
import com.wx.cloudnotes.common.WxResult;
import com.wx.cloudnotes.domain.NoteBook;
import com.wx.cloudnotes.service.NoteService;
import com.wx.cloudnotes.utils.log.LogUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import org.thymeleaf.util.LoggingUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class NoteController {

    @Autowired
    private NoteService noteService;

    @RequestMapping("/note/index/inotecenter")
    public String noteIndex() {
        /**这里验证用户是否登陆，如果没有登陆则跳转到登陆的页面*/
        System.out.println("");
        return "/note/inotecenter";
    }

    /***/
    @RequestMapping("/note/getAllNoteBook")
    public ModelAndView getAllNoteBook(HttpServletRequest request) {
        ModelAndView modelAndView = null;
        String userName = null;
        try {
            // 从session中获取用户名
            userName = (String) request.getSession().getAttribute(Constants.USER_INFO);
            // 查询用户笔记本
            List<NoteBook> allNoteBook = noteService.getAllNoteBook(userName);// 查询所有笔记本
            // 封装返回值
            ModelMap map = new ModelMap();
            map.put("allNoteBook", allNoteBook);
            map.put("recycleBtRowKey", userName + Constants.RECYCLE);
            map.put("starBtRowKey", userName + Constants.STAR);
            map.put("activityBtRowKey", userName + Constants.ACTIVITY);
            modelAndView = new ModelAndView(new MappingJackson2JsonView(), map);
        } catch (Exception e) {
            Logger platformLogger = LogUtils.getPlatformLogger();
            platformLogger.error("用户" + userName + "获取所有笔记本异常|方法:getAllNoteBook|参数： userName:" + userName, e);
            e.printStackTrace();
        }
        return modelAndView;
    }

    /**
     * 添加笔记本
     *
     * @param request
     * @param noteBookName
     * @return
     */
    @RequestMapping("/note/addNoteBook")
    public ModelAndView addNoteBook(HttpServletRequest request, String noteBookName) {
        ModelAndView modelAndView = null;
        // 从session中获取用户循序
        String userName = (String) request.getSession().getAttribute(Constants.USER_INFO);
        try {
            // 创建时间戳
            Long createTime = System.currentTimeMillis();
            // 保存笔记本
            boolean b = noteService.addNoteBook(noteBookName, userName, createTime.toString(), 0);
            ModelMap map = new ModelMap();
            if (b) {
                // 封装rowkey信息返回前台
                map.put("resource", userName + Constants.ROWKEY_SEPARATOR + createTime);
                map.put("success", true);
            } else {
                map.put("success", false);
            }
            modelAndView = new ModelAndView(new MappingJackson2JsonView(), map);

        } catch (Exception e) {
            Logger bussinessLogger = LogUtils.getBussinessLogger();
            bussinessLogger.error("用户" + userName + "添加笔记本异常|方法：addNoteBook|  参数：noteBookName:" + noteBookName, e);
            e.printStackTrace();
        }
        return modelAndView;
    }
}
