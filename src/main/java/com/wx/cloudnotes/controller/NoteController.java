package com.wx.cloudnotes.controller;

import com.wx.cloudnotes.common.Constants;
import com.wx.cloudnotes.domain.Note;
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

@Controller
public class NoteController {

    @Autowired
    private NoteService noteService;

    /**
     * 单机添加笔记
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
            // 创建时间戳
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
     * 保存笔记
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
        long createTime = System.currentTimeMillis();
        try {
            boolean isUpdateNote = noteService.updateNote(noteRowKey, noteName, createTime + "", content, 0 + "", oldNoteName, noteBookRowkey);
            ModelMap modelMap = new ModelMap();
            if (isUpdateNote) {
                //保存成功
                modelMap.put("success", true);

            } else {
                modelMap.put("success", false);
            }
            modelAndView = new ModelAndView(new MappingJackson2JsonView(), modelMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return modelAndView;
    }


}
