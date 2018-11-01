package app.crawler.htmlparser;

import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.crawler.housingfund.json.MessageLoginForHousing;
import com.crawler.microservice.unit.CommonUnit;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.microservice.dao.entity.crawler.housing.basic.TaskHousing;
import com.microservice.dao.entity.crawler.housing.shangrao.HousingShangRaoHtml;
import com.microservice.dao.entity.crawler.housing.shangrao.HousingShangRaoPaydetails;
import com.microservice.dao.entity.crawler.housing.shangrao.HousingShangRaoUserInfo;
import com.microservice.dao.repository.crawler.housing.basic.TaskHousingRepository;
import com.microservice.dao.repository.crawler.housing.shangrao.HousingShangRaoHtmlRepository;
import com.microservice.dao.repository.crawler.housing.shangrao.HousingShangRaoPaydetailsRepository;
import com.module.htmlunit.WebCrawler;

import app.commontracerlog.TracerLog;
import app.crawler.bean.InfoParam;
import app.crawler.bean.WebParam;


@Component
public class HousingShangRaoParse {

	@Autowired
	private TracerLog tracer;
	@Autowired
	private HousingShangRaoPaydetailsRepository housingShangRaoPaydetailsRepository;
	@Autowired
	private HousingShangRaoHtmlRepository housingShangRaoHtmlRepository;
	@Autowired
	private TaskHousingRepository  taskHousingRepository;
	/**
	 * 登录
	 * 
	 * @param messageLoginForHousing
	 * @param taskHousing
	 * @return
	 */
	public WebParam login(MessageLoginForHousing messageLoginForHousing) {
		TaskHousing taskHousing=taskHousingRepository.findByTaskid(messageLoginForHousing.getTask_id());
		tracer.addTag("HousingShangRaoParse.login", taskHousing.getTaskid());
		try {
			WebClient webClient = WebCrawler.getInstance().getNewWebClient();
			WebParam webParam = new WebParam();
			String url = "http://www.srgjj.com:8080/wscx/zfbzgl/wscx/login_hidden.jsp?password="+messageLoginForHousing.getPassword()+"&sfzh="+messageLoginForHousing.getNum()+"&cxyd=0&dbname=GJJ&dlfs=01&yzm=01";	         		
			webClient.getOptions().setJavaScriptEnabled(false);
			Page searchPage = getPage(webClient, taskHousing, url, null, null, "GBK", null, null);			
			if (null != searchPage) {
				String searchPageString = searchPage.getWebResponse().getContentAsString();
				Document doc = Jsoup.parse(searchPageString);
				// 职工账号
				Elements selectzgzh = doc.select("input[name=zgzh]");
				String zgzh = selectzgzh.get(0).val();
				// 身份证号
				Elements selectsfzh = doc.select("input[name=sfzh]");
				String sfzh = selectsfzh.get(0).val();
				// 职工姓名
				Elements selectzgxm = doc.select("input[name=zgxm]");
				String zgxm = selectzgxm.get(0).val();
				// 感觉像“单位编码”
				Elements selectdwbm = doc.select("input[name=dwbm]");
				String dwbm = selectdwbm.get(0).val();
				// 当前状态
				Elements selectzgzt = doc.select("input[name=zgzt]");
				String zgzt = selectzgzt.get(0).val();
				// 弹框
				String alertMsg = WebCrawler.getAlertMsg();
				InfoParam infoParam = new InfoParam(zgzh, sfzh, zgxm, dwbm, zgzt);
				webParam.setInfoParam(infoParam);
				webParam.setAlertMsg(alertMsg);
				webParam.setHtml(searchPageString);
				webParam.setWebClient(webClient);
				return webParam;
			}
		} catch (Exception e) {
			tracer.addTag("HousingShangRaoParse.login:",
					messageLoginForHousing.getTask_id() + "---ERROR:" + e.toString());
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 用户信息
	 * 
	 * @param taskMobile
	 * @return
	 * @throws Exception
	 */
	public WebParam getUserInfo(TaskHousing taskHousing, InfoParam infoParam) throws Exception {
		tracer.addTag("HousingShangRaoParse.getUserInfo", taskHousing.getTaskid());
		WebParam webParam = new WebParam();
		try {
			WebClient webClient = WebCrawler.getInstance().getNewWebClient();
			webClient= addcookie(webClient, taskHousing);
			String url = "http://www.srgjj.com:8080/wscx/zfbzgl/gjjxxcx/gjjxxcx.jsp";
			List<NameValuePair> paramsList = new ArrayList<NameValuePair>();			
			paramsList.add(new NameValuePair("sfzh", infoParam.getSfzh()));
			paramsList.add(new NameValuePair("zgxm", infoParam.getZgxm()));
			paramsList.add(new NameValuePair("zgzh", infoParam.getZgzh()));
			paramsList.add(new NameValuePair("dwbm", infoParam.getDwbm()));
			paramsList.add(new NameValuePair("cxyd", "0"));
			paramsList.add(new NameValuePair("zgzt", infoParam.getZgzt()));
			Page page = getPage(webClient, taskHousing, url, HttpMethod.POST, paramsList, "GBK", null, null);
			if (null != page) {
				String html = page.getWebResponse().getContentAsString();
				tracer.addTag("HousingShangRaoParse.getUserInfo---用户信息" + taskHousing.getTaskid(),
						"<xmp>" + html + "</xmp>");
				HousingShangRaoUserInfo housingShangRaoUserInfo = htmlUserInfoParser(taskHousing, html);
				webParam.setPage(page);
				webParam.setHtml(html);
				webParam.setHousingShangRaoUserInfo(housingShangRaoUserInfo);
				webParam.setUrl(page.getUrl().toString());
				webParam.setCode(page.getWebResponse().getStatusCode());
				return webParam;
			}
		} catch (Exception e) {
			tracer.addTag("HousingShangRaoParse.getUserInfo---ERROR:",
					taskHousing.getTaskid() + "---ERROR:" + e.toString());
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 解析用户信息
	 * 
	 * @param html
	 * @param taskInsurance
	 * @return
	 */
	public HousingShangRaoUserInfo htmlUserInfoParser(TaskHousing taskHousing, String html) {
		tracer.addTag("HousingShangRaoParse.htmlUserInfoParser---info:" + taskHousing.getTaskid(),
				"<xmp>" + html + "</xmp>");
		try {
			Document doc = Jsoup.parse(html);
			String username = getNextLabelByKeyword(doc, "职工姓名");
			String cardNum = getNextLabelByKeyword(doc, "联名卡卡号");
			String idNum = getNextLabelByKeyword(doc, "身份证号");
			String fundNum = getNextLabelByKeyword(doc, "职工公积金编号");
			String companyName = getNextLabelByKeyword(doc, "所在单位");
			String office = getNextLabelByKeyword(doc, "所属管理部");
			String openingDate = getNextLabelByKeyword(doc, "开户日期");			
			String state = getNextLabelByKeyword(doc, "当前状态");
			String basemny = getNextLabelByKeyword(doc, "月缴基数");
			String proportion = getNextLabelByKeyword(doc, "缴存比例");// 缴存比例
			String monthlyPay = getNextLabelByKeyword(doc, "月缴金额");
			String yearBalance = getNextLabelByKeyword(doc, "上年余额");
			String companyMonthlyPay = getNextLabelByKeyword(doc, "单位月缴额");
			String yearBackupPay = getNextLabelByKeyword(doc, "本年补缴");
			String psnMonthlyPay = getNextLabelByKeyword(doc, "个人月缴额");
			String yearDraw = getNextLabelByKeyword(doc, "本年支取");
			String financeMonthlyPay = getNextLabelByKeyword(doc, "财配月缴额");
			String yearInterest = getNextLabelByKeyword(doc, "本年利息");
			String yearPay = getNextLabelByKeyword(doc, "本年缴交");
			String balance = getNextLabelByKeyword(doc, "公积金余额");
			String yearInto = getNextLabelByKeyword(doc, "本年转入");
			String paymonth = getNextLabelByKeyword(doc, "应缴年月");
			HousingShangRaoUserInfo housingShangRaoUserInfo = new HousingShangRaoUserInfo(taskHousing.getTaskid(), username, cardNum, idNum, fundNum,
					companyName, office, openingDate, state, basemny, proportion,
					monthlyPay, yearBalance, companyMonthlyPay, yearBackupPay, psnMonthlyPay,
					yearDraw, financeMonthlyPay, yearInterest, yearPay,balance, yearInto,
					paymonth);
			return housingShangRaoUserInfo;
		} catch (Exception e) {
			e.printStackTrace();
			tracer.addTag("HousingShangRaoParse.htmlUserInfoParser---ERROR:",
					taskHousing.getTaskid() + "---ERROR:" + e.toString());
		}
		return null;
	}

	/**
	 * @Des 获取目标标签的下一个兄弟标签的内容
	 * @param document
	 * @param keyword
	 * @return
	 */
	public static String getNextLabelByKeyword(Document document, String keyword) {
		Elements es = document.select("td:contains(" + keyword + ")");
		if (null != es && es.size() > 0) {
			Element element = es.first();
			Element nextElement = element.nextElementSibling();
			if (null != nextElement) {
				return nextElement.text();
			}
		}
		return null;
	}
	/**
	 * 通过url获取 Page
	 * 
	 * @param taskMobile
	 * @param url
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public Page getPage(WebClient webClient, TaskHousing taskHousing, String url, HttpMethod type,
			List<NameValuePair> paramsList, String code, String body, Map<String, String> map) throws Exception {
		tracer.addTag("HousingShangRaoParse.getPage---url:", url + "---taskId:" + taskHousing.getTaskid());

		WebRequest webRequest = new WebRequest(new URL(url), null != type ? type : HttpMethod.GET);

		if (null != map) {
			for (Map.Entry<String, String> entry : map.entrySet()) {
				webRequest.setAdditionalHeader(entry.getKey(), entry.getValue());
			}
		}

		if (null != body && !"".equals(body)) {
			webRequest.setRequestBody(body);
		}

		if (null != code && !"".equals(code)) {
			webRequest.setCharset(Charset.forName(code));
		}
		if (paramsList != null) {
			webRequest.setRequestParameters(paramsList);
		}
		Page searchPage = webClient.getPage(webRequest);
		Thread.sleep(1500);
		int statusCode = searchPage.getWebResponse().getStatusCode();
		tracer.addTag("HousingShangRaoParse.getPage.statusCode:" + statusCode, "---taskid:" + taskHousing.getTaskid());
		if (200 == statusCode) {
			String html = searchPage.getWebResponse().getContentAsString();
			tracer.addTag("HousingShangRaoParse.getPage---taskid:",
					taskHousing.getTaskid() + "---url:" + url + "<xmp>" + html + "</xmp>");
			return searchPage;
		}

		return null;
	}

	/**
	 * 缴费信息
	 * 
	 * @param taskMobile
	 * @return
	 * @throws Exception
	 */
	public WebParam<HousingShangRaoPaydetails> getPay( TaskHousing taskHousing,
			InfoParam infoParam) throws Exception {
		tracer.addTag("HousingShangRaoParse.getPay", taskHousing.getTaskid());
		WebParam<HousingShangRaoPaydetails> webParam = new WebParam<HousingShangRaoPaydetails>();
		try {
			WebClient webClient = WebCrawler.getInstance().getNewWebClient();
			webClient= addcookie(webClient, taskHousing);
			String url = "http://www.srgjj.com:8080/wscx/zfbzgl/gjjmxcx/gjjmxcx.jsp";
			List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
			paramsList.add(new NameValuePair("sfzh", infoParam.getSfzh()));
			paramsList.add(new NameValuePair("zgxm", infoParam.getZgxm()));
			paramsList.add(new NameValuePair("zgzh", infoParam.getZgzh()));
			paramsList.add(new NameValuePair("dwbm", infoParam.getDwbm()));
			paramsList.add(new NameValuePair("cxyd", "0"));
			paramsList.add(new NameValuePair("zgzt", infoParam.getZgzt()));
			Page page = getPage(webClient, taskHousing, url, HttpMethod.POST, paramsList, "GBK", null, null);
			if (null != page) {
				String html = page.getWebResponse().getContentAsString();
				tracer.addTag("HousingShangRaoParse.getPay---缴费信息第一次请求" + taskHousing.getTaskid(),
						"<xmp>" + html + "</xmp>");
				List<String> datalist = new ArrayList<>();
				List<HousingShangRaoPaydetails> list = htmlPayParser(html, taskHousing,"当前年度");
				Document doc = Jsoup.parse(html);
				Elements option = doc.getElementsByTag("option");
				for (Element element : option) {
					String text = element.text();
					if (!"当前年度".equals(text) && !(infoParam.getZgzh().equals(text))) {
						datalist.add(text);
					}
				}
				Elements elementsByAttributeValue = doc.getElementsByAttributeValue("name", "totalpages");
				String text = elementsByAttributeValue.get(0).val();
				if ("2".equals(text)) {
					getPayTWO(webClient, taskHousing, infoParam);
				}
				webParam.setPage(page);
				webParam.setWebClient(webClient);
				webParam.setHtml(page.getWebResponse().getContentAsString());
				webParam.setList(list);
				webParam.setText(text);
				webParam.setDatalist(datalist);
				webParam.setUrl(page.getUrl().toString());
				webParam.setCode(page.getWebResponse().getStatusCode());

				housingShangRaoPaydetailsRepository.saveAll(list);
				HousingShangRaoHtml housingShangRaoHtml = new HousingShangRaoHtml(taskHousing.getTaskid(),
						"housing_ShangRao_pay", "当前年度第一页", page.getUrl().toString(), html);
				housingShangRaoHtmlRepository.save(housingShangRaoHtml);
				return webParam;
			}
		} catch (Exception e) {
			tracer.addTag("HousingShangRaoParse.getPay---ERROR:", taskHousing.getTaskid() + "---ERROR:" + e.toString());
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 缴费信息
	 * 
	 * @param taskMobile
	 * @return
	 * @throws Exception
	 */
	public void getPayTWO(WebClient webClient, TaskHousing taskHousing, InfoParam infoParam)
			throws Exception {
		tracer.addTag("HousingShangRaoParse.getPayTWO", taskHousing.getTaskid());
		try {
	    	String url = "http://www.srgjj.com:8080/wscx/zfbzgl/gjjmxcx/gjjmxcx.jsp";
			List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
			paramsList.add(new NameValuePair("cxydone", "0"));
			paramsList.add(new NameValuePair("cxydtwo", "当前年度"));
			paramsList.add(new NameValuePair("yss", "2"));
			paramsList.add(new NameValuePair("totalpages", "2"));
			paramsList.add(new NameValuePair("cxyd", "当前年度"));
			paramsList.add(new NameValuePair("zgzh", infoParam.getZgzh()));
			paramsList.add(new NameValuePair("sfzh", infoParam.getSfzh()));
			paramsList.add(new NameValuePair("zgxm", infoParam.getZgxm()));
			paramsList.add(new NameValuePair("dwbm", infoParam.getDwbm()));
			Page page = getPage(webClient, taskHousing, url, HttpMethod.POST, paramsList, "GBK", null, null);

			if (null != page) {
				String html = page.getWebResponse().getContentAsString();
				tracer.addTag("HousingShangRaoParse.getPayTWO---缴费信息第二次请求" + taskHousing.getTaskid(),
						"<xmp>" + html + "</xmp>");
				List<HousingShangRaoPaydetails> list = htmlPayParser(html, taskHousing,"当前年度");

				housingShangRaoPaydetailsRepository.saveAll(list);
				HousingShangRaoHtml housingShangRaoHtml = new HousingShangRaoHtml(taskHousing.getTaskid(),
						"housing_ShangRao_pay", "当前年度第二页", page.getUrl().toString(), html);
				housingShangRaoHtmlRepository.save(housingShangRaoHtml);
			}
		} catch (Exception e) {
			tracer.addTag("HousingShangRaoParse.getPayTWO---ERROR:",
					taskHousing.getTaskid() + "---ERROR:" + e.toString());
			e.printStackTrace();
		}
	}

	/**
	 * 缴费信息
	 * 
	 * @param taskMobile
	 * @return
	 * @throws Exception
	 */
	public WebParam<HousingShangRaoPaydetails> getPaydata(WebClient webClient,
			TaskHousing taskHousing, InfoParam infoParam, String data) throws Exception {
		tracer.addTag("HousingShangRaoParse.getPay" + data, taskHousing.getTaskid());
		WebParam<HousingShangRaoPaydetails> webParam = new WebParam<HousingShangRaoPaydetails>();
		try {
			String url = " http://www.srgjj.com:8080/wscx/zfbzgl/gjjmxcx/gjjmxcx.jsp";
			List<NameValuePair> paramsList = new ArrayList<NameValuePair>();	
			paramsList.add(new NameValuePair("cxydtwo", data));
			paramsList.add(new NameValuePair("yss", "1"));
			paramsList.add(new NameValuePair("totalpages", "1"));
			paramsList.add(new NameValuePair("cxyd", "0"));
			paramsList.add(new NameValuePair("zgzh", infoParam.getZgzh()));
			paramsList.add(new NameValuePair("sfzh", infoParam.getSfzh()));
			paramsList.add(new NameValuePair("zgxm", infoParam.getZgxm()));
			paramsList.add(new NameValuePair("dwbm", infoParam.getDwbm()));
			Page page = getPage(webClient, taskHousing, url, HttpMethod.POST, paramsList, "GBK", null, null);
			if (null != page) {
				String html = page.getWebResponse().getContentAsString();
				tracer.addTag("HousingShangRaoParse.getPay" + data + "---缴费信息第一次请求" + taskHousing.getTaskid(),
						"<xmp>" + html + "</xmp>");
				List<String> datalist = new ArrayList<>();
				List<HousingShangRaoPaydetails> list = htmlPayParser(html, taskHousing,data);
				Document doc = Jsoup.parse(html);
				Elements elementsByAttributeValue = doc.getElementsByAttributeValue("name", "totalpages");
				String text = elementsByAttributeValue.get(0).val();
				if ("2".equals(text)) {
					tracer.addTag("查询月份" + data + "第二页", taskHousing.getTaskid());
					getPaydataTWO(webClient, taskHousing, infoParam, data);
				}
				webParam.setPage(page);
				webParam.setHtml(page.getWebResponse().getContentAsString());
				webParam.setList(list);
				webParam.setDatalist(datalist);
				webParam.setUrl(page.getUrl().toString());
				webParam.setCode(page.getWebResponse().getStatusCode());			
				housingShangRaoPaydetailsRepository.saveAll(list);
				HousingShangRaoHtml housingShangRaoHtml = new HousingShangRaoHtml(taskHousing.getTaskid(),
						"housing_ShangRao_pay", data + "第一页", page.getUrl().toString(), html);
				housingShangRaoHtmlRepository.save(housingShangRaoHtml);
				return webParam;
			}
		} catch (Exception e) {
			tracer.addTag("HousingShangRaoParse.getPay" + data + "---ERROR:",
					taskHousing.getTaskid() + "---ERROR:" + e.toString());
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 缴费信息
	 * 
	 * @param taskMobile
	 * @return
	 * @throws Exception
	 */
	public void getPaydataTWO(WebClient webClient, TaskHousing taskHousing,
			InfoParam infoParam, String data) throws Exception {
		tracer.addTag("HousingShangRaoParse.getPaydataTWO" + data, taskHousing.getTaskid());
		try {
			String url = "http://www.srgjj.com:8080/wscx/zfbzgl/gjjmxcx/gjjmxcx.jsp";
			List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
			paramsList.add(new NameValuePair("cxydtwo", data));
			paramsList.add(new NameValuePair("yss", "2"));
			paramsList.add(new NameValuePair("totalpages", "2"));
			paramsList.add(new NameValuePair("cxyd", "0"));
			paramsList.add(new NameValuePair("zgzh", infoParam.getZgzh()));
			paramsList.add(new NameValuePair("sfzh", infoParam.getSfzh()));
			paramsList.add(new NameValuePair("zgxm", infoParam.getZgxm()));
			paramsList.add(new NameValuePair("dwbm", infoParam.getDwbm()));
			Page page = getPage(webClient, taskHousing, url, HttpMethod.POST, paramsList, "GBK", null, null);
			if (null != page) {
				String html = page.getWebResponse().getContentAsString();
				tracer.addTag("HousingShangRaoParse.getPaydataTWO" + data + "---缴费信息第二次请求" + taskHousing.getTaskid(),
						"<xmp>" + html + "</xmp>");
				List<HousingShangRaoPaydetails> list = htmlPayParser(html, taskHousing,data);
				housingShangRaoPaydetailsRepository.saveAll(list);
				HousingShangRaoHtml housingShangRaoHtml = new HousingShangRaoHtml(taskHousing.getTaskid(),
						"housing_ShangRao_pay", data + "年 第二页", page.getUrl().toString(), html);
				housingShangRaoHtmlRepository.save(housingShangRaoHtml);
			}
		} catch (Exception e) {
			tracer.addTag("HousingShangRaoParse.getPayTWO" + data + "---ERROR:",
					taskHousing.getTaskid() + "---ERROR:" + e.toString());
			e.printStackTrace();
		}
	}
	/**
	 * 解析缴费信息
	 * 
	 * @param html
	 * @param taskInsurance
	 * @return
	 */
	private List<HousingShangRaoPaydetails> htmlPayParser(String html, TaskHousing taskHousing,String year) {
		List<HousingShangRaoPaydetails> list = new ArrayList<>();
		try {
			Document doc = Jsoup.parse(html);
			Elements tr = doc.getElementsByTag("tr");
			for (Element element : tr) {
				Elements td = element.getElementsByTag("td");
				if (td.size() == 5) {
					String date = td.get(0).text();
					if (!"日期".equals(date)) {
						String summary = td.get(1).text();
						String reduceAmount = td.get(2).text();
						String increaseAmount = td.get(3).text();
						String balance = td.get(4).text();				
						HousingShangRaoPaydetails housingShangRaoPaydetails  = new HousingShangRaoPaydetails(taskHousing.getTaskid(), date, summary,
								reduceAmount, increaseAmount, balance);
						list.add(housingShangRaoPaydetails);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			tracer.addTag("HousingShangRaoParse.htmlPayParser---ERROR:",
					taskHousing.getTaskid() + "---ERROR:" + e.toString());
		}
		return list;
	}
	
	public  WebClient addcookie(WebClient webclient, TaskHousing taskHousing) {
		Set<Cookie> cookies = CommonUnit.transferJsonToSet(taskHousing.getCookies());
		 for(Cookie cookie : cookies){
			 webclient.getCookieManager().addCookie(cookie);
		  }
		return webclient;
	}
}
