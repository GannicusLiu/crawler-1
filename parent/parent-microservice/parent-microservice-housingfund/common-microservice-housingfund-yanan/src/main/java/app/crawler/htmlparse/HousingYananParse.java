package app.crawler.htmlparse;

import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
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
import com.microservice.dao.entity.crawler.housing.yanan.HousingYananHtml;
import com.microservice.dao.entity.crawler.housing.yanan.HousingYananPay;
import com.microservice.dao.entity.crawler.housing.yanan.HousingYananUserInfo;
import com.microservice.dao.repository.crawler.housing.yanan.HousingYananHtmlRepository;
import com.microservice.dao.repository.crawler.housing.yanan.HousingYananPayRepository;
import com.module.htmlunit.WebCrawler;

import app.commontracerlog.TracerLog;
import app.crawler.bean.InfoParam;
import app.crawler.bean.WebParam;

@Component
public class HousingYananParse {

	@Autowired
	private TracerLog tracer;
	@Autowired
	private HousingYananPayRepository housingYananPayRepository;

	@Autowired
	private HousingYananHtmlRepository housingYananHtmlRepository;
	/**
	 * 登录
	 * 
	 * @param messageLoginForHousing
	 * @param taskHousing
	 * @return
	 */
	public WebParam login(MessageLoginForHousing messageLoginForHousing, TaskHousing taskHousing) {
		tracer.addTag("HousingYananParse.login", taskHousing.getTaskid());
		WebClient webClient = WebCrawler.getInstance().getNewWebClient();
		WebParam webParam = new WebParam();
		try {
			String loginUrl = "";
			if (null != messageLoginForHousing.getCountNumber() && null != messageLoginForHousing.getNum()) {
				loginUrl = "http://210.74.154.163/wscx/zfbzgl/zfbzsq/login_hidden.jsp?password="
						+ messageLoginForHousing.getPassword() + "&sfzh=" + messageLoginForHousing.getNum() + "&cxyd="
						+ URLEncoder.encode("当前年度", "GBK") + "&zgzh=" + messageLoginForHousing.getCountNumber();
			} else if(null != messageLoginForHousing.getCountNumber()){
				loginUrl="http://210.74.154.163/wscx/zfbzgl/zfbzsq/login_hidden.jsp?password="+messageLoginForHousing.getPassword()
				+"&sfzh=&cxyd="+ URLEncoder.encode("当前年度", "GBK")+"&zgzh="+messageLoginForHousing.getCountNumber();					
			}else{
				loginUrl = "http://210.74.154.163/wscx/zfbzgl/zfbzsq/login_hidden.jsp?sfzh="
						+ messageLoginForHousing.getNum() + "&cxyd=" + URLEncoder.encode("当前年度", "GBK")
						+ "&zgzh=&password=" + messageLoginForHousing.getPassword();
			}		
			Page searchPage = getPage(webClient, taskHousing, loginUrl, null, null, "GBK", null, null);		
			webParam.setUrl(loginUrl);
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
				// 弹框
				String alert = WebCrawler.getAlertMsg();
				InfoParam infoParam = new InfoParam(zgzh, sfzh, zgxm, dwbm);
				webParam.setInfoParam(infoParam);
				webParam.setText(alert);
				String cookies = CommonUnit.transcookieToJson(webClient);
				webParam.setCookies(cookies);
				webParam.setHtml(searchPageString);
			}
		} catch (Exception e) {
			tracer.addTag("HousingYananParse.login:",
					messageLoginForHousing.getTask_id() + "---ERROR:" + e.toString());
			e.printStackTrace();
		}
		return webParam;
	}
	/**
	 * 用户信息
	 * 
	 * @param TaskHousing
	 * @return
	 * @throws Exception
	 */
	public WebParam getUserInfo(TaskHousing taskHousing, InfoParam infoParam) throws Exception {

		tracer.addTag("HousingYananParse.getUserInfo", taskHousing.getTaskid());
		WebParam webParam = new WebParam();
		try {
			WebClient webClient = addcookie(taskHousing);
			String url = "http://210.74.154.163/wscx/zfbzgl/gjjxxcx/gjjxx_cx.jsp";
			String requestBody = "sfzh=" + infoParam.getSfzh() + "&zgxm=" + URLEncoder.encode(infoParam.getZgxm(), "GBK")
					+ "&zgzh=" + infoParam.getZgzh() + "&dwbm=" + infoParam.getDwbm() + "&cxyd="
					+ URLEncoder.encode("当前年度", "GBK");
			Page page = getPage(webClient, taskHousing, url, HttpMethod.POST, null, "GBK", requestBody, null);
			if (null != page) {
				String html = page.getWebResponse().getContentAsString();
				tracer.addTag("HousingYananParse.getUserInfo---用户信息" + taskHousing.getTaskid(),
						"<xmp>" + html + "</xmp>");
				HousingYananUserInfo housingYananUserInfo = htmlUserInfoParser(taskHousing, html);
				webParam.setPage(page);
				webParam.setHtml(html);
				webParam.setHousingYananUserInfo(housingYananUserInfo);
				webParam.setUrl(page.getUrl().toString());
				webParam.setCode(page.getWebResponse().getStatusCode());
				return webParam;
			}
		} catch (Exception e) {
			tracer.addTag("HousingYananParse.getUserInfo---ERROR:",
					taskHousing.getTaskid() + "---ERROR:" + e.toString());
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 解析用户信息
	 * 
	 * @param html
	 * @param TaskHousing
	 * @return
	 */
	public HousingYananUserInfo htmlUserInfoParser(TaskHousing taskHousing, String html) {
		tracer.addTag("HousingYananParse.htmlUserInfoParser---info:" + taskHousing.getTaskid(),
				"<xmp>" + html + "</xmp>");
		try {
			Document doc = Jsoup.parse(html);
			String username = getNextLabelByKeyword(doc, "职工姓名");
			String bankNum = getNextLabelByKeyword(doc, "银行账号");
			String idNum = getNextLabelByKeyword(doc, "身份证号");
			String staffAccount = getNextLabelByKeyword(doc, "职工账号");
			String company = getNextLabelByKeyword(doc, "所在单位");
			String office = getNextLabelByKeyword(doc, "所属办事处");
			String openingDate = getNextLabelByKeyword(doc, "开户日期");
			String state = getNextLabelByKeyword(doc, "当前状态");
			String basemny = getNextLabelByKeyword(doc, "月缴基数");
			String proportion = getNextLabelByKeyword(doc, "个人/单位");// 缴存比例
			String monthlyPay = getNextLabelByKeyword(doc, "月缴金额");
			String supplyAmount = getNextLabelByKeyword(doc, "补贴额");
			String yearBalance = getNextLabelByKeyword(doc, "上年余额");
			
			String yearPay = getNextLabelByKeyword(doc, "本年补缴");
			String yearPayable = getNextLabelByKeyword(doc, "本年缴交");
			String yearDraw = getNextLabelByKeyword(doc, "本年支取");
	
			String yearInterest = getNextLabelByKeyword(doc, "本年利息");
			String yearInto = getNextLabelByKeyword(doc, "本年转入");
			String balance = getNextLabelByKeyword(doc, "本金余额");
			String totalAmount = getNextLabelByKeyword(doc, "实存总额");
			HousingYananUserInfo housingYananUserInfo = new HousingYananUserInfo(taskHousing.getTaskid(),   username,  bankNum,  idNum,  staffAccount,
					 company,  office,  openingDate,  state,  basemny,  proportion,
					 monthlyPay,  supplyAmount,  yearBalance,  yearPay,  yearPayable,
					 yearDraw,  yearInterest,  yearInto,  totalAmount,  balance);
			return housingYananUserInfo;
		} catch (Exception e) {
			e.printStackTrace();
			tracer.addTag("HousingYananParse.htmlUserInfoParser---ERROR:",
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
		Elements es = document.select("tr[class=jtpsoft] td:contains(" + keyword + ")");
		if (null != es && es.size() > 0) {
			Element element = es.first();
			Element nextElement = element.nextElementSibling();
			if (null != nextElement) {
				return nextElement.text();
			}
		}
		return null;
	}

	public WebClient addcookie(TaskHousing taskHousing) {

		WebClient webClient = WebCrawler.getInstance().getNewWebClient();

		Set<Cookie> cookies = CommonUnit.transferJsonToSet(taskHousing.getCookies());
		Iterator<Cookie> i = cookies.iterator();
		while (i.hasNext()) {
			webClient.getCookieManager().addCookie(i.next());
		}

		return webClient;
	}

	/**
	 * 通过url获取 Page
	 * 
	 * @param TaskHousing
	 * @param url
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public Page getPage(WebClient webClient, TaskHousing taskHousing, String url, HttpMethod type,
			List<NameValuePair> paramsList, String code, String body, Map<String, String> map) throws Exception {
		tracer.addTag("HousingYananParse.getPage---url:", url + "---taskId:" + taskHousing.getTaskid());

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
		Thread.sleep(2500);
		int statusCode = searchPage.getWebResponse().getStatusCode();
		tracer.addTag("HousingYananParse.getPage.statusCode:" + statusCode, "---taskid:" + taskHousing.getTaskid());
		if (200 == statusCode) {
			String html = searchPage.getWebResponse().getContentAsString();
			tracer.addTag("HousingYananParse.getPage---taskid:",
					taskHousing.getTaskid() + "---url:" + url + "<xmp>" + html + "</xmp>");
			return searchPage;
		}

		return null;
	}

	/**
	 * 缴费信息
	 * 
	 * @param TaskHousing
	 * @return
	 * @throws Exception
	 */
	public WebParam<HousingYananPay> getPay(MessageLoginForHousing messageLoginForHousing, TaskHousing taskHousing,
			InfoParam infoParam) throws Exception {
		tracer.addTag("HousingYananParse.getPay", taskHousing.getTaskid());
		WebParam<HousingYananPay> webParam = new WebParam<HousingYananPay>();
		try {
			WebClient webClient = addcookie(taskHousing);
			String url = "http://210.74.154.163/wscx/zfbzgl/gjjmxcx/gjjmx_cx.jsp";
		    List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
			paramsList.add(new NameValuePair("sfzh", infoParam.getSfzh()));
			paramsList.add(new NameValuePair("zgxm", URLEncoder.encode(infoParam.getZgxm(), "GBK")));
			paramsList.add(new NameValuePair("zgzh", infoParam.getZgzh()));
			paramsList.add(new NameValuePair("dwbm", infoParam.getDwbm()));
			paramsList.add(new NameValuePair("cxyd", URLEncoder.encode("当前年度", "GBK")));
			Page page = getPage(webClient, taskHousing, url, HttpMethod.POST, paramsList, "GBK", null, null);
			if (null != page) {
				String html = page.getWebResponse().getContentAsString();
				tracer.addTag("HousingYananParse.getPay---缴费信息第一次请求" + taskHousing.getTaskid(),
						"<xmp>" + html + "</xmp>");
				List<String> datalist = new ArrayList<>();
				List<HousingYananPay> list = htmlPayParser(html, taskHousing);
				Document doc = Jsoup.parse(html);
				Elements option = doc.getElementsByTag("option");
				for (Element element : option) {
					String text = element.text();
					if (!"当前年度".equals(text)) {
						datalist.add(text);
					}
				}
				webParam.setPage(page);
				webParam.setHtml(page.getWebResponse().getContentAsString());
				webParam.setList(list);
				webParam.setDatalist(datalist);
				webParam.setUrl(page.getUrl().toString());
				webParam.setCode(page.getWebResponse().getStatusCode());
				housingYananPayRepository.saveAll(list);
				HousingYananHtml housingYananHtml = new HousingYananHtml(taskHousing.getTaskid(),
						"housing_Yanan_pay", "当前年度第一页", page.getUrl().toString(), html);
				housingYananHtmlRepository.save(housingYananHtml);

				return webParam;
			}
		} catch (Exception e) {
			tracer.addTag("HousingYananParse.getPay---ERROR:", taskHousing.getTaskid() + "---ERROR:" + e.toString());
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 缴费信息
	 * 
	 * @param TaskHousing
	 * @return
	 * @throws Exception
	 */
	public WebParam<HousingYananPay> getPaydata(MessageLoginForHousing messageLoginForHousing,
			TaskHousing taskHousing, InfoParam infoParam, String data) throws Exception {
		tracer.addTag("HousingYananParse.getPay" + data, taskHousing.getTaskid());
		WebParam<HousingYananPay> webParam = new WebParam<HousingYananPay>();
		try {
			WebClient webClient = addcookie(taskHousing);
			String url = "http://210.74.154.163/wscx/zfbzgl/zfbzsq/gjjmx_cxtwo.jsp";
			List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
			paramsList.add(new NameValuePair("cxydtwo", data));
			paramsList.add(new NameValuePair("cxydtwo", data));
			paramsList.add(new NameValuePair("cxyd", URLEncoder.encode("当前年度", "GBK")));
			paramsList.add(new NameValuePair("zgzh", infoParam.getZgzh()));
			paramsList.add(new NameValuePair("sfzh", infoParam.getSfzh()));			
			paramsList.add(new NameValuePair("zgxm", infoParam.getZgxm()));
			paramsList.add(new NameValuePair("dwbm", infoParam.getDwbm()));
			Page page = getPage(webClient, taskHousing, url, HttpMethod.POST, paramsList, "GBK", null, null);
			if (null != page) {
				String html = page.getWebResponse().getContentAsString();
				tracer.addTag("HousingYananParse.getPay" + data + "---缴费信息第一次请求" + taskHousing.getTaskid(),
						"<xmp>" + html + "</xmp>");
				List<HousingYananPay>  inList=housingYananPayRepository.findByTaskid(taskHousing.getTaskid());
				List<HousingYananPay> list = htmlPayParserFor(html, taskHousing,inList);
				webParam.setPage(page);
				webParam.setHtml(page.getWebResponse().getContentAsString());
				webParam.setList(list);
				webParam.setUrl(page.getUrl().toString());
				webParam.setCode(page.getWebResponse().getStatusCode());
				housingYananPayRepository.saveAll(list);
				HousingYananHtml housingYananHtml = new HousingYananHtml(taskHousing.getTaskid(),
						"housing_Yanan_pay", data + "第一页", page.getUrl().toString(), html);
				housingYananHtmlRepository.save(housingYananHtml);
				return webParam;
			}
		} catch (Exception e) {
			tracer.addTag("HousingYananParse.getPay" + data + "---ERROR:",
					taskHousing.getTaskid() + "---ERROR:" + e.toString());
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 解析缴费信息
	 * 
	 * @param html
	 * @param TaskHousing
	 * @return
	 */
	private List<HousingYananPay> htmlPayParser(String html, TaskHousing taskHousing) {
		List<HousingYananPay> list = new ArrayList<>();
		try {
			Document doc = Jsoup.parse(html);
			HousingYananPay housingYananPay = null;
			Elements tr = doc.getElementsByTag("tr");
			for (Element element : tr) {
				Elements td = element.getElementsByTag("td");
				if (td.size() == 6) {
					String date = td.get(0).text();
					if (!"日期".equals(date)) {
						String summary = td.get(1).text();
						String debtAmount = td.get(2).text();
						String creditAmount= td.get(3).text();
						String lendingdirection = td.get(4).text();
						String balance = td.get(5).text();				
						housingYananPay = new HousingYananPay(taskHousing.getTaskid(), date,summary,debtAmount,creditAmount,
								 lendingdirection,balance);
						list.add(housingYananPay);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			tracer.addTag("HousingYananParse.htmlPayParser---ERROR:",
					taskHousing.getTaskid() + "---ERROR:" + e.toString());
		}
		return list;
	}
	
	/**
	 * 解析缴费信息
	 * 
	 * @param html
	 * @param TaskHousing
	 * @return
	 */
	private List<HousingYananPay> htmlPayParserFor(String html, TaskHousing taskHousing,List<HousingYananPay> inList) {
		List<HousingYananPay> list = new ArrayList<>();
		try {
			Document doc = Jsoup.parse(html);
			HousingYananPay housingYananPay = null;
			Elements tr = doc.getElementsByTag("tr");
			for (Element element : tr) {
				Elements td = element.getElementsByTag("td");
				if (td.size() == 6) {
					String date = td.get(0).text();
					int k = 0;
					if (!"日期".equals(date)) {
						for (int i = 0; i < inList.size(); i++) {
							if (date.equals(inList.get(i).getDate())) {
								k++;
								break;
							}
						}
						if (k == 0) {
							String debtAmount = td.get(1).text();
							String creditAmount = td.get(2).text();
							String balance = td.get(3).text();
							String lendingdirection = td.get(4).text();
							String summary = td.get(5).text();
							housingYananPay = new HousingYananPay(taskHousing.getTaskid(),date,summary,debtAmount,creditAmount,
									 lendingdirection,balance);
							list.add(housingYananPay);
						}
					}
				}
			}			
		} catch (Exception e) {
			e.printStackTrace();
			tracer.addTag("HousingYananParse.htmlPayParser---ERROR:",
					taskHousing.getTaskid() + "---ERROR:" + e.toString());
		}
		return list;
	}
}