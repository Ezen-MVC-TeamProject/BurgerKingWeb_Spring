package com.ezen.burger.controller;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartRequest;

import com.ezen.burger.dto.AdminVO;
import com.ezen.burger.dto.EventVO;
import com.ezen.burger.dto.MemberVO;
import com.ezen.burger.dto.Paging;
import com.ezen.burger.dto.QnaVO;
import com.ezen.burger.service.AdminService;
import com.ezen.burger.service.EventService;
import com.ezen.burger.service.MemberService;
import com.ezen.burger.service.QnaService;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

@Controller
public class AdminController {
	@Autowired
	AdminService as;

	@Autowired
	MemberService ms;
	
	@Autowired
	QnaService qs;
	
	@Autowired
	EventService es;
	
	@Autowired
	ServletContext context; 

	@RequestMapping(value = "/adminLogin", method = RequestMethod.POST)
	public String adminLogin(@ModelAttribute("dto") @Valid AdminVO adminvo, BindingResult result,
			HttpServletRequest request, Model model) {
		if (result.getFieldError("id") != null) {
			model.addAttribute("message", result.getFieldError("id").getDefaultMessage());
			return "admin/adminLogin";
		} else if (result.getFieldError("pwd") != null) {
			model.addAttribute("message", result.getFieldError("pwd").getDefaultMessage());
			return "admin/adminLogin";
		}

		AdminVO avo = as.adminCheck(adminvo.getId());

		if (avo == null) {
			model.addAttribute("message", "id가 없습니다.");
			return "admin/adminLogin";
		} else if (avo.getPwd() == null) {
			model.addAttribute("message", "관리자에게 문의하세요");
			return "admin/adminLogin";
		} else if (!avo.getPwd().equals(adminvo.getPwd())) {
			model.addAttribute("message", "비밀번호가 맞지 않습니다.");
			return "admin/adminLogin";
		} else if (avo.getPwd().equals(adminvo.getPwd())) {
			HttpSession session = request.getSession();
			session.setAttribute("loginAdmin", avo);
			return "admin/main";
		} else {
			model.addAttribute("message", "원인미상의 오류로 로그인 불가");
			return "admin/adminLogin";
		}
	}

	@RequestMapping("/adminLogout")
	public String logout(HttpServletRequest request) {
		HttpSession session = request.getSession();
		session.removeAttribute("loginAdmin");
		return "redirect:/admin";
	}

	@RequestMapping("adminMemberList")
	public String adminMemberList(HttpServletRequest request, Model model) {
		HttpSession session = request.getSession();
		if (session.getAttribute("loginAdmin") == null) {
			return "admin/adminLogin";
		} else {
			int page = 1;
			if (request.getParameter("page") != null) {
				page = Integer.parseInt(request.getParameter("page"));
				session.setAttribute("page", page);
			} else if (session.getAttribute("page") != null) {
				page = (int) session.getAttribute("page");
			} else {
				page = 1;
				session.removeAttribute("page");
			}

			String key = "";
			if (request.getParameter("key") != null) {
				key = request.getParameter("key");
				session.setAttribute("key", key);
			} else if (session.getAttribute("key") != null) {
				key = (String) session.getAttribute("key");
			} else {
				session.removeAttribute("key");
				key = "";
			}

			Paging paging = new Paging();
			paging.setPage(page);

			int count = as.getAllCount("member", "name", key);
			paging.setTotalCount(count);
			paging.paging();

			ArrayList<MemberVO> memberList = as.listMember(paging, key);

			model.addAttribute("memberList", memberList);
			model.addAttribute("paging", paging);
			model.addAttribute("key", key);
		}
		return "admin/member/memberList";
	}
	

