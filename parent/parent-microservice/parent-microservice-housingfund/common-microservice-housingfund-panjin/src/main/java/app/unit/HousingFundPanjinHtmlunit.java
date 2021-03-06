package app.unit;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.crawler.housingfund.json.MessageLoginForHousing;
import com.crawler.microservice.unit.CommonUnit;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.microservice.dao.entity.crawler.housing.basic.TaskHousing;
import com.microservice.dao.entity.crawler.housing.panjin.HousingPanjinPaydetails;
import com.microservice.dao.entity.crawler.housing.panjin.HousingPanjinUserInfo;
import com.module.htmlunit.WebCrawler;

import app.commontracerlog.TracerLog;
import app.crawler.domain.WebParam;
import app.parser.HousingFundPanjinParser;

@Component
public class HousingFundPanjinHtmlunit {
	public static final Logger log = LoggerFactory.getLogger(HousingFundPanjinHtmlunit.class);
	
	@Autowired
	private HousingFundPanjinParser  housingFundPanjinParser;
	@Autowired
	private TracerLog tracer;
	public WebParam login(MessageLoginForHousing messageLoginForHousing, TaskHousing taskHousing,int count) throws Exception {
		WebParam webParam= new WebParam();
		WebClient webClient = WebCrawler.getInstance().getNewWebClient();
		String url = "http://gjjcx.panjin.gov.cn/PersonLoginServlet";	
		WebRequest webRequest = new WebRequest(new URL(url), HttpMethod.POST);
		List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
		paramsList.add(new NameValuePair("gcode", messageLoginForHousing.getNum()));
		paramsList.add(new NameValuePair("gpsd", ""));
		paramsList.add(new NameValuePair("gpage", "1"));
		webRequest.setAdditionalHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		webRequest.setAdditionalHeader("Accept-Encoding", "gzip, deflate");
		webRequest.setAdditionalHeader("Accept-Language", "zh-CN,zh;q=0.8");
		webRequest.setAdditionalHeader("Connection", "keep-alive");
		webRequest.setAdditionalHeader("Content-Type", "application/x-www-form-urlencoded");
		webRequest.setAdditionalHeader("Host", "gjjcx.panjin.gov.cn");
		webRequest.setAdditionalHeader("Origin", "http://gjjcx.panjin.gov.cn");
		webRequest.setAdditionalHeader("Referer", "http://gjjcx.panjin.gov.cn/personal_searchnew_1.jsp");
		webRequest.setAdditionalHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.90 Safari/537.36");
		webRequest.setRequestParameters(paramsList);
		HtmlPage page = webClient.getPage(webRequest);
		Thread.sleep(1500);
		tracer.addTag("action.crawler.login.page", "<xmp>"+page.asXml()+"</xmp>");
		webParam.setUrl(url);			
		HousingPanjinUserInfo userInfo=new HousingPanjinUserInfo();
		 if(200 == page.getWebResponse().getStatusCode()){					
				String html=page.asXml();				
				webParam.setHtml(html);
				if (html.contains("基本信息")) {
					Document doc = Jsoup.parse(html, "utf-8"); 
					Element table= doc.getElementById("prtable").nextElementSibling();					 
				    Elements td= table.select("td");
					String  a_text=td.last().getElementsByTag("a").last().attr("href");
				    String[] ss=a_text.split("=");
					System.out.println(ss[1]);
					webParam.setPageSize(Integer.parseInt(ss[1])); 
					userInfo=housingFundPanjinParser.htmlUserInfoParser(html,taskHousing);
					webParam.setUserInfo(userInfo);
					webParam.setWebClient(webClient);
					webParam.setHtmlPage(page);
					webParam.setLogin(true);	
					
				}else{
					if (count < 3) {
						count++;
						tracer.addTag("action.crawler.login.count" + count, "这是第" + count + "次登陆");
						Thread.sleep(1500);
						login(messageLoginForHousing, taskHousing, count);
					}					
				}
		
		}
		return webParam;	
	}
	
	public WebParam  getPaydetails(MessageLoginForHousing messageLoginForHousing, TaskHousing taskHousing,int pageNum) throws Exception {
		tracer.addTag("action.crawler.getPaydetails 第"+pageNum+"页", messageLoginForHousing.getTask_id());
		WebParam webParam= new WebParam();
		WebClient webClient = WebCrawler.getInstance().getNewWebClient();
		webClient = addcookie(webClient,taskHousing);
		String url = "http://gjjcx.panjin.gov.cn/PersonViewServlet?pageNumber="+pageNum;	
		WebRequest webRequest = new WebRequest(new URL(url), HttpMethod.GET);
		webRequest.setAdditionalHeader("Accept", "t*/*;");
		webRequest.setAdditionalHeader("Accept-Encoding", "gzip, deflate");
		webRequest.setAdditionalHeader("Accept-Language", "zh,zh-CN;q=0.8,en-US;q=0.5,en;q=0.3");
		webRequest.setAdditionalHeader("Connection", "keep-alive");
		webRequest.setAdditionalHeader("Host", "gjjcx.panjin.gov.cn");
		webRequest.setAdditionalHeader("Referer", "http://gjjcx.panjin.gov.cn/PersonLoginServlet");
		webRequest.setAdditionalHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.90 Safari/537.36");
		webRequest.setAdditionalHeader("X-Requested-With", "XMLHttpRequest");	
		HtmlPage page= webClient.getPage(webRequest);	
		List<HousingPanjinPaydetails> paydetails=new ArrayList<HousingPanjinPaydetails>();
		String html=page.getWebResponse().getContentAsString();
		tracer.addTag("action.crawler.getPaydetails", "<xmp>"+html+"</xmp>");
		webParam.setHtml(html);
		webParam.setUrl(url);
	    if (html.contains("prtable")) {
		    paydetails=housingFundPanjinParser.htmlPaydetailsParser(html, taskHousing);
			webParam.setPaydetails(paydetails);
			webParam.setHtml(html);
			webParam.setPage(page);
		  }				
		return webParam;
	}
	
	public  WebClient addcookie(WebClient webclient, TaskHousing taskHousing) {
		Set<Cookie> cookies = CommonUnit.transferJsonToSet(taskHousing.getCookies());
		 for(Cookie cookie : cookies){
			 webclient.getCookieManager().addCookie(cookie);
		  }
		return webclient;
	}
}
