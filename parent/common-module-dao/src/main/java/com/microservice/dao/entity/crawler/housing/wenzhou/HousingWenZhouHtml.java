package com.microservice.dao.entity.crawler.housing.wenzhou;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.microservice.dao.entity.IdEntity;

@Entity
@DynamicUpdate(true)
@DynamicInsert
@Table(name = "housing_wenzhou_html",indexes = {@Index(name = "index_housing_wenzhou_html_taskid", columnList = "taskid")})
public class HousingWenZhouHtml extends IdEntity implements Serializable{

	private static final long serialVersionUID = 7874933455038037278L;
    private String taskid;
    private String type;
    private Integer pagenumber;
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
	public Integer getPagenumber() {
		return pagenumber;
	}
	public void setPagenumber(Integer pagenumber) {
		this.pagenumber = pagenumber;
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
    
    
}
