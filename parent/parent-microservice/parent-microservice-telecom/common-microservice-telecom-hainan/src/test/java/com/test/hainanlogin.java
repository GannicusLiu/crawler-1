package com.test;

import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageReader;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import com.crawler.mobile.json.MessageLogin;
import com.crawler.mobile.json.StatusCodeEnum;
import com.crawler.mobile.json.StatusCodeRec;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.google.gson.Gson;
import com.microservice.dao.entity.crawler.mobile.TaskMobile;
import com.module.htmlunit.WebCrawler;

import app.bean.ValidationLoginDataObject;
import app.bean.ValidationLoginRoot;
import app.bean.WebParamTelecom;
import app.crawler.telecom.htmlparse.TelecomParseCommon;
import app.service.common.LoginAndGetCommon;
import app.unit.TeleComCommonUnit;

/**
 * 
 * 项目名称：common-microservice-telecom-hainan 类名称：hainanlogin 类描述： 创建人：hyx
 * 创建时间：2018年10月29日 下午2:19:39
 * 
 * @version
 */
public class hainanlogin {

	public static void main(String[] args) throws Exception {
		String phonenum = "17763819087";
		String password = "211314";

		MessageLogin messageLogin = new MessageLogin();

		messageLogin.setName(phonenum);
		messageLogin.setPassword(password);
		login(messageLogin);
	}

	public static WebParamTelecom<?> login(MessageLogin messageLogin) throws Exception {

		WebClient webClient = WebCrawler.getInstance().getNewWebClient();
		String url = "http://login.189.cn/web/login";
		HtmlPage htmlpage = (HtmlPage) TeleComCommonUnit.getHtml(url, webClient);

		HtmlTextInput username = (HtmlTextInput) htmlpage.getFirstByXPath("//input[@id='txtAccount']");
		HtmlElement htmlElement = (HtmlTextInput) htmlpage.getFirstByXPath("//input[@id='txtShowPwd']");
		htmlElement.click();
		HtmlPasswordInput passwordInput = (HtmlPasswordInput) htmlpage.getFirstByXPath("//input[@id='txtPassword']");
		HtmlElement button = (HtmlElement) htmlpage.getFirstByXPath("//a[@id='loginbtn']");
		username.setText(messageLogin.getName().trim());
		passwordInput.setText(messageLogin.getPassword().trim());

		htmlpage = button.click();

		WebParamTelecom<?> webParamTelecom = new WebParamTelecom<>();
		;

		if (htmlpage.asXml().indexOf("登录失败") != -1) {

			HtmlImage valiCodeImg = htmlpage.getFirstByXPath("//*[@id='imgCaptcha']");
			if ((valiCodeImg.isDisplayed())) {
				htmlpage = loginByImage(htmlpage, messageLogin);
				if (htmlpage.asXml().indexOf("验证码不正确") != -1) {
					htmlpage = loginByImage(htmlpage, messageLogin);
					if (htmlpage.asXml().indexOf("验证码不正确") != -1) {
						htmlpage = loginByImage(htmlpage, messageLogin);
					}

				}
				if (htmlpage.asXml().indexOf("登录失败") != -1) {
					webParamTelecom = TelecomParseCommon.loginerror_Parser(htmlpage.asXml());
					webParamTelecom.setStatusCodeEnum(StatusCodeEnum.MESSAGE_LOGIN_ERROR_FOURE);
					webParamTelecom.setStatusCodeRec(StatusCodeRec.MESSAGE_LOGIN_ERROR_FOURE);

					System.out.println("登录失败::::::::::" + webParamTelecom.toString());
					return webParamTelecom;
				}
			} else {
				webParamTelecom = TelecomParseCommon.loginerror_Parser(htmlpage.asXml());
				webParamTelecom.setStatusCodeEnum(StatusCodeEnum.MESSAGE_LOGIN_ERROR_FOURE);
				webParamTelecom.setStatusCodeRec(StatusCodeRec.MESSAGE_LOGIN_ERROR_FOURE);
				System.out.println("登录失败::::::::::" + webParamTelecom.toString());
				return webParamTelecom;
			}
		}

		url = "http://www.189.cn/dqmh/my189/initMy189home.do";
		LoginAndGetCommon.getHtml(url, webClient);
		// System.out.println(page.asXml());
		ValidationLoginDataObject dataObject = ValidationLogin(webClient);

		if (dataObject.getNickName() == null) {

			webParamTelecom = TelecomParseCommon.loginerror_Parser(htmlpage.asXml());

			webParamTelecom.setStatusCodeEnum(StatusCodeEnum.MESSAGE_LOGIN_ERROR_FOURE);

			webParamTelecom.setStatusCodeRec(StatusCodeRec.MESSAGE_LOGIN_ERROR_FOURE);
			return webParamTelecom;
		}

		System.out.println("登录成功：：：：：：：：：：：；" + dataObject.toString());

		webParamTelecom.setPage(htmlpage);

		url = "http://www.189.cn/dqmh/my189/initMy189home.do?fastcode=02091577";
		Page html3 = getHtml(url, webClient);
		url = "http://hi.189.cn/service/bill/feequery.jsp?TABNAME=xdcx&fastcode=02091577&cityCode=hi";
		Page html4 = getHtml(url, webClient);

		System.out.println("=========================");
		System.out.println(html4.getWebResponse().getContentAsString());
		System.out.println("=========================");

		getPhonecode(webClient);
		return webParamTelecom;
	}

