package com.gwong.thereisaplace.data;

public class XmlData {
	private String uid;
	private String name;
	private String writer;
	private String msg;
	private String reg_date;

	public XmlData(String uid, String name, String writer, String msg, String reg_date) {
		super();
		this.uid = uid;
		this.name = name;
		this.writer = writer;
		this.msg = msg;
		this.reg_date = reg_date;
	}
	
	public XmlData(){
		
	}


	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getWriter() {
		return writer;
	}

	public void setWriter(String writer) {
		this.writer = writer;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getReg_date() {
		return reg_date;
	}

	public void setReg_date(String reg_date) {
		this.reg_date = reg_date;
	}

}