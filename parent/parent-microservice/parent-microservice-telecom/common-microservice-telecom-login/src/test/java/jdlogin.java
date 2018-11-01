import java.io.IOException;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.net.URL;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Scanner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import javax.swing.JOptionPane;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import com.crawler.microservice.unit.CommonUnit;
import com.crawler.mobile.json.MessageLogin;
import com.crawler.mobile.json.StatusCodeEnum;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.gargoylesoftware.htmlunit.xml.XmlPage;
import com.google.gson.Gson;
import com.microservice.dao.entity.crawler.mobile.TaskMobile;
import com.module.htmlunit.WebCrawler;
import app.bean.ValidationLoginDataObject;
import app.bean.ValidationLoginRoot;
import app.crawler.telecom.htmlparse.TelecomParseCommon;
import app.service.common.LoginAndGetCommon;
import app.unit.TeleComCommonUnit;

/**
 * 
 * 项目名称：common-microservice-e_commerce-jd 类名称：jdlogin 类描述： 创建人：hyx
 * 创建时间：2017年12月8日 下午6:04:36
 * 
 * @version
 */
public class jdlogin {

	static String driverPath = "C:\\chromedriver.exe";
	// private static String driverPath =
	// "D:\\software\\IEDriverServer_Win32\\chromedriver.exe";

	static Boolean headless = false;

	protected static Gson gs = new Gson();

	public static WebDriver intiChrome() throws Exception {
		System.out.println("launching chrome browser");
		System.setProperty("webdriver.chrome.driver", driverPath);

		// WebDriver driver = new ChromeDriver();
		ChromeOptions chromeOptions = new ChromeOptions();
		// 设置为 headless 模式 （必须）
		System.out.println("headless-------" + headless);
		// if(headless){
		// chromeOptions.addArguments("headless");// headless mode
		// }

		chromeOptions.addArguments("disable-gpu");
		// 设置浏览器窗口打开大小 （非必须）
		// chromeOptions.addArguments("--window-size=1920,1080");
		WebDriver driver = new ChromeDriver(chromeOptions);
		return driver;
	}

