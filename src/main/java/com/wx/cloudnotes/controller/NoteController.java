package com.wx.cloudnotes.controller;

import com.wx.cloudnotes.common.Constants;
import com.wx.cloudnotes.common.WxResult;
import com.wx.cloudnotes.domain.Note;
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

    /**
     * 登陆成功后加载redis中对应用户的笔记本信息
     *
     * @param request
     * @return
     */
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

    /**
     * 修改笔记本名称
     *
     * @param oldNoteBookName 旧笔记本的名字
     * @param newNoteBookName 新笔记本的名字
     * @param rowKey          rowkey  wx@163.com_312312
     * @return
     */
    @RequestMapping("/note/updateNoteBook")
    public ModelAndView updateNoteBook(HttpServletRequest request, String oldNoteBookName, String newNoteBookName, String rowKey) {
        ModelAndView modelAndView = null;
        try {
            // 分割row，取username和时间戳
            String[] split = rowKey.split("\\" + Constants.ROWKEY_SEPARATOR);
            // 重命名笔记本
            boolean flag = noteService.updateNoteBook(newNoteBookName, oldNoteBookName, split[0], split[1], 0);
            ModelMap map = new ModelMap();
            map.put("success", flag);
            modelAndView = new ModelAndView(new MappingJackson2JsonView(), map);
        } catch (Exception e) {
            String userName = (String) request.getSession().getAttribute(Constants.USER_INFO);
            Logger bussinessLogger = LogUtils.getBussinessLogger();
            bussinessLogger.error("用户" + userName + "修改笔记本异常|方法:updateNoteBook|参数： oldNoteBookName:" + oldNoteBookName + ";newNoteBookName:" + newNoteBookName + ";rowKey:" + rowKey, e);
            e.printStackTrace();
        }
        return modelAndView;
    }

    /**
     * 删除笔记本
     *
     * @param request
     * @param noteBookName
     * @param rowKey
     * @return
     */
    @RequestMapping("/note/deleteNoteBook")
    public ModelAndView deleteNoteBook(HttpServletRequest request, String noteBookName, String rowKey) {
        ModelAndView modelAndView = null;  //wx@163.com_312312|java基础|123213|0
        try {
            ModelMap map = new ModelMap();
            //首先判断笔记本中是否有笔记，如果有笔记那么提示先删除笔记再删除笔记本
            List<Note> noteListByNotebook = noteService.getNoteListByNotebook(rowKey);
            if (noteListByNotebook != null && noteListByNotebook.size() > 0) {
                map.put("success", false);
                map.put("message", "该笔记本中有笔记，请先删除笔记！");
                modelAndView = new ModelAndView(new MappingJackson2JsonView(), map);
                return modelAndView;
            } else {
                String[] split = rowKey.split("\\" + Constants.ROWKEY_SEPARATOR);
                boolean re = noteService.deleteNoteBook(noteBookName, split[0], split[1], 0);
                if (re) {
                    map.put("message", "删除成功！");
                } else {
                    map.put("message", "删除失败！");
                }
                map.put("success", re);
                modelAndView = new ModelAndView(new MappingJackson2JsonView(), map);
            }

        } catch (Exception e) {
            String userName = (String) request.getSession().getAttribute(Constants.USER_INFO);
            Logger bussinessLogger = LogUtils.getBussinessLogger();
            bussinessLogger.error("用户" + userName + "删除笔记本异常|方法:deleteNoteBook|参数： noteBookName:" + noteBookName + ";rowKey:" + rowKey, e);
            e.printStackTrace();
        }
        return modelAndView;
    }

}