	@RequestMapping(value="/adminMemberDelete", method=RequestMethod.POST)
	public String adminMemberDelete(@RequestParam("mseq") int [] mseqArr) {
		for(int mseq:mseqArr)
			as.adminMemberDelete(mseq);
		return "redirect:/adminMemberList";
	}
	
	
	
//event
	@RequestMapping("/adminEventList")
	public String adminEventList(HttpServletRequest request, Model model) {
		HttpSession session = request.getSession();
		if (session.getAttribute("loginAdmin") == null) {
			return "admin/adminLogin";
		} else {
			int page = 1;
			if (request.getParameter("page") != null) {
				page = Integer.parseInt(request.getParameter("page"));
				session.setAttribute("page", page);
			} else if (session.getAttribute("page") != null) {
				page = (int) session.getAttribute("page");
			} else {
				page = 1;
				session.removeAttribute("page");
			}

			String key = "";
			if (request.getParameter("key") != null) {
				key = request.getParameter("key");
				session.setAttribute("key", key);
			} else if (session.getAttribute("key") != null) {
				key = (String) session.getAttribute("key");
			} else {
				session.removeAttribute("key");
				key = "";
			}

			Paging paging = new Paging();
			paging.setPage(page);
			int count = as.getAllCount("event", "subject", key);
			paging.setTotalCount(count);
			paging.paging();

			ArrayList<EventVO> list = as.listEvent(paging, key);

			model.addAttribute("eventList", list);
			model.addAttribute("paging", paging);
			model.addAttribute("key", key);
			return "admin/event/eventList";
		}
	}
	
	@RequestMapping("/adminEventDetail")
	public String adminEventDetail(HttpServletRequest request, Model model, @RequestParam("eseq")int eseq) {
		HttpSession session = request.getSession();
		if (session.getAttribute("loginAdmin") == null) {
			return "admin/adminLogin";
		} else {
			EventVO evo =es.getEvent(eseq);
			model.addAttribute("eventVO", evo);			
			return "admin/event/eventDetail";
		}
   }
	@RequestMapping("/adminEventWriteForm")
	public String adminEventWriteForm(HttpServletRequest request, Model model) {
		HttpSession session = request.getSession();
		if (session.getAttribute("loginAdmin") == null) return "admin/adminLogin";
	
			return "admin/event/eventWrite";
	}
	
	@RequestMapping(value="/adminEventWrite", method=RequestMethod.POST)
	public String adminEventWrite(@ModelAttribute("dto") @Valid EventVO eventvo,
			BindingResult result, Model model, HttpServletRequest request) {
			
		System.out.println("subject : " + eventvo.getSubject() );
		System.out.println("content : " + eventvo.getContent() );
		System.out.println("enddate : " + eventvo.getEnddate() );
		System.out.println("image : " + eventvo.getImage() );
		
		if( result.getFieldError("subject")!=null) {
			return "evnet/evnetWriteForm";
		}else  if(result.getFieldError("content")!=null) {
			return "evnet/evnetWriteForm";
		}else if(result.getFieldError("enddate")!=null) {
			return "evnet/evnetWriteForm";
		}else if(result.getFieldError("image")!=null) {
			return "evnet/evnetWriteForm";
		}else {
			es.insertEvent(eventvo);
			return "redirect:/adminEventList";
		}
		}
		

	@RequestMapping(value = "/adminEventDelete")
	public String adminEventDelete(@RequestParam("delete") int[] eseqArr,HttpServletRequest request ) {
		HttpSession session = request.getSession();
		if (session.getAttribute("loginAdmin") == null) {
			return "admin/adminLogin";
		} else {
		for (int eseq : eseqArr)
			es.deleteEvent(eseq);
		return "redirect:/adminEventList";
      }
	}
	
	@RequestMapping(value="/adminEventUpdateForm")
	public String adminEventUpdateForm(@RequestParam("eseq") int eseq,
			HttpServletRequest request, Model model) {
		
		HttpSession session= request.getSession();
		if( session.getAttribute("loginAdmin") == null) {
			return "admin/adminLogin";
		}else {
			EventVO evo = es.getEvent(eseq);
			model.addAttribute("eventVO", evo);
			
			return "admin/event/evnetUpdate";
		}
	}
	/*
	 * @RequestMapping(value="/adminEventUpdate" , method=RequestMethod.POST) public
	 * String adminEventUpdate(@ModelAttribute("EventVO") @Valid EventVO evo,
	 * BindingResult result, HttpServletRequest request, Model model) {
	 * 
	 * }
	 */
	

	@RequestMapping(value="/adminMemberUpdateForm")
	public String adminMemberUpdateForm(@RequestParam("mseq") int mseq,
			HttpServletRequest request, Model model) {
		HttpSession session= request.getSession();
		if( session.getAttribute("loginAdmin") == null) {
			return "admin/adminLogin";
		}else {
			MemberVO mvo = ms.getMember_mseq(mseq);
			model.addAttribute("memberVO", mvo);
			
			return "admin/member/memberUpdate";
		}
	}
	
	@RequestMapping(value="/adminMemberUpdate", method=RequestMethod.POST)
	public String adminMemberUpdateForm(@ModelAttribute("memberVO") @Valid MemberVO mvo,
			BindingResult result, HttpServletRequest request, Model model,
			@RequestParam(value="pwd_chk", required=false) String pwd_chk) {
		if(result.getFieldError("pwd")!=null) {
			model.addAttribute("message", "암호를 입력하세요");
			return "admin/member/memberUpdate";
		}else if(result.getFieldError("name")!=null) {
			model.addAttribute("message", "이름을 입력하세요");
			return "admin/member/memberUpdate";
		}else if(pwd_chk == null || (pwd_chk!=null && !pwd_chk.equals(mvo.getPwd()))) {
			model.addAttribute("message", "비밀번호 확인이 일치하지 않습니다.");
			return "admin/member/memberUpdate";
		}if(result.getFieldError("phone")!=null) {
			model.addAttribute("message", "전화번호를 입력하세요");
			return "admin/member/memberUpdate";
		}else {
			ms.updateMember(mvo);
			return "redirect:/adminMemberList";
		}
	}
	
	@RequestMapping(value="/adminQnaList")
	public String adminQnaList(HttpServletRequest request, Model model) {
		HttpSession session= request.getSession();
		if( session.getAttribute("loginAdmin") == null) {
			return "admin/adminLogin";
		}
		else {						
			int page = 1;
			if(request.getParameter("page") != null){ 
				page = Integer.parseInt(request.getParameter("page")); 
				session.setAttribute("page", page); 
			}else if(session.getAttribute("page") != null) { 
				page = (int)session.getAttribute("page"); 
			}else { 
				page = 1;
				session.removeAttribute("page"); 
			}
			
			String key = "";
			if(request.getParameter("key") != null) { 
				key = request.getParameter("key"); session.setAttribute("key", key); 
			}else if(session.getAttribute("key") != null) {
				key = (String)session.getAttribute("key");
			}else { 
				session.removeAttribute("key");
				key = "";
			}
			
			
			Paging paging = new Paging();
			paging.setPage(page);
			
			int count = as.getAllCount("qna", "id", key);
			paging.setTotalCount(count);
			paging.paging();
			
			ArrayList<QnaVO> qnaList = as.listQna(paging, key);
			
			model.addAttribute("qnaList", qnaList);
			model.addAttribute("paging",paging);
			model.addAttribute("key",key);
		}
		return "admin/qna/qnaList";
	}
	
	@RequestMapping(value="/adminQnaDelete", method=RequestMethod.POST)
	public String adminQnaDelete(@RequestParam("delete") int [] qseqArr) {
		for(int qseq:qseqArr)
			as.adminQnaDelete(qseq);
		return "redirect:/adminQnaList";
	}
	
	@RequestMapping("/adminQnaDetail")
	public String adminQnaDetail(@RequestParam("qseq") int qseq, HttpServletRequest request, Model model) {
		HttpSession session= request.getSession();
		if( session.getAttribute("loginAdmin") == null) {
			return "admin/adminLogin";
		}else {
			QnaVO qvo = qs.getQna(qseq);
			model.addAttribute("qnaVO",qvo);
			return "admin/qna/qnaDetail";
		}
		
	}
	
	@RequestMapping("/adminQnaRepsave")
	public String adminQnaRepsave(HttpServletRequest request, Model model,
			@RequestParam("qseq") int qseq,  @RequestParam("reply") String reply) {
		HttpSession session= request.getSession();
		if( session.getAttribute("loginAdmin") == null) {
			return "admin/adminLogin";
		}else {
			qs.updateQna(qseq, reply);
			return "redirect:/adminQnaDetail?qseq="+qseq;
		}		
		
	}
	
}
