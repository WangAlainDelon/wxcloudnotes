package com.wx.cloudnotes.controller;

import com.wx.cloudnotes.common.Constants;
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

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class NoteController {

    @Autowired
    private NoteService noteService;

    /**
     * 单击添加笔记
     *
     * @param request
     * @param noteName
     * @param noteBookRowkey
     * @return
     */
    @RequestMapping("/note/addNote")
    public ModelAndView addNote(HttpServletRequest request, String noteName, String noteBookRowkey) {
        ModelAndView modelAndView = null;
        //笔记本的rowKey ： loginName_timestamp
        String userName = (String) request.getSession().getAttribute(Constants.USER_INFO);
        try {
            // 创建时间戳，
            Long createTime = System.currentTimeMillis();

            StringBuffer noteRowKey = new StringBuffer();
            noteRowKey.append(userName.trim() + Constants.ROWKEY_SEPARATOR).append(String.valueOf(createTime).trim());
            boolean isAddNote = noteService.addNote(noteRowKey.toString(), noteName, createTime.toString(), 0 + "", noteBookRowkey);
            ModelMap map = new ModelMap();
            if (isAddNote) {
                map.put("resource", userName + Constants.ROWKEY_SEPARATOR + createTime);
                map.put("success", true);
            } else {
                map.put("success", false);
            }
            modelAndView = new ModelAndView(new MappingJackson2JsonView(), map);
        } catch (Exception e) {
            Logger bussinessLogger = LogUtils.getBussinessLogger();
            bussinessLogger.error("用户" + userName + "添加笔记本的笔记异常|addNote|参数:noteName:" + noteName + " noteBookRowkey:" + noteBookRowkey, e);
            e.printStackTrace();
        }
        return modelAndView;
    }

    /**
     * 单击笔记查询笔记详情
     *
     * @param request
     * @param noteRowkey
     * @return
     */
    @RequestMapping("/note/getNote")
    public ModelAndView getNote(HttpServletRequest request, String noteRowkey) {
        ModelAndView modelAndView = null;
        String userName = (String) request.getSession().getAttribute(Constants.USER_INFO);
        try {
            Note note = noteService.getNoteByRowKey(noteRowkey);
            ModelMap modelMap = new ModelMap();
            if (note != null) {
                modelMap.put("success", true);
                modelMap.put("note", note);
            } else {
                modelMap.put("success", false);
                modelMap.put("message", "该笔为空！");
            }
            modelAndView = new ModelAndView(new MappingJackson2JsonView(), modelMap);
        } catch (Exception e) {
            Logger bussinessLogger = LogUtils.getBussinessLogger();
            bussinessLogger.error("用户" + userName + "查询笔记详情异常|getNote|参数:noteRowkey:" + noteRowkey, e);
            e.printStackTrace();
        }
        return modelAndView;
    }

    /**
     * 保存修改笔记
     *
     * @param request
     * @param noteName
     * @param oldNoteName
     * @param noteRowKey
     * @param content
     * @param noteBookRowkey
     * @return
     */
    @RequestMapping("/note/updateNote")
    public ModelAndView updateNote(HttpServletRequest request, String noteName, String oldNoteName, String noteRowKey, String content, String noteBookRowkey) {
        ModelAndView modelAndView = null;
        String userName = (String) request.getSession().getAttribute(Constants.USER_INFO);
        //保存笔记的时候 这个时间戳不能重新创建，因为重新创建会产生新的数据
        //long createTime = System.currentTimeMillis();
        String[] split = noteRowKey.split("\\" + Constants.ROWKEY_SEPARATOR);
        try {
            boolean isUpdateNote = noteService.updateNote(noteRowKey, noteName, split[1] + "", content, 0 + "", oldNoteName, noteBookRowkey);
            ModelMap modelMap = new ModelMap();
            if (isUpdateNote) {
                //保存成功
                modelMap.put("success", true);

            } else {
                modelMap.put("success", false);
            }
            modelAndView = new ModelAndView(new MappingJackson2JsonView(), modelMap);
        } catch (Exception e) {
            Logger bussinessLogger = LogUtils.getBussinessLogger();
            bussinessLogger.error("用户" + userName + "保存修改笔记异常|方法：updateNote|参数：noteName:" + noteName + ";oldNoteName:" + oldNoteName + ";noteRowKey:" + noteRowKey + ";content:" + content + ":noteBookRowkey:" + noteBookRowkey, e);
            e.printStackTrace();
        }
        return modelAndView;
    }

    /**
     * 移动并点击删除笔记
     *
     * @param request
     * @param noteRowKey
     * @param oldNoteBookRowkey
     * @param newNoteBookRowkey
     * @param noteName
     * @return
     */
    @RequestMapping("/note/moveAndDeleteNote")
    public ModelAndView moveAndDeleteNote(HttpServletRequest request, String noteRowKey, String oldNoteBookRowkey, String newNoteBookRowkey, String noteName) {
        ModelAndView modelAndView = null;
        try {
            boolean isMove = noteService.moveAndDeleteNote(noteRowKey, oldNoteBookRowkey, newNoteBookRowkey, noteName);
            ModelMap modelMap = new ModelMap();
            if (isMove) {
                modelMap.put("success", isMove);
            } else {
                modelMap.put("success", false);
            }
            modelAndView = new ModelAndView(new MappingJackson2JsonView(), modelMap);
        } catch (Exception e) {
            String userName = (String) request.getSession().getAttribute(Constants.USER_INFO);
            Logger bussinessLogger = LogUtils.getBussinessLogger();
            bussinessLogger.error("用户" + userName + "移动并删除笔记异常|方法：moveAndDeleteNote|参数：noteRowKey:" + noteRowKey + ";oldNoteBookRowkey:" + oldNoteBookRowkey + ";newNoteBookRowkey:" + newNoteBookRowkey, e);
            e.printStackTrace();
        }
        return modelAndView;
    }

    /**
     * 移动笔记
     *
     * @param userName
     * @return
     */
    @RequestMapping("/note/getAllNoteBookByUserName")
    public ModelAndView getAllNoteBookByUserName(HttpServletRequest request, String userName) {
        ModelAndView modelAndView = null;
        try {
            request.getSession().setAttribute(Constants.USER_INFO, userName);
            List<NoteBook> allNoteBook = noteService.getAllNoteBook(userName);// 查询所有笔记本
            ModelMap map = new ModelMap();
            map.put("allNoteBook", allNoteBook);
            map.put("recycleBtRowKey", userName + Constants.RECYCLE);
            map.put("starBtRowKey", userName + Constants.STAR);
            map.put("activityBtRowKey", userName + Constants.ACTIVITY);
            modelAndView = new ModelAndView(new MappingJackson2JsonView(), map);
        } catch (Exception e) {
            Logger bussinessLogger = LogUtils.getBussinessLogger();
            bussinessLogger.error("用户" + userName + "获取所有笔记本异常|方法:getAllNoteBookByUserName|参数： userName:" + userName, e);
            e.printStackTrace();
        }
        return modelAndView;
    }


    //彻底删除笔记

}
