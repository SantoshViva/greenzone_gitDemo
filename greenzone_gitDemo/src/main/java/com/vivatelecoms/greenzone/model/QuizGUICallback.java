package com.vivatelecoms.greenzone.model;

public class QuizGUICallback {
	private String cdrid;
	private String cdrstarttime;
	private String cdrendtime;
	private String quiztitle;
	private String quiztotalscore;
	private String cdrcalleridnumber;
	private String cdrduration;
	private String cdrcallstatus;
	public String getCdrid() {
		return cdrid;
	}
	public void setCdrid(String cdrid) {
		this.cdrid = cdrid;
	}
	public String getCdrstarttime() {
		return cdrstarttime;
	}
	public void setCdrstarttime(String cdrstarttime) {
		this.cdrstarttime = cdrstarttime;
	}
	public String getCdrendtime() {
		return cdrendtime;
	}
	public void setCdrendtime(String cdrendtime) {
		this.cdrendtime = cdrendtime;
	}
	public String getQuiztitle() {
		return quiztitle;
	}
	public void setQuiztitle(String quiztitle) {
		this.quiztitle = quiztitle;
	}
	public String getQuiztotalscore() {
		return quiztotalscore;
	}
	public void setQuiztotalscore(String quiztotalscore) {
		this.quiztotalscore = quiztotalscore;
	}
	public String getCdrcalleridnumber() {
		return cdrcalleridnumber;
	}
	public void setCdrcalleridnumber(String cdrcalleridnumber) {
		this.cdrcalleridnumber = cdrcalleridnumber;
	}
	public String getCdrduration() {
		return cdrduration;
	}
	public void setCdrduration(String cdrduration) {
		this.cdrduration = cdrduration;
	}
	public String getCdrcallstatus() {
		return cdrcallstatus;
	}
	public void setCdrcallstatus(String cdrcallstatus) {
		this.cdrcallstatus = cdrcallstatus;
	}
	@Override
	public String toString() {
		return "QuizGUICallback [cdrid=" + cdrid + ", cdrstarttime=" + cdrstarttime + ", cdrendtime=" + cdrendtime
				+ ", quiztitle=" + quiztitle + ", quiztotalscore=" + quiztotalscore + ", cdrcalleridnumber="
				+ cdrcalleridnumber + ", cdrduration=" + cdrduration + ", cdrcallstatus=" + cdrcallstatus + "]";
	}
	
	

}
