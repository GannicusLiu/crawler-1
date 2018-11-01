package app.crawler.domain;

import java.util.List;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.microservice.dao.entity.crawler.housing.chuxiong.HousingChuXiongPaydetails;
import com.microservice.dao.entity.crawler.housing.chuxiong.HousingChuXiongUserInfo;

public class WebParam {
	
	public HtmlPage htmlPage;
	public Page page;
	public Integer code;
	public String url;
	public String html;
	public String text;
	public WebClient webClient;	
	public List<HousingChuXiongPaydetails>  paydetails;
	public HousingChuXiongUserInfo userInfo;
	public boolean isLogin;
	public HtmlPage getHtmlPage() {
		return htmlPage;
	}
	public void setHtmlPage(HtmlPage htmlPage) {
		this.htmlPage = htmlPage;
	}
	public Page getPage() {
		return page;
	}
	public void setPage(Page page) {
		this.page = page;
	}
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getHtml() {
		return html;
	}
	public void setHtml(String html) {
		this.html = html;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public WebClient getWebClient() {
		return webClient;
	}
	public void setWebClient(WebClient webClient) {
		this.webClient = webClient;
	}
	public boolean isLogin() {
		return isLogin;
	}
	public void setLogin(boolean isLogin) {
		this.isLogin = isLogin;
	}
	public List<HousingChuXiongPaydetails> getPaydetails() {
		return paydetails;
	}
	public void setPaydetails(List<HousingChuXiongPaydetails> paydetails) {
		this.paydetails = paydetails;
	}
	public HousingChuXiongUserInfo getUserInfo() {
		return userInfo;
	}
	public void setUserInfo(HousingChuXiongUserInfo userInfo) {
		this.userInfo = userInfo;
	}
	
}