	private static HtmlPage loginByImage(HtmlPage html, MessageLogin messageLogin) throws Exception {
		HtmlImage valiCodeImg = html.getFirstByXPath("//*[@id='imgCaptcha']");
		System.out.println("Element is  displayed!");
		ImageReader imageReader = valiCodeImg.getImageReader();
		BufferedImage bufferedImage = imageReader.read(0);

		JFrame f2 = new JFrame();
		JLabel l = new JLabel();
		l.setIcon(new ImageIcon(bufferedImage));
		f2.getContentPane().add(l);
		f2.setSize(200, 200);
		f2.setTitle("验证码");
		f2.setVisible(true);

		String valicodeStr = JOptionPane.showInputDialog("请输入验证码：");

		HtmlTextInput username = (HtmlTextInput) html.getFirstByXPath("//input[@id='txtAccount']");
		HtmlPasswordInput passwordInput = (HtmlPasswordInput) html.getFirstByXPath("//input[@id='txtPassword']");
		HtmlTextInput valicodeStrinput = (HtmlTextInput) html.getFirstByXPath("//*[@id='txtCaptcha']");

		HtmlElement button = (HtmlElement) html.getFirstByXPath("//a[@id='loginbtn']");

		username.setText(messageLogin.getName().trim());
		passwordInput.setText(messageLogin.getPassword().trim());
		valicodeStrinput.setText(valicodeStr.toLowerCase().trim());

		HtmlPage htmlpage2 = button.click();
		return htmlpage2;
	}

	private static Gson gs = new Gson();

	private static ValidationLoginDataObject ValidationLogin(WebClient webClient) {

		try {
			String url = "http://www.189.cn/login/index.do";
			Page page = getHtml(url, webClient);

			ValidationLoginRoot jsonObject = gs.fromJson(page.getWebResponse().getContentAsString(),
					ValidationLoginRoot.class);

			return jsonObject.getDataObject();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static void getPhonecode(WebClient webClient) throws Exception {
	
		String url = "http://www.hi.189.cn/BUFFALO/buffalo/CommonAjaxService";

		WebRequest webRequest = new WebRequest(new URL(url), HttpMethod.POST);
		webRequest.setAdditionalHeader("Referer", "http://www.hi.189.cn/service/jf/integralHistory.jsp");
		webRequest.setAdditionalHeader("Referer",
				"http://www.hi.189.cn/service/bill/feequery.jsp?TABNAME=xdcx&fastcode=02091577&cityCode=hi");
		webRequest.setAdditionalHeader("Host", "www.hi.189.cn");
		webRequest.setAdditionalHeader("Pragma", "no-cache");
		webRequest.setAdditionalHeader("Origin", "http://www.hi.189.cn");
		webRequest.setAdditionalHeader("Accept", "*/*");
		webRequest.setAdditionalHeader("Accept-Encoding", "gzip, deflate");
		webRequest.setAdditionalHeader("Accept-Language", "zh-CN,zh;q=0.8");
		webRequest.setAdditionalHeader("Cache-Control", "no-cache");
		webRequest.setAdditionalHeader("Connection", "keep-alive");
		// webRequest.setAdditionalHeader("Content-Length", "160");
		webRequest.setAdditionalHeader("Content-Type", "text/xml;charset=UTF-8");

		webRequest.setRequestBody("<buffalo-call>" + "<method>getSmsCode</method>" + "<map>"
				+ "<type>java.util.HashMap</type>" + "<string>PHONENUM</string>" + "<string>"
				+ "17763819087" + "</string>" + "<string>PRODUCTID</string>" + "<string>50</string>"
				+ "<string>RTYPE</string>" + "<string>QD</string>" + "</map>" + "</buffalo-call>");
		Page page = TeleComCommonUnit.gethtmlWebRequest(webClient, webRequest);

		System.out.println(page.getWebResponse().getContentAsString());

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
}