package com.ezen.burger.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import org.springframework.web.servlet.ModelAndView;

import com.ezen.burger.dto.AdminVO;
import com.ezen.burger.dto.EventVO;
import com.ezen.burger.dto.MemberVO;
import com.ezen.burger.dto.Paging;
import com.ezen.burger.dto.ProductVO;
import com.ezen.burger.dto.QnaVO;
import com.ezen.burger.dto.orderVO;
import com.ezen.burger.dto.subproductOrderVO;
import com.ezen.burger.service.AdminService;
import com.ezen.burger.service.EventService;
import com.ezen.burger.service.MemberService;
import com.ezen.burger.service.OrderService;
import com.ezen.burger.service.ProductService;
import com.ezen.burger.service.QnaService;
import com.oreilly.servlet.MultipartRequest;
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
	ProductService ps;
	
	@Autowired
	OrderService os;

	@Autowired
	ServletContext context;

	@RequestMapping(value = "/adminLogin")
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
			model.addAttribute("message", "id??? ????????????.");
			return "admin/adminLogin";
		} else if (avo.getPwd() == null) {
			model.addAttribute("message", "??????????????? ???????????????");
			return "admin/adminLogin";
		} else if (!avo.getPwd().equals(adminvo.getPwd())) {
			model.addAttribute("message", "??????????????? ?????? ????????????.");
			return "admin/adminLogin";
		} else if (avo.getPwd().equals(adminvo.getPwd())) {
			HttpSession session = request.getSession();
			session.setAttribute("loginAdmin", avo);
			return "admin/main";
		} else {
			model.addAttribute("message", "??????????????? ????????? ????????? ??????");
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
	public String adminMemberList(HttpServletRequest request, Model model,
			@RequestParam(value="message", required = false) String message) {
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

			if(message !=null) {
				model.addAttribute("message", message);
			}
			model.addAttribute("memberList", memberList);
			model.addAttribute("paging", paging);
			model.addAttribute("key", key);
		}
		return "admin/member/memberList";
	}

	@RequestMapping(value = "/adminMemberDelete", method = RequestMethod.POST)
	public ModelAndView adminMemberDelete(HttpServletRequest request,
			@RequestParam("delete") int[] mseqArr) {
		ModelAndView mav = new ModelAndView();
		HttpSession session = request.getSession();
		MemberVO mvo = (MemberVO)session.getAttribute("loginUser");
		
		// ?????? ???????????? result??? 2,3??? ????????? ????????? ???
		ArrayList<orderVO> list = os.getOrderListResult2(mvo.getId());
		
		// ?????? ???????????? 1????????? ????????? ?????? ???????????? ????????? ?????? ?????? ?????? ????????????
		// ????????? ?????? ?????? ???????????? ????????? ????????????.
		if(list.size() > 0) {
			mav.addObject("message", "???????????? ????????? ????????? ??????????????? ??????????????????.");
			mav.setViewName("redirect:/adminMemberList?page=1&key=");
			return mav;
		}
		
		for (int mseq : mseqArr)
			as.deleteMember(mseq);
		
		mav.setViewName("redirect:/adminMemberList");
		return mav;
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
//?????????????????????
	@RequestMapping("/adminEventDetail")
	public String adminEventDetail(HttpServletRequest request, Model model, @RequestParam("eseq") int eseq) {
		HttpSession session = request.getSession();
		if (session.getAttribute("loginAdmin") == null) {
			return "admin/adminLogin";
		} else {
			EventVO evo = es.getEvent(eseq);
			model.addAttribute("eventVO", evo);
			return "admin/event/eventDetail";
		}
	}

	@RequestMapping("/adminEventWriteForm")
	public String adminEventWriteForm(HttpServletRequest request, Model model) {
		HttpSession session = request.getSession();
		if (session.getAttribute("loginAdmin") == null)
			return "admin/adminLogin";

		return "admin/event/eventWrite";
	}
//???????????????
	@RequestMapping(value = "/adminEventWrite", method = RequestMethod.POST) 
	public String adminEventWrite(Model model, HttpServletRequest request) {
		String savePath = context.getRealPath("image/main/event");
		System.out.println(savePath);

		try {
			MultipartRequest multi = new MultipartRequest(request, savePath, 5 * 1024 * 1024, "UTF-8",
					new DefaultFileRenamePolicy());
			EventVO evo = new EventVO();
			evo.setSubject(multi.getParameter("subject"));
			evo.setContent(multi.getParameter("content"));
			evo.setEnddate(multi.getParameter("enddate"));
			evo.setImage(multi.getFilesystemName("image"));
			evo.setThumbnail(multi.getFilesystemName("thumbnail"));
			if (multi.getParameter("subject") == null) {
				System.out.println("??????????????? ???????????????");
				model.addAttribute("evo", evo);
				return "admin/event/eventWrite.jsp";
			}
			as.insertEvent(evo);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "redirect:/adminEventList";
	}
//???????????????
	@RequestMapping(value = "/adminEventDelete")
	public String adminEventDelete(@RequestParam("delete") int[] eseqArr, HttpServletRequest request) {
		HttpSession session = request.getSession();
		if (session.getAttribute("loginAdmin") == null) {
			return "admin/adminLogin";
		} else {
			for (int eseq : eseqArr)
				es.deleteEvent(eseq);
			return "redirect:/adminEventList";
		}
	}

	@RequestMapping(value = "/adminEventUpdateForm")
	public String adminEventUpdateForm(HttpServletRequest request, Model model, @RequestParam("eseq")int eseq) {
		HttpSession session = request.getSession();
		if (session.getAttribute("loginAdmin") == null) {
			return "admin/adminLogin";
		} else {
			EventVO evo = es.getEvent(eseq);
			model.addAttribute("eventVO", evo);

			return "admin/event/eventUpdate";
		}
	}
	//???????????????
	@RequestMapping(value="/adminEventUpdate" , method=RequestMethod.POST) 
	  public String adminEventUpdate( Model model, HttpServletRequest request) { 
	  String savePath=context.getRealPath("image/main/event");
		System.out.println(savePath);
		try {
			MultipartRequest multi=new MultipartRequest(request, savePath,
					5*1024*1024, "UTF-8", new DefaultFileRenamePolicy());
			EventVO evo=new EventVO();
			
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
	        SimpleDateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss");

			String enddate = multi.getParameter("enddate");
			int state = 1;
			// ??????????????? ????????? ?????? ????????? ???????????? ??????????????? ????????? ?????????????????? ?????????
			// state??? 0?????? ???????????? ????????? ???????????? ??????
	        if(sdf.format(timestamp).compareTo(enddate) > 0) {
	        	state = 0;
	        }
	        evo.setState(state);
			enddate = enddate.substring(0, 10);
			evo.setEseq(Integer.parseInt(multi.getParameter("eseq")));
			evo.setSubject(multi.getParameter("subject"));
			evo.setContent(multi.getParameter("content"));
		    evo.setEnddate(enddate);
			
		    evo.setImage(multi.getFilesystemName("image"));		
			if(multi.getFilesystemName("image") == null)
				evo.setImage(multi.getParameter("oldImage"));
			else
				evo.setImage(multi.getFilesystemName("image"));
			
			evo.setThumbnail(multi.getFilesystemName("thumbnail"));
			if(multi.getFilesystemName("thumbnail") == null)
				evo.setThumbnail(multi.getParameter("oldthumbnail"));
			else
				evo.setThumbnail(multi.getFilesystemName("thumbnail"));
			as.updateEvent(evo);
		} catch (IOException e) {		e.printStackTrace();	}
		return "redirect:/adminEventList";
	  }
	
	@RequestMapping(value = "/adminMemberUpdateForm")
	public String adminMemberUpdateForm(@RequestParam("mseq") int mseq, HttpServletRequest request, Model model) {
		HttpSession session = request.getSession();
		if (session.getAttribute("loginAdmin") == null) {
			return "admin/adminLogin";
		} else {
			MemberVO mvo = ms.getMember_mseq(mseq);
			model.addAttribute("memberVO", mvo);

			return "admin/member/memberUpdate";
		}
	}

	@RequestMapping(value = "/adminMemberUpdate", method = RequestMethod.POST)
	public String adminMemberUpdateForm(@ModelAttribute("memberVO") @Valid MemberVO mvo, BindingResult result,
			HttpServletRequest request, Model model,
			@RequestParam(value = "pwd_chk", required = false) String pwd_chk) {
		if (result.getFieldError("pwd") != null) {
			model.addAttribute("message", "????????? ???????????????");
			return "admin/member/memberUpdate";
		} else if (result.getFieldError("name") != null) {
			model.addAttribute("message", "????????? ???????????????");
			return "admin/member/memberUpdate";
		} else if (pwd_chk == null || (pwd_chk != null && !pwd_chk.equals(mvo.getPwd()))) {
			model.addAttribute("message", "???????????? ????????? ???????????? ????????????.");
			return "admin/member/memberUpdate";
		}
		if (result.getFieldError("phone") != null) {
			model.addAttribute("message", "??????????????? ???????????????");
			return "admin/member/memberUpdate";
		} else {
			ms.updateMember(mvo);
			return "redirect:/adminMemberList";
		}
	}
//qna
	@RequestMapping(value = "/adminQnaList")
	public String adminQnaList(HttpServletRequest request, Model model) {
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

			int count = as.getAllCount("qna", "id", key);
			paging.setTotalCount(count);
			paging.paging();

			ArrayList<QnaVO> qnaList = as.listQna(paging, key);

			model.addAttribute("qnaList", qnaList);
			model.addAttribute("paging", paging);
			model.addAttribute("key", key);
		}
		return "admin/qna/qnaList";
	}

	@RequestMapping(value = "/adminQnaDelete", method = RequestMethod.POST)
	public String adminQnaDelete(@RequestParam("delete") int[] qseqArr) {
		for (int qseq : qseqArr)
			as.deleteQna(qseq);
		return "redirect:/adminQnaList";
	}

	@RequestMapping("/adminQnaDetail")
	public String adminQnaDetail(@RequestParam("qseq") int qseq, HttpServletRequest request, Model model) {
		HttpSession session = request.getSession();
		if (session.getAttribute("loginAdmin") == null) {
			return "admin/adminLogin";
		} else {
			QnaVO qvo = qs.getQna(qseq);
			model.addAttribute("qnaVO", qvo);
			return "admin/qna/qnaDetail";
		}

	}
// QnA ????????????
	@RequestMapping("/adminQnaRepsave")
	public String adminQnaRepsave(HttpServletRequest request, Model model, @RequestParam("qseq") int qseq,
			@RequestParam("reply") String reply) {
		HttpSession session = request.getSession();
		if (session.getAttribute("loginAdmin") == null) {
			return "admin/adminLogin";
		} else {
			qs.updateQna(qseq, reply);
			return "redirect:/adminQnaDetail?qseq=" + qseq;
		}

	}
// shortproduct??? ???????????? ?????? ??????
	@RequestMapping("adminShortProductList")
	public String adminShortProductList(HttpServletRequest request, Model model) {
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

			int count = as.getShortProductAllCount(key);
			paging.setTotalCount(count);
			paging.paging();

			ArrayList<ProductVO> shortproductList = as.listShortProduct(paging, key);

			model.addAttribute("shortproductList", shortproductList);
			model.addAttribute("paging", paging);
			model.addAttribute("key", key);
		}
		return "admin/product/shortproductList";
	}

	@RequestMapping("adminProductList")
	public String adminProductList(HttpServletRequest request, Model model) {
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

			int count = as.getProductAllCount(key);
			paging.setTotalCount(count);
			paging.paging();

			ArrayList<ProductVO> productList = as.listProduct(paging, key);

			model.addAttribute("productList", productList);
			model.addAttribute("paging", paging);
			model.addAttribute("key", key);
		}
		return "admin/product/productList";
	}

	@RequestMapping(value = "/adminProductDelete", method = RequestMethod.POST)
	public String adminProductDelete(@RequestParam("delete") int[] pseqArr) {
		for (int pseq : pseqArr)
			as.deleteProduct(pseq);
		return "redirect:/adminShortProductList";
	}

	@RequestMapping("/adminProductWriteForm")
	public String adminProductWriteForm(HttpServletRequest request, Model model) {
		HttpSession session = request.getSession();
		if (session.getAttribute("loginAdmin") == null) {
			return "admin/adminLogin";
		} else {
			String kindList[] = { "?????????&?????????", "????????????", "??????", "?????????&??????", "????????????&????????????", "?????????", "??????&?????????", "??????" };
			model.addAttribute("kindList", kindList);
			return "admin/product/productWrite";
		}
	}

	@RequestMapping("/adminShortProductWriteForm")
	public String adminShortProductWriteForm(HttpServletRequest request, Model model) {
		HttpSession session = request.getSession();
		if (session.getAttribute("loginAdmin") == null) {
			return "admin/adminLogin";
		} else {
			String kindList[] = { "?????????&?????????", "????????????", "??????", "?????????&??????", "????????????&????????????", "?????????", "??????&?????????", "??????" };
			model.addAttribute("kindList", kindList);
			return "admin/product/shortproductWrite";
		}
	}

	@RequestMapping(value = "adminProductWrite", method = RequestMethod.POST)
	public String adminProductWrite(Model model, HttpServletRequest request,
			HttpServletResponse response) {
		String savePath = context.getRealPath("/image/menu/product");
		System.out.println(savePath);

		try {
			MultipartRequest multi = new MultipartRequest(request, savePath, 5 * 1024 * 1024, "UTF-8",
					new DefaultFileRenamePolicy());
			
			String k1 = multi.getParameter("kind1");
			String k2 = multi.getParameter("kind2");
			String k3 = multi.getParameter("kind3");
			
			int result = as.checkShortProductYN(k1, k2, k3);
			
			if(result == 2) {
				response.setContentType("text/html; charset=UTF-8");
				PrintWriter writer = response.getWriter();
				writer.println("<script>alert('???????????? ???????????? ?????? ????????????.'); location.href='adminProductWriteForm';</script>");
				writer.close();
			}else if(result == 3) {
				response.setContentType("text/html; charset=UTF-8");
				PrintWriter writer = response.getWriter();
				writer.println("<script>alert('???????????? ??????????????? ?????? ???????????? ????????????.'); location.href='adminProductWriteForm';</script>");
				writer.close();
			}else if(result == 4) {
				response.setContentType("text/html; charset=UTF-8");
				PrintWriter writer = response.getWriter();
				writer.println("<script>alert('????????? ??? ?????? ?????? ????????????.'); location.href='adminProductWriteForm';</script>");
				writer.close();
			}else {
				ProductVO pvo = new ProductVO();
				
				pvo.setKind1(multi.getParameter("kind1"));
				pvo.setKind2(multi.getParameter("kind2"));
				pvo.setKind3(multi.getParameter("kind3"));
						
			    pvo.setPname(multi.getParameter("pname"));
			    pvo.setPrice1(Integer.parseInt(multi.getParameter("price1")));
			    pvo.setPrice2(Integer.parseInt("0"));
			    pvo.setPrice3(Integer.parseInt("0"));
			    pvo.setContent(multi.getParameter("content"));
			    pvo.setImage(multi.getFilesystemName("image"));
			    pvo.setUseyn(multi.getParameter("useyn"));
			    
			    as.insertProduct(pvo);
			}
			
		} catch (IOException e) {e.printStackTrace();	}
		return "redirect:/adminProductList";
	}
	@RequestMapping(value = "adminShortProductWrite", method = RequestMethod.POST)
	public String adminShortProductWrite(Model model, HttpServletRequest request,
			HttpServletResponse response) {
		String savePath = context.getRealPath("/image/menu/product");
		System.out.println(savePath);

		try {
			MultipartRequest multi = new MultipartRequest(request, savePath, 5 * 1024 * 1024, "UTF-8",
					new DefaultFileRenamePolicy());
			
			String k1 = multi.getParameter("kind1");
			String k2 = multi.getParameter("kind2");
			
			int result = as.checkShortProductYN2(k1, k2);
			
			if(result == 2) {
				response.setContentType("text/html; charset=UTF-8");
				PrintWriter writer = response.getWriter();
				writer.println("<script>alert('???????????? ???????????? ?????? ?????? ????????????.'); location.href='adminShortProductWriteForm';</script>");
				writer.close();
			}else {
				ProductVO pvo = new ProductVO();
				pvo.setKind1(multi.getParameter("kind1"));
				pvo.setKind2(multi.getParameter("kind2"));
				pvo.setKind3("4");
			    pvo.setPname(multi.getParameter("pname"));
			    pvo.setPrice1(Integer.parseInt(multi.getParameter("price1")));
			    pvo.setPrice2(0);
			    pvo.setPrice3(0);
			    pvo.setContent("");
			    pvo.setImage(multi.getFilesystemName("image"));
			    pvo.setUseyn(multi.getParameter("useyn"));
			    
			    as.insertProduct(pvo);
			}
			
		} catch (IOException e) {e.printStackTrace();	}
		return "redirect:/adminShortProductList";
	}
	@RequestMapping("adminProductDetail")
	public String productDetail(@RequestParam("pseq") int pseq, HttpServletRequest request, Model model) {
		HttpSession session = request.getSession();
		if (session.getAttribute("loginAdmin") == null) {
			return "admin/adminLogin";
		} else {
			ProductVO pvo = as.productDetail(pseq);

			// ???????????? ??? ???????????? ????????? ?????? 
			String kindList1[] = {"0", "?????????&?????????", "????????????", "??????", "?????????&??????", "????????????&????????????", "?????????", "??????&?????????", "??????"};
			int index = Integer.parseInt(pvo.getKind1());
			String kindList3[] = {"0", "Single", "Set", "LargeSet", "Menu list"};
			int index2 = Integer.parseInt(pvo.getKind3());
			// ????????? kind ????????? ???????????? ?????? ????????? ?????? & ??????????????? ?????? 
			request.setAttribute("kind1", kindList1[index]);
			request.setAttribute("kind3", kindList3[index2]);
			request.setAttribute("productVO", pvo); 
			return "admin/product/productDetail";
		}
	}
	
	@RequestMapping("adminShortProductDetail")
	public String shortProductDetail(@RequestParam("pseq") int pseq, HttpServletRequest request, Model model) {
		HttpSession session = request.getSession();
		if (session.getAttribute("loginAdmin") == null) {
			return "admin/adminLogin";
		} else {
			ProductVO pvo = as.productDetail(pseq);

			// ???????????? ??? ???????????? ????????? ?????? 
			String kindList1[] = {"0", "?????????&?????????", "????????????", "??????", "?????????&??????", "????????????&????????????", "?????????", "??????&?????????", "??????"};
			int index = Integer.parseInt(pvo.getKind1());
			String kindList3[] = {"0", "Single", "Set", "LargeSet", "Menu list"};
			int index2 = Integer.parseInt(pvo.getKind3());
			String useynList[] = {"0", "??????", "?????????"};
			int index3 = Integer.parseInt(pvo.getUseyn());
			// ????????? kind ????????? ???????????? ?????? ????????? ?????? & ??????????????? ?????? 
			request.setAttribute("kind1", kindList1[index]);
			request.setAttribute("kind3", kindList3[index2]);
			request.setAttribute("useyn", useynList[index3]);
			request.setAttribute("productVO", pvo); 
			request.setAttribute("k1", pvo.getKind1());
			model.addAttribute("pseq", pseq);
			return "admin/product/shortproductDetail";
		}
	}
	
	@RequestMapping("adminProductUpdateForm")
	public String adminProductUpdateForm(@RequestParam("pseq") int pseq, HttpServletRequest request, Model model) {
		ProductVO pvo = as.productDetail(pseq);
		model.addAttribute("productVO",pvo);
		String kindList1[] = {"?????????&?????????", "????????????", "??????", "?????????&??????", "????????????&????????????", "?????????", "??????&?????????", "??????"};
		String kindList3[] = {"Single", "Set", "LargeSet"};
		
		request.setAttribute("kindList1", kindList1);
		request.setAttribute("kindList3", kindList3);
		int index = Integer.parseInt(pvo.getKind1());
		int index2 = Integer.parseInt(pvo.getKind3());
		request.setAttribute("kind", kindList1[index-1]);
		request.setAttribute("kind3", kindList3[index2-1]);
		return "admin/product/productUpdate";
	}
	
	@RequestMapping(value="adminShortProductUpdateForm", method = RequestMethod.POST)
	public String adminShortProductUpdateForm(@RequestParam("pseq") int pseq,
			HttpServletRequest request, Model model) {
		ProductVO pvo = as.productDetail(pseq);
		model.addAttribute("productVO",pvo);
		String k1 = request.getParameter("k1");
		String kindList1[] = {"?????????&?????????", "????????????", "??????", "?????????&??????", "????????????&????????????", "?????????", "??????&?????????", "??????"};
		int index = Integer.parseInt(pvo.getKind1());
		request.setAttribute("kindList1", kindList1);
		request.setAttribute("kind", kindList1[index-1]);
		request.setAttribute("k1", k1);
		return "admin/product/shortproductUpdate";
	}
	
	@RequestMapping("/selectimg")
	public String selectimg(HttpServletRequest request) {
		String k1 = request.getParameter("k1");
		request.setAttribute("k1", k1);
		return "admin/product/selectimg";
	}
	
	@RequestMapping(value="/fileupload", method = RequestMethod.POST)
	public String fileupload(Model model, HttpServletRequest request, @ModelAttribute("ProductVO")
		ProductVO p ,@RequestParam("k1") String k1) {
		String path = context.getRealPath("/image/menu/product");
		
		try {
			MultipartRequest multi = new MultipartRequest(
					request, path, 5*1024*1024, "UTF-8", new DefaultFileRenamePolicy()
			);
			
			// ????????? ????????? ????????? ??????, ?????? ????????? ????????? ???????????????.
			model.addAttribute("image", multi.getFilesystemName("image"));
			model.addAttribute("originalFilename", multi.getFilesystemName("image"));
		} catch (IOException e) {e.printStackTrace();
		}
		model.addAttribute("k1", k1);
		return "admin/product/completupload";
	}
	
	@RequestMapping(value="/adminShortProductUpdate", method = RequestMethod.POST)
	public String adminShortProductUpdate(HttpServletRequest request, @ModelAttribute("ProductVO")
			ProductVO p, Model model, @RequestParam("k1") String k1) {				
		ProductVO pvo = new ProductVO();
		int pseq=0;
		String savePath = context.getRealPath("/image/menu/product");
		MultipartRequest multi;
		try {
			multi = new MultipartRequest(
					request, savePath , 5*1024*1024,  "UTF-8", new DefaultFileRenamePolicy() );
			pvo.setPseq(Integer.parseInt(multi.getParameter("pseq")));
			pseq=Integer.parseInt(multi.getParameter("pseq"));
			pvo.setKind1(multi.getParameter("kind1"));
			pvo.setKind2(multi.getParameter("kind2"));
			pvo.setKind3(multi.getParameter("kind3"));
			pvo.setPname(multi.getParameter("pname"));
			pvo.setPrice1(0);
			pvo.setPrice2(0);
			pvo.setPrice3(0);
			pvo.setContent("");
			System.out.println(multi.getParameter("useyn") + "//////");
			if(multi.getParameter("useyn") == null) {
				pvo.setUseyn("2");
			}else {
				pvo.setUseyn("1");
			}
			if(multi.getFilesystemName("image") == null)
				pvo.setImage(multi.getParameter("oldImage"));
			else
				pvo.setImage(multi.getFilesystemName("image"));
		} catch (IOException e) {e.printStackTrace();}
		as.updateProduct(pvo);
		return "redirect:/adminShortProductDetail?pseq="+pseq;
	}	
	
	@RequestMapping(value="/adminProductUpdate", method = RequestMethod.POST)
	public String adminProductUpdate(HttpServletRequest request, @ModelAttribute("ProductVO")
			ProductVO p, Model model) {				
		ProductVO pvo = new ProductVO();
		int pseq=0;
		String savePath = context.getRealPath("/image/menu/product");
		MultipartRequest multi;
		try {
			multi = new MultipartRequest(
					request, savePath , 5*1024*1024,  "UTF-8", new DefaultFileRenamePolicy() );
			pvo.setPseq(Integer.parseInt(multi.getParameter("pseq")));
			pseq=Integer.parseInt(multi.getParameter("pseq"));
			pvo.setKind1(multi.getParameter("kind1"));
			pvo.setKind2(multi.getParameter("kind2"));
			pvo.setKind3(multi.getParameter("kind3"));
			pvo.setPname(multi.getParameter("pname"));
			pvo.setPrice1(Integer.parseInt(multi.getParameter("price1")));
			pvo.setPrice2(Integer.parseInt(multi.getParameter("price2")));
			pvo.setPrice3(Integer.parseInt(multi.getParameter("price3")));
			pvo.setContent(multi.getParameter("content"));
			System.out.println(multi.getParameter("useyn") + "//////");
			if(multi.getParameter("useyn") == null) {
				pvo.setUseyn("2");
			}else {
				pvo.setUseyn("1");
			}
			if(multi.getFilesystemName("image") == null)
				pvo.setImage(multi.getParameter("oldImage"));
			else
				pvo.setImage(multi.getFilesystemName("image"));
		} catch (IOException e) {e.printStackTrace();	}
		as.updateProduct(pvo);
		return "redirect:/adminProductDetail?pseq="+pseq;
	}
	
	// ????????? ?????? ?????????
	@RequestMapping(value="/adminOrderList")
	public ModelAndView adminOrderList(HttpServletRequest request,
			@RequestParam("kind")String kind) {
		// ?????? param kind??? ??????:1, ?????????:2??? ?????? ????????? ????????????.
		ModelAndView mav = new ModelAndView();
		HttpSession session = request.getSession();
		
		if (session.getAttribute("loginAdmin") == null) {
			mav.setViewName("redirect:/admin");
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
			int count = 0;
			
			// kind????????? ?????? ?????? ???????????? count?????? ????????? paging??? ????????????.
			if(kind.equals("1")) {
				count = as.getAllCount("order_view", "mname", key);
			}else {
				count = count + as.getAllCount("order_view2", "mname", key);
			}
			paging.setTotalCount(count);
			paging.paging();

			// kind?????? ???????????? order_view??? ???????????? ????????????.
			ArrayList<orderVO> orderList = as.listOrder(paging, key, kind);
			
			mav.addObject("kind", kind);
			mav.addObject("orderList", orderList);
			mav.addObject("paging", paging);
			mav.addObject("key", key);
			
			mav.setViewName("admin/order/orderList");
		}
		return mav;
	}
	
	// ?????? ?????? ?????? (1, 2, 3)
	@RequestMapping(value="/adminOrderSave")
	public ModelAndView adminOrderSave(HttpServletRequest request,
			@RequestParam("kind")String kind) {
		ModelAndView mav = new ModelAndView();
		HttpSession session = request.getSession();
		if (session.getAttribute("loginAdmin") == null) {
			mav.setViewName("redirect:/admin");
		}else {
			// ????????? ??????????????? odseq?????? ????????????.
			String[] result = request.getParameterValues("result");
			for(int i = 0; i < result.length; i++) {
				// odseq?????? ????????? ?????? ??????????????? result+1?????? ????????????.
				String step = as.getResult(result[i]);
				// result?????? ????????????.
				as.updateOrderResult(result[i], step); 
			}
			mav.setViewName("redirect:/adminOrderList?kind="+kind);
		}
		return mav;
	}
	
	// ????????? ?????? ????????? ??????
	@RequestMapping(value="/adminOrderDelete")
	public ModelAndView adminOrderDelete(HttpServletRequest request,
			@RequestParam("kind")String kind) {
		ModelAndView mav = new ModelAndView();
		HttpSession session = request.getSession();
		if (session.getAttribute("loginAdmin") == null) {
			mav.setViewName("redirect:/admin");
		}else {
			String[] oseqArr = request.getParameterValues("delete");
			
			for(String odseq : oseqArr) { 
				// odseq ????????? ?????? ?????? ?????? ?????? ????????? oseq ??? ??????
				int oseq = os.getOseq(odseq);
				
				// ?????????????????? odseq ?????? ????????? oseq ?????? ????????? orders??? order_detail?????? ????????? ??????
				// ????????? ????????? ?????? ????????? ??????.
				os.deleteOrder(odseq, oseq);
			}
			
			mav.setViewName("redirect:/adminOrderList?kind="+kind);
		}
		return mav;
	}
	
	// ????????? ?????? ???????????????
	@RequestMapping(value="/adminOrderDetailForm")
	public ModelAndView adminOrderDetailForm(HttpServletRequest request,
			@RequestParam("kind")String kind, @RequestParam("seq")String odseq) {
		ModelAndView mav = new ModelAndView();
		HttpSession session = request.getSession();
		if (session.getAttribute("loginAdmin") == null) {
			mav.setViewName("redirect:/admin");
		}else {
			// kind ?????? ?????? order_view1,2?????? odseq?????? ?????? orderVO??? ????????????.
			if(kind.equals("1")) {
				orderVO ovo = os.getOrder_view(odseq);
				int totalPrice = ovo.getPrice1() * ovo.getQuantity();
				
				// odseq?????? ?????? ??????????????? ????????????.
				ArrayList<subproductOrderVO> list = ps.selectSubProductOrder6(odseq);
				
				for(subproductOrderVO sovo : list) {
					totalPrice += sovo.getAddprice();
				}
				
				// ????????? ????????? ????????????.
				mav.addObject("totalPrice", totalPrice);
				mav.addObject("kind", kind);
				mav.addObject("ovo", ovo);
				mav.addObject("spseqAm", list);
			}else if(kind.equals("2")) {
				orderVO ovo = os.getOrder_view2(odseq);
				int totalPrice = ovo.getPrice1() * ovo.getQuantity();
				
				// odseq?????? ?????? ??????????????? ????????????.
				ArrayList<subproductOrderVO> list = ps.selectSubProductOrder6(odseq);
				
				for(subproductOrderVO sovo : list) {
					totalPrice += sovo.getAddprice();
				}
				
				// ????????? ????????? ????????????.
				mav.addObject("totalPrice", totalPrice);
				mav.addObject("ovo", ovo);
				mav.addObject("kind", kind);
				mav.addObject("spseqAm", list);
			}
			
			mav.setViewName("admin/order/orderDetail");
		}
		return mav;
	}
	
	// ????????? ??????????????? ????????? ?????? ?????? ??????
	@RequestMapping(value="/adminOrderMDelete")
	public ModelAndView adminOrderMDelete(HttpServletRequest request,
			@RequestParam("kind")String kind, @RequestParam("sposeq")String sposeq,
			@RequestParam("odseq")String odseq) {
		ModelAndView mav = new ModelAndView();
		HttpSession session = request.getSession();
		if (session.getAttribute("loginAdmin") == null) {
			mav.setViewName("redirect:/admin");
		}else {
			ps.deleteSpo(sposeq);
			mav.setViewName("redirect:/adminOrderDetailForm?kind="+kind+"&seq="+odseq);
		}
		return mav;
	}
}