	public static String loginChrome() throws Exception {
		WebDriver driver = intiChrome();
		driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
		driver.manage().timeouts().setScriptTimeout(30, TimeUnit.SECONDS);
		String baseUrl = "http://login.189.cn/web/login";
		driver.get(baseUrl);
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(60, TimeUnit.SECONDS)
				.pollingEvery(2, TimeUnit.SECONDS).ignoring(NoSuchElementException.class);
		WebElement loginByUserButton = wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(By.id("txtAccount"));
			}
		});

		loginByUserButton.click();

		wait = new FluentWait<WebDriver>(driver).withTimeout(60, TimeUnit.SECONDS).pollingEvery(2, TimeUnit.SECONDS)
				.ignoring(NoSuchElementException.class);
		WebElement loginname = wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(By.id("txtAccount"));
			}
		});
		loginname.sendKeys("18131003055");
		driver.findElement(By.id("txtShowPwd")).click();
		driver.findElement(By.id("txtPassword")).sendKeys("900628");
		String code = JOptionPane.showInputDialog("请输入验证码：");
		driver.findElement(By.id("txtCaptcha")).sendKeys(code.trim());

		driver.findElement(By.id("loginbtn")).click();
		Thread.sleep(5000);
		// System.out.println(driver.getPageSource());
		// WebElement name = wait.until(new Function<WebDriver, WebElement>() {
		// public WebElement apply(WebDriver driver) {
		// return driver.findElement(By.id("userType"));
		// }
		// });
		//
		// System.out.println("============="+name+"=================");
		Set<org.openqa.selenium.Cookie> cookiesDriverForLogin = driver.manage().getCookies();
		WebClient webClientForLogin = WebCrawler.getInstance().getNewWebClient();
		// JSESSIONID-JT
		Cookie cookieAnother = null;
		for (org.openqa.selenium.Cookie cookie : cookiesDriverForLogin) {
			Cookie cookieWebClient = new Cookie(cookie.getDomain(), cookie.getName(), cookie.getValue());
			System.out.println(cookie.getName() + ":" + cookie.getValue());
			if (cookie.getName().indexOf("JSESSIONID-JT") != -1) {
				cookieAnother = new Cookie(cookie.getDomain(), cookie.getName(), cookie.getValue());
			}
			webClientForLogin.getCookieManager().addCookie(cookieWebClient);
		}
		if (ValidationLogin(webClientForLogin) == null) {
			return null;
		}
		System.out.println("sucess");
		// String htmlsource3 = driver.getPageSource();
		// Set<org.openqa.selenium.Cookie> cookiesDriver =
		// driver.manage().getCookies();
		// System.out.println("============" + driver.getCurrentUrl());
		System.out.println("====================clieck===================");
		/* driver.findElement(By.id("hqyzm")).click(); */
		System.out.println("====================clieck===================");
		// Thread.sleep(130000);

		driver.get("http://www.189.cn/dqmh/my189/initMy189home.do?fastcode=00380407");
		driver.get("http://he.189.cn/service/bill/feeQuery_iframe.jsp?SERV_NO=SHQD1&fastcode=00380407&cityCode=he");

		Thread.sleep(1000);
		/*
		 * wait = new FluentWait<WebDriver>(driver).withTimeout(80,
		 * TimeUnit.SECONDS) .pollingEvery(2,
		 * TimeUnit.SECONDS).ignoring(NoSuchElementException.class); WebElement
		 * senbutton2 = wait.until(new Function<WebDriver, WebElement>() {
		 * public WebElement apply(WebDriver driver) { return
		 * driver.findElement(By.id("bodyIframe")); } });
		 * System.out.println("======================");
		 * driver.switchTo().frame("bodyIframe");
		 */

		Set<org.openqa.selenium.Cookie> cookiesDriver1 = driver.manage().getCookies();
		WebClient webClient1 = WebCrawler.getInstance().getNewWebClient();

		for (org.openqa.selenium.Cookie cookie : cookiesDriver1) {
			Cookie cookieWebClient = new Cookie("he.189.cn", cookie.getName(), cookie.getValue());
			System.out.println(cookieWebClient.getName() + ":" + cookieWebClient.getValue());
			webClient1.getCookieManager().addCookie(cookieWebClient);
		}

		webClient1.getCookieManager().addCookie(cookieAnother);

		// 第三种：推荐，尤其是容量大时
		System.out.println("第三种：通过Map.entrySet遍历key和value");
		Set<Cookie> cookieMap = webClient1.getCookieManager().getCookies();
		for (Cookie cookie : cookieMap) {
			System.out.println("another :" + cookie.getName() + ":" + cookie.getValue());
		}

		// System.out.println(driver.getPageSource());
		String cityCode = parserCityCode(driver.getPageSource());
		System.out.println("城市代码：" + cityCode);

		/*
		 * Set<org.openqa.selenium.Cookie> cookiesDriver2 =
		 * driver.manage().getCookies(); WebClient webClient2 =
		 * WebCrawler.getInstance().getNewWebClient();
		 * 
		 * for (org.openqa.selenium.Cookie cookie : cookiesDriver2) { Cookie
		 * cookieWebClient = new Cookie("he.189.cn", cookie.getName(),
		 * cookie.getValue());
		 * System.out.println("==========="+cookieWebClient.getName()+":"+
		 * cookieWebClient.getValue());
		 * webClient2.getCookieManager().addCookie(cookieWebClient); }
		 */
		// 发送短信
		// driver.close();
		/*
		 * sendsms(webClient1);
		 * 
		 * @SuppressWarnings("resource") Scanner scanner = new
		 * Scanner(System.in); String sjmput =
		 * JOptionPane.showInputDialog("请输入短信验证码："); //获取通话记录
		 * getCallRec(webClient1,sjmput);
		 */

		return null;
	}

	private static void getCallRec(WebClient webClient, String sms) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = new GregorianCalendar();
		calendar.add(Calendar.MONTH, -1);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		String BEGIN_DATE = sdf.format(calendar.getTime());

		calendar.add(Calendar.MONTH, 1);
		calendar.set(Calendar.DAY_OF_MONTH, 0);
		System.out.println(sdf.format(calendar.getTime()));
		String END_DATE = sdf.format(calendar.getTime());

		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMM");
		Calendar c1 = Calendar.getInstance();
		c1.setTime(new Date());
		c1.add(Calendar.MONTH, -1);
		Date date1 = c1.getTime();
		String currentmon = sdf2.format(date1);

		String callUrl = "http://he.189.cn/service/bill/action/ifr_bill_detailslist_em_new.jsp";
		System.out.println("通话记录的url：" + callUrl);
		Page page = getPage(callUrl, webClient, sms);

		System.out.println("通话记录page：" + page.getWebResponse().getContentAsString());
	}

	private static void sendsms(WebClient webClient) throws Exception {
		String smsUrl = "http://he.189.cn/service/transaction/postValidCode.jsp?"
				+ "reDo=Tue+Feb+27+2018+14%3A39%3A53+GMT%2B0800+(%E4%B8%AD%E5%9B%BD%E6%A0%87%E5%87%86%E6%97%B6%E9%97%B4)"
				+ "&OPER_TYPE=CR1&RAND_TYPE=006&PRODTYPE=2020966&MOBILE_NAME=18131003055&MOBILE_NAME_PWD=&NUM=18131003055&AREA_CODE=186&LOGIN_TYPE=21";

		XmlPage page = (XmlPage) LoginAndGetCommon.getHtml(smsUrl, webClient);
		System.out.println("发送短信后返送的内容：" + page.getWebResponse().getContentAsString());

	}

	private static ValidationLoginDataObject ValidationLogin(WebClient webClient) {

		try {
			String url = "http://www.189.cn/login/index.do";
			Page page = getHtml(url, webClient);

			System.out.println("*************************************** index.do");
			System.out.println(page.getWebResponse().getContentAsString());

			ValidationLoginRoot jsonObject = gs.fromJson(page.getWebResponse().getContentAsString(),
					ValidationLoginRoot.class);

			return jsonObject.getDataObject();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static Page getHtml(String url, WebClient webClient) throws Exception {
		WebRequest webRequest = new WebRequest(new URL(url), HttpMethod.GET);
		webRequest.setAdditionalHeader("Accept", "*");
		webRequest.setAdditionalHeader("Accept-Encoding", "gzip, deflate");
		webRequest.setAdditionalHeader("Accept-Language", "zh-CN,zh;q=0.8");
		webRequest.setAdditionalHeader("Connection", "keep-alive");
		webRequest.setAdditionalHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

		webRequest.setAdditionalHeader("Host", "www.189.cn");
		webRequest.setAdditionalHeader("Referer", "http://www.189.cn/html/login/right.html");
		webRequest.setAdditionalHeader("Origin", "http://www.czgjj.com");
		webRequest.setAdditionalHeader("X-Requested-With", "XMLHttpRequest");
		webClient.setJavaScriptTimeout(50000);
		webClient.getOptions().setTimeout(50000); // 15->60
		Page searchPage = webClient.getPage(webRequest);
		return searchPage;
	}

	// 得到验证码方法
	public static void getYzm2(WebClient webClient) {

		try {
			String wdzlurl = "http://www.189.cn/dqmh/my189/initMy189home.do?fastcode=20000776";
			WebRequest webRequestwdzl;
			webRequestwdzl = new WebRequest(new URL(wdzlurl), HttpMethod.GET);
			HtmlPage wdzl = webClient.getPage(webRequestwdzl);
			webClient = wdzl.getWebClient();
			int statusCode = wdzl.getWebResponse().getStatusCode();
			if (statusCode == 200) {

				String packurl7 = "http://nx.189.cn/bfapp/buffalo/CtQryService";
				String requestPayloadSend7 = "<buffalo-call><method>getCustOfBillByCustomerCode</method><string>2951121277270000__giveup</string></buffalo-call>";
				WebRequest webRequestpack7 = new WebRequest(new URL(packurl7), HttpMethod.POST);
				webRequestpack7.setAdditionalHeader("Content-Type", "text/plain;charset=UTF-8");
				webRequestpack7.setAdditionalHeader("Host", "nx.189.cn");
				webRequestpack7.setAdditionalHeader("Origin", "http://nx.189.cn");
				webRequestpack7.setAdditionalHeader("Referer",
						"http://nx.189.cn/jt/bill/xd/?fastcode=20000776&cityCode=nx");
				webRequestpack7.setAdditionalHeader("User-Agent",
						"Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36");
				webRequestpack7.setRequestBody(requestPayloadSend7);
				webClient.getPage(webRequestpack7);

				String packurl8 = "http://nx.189.cn/bfapp/buffalo/CtQryService";
				String requestPayloadSend8 = "<buffalo-call><method>getFeeNumByHT</method><string>10732369</string><string>201708</string></buffalo-call>";
				WebRequest webRequestpack8 = new WebRequest(new URL(packurl8), HttpMethod.POST);
				webRequestpack8.setAdditionalHeader("Content-Type", "text/plain;charset=UTF-8");
				webRequestpack8.setAdditionalHeader("Host", "nx.189.cn");
				webRequestpack8.setAdditionalHeader("Origin", "http://nx.189.cn");
				webRequestpack8.setAdditionalHeader("Referer",
						"http://nx.189.cn/jt/bill/xd/?fastcode=20000776&cityCode=nx");
				webRequestpack8.setAdditionalHeader("User-Agent",
						"Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36");
				webRequestpack8.setRequestBody(requestPayloadSend8);
				webClient.getPage(webRequestpack8);

				String packurl9 = "http://nx.189.cn/bfapp/buffalo/CtQryService";
				String requestPayloadSend9 = "<buffalo-call><method>getSelectedFeeProdNum</method><string>undefined</string><string>"
						+ "18995154123" + "</string><string>2</string></buffalo-call>";
				WebRequest webRequestpack9 = new WebRequest(new URL(packurl9), HttpMethod.POST);
				webRequestpack9.setAdditionalHeader("Content-Type", "text/plain;charset=UTF-8");
				webRequestpack9.setAdditionalHeader("Host", "nx.189.cn");
				webRequestpack9.setAdditionalHeader("Origin", "http://nx.189.cn");
				webRequestpack9.setAdditionalHeader("Referer",
						"http://nx.189.cn/jt/bill/xd/?fastcode=20000776&cityCode=nx");
				webRequestpack9.setAdditionalHeader("User-Agent",
						"Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36");
				webRequestpack9.setRequestBody(requestPayloadSend9);
				Page page2 = webClient.getPage(webRequestpack9);
				System.out.println("发送验证码接口" + page2.getWebResponse().getContentAsString());
				// 发送验证码接口
				String sendurl = "http://nx.189.cn/bfapp/buffalo/CtSubmitService";
				String requestPayloadSend = "<buffalo-call><method>sendDXYzmForBill</method></buffalo-call>";
				WebRequest webRequestSend = new WebRequest(new URL(sendurl), HttpMethod.POST);
				webRequestSend.setAdditionalHeader("Content-Type", "text/plain;charset=UTF-8");
				webRequestSend.setAdditionalHeader("Host", "nx.189.cn");
				webRequestSend.setAdditionalHeader("Origin", "http://nx.189.cn");
				webRequestSend.setAdditionalHeader("Referer",
						"http://nx.189.cn/jt/bill/xd/?fastcode=20000776&cityCode=nx");
				webRequestSend.setAdditionalHeader("User-Agent",
						"Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36");
				webRequestSend.setRequestBody(requestPayloadSend);
				Page pageSend = webClient.getPage(webRequestSend);

				String send = pageSend.getWebResponse().getContentAsString();
				System.out.println("发送验证码接口" + send);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void getYzm(WebClient webClient) {
		/*
		 * for (Cookie cookie : cookies) {
		 * webClient.getCookieManager().addCookie(cookie); }
		 */
		try {
			/*
			 * String wdzlurl =
			 * "http://www.189.cn/dqmh/my189/initMy189home.do?fastcode=10000501";
			 * WebRequest webRequestwdzl = new WebRequest(new URL(wdzlurl),
			 * HttpMethod.GET); HtmlPage wdzl =
			 * webClient.getPage(webRequestwdzl);
			 * System.out.println(wdzl.asXml()); webClient =
			 * wdzl.getWebClient(); String url =
			 * "http://nx.189.cn/jt/bill/xd/?fastcode=20000776&cityCode=nx";
			 * WebRequest webRequestGet = new WebRequest(new URL(url),
			 * HttpMethod.GET);
			 * webRequestGet.setAdditionalHeader("Content-Type",
			 * "text/plain;charset=UTF-8");
			 * webRequestGet.setAdditionalHeader("Host", "nx.189.cn");
			 * webRequestGet.setAdditionalHeader("Referer",
			 * "http://www.189.cn/dqmh/my189/initMy189home.do?fastcode=20000779"
			 * ); webRequestGet.setAdditionalHeader("User-Agent",
			 * "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36"
			 * ); Page fastcode = webClient.getPage(webRequestGet);
			 * System.out.println("=========================================");
			 * System.out.println(fastcode.getWebResponse().getContentAsString()
			 * );
			 * System.out.println("=========================================");
			 * 
			 * String url2 =
			 * "http://nx.189.cn/html/wt/service/bill/printXd.html"; Page
			 * fastcode2 = webClient.getPage(webRequestGet);
			 * 
			 * String wdzlurl2 =
			 * "http://www.189.cn/dqmh/my189/checkMy189Session.do"; WebRequest
			 * webRequestwdzl2 = new WebRequest(new URL(wdzlurl),
			 * HttpMethod.POST); List<NameValuePair> paramsList = new
			 * ArrayList<NameValuePair>(); paramsList.add(new
			 * NameValuePair("fastcode", "20000776"));
			 * webRequestwdzl2.setAdditionalHeader("Content-Type",
			 * "text/plain;charset=UTF-8");
			 * webRequestwdzl2.setAdditionalHeader("Host", "www.189.cn");
			 * webRequestwdzl2.setAdditionalHeader("Origin",
			 * "http://www.189.cn");
			 * webRequestwdzl2.setAdditionalHeader("Referer",
			 * "http://www.189.cn/dqmh/my189/initMy189home.do?fastcode=20000779"
			 * ); webRequestwdzl2.setAdditionalHeader("User-Agent",
			 * "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36"
			 * ); webRequestwdzl2.setRequestParameters(paramsList);
			 * 
			 * Page wdzl2 = webClient.getPage(webRequestwdzl2);
			 * System.out.println("=========================================");
			 * System.out.println(wdzl.getWebResponse().getContentAsString());
			 * System.out.println("=========================================");
			 * 
			 * // webClient = wdzl.getWebClient();
			 * 
			 * String packurl7 = "http://nx.189.cn/bfapp/buffalo/CtQryService";
			 * String requestPayloadSend7 =
			 * "<buffalo-call><method>getSelectedFeeProdNum</method>" +
			 * "<string>0951</string><string>18995154123</string><string>2</string></buffalo-call>";
			 * WebRequest webRequestpack7 = new WebRequest(new URL(packurl7),
			 * HttpMethod.POST);
			 * 
			 * webRequestpack7.setAdditionalHeader("Content-Type",
			 * "text/plain;charset=UTF-8");
			 * webRequestpack7.setAdditionalHeader("Accept-Encoding",
			 * "gzip, deflate");
			 * webRequestpack7.setAdditionalHeader("Accept-Language",
			 * "zh-CN,zh;q=0.9"); webRequestpack7.setAdditionalHeader("Host",
			 * "nx.189.cn"); webRequestpack7.setAdditionalHeader("Origin",
			 * "http://nx.189.cn");
			 * webRequestpack7.setAdditionalHeader("Referer",
			 * "http://nx.189.cn/jt/bill/xd/?fastcode=20000776&cityCode=nx");
			 * webRequestpack7.setAdditionalHeader("User-Agent",
			 * "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36"
			 * ); webRequestpack7.setAdditionalHeader("X-Buffalo-Version",
			 * "2.0");
			 * 
			 * webRequestpack7.setRequestBody(requestPayloadSend7);
			 * webClient.getPage(webRequestpack7); Page page1 =
			 * webClient.getPage(webRequestpack7);
			 * 
			 * String packurl9 = "http://nx.189.cn/bfapp/buffalo/CtQryService";
			 * String requestPayloadSend9 =
			 * "<buffalo-call><method>checkIsBillSMSShow</method></buffalo-call>";
			 * WebRequest webRequestpack9 = new WebRequest(new URL(packurl9),
			 * HttpMethod.POST);
			 * webRequestpack9.setAdditionalHeader("Content-Type",
			 * "text/plain;charset=UTF-8");
			 * webRequestpack9.setAdditionalHeader("Host", "nx.189.cn");
			 * webRequestpack9.setAdditionalHeader("Origin",
			 * "http://nx.189.cn");
			 * webRequestpack9.setAdditionalHeader("Referer",
			 * "http://nx.189.cn/jt/bill/xd/?fastcode=20000776&cityCode=nx");
			 * webRequestpack9.setAdditionalHeader("User-Agent",
			 * "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36"
			 * ); webRequestpack9.setRequestBody(requestPayloadSend9); Page
			 * page2 = webClient.getPage(webRequestpack9);
			 */
			// 发送验证码接口
			/*
			 * String sendurl =
			 * "http://nx.189.cn/bfapp/buffalo/CtSubmitService"; String
			 * requestPayloadSend =
			 * "<buffalo-call><method>sendDXYzmForBill</method></buffalo-call>";
			 * WebRequest webRequestSend = new WebRequest(new URL(sendurl),
			 * HttpMethod.POST);
			 * webRequestSend.setAdditionalHeader("Content-Type",
			 * "text/plain;charset=UTF-8");
			 * webRequestSend.setAdditionalHeader("Host", "nx.189.cn");
			 * webRequestSend.setAdditionalHeader("Origin", "http://nx.189.cn");
			 * webRequestSend.setAdditionalHeader("Referer",
			 * "http://nx.189.cn/jt/bill/xd/?fastcode=20000776&cityCode=nx");
			 * webRequestSend.setAdditionalHeader("User-Agent",
			 * "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36"
			 * ); webRequestSend.setRequestBody(requestPayloadSend); Page
			 * pageSend = webClient.getPage(webRequestSend);
			 */

			// String send = pageSend.getWebResponse().getContentAsString();
			/*
			 * System.out.println("page1=" +
			 * page1.getWebResponse().getContentAsString());
			 * System.out.println("page2=" +
			 * page2.getWebResponse().getContentAsString());
			 */
			String packurl7 = "http://nx.189.cn/bfapp/buffalo/CtSubmitService";
			String requestPayloadSend7 = "<buffalo-call><method>sendDXYzmForBill</method></buffalo-call>";
			WebRequest webRequestpack7 = new WebRequest(new URL(packurl7), HttpMethod.POST);

			webRequestpack7.setAdditionalHeader("Content-Type", "text/plain;charset=UTF-8");
			webRequestpack7.setAdditionalHeader("Accept-Encoding", "gzip, deflate");
			webRequestpack7.setAdditionalHeader("Accept-Language", "zh-CN,zh;q=0.9");
			webRequestpack7.setAdditionalHeader("Host", "nx.189.cn");
			webRequestpack7.setAdditionalHeader("Origin", "http://nx.189.cn");
			webRequestpack7.setAdditionalHeader("Referer",
					"http://nx.189.cn/jt/bill/xd/?fastcode=10000501&cityCode=nx");
			webRequestpack7.setAdditionalHeader("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36");
			webRequestpack7.setAdditionalHeader("X-Buffalo-Version", "2.0");

			webRequestpack7.setRequestBody(requestPayloadSend7);
			webClient.getPage(webRequestpack7);
			Page page1 = webClient.getPage(webRequestpack7);
			String send = page1.getWebResponse().getContentAsString();
			System.out.println("发送验证码接口" + send);

			Set<Cookie> cookies = webClient.getCookieManager().getCookies();
			for (Cookie cookie : cookies) {
				System.out.println(cookie.getName() + ":" + cookie.getValue());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// https://order.jd.com/center/list.action
	public static void main(String[] args) {
		try {
			loginChrome();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Page getPage(String url, WebClient webClient, String sms) throws Exception {
		WebRequest webRequest = new WebRequest(new URL(url), HttpMethod.POST);
		webRequest.setAdditionalHeader("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		webRequest.setAdditionalHeader("Accept-Encoding", "gzip, deflate");
		webRequest.setAdditionalHeader("Accept-Language", "zh-CN,zh;q=0.8");
		webRequest.setAdditionalHeader("Connection", "keep-alive");
		webRequest.setAdditionalHeader("Content-Type", "application/x-www-form-urlencoded");

		webRequest.setAdditionalHeader("Host", "he.189.cn");
		webRequest.setAdditionalHeader("Referer",
				"http://he.189.cn/service/bill/feeQuery_iframe.jsp?SERV_NO=SHQD1&fastcode=00380407&cityCode=he");
		webRequest.setAdditionalHeader("Origin", "http://he.189.cn");
		webRequest.setAdditionalHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
		webClient.setJavaScriptTimeout(50000);
		webClient.getOptions().setTimeout(50000); // 15->60

		webRequest.setRequestParameters(new ArrayList<NameValuePair>());
		webRequest.getRequestParameters().add(new NameValuePair("ACC_NBR", "18131003055"));
		webRequest.getRequestParameters().add(new NameValuePair("CITY_CODE", "186"));
		webRequest.getRequestParameters().add(new NameValuePair("BEGIN_DATE", "2018-02-01 00:00:00"));
		webRequest.getRequestParameters().add(new NameValuePair("END_DATE", "2018-02-28 23:59:59"));
		webRequest.getRequestParameters().add(new NameValuePair("FEE_DATE", "201802"));
		webRequest.getRequestParameters().add(new NameValuePair("SERVICE_KIND", "8"));
		webRequest.getRequestParameters().add(new NameValuePair("retCode", "0000"));
		webRequest.getRequestParameters().add(new NameValuePair("QUERY_FLAG", "1"));
		webRequest.getRequestParameters().add(new NameValuePair("QUERY_TYPE_NAME", "移动语音详单"));
		webRequest.getRequestParameters().add(new NameValuePair("QUERY_TYPE", "1"));
		webRequest.getRequestParameters().add(new NameValuePair("radioQryType", "on"));
		webRequest.getRequestParameters().add(new NameValuePair("QRY_FLAG", "1"));
		webRequest.getRequestParameters().add(new NameValuePair("ACCT_DATE", "201802"));
		webRequest.getRequestParameters().add(new NameValuePair("ACCT_DATE_1", "201803"));
		webRequest.getRequestParameters().add(new NameValuePair("sjmput", sms));

		Page searchPage = webClient.getPage(webRequest);
		return searchPage;
	}

	public static String parserCityCode(String html) {
		Document doc = Jsoup.parse(html);
		String cityCode = doc.select("[name=AREA_CODE]").first().val();
		System.out.println("城市代码：" + cityCode);
		return cityCode;
	}
}