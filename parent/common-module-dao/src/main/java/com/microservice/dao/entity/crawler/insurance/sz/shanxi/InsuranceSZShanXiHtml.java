package com.microservice.dao.entity.crawler.insurance.sz.shanxi;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import com.microservice.dao.entity.IdEntity;

@Entity
@Table(name="insurance_sz_shanxi_html",indexes = {@Index(name = "index_insurance_sz_shanxi_html_taskid", columnList = "taskid")})
public class InsuranceSZShanXiHtml extends IdEntity{


	private String taskid;							//uuid 前端通过uuid访问状态结果
	private String type;
	private Integer pageCount;	
	private String url;	
	private String html;
	public String getTaskid() {
		return taskid;
	}
	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Integer getPageCount() {
		return pageCount;
	}
	public void setPageCount(Integer pageCount) {
		this.pageCount = pageCount;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	@Column(columnDefinition="text")
	public String getHtml() {
		return html;
	}
	public void setHtml(String html) {
		this.html = html;
	}
	@Override
	public String toString() {
		return "InsuranceSZShanXiHtml [taskid=" + taskid + ", type=" + type + ", pageCount=" + pageCount + ", url="
				+ url + ", html=" + html + "]";
	}
	


}