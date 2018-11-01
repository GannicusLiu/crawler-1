package app.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.crawler.housingfund.json.HousingfundStatusCodeEnum;
import com.crawler.housingfund.json.MessageLoginForHousing;
import com.crawler.microservice.unit.CommonUnit;
import com.crawler.mobile.json.StatusCodeEnum;
import com.microservice.dao.entity.crawler.housing.basic.TaskHousing;
import com.microservice.dao.entity.crawler.housing.heihe.HousingHeiheHtml;
import com.microservice.dao.repository.crawler.housing.basic.TaskHousingRepository;
import com.microservice.dao.repository.crawler.housing.heihe.HousingHeiheHtmlRepository;
import com.microservice.dao.repository.crawler.housing.heihe.HousingHeiheUserInfoRepository;

import app.commontracerlog.TracerLog;
import app.crawler.domain.WebParam;
import app.service.common.HousingBasicService;
import app.service.common.aop.ICrawlerLogin;
import app.unit.HousingFundHeiheHtmlunit;

@Component
@Service
@EnableAsync
@EntityScan(basePackages = "com.microservice.dao.entity.crawler.housing.heihe")
@EnableJpaRepositories(basePackages = "com.microservice.dao.repository.crawler.housing.heihe")
public class HousingHeiheCrawlerService extends HousingBasicService implements ICrawlerLogin{

	public static final Logger log = LoggerFactory.getLogger(HousingHeiheService.class);
	@Autowired
	private HousingHeiheHtmlRepository housingHeiheHtmlRepository;
	@Autowired
	private HousingHeiheUserInfoRepository housingHeiheUserInfoRepository;
	@Autowired
	private HousingFundHeiheHtmlunit housingFundHeiheHtmlunit;
	@Autowired
	private TaskHousingRepository  taskHousingRepository;
	@Autowired
	private HousingHeiheService housingHeiheService;
	@Autowired
	private TracerLog tracer;	
	private int pageNum=0;
	@Override
	public TaskHousing login( MessageLoginForHousing messageLoginForHousing) {
		tracer.addTag("action.HousingHeiheService.login.taskid", messageLoginForHousing.getTask_id());
		TaskHousing taskHousing =taskHousingRepository.findByTaskid(messageLoginForHousing.getTask_id());
		String messageLoginJson = gs.toJson(messageLoginForHousing);
		taskHousing.setLoginMessageJson(messageLoginJson);
		save(taskHousing);
		WebParam webParam = new WebParam();
		webParam.setCode(0);
		try {
			webParam = housingFundHeiheHtmlunit.login(messageLoginForHousing);
			if(webParam.getHtmlPage().asXml().contains("基本信息")){
				tracer.addTag("action.HousingHeiheService.login.success", "登陆成功");
				taskHousing.setPhase(HousingfundStatusCodeEnum.HOUSING_LOGIN_SUCCESS.getPhase());
				taskHousing.setPhase_status(HousingfundStatusCodeEnum.HOUSING_LOGIN_SUCCESS.getPhasestatus());
				taskHousing.setDescription(HousingfundStatusCodeEnum.HOUSING_LOGIN_SUCCESS.getDescription());
				taskHousing.setError_code(HousingfundStatusCodeEnum.HOUSING_LOGIN_SUCCESS.getError_code());
				String cookies = CommonUnit.transcookieToJson(webParam.getWebClient());
				taskHousing.setCookies(cookies);
				save(taskHousing);			
				pageNum=webParam.getPageSize();
				if (null !=webParam.getHtml()) {
					HousingHeiheHtml housingHeiheHtml = new HousingHeiheHtml();
					housingHeiheHtml.setPageCount(1);
					housingHeiheHtml.setHtml(webParam.getHtml());
					housingHeiheHtml.setType("userInfo");
					housingHeiheHtml.setUrl(webParam.getUrl());
					housingHeiheHtml.setTaskid(taskHousing.getTaskid());
					housingHeiheHtmlRepository.save(housingHeiheHtml);		
				}				
				if (null !=webParam.getUserInfo()) {
					housingHeiheUserInfoRepository.save(webParam.getUserInfo());					
					tracer.addTag("action.housing.crawler.getUserInfo", "用户信息入库"+webParam.getUserInfo());
					taskHousing.setUserinfoStatus(200);
					taskHousing.setPhase(HousingfundStatusCodeEnum.HOUSING_CRAWLER_READ.getPhase());
					taskHousing.setPhase_status(HousingfundStatusCodeEnum.HOUSING_CRAWLER_USER_MSG_SUCCESS.getPhasestatus());
					taskHousing.setDescription(HousingfundStatusCodeEnum.HOUSING_CRAWLER_USER_MSG_SUCCESS.getDescription());
					taskHousing.setError_code(HousingfundStatusCodeEnum.HOUSING_CRAWLER_USER_MSG_SUCCESS.getError_code());
					save(taskHousing);
				}else{				
					tracer.addTag("action.housing.crawler.getUserInfo", "用户信息为空");	
					taskHousing.setUserinfoStatus(201);
					taskHousing.setPhase(HousingfundStatusCodeEnum.HOUSING_CRAWLER_READ.getPhase());
					taskHousing.setPhase_status(HousingfundStatusCodeEnum.HOUSING_CRAWLER_USER_MSG_ERROR.getPhasestatus());
					taskHousing.setDescription(HousingfundStatusCodeEnum.HOUSING_CRAWLER_USER_MSG_SUCCESS.getDescription());
					taskHousing.setError_code(HousingfundStatusCodeEnum.HOUSING_CRAWLER_USER_MSG_ERROR.getError_code());
					save(taskHousing);				
				 }
				}else if(webParam.getHtmlPage().asXml().contains("身份证号输入错误")){
					tracer.addTag("action.HousingHeiheService.login.fail", "登陆失败 账户错误");
					taskHousing.setPhase(HousingfundStatusCodeEnum.HOUSING_LOGIN_ERROR.getPhase());
					taskHousing.setPhase_status(HousingfundStatusCodeEnum.HOUSING_LOGIN_ERROR.getPhasestatus());
					taskHousing.setDescription(HousingfundStatusCodeEnum.HOUSING_LOGIN_ERROR.getDescription());
					taskHousing.setError_code(HousingfundStatusCodeEnum.HOUSING_LOGIN_ERROR.getError_code());
					save(taskHousing);			
				}else if(webParam.getHtmlPage().asXml().contains("身份证号或密码错误")){
					tracer.addTag("action.HousingHeiheService.login.fail", "登陆失败");
					taskHousing.setPhase(HousingfundStatusCodeEnum.HOUSING_LOGIN_ERROR_FOURE.getPhase());
					taskHousing.setPhase_status(HousingfundStatusCodeEnum.HOUSING_LOGIN_ERROR_FOURE.getPhasestatus());
					taskHousing.setDescription(HousingfundStatusCodeEnum.HOUSING_LOGIN_ERROR_FOURE.getDescription());
					taskHousing.setError_code(HousingfundStatusCodeEnum.HOUSING_LOGIN_ERROR_FOURE.getError_code());
					save(taskHousing);
				}			
		
		} catch (Exception e) {
			e.printStackTrace();
			tracer.addTag("action.HousingHeiheService.login.error", e.toString());
			tracer.addTag("action.HousingHeiheService.login.fail3", "登录异常");
			taskHousing.setPhase(HousingfundStatusCodeEnum.HOUSING_LOGIN_ERROR.getPhase());
			taskHousing.setPhase_status(HousingfundStatusCodeEnum.HOUSING_LOGIN_ERROR.getPhasestatus());
			taskHousing.setDescription("登录异常，请您稍后重试。");
			taskHousing.setError_code(HousingfundStatusCodeEnum.HOUSING_LOGIN_ERROR.getError_code());
			save(taskHousing);			
		}		
		taskHousing =taskHousingRepository.findByTaskid(messageLoginForHousing.getTask_id());
		return taskHousing;
	}
	
	@Override
	public TaskHousing getAllData(MessageLoginForHousing messageLoginForHousing) {
		tracer.addTag("action.HousingHeiheService.getAllData.taskid", messageLoginForHousing.getTask_id());
		TaskHousing taskHousing =taskHousingRepository.findByTaskid(messageLoginForHousing.getTask_id());
		housingHeiheService.getPaydetails(messageLoginForHousing, pageNum);
		taskHousing =taskHousingRepository.findByTaskid(messageLoginForHousing.getTask_id());
		return taskHousing;
	}
	@Override
	public TaskHousing getAllDataDone(String taskId) {
		// TODO Auto-generated method stub
		return null;
	}
	
}