package com.ezen.burger.service;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ezen.burger.dao.IMemberDao;
import com.ezen.burger.dto.MemberVO;

@Service
public class MemberService {
	@Autowired
	IMemberDao mdao;

	public MemberVO getMember(String id) {
		return mdao.getMember(id);
	}

	public MemberVO findMember(String name, String phone) {
		return mdao.findMember(name, phone);
	}

	public MemberVO findPwd(String name, String id) {
		return mdao.findPwd(name, id);
	}

	public void updatePwd(String mseq, String pwd) {
		mdao.updatePwd(mseq, pwd);
	}

	public MemberVO getMember_mseq(int mseq) {
		return mdao.getMember_mseq(mseq);
	}

	public void updateMember(@Valid MemberVO mvo) {
		mdao.updateMember(mvo);
		
	}
}
