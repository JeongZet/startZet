package com.gebalstory.gcbus.main.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.gebalstory.gcbus.common.common.CommandMap;
import com.gebalstory.gcbus.main.service.BoardService;

@Controller
public class BoardController {
	
	@Resource(name="boardService")
	private BoardService boardService;
	
	//게시판을 위한 컨트롤러
	@RequestMapping(value="/openBoardList.do")
	public ModelAndView openBoardList(CommandMap commandMap) throws Exception{
		ModelAndView mv = new ModelAndView("board/boardList");
		mv.addObject("list",boardService.selectBoardList(commandMap.getMap()));
		return mv;
	}
	
	@RequestMapping(value="/openBoardWrite.do")
	public ModelAndView openBoardWrite(CommandMap commandMap) throws Exception{
		ModelAndView mv = new ModelAndView("board/boardWrite");
		
		return mv;
	}
	@RequestMapping(value="/writeBoard.do")
	public ModelAndView writeBoard(CommandMap commandMap,HttpServletRequest req) throws Exception{
		ModelAndView mv = new ModelAndView("redirect:/openBoardDetail.do");
		boardService.insertBoardInfo(commandMap.getMap(),req);
		mv.addObject("BOARD_IDX",commandMap.get("BOARD_IDX"));
		
		return mv;
	}
	
	@RequestMapping(value="/openBoardDetail.do")
	public ModelAndView openBoardDetail(CommandMap commandMap) throws Exception{
		ModelAndView mv = new ModelAndView("board/boardDetail");
		commandMap.getMap().put("type", "detail");
		mv.addObject("post", boardService.selectBoardInfo(commandMap.getMap()));
		mv.addObject("file", boardService.selectFileInfo(commandMap.getMap()));
		return mv;
	}
	
	@RequestMapping(value="/openBoardModify.do")
	public ModelAndView openBoardModify(CommandMap commandMap) throws Exception{
		ModelAndView mv = new ModelAndView("board/boardModify");
		commandMap.getMap().put("type", "modify");
		mv.addObject("post",boardService.selectBoardInfo(commandMap.getMap()));
		mv.addObject("file", boardService.selectFileInfo(commandMap.getMap()));
		return mv;
	}
	
	@RequestMapping(value="/modifyBoard.do")
	public ModelAndView modifyBoard(CommandMap commandMap, HttpServletRequest req) throws Exception{
		ModelAndView mv = new ModelAndView("redirect:/openBoardDetail.do");
		boardService.updateBoardInfo(commandMap.getMap(),req);
		mv.addObject("BOARD_IDX",commandMap.getMap().get("BOARD_IDX"));
		
		return mv;
	}
	
	@RequestMapping(value="/deleteBoard.do")
	public ModelAndView deleteBoard(CommandMap commandMap) throws Exception{
		ModelAndView mv = new ModelAndView("redirect:/openBoardList.do");
		boardService.deleteBoardInfo(commandMap.getMap());
		
		return mv;
	}
}
