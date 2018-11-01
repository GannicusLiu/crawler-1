package app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.crawler.housingfund.json.HousingfundStatusCodeEnum;
import com.crawler.housingfund.json.MessageLoginForHousing;
import com.crawler.microservice.unit.CommonUnit;
import com.microservice.dao.entity.crawler.housing.basic.TaskHousing;
import com.microservice.dao.entity.crawler.housing.zhuzhou.HousingFundZhuZhouAccount;
import com.microservice.dao.entity.crawler.housing.zhuzhou.HousingFundZhuZhouHtml;
import com.microservice.dao.entity.crawler.housing.zhuzhou.HousingFundZhuZhouUserInfo;
import com.microservice.dao.repository.crawler.housing.zhuzhou.HousingFundZhuZhouRepositoryAccount;
import com.microservice.dao.repository.crawler.housing.zhuzhou.HousingFundZhuZhouRepositoryHtml;
import com.microservice.dao.repository.crawler.housing.zhuzhou.HousingFundZhuZhouRepositoryUserInfo;

import app.common.WebParam;
import app.parser.HousingFundZhuZhouParser;
import app.service.common.HousingBasicService;

@Component
@Service
@EnableAsync
@EntityScan(basePackages = "com.microservice.dao.entity.crawler.housing.zhuzhou")
@EnableJpaRepositories(basePackages = "com.microservice.dao.repository.crawler.housing.zhuzhou")
public class HousingFundZhuZhouService  extends HousingBasicService{

	@Autowired
	private HousingFundZhuZhouParser housingFundZhuZhouParser;
	@Autowired
	private HousingFundZhuZhouRepositoryUserInfo housingFundZhuZhouRepositoryUserInfo;
	@Autowired
	private HousingFundZhuZhouRepositoryHtml housingFundZhuZhouRepositoryHtml;
	@Autowired
	private HousingFundZhuZhouRepositoryAccount housingFundZhuZhouRepositoryAccount;
	public void login(MessageLoginForHousing messageLoginForHousing, TaskHousing taskHousing) {
		try {
			WebParam webParam = housingFundZhuZhouParser.login(messageLoginForHousing,taskHousing);
			if(null != webParam)
			{
				if(webParam.getHtml().contains("您好，欢迎登录本系统"))
				{
					tracer.addTag("action.login.SUCCESS", taskHousing.getTaskid());
					taskHousing.setPhase(HousingfundStatusCodeEnum.HOUSING_LOGIN_SUCCESS.getPhase());
					taskHousing.setPhase_status(HousingfundStatusCodeEnum.HOUSING_LOGIN_SUCCESS.getPhasestatus());
					taskHousing.setDescription(HousingfundStatusCodeEnum.HOUSING_LOGIN_SUCCESS.getDescription());
					taskHousing.setPassword(messageLoginForHousing.getPassword().trim());
					String cookieString = CommonUnit.transcookieToJson(webParam.getWebClient());
					taskHousing.setCookies(cookieString);
					save(taskHousing);
				}
				else{
					tracer.addTag("action.login.ERROR1", taskHousing.getTaskid());
					taskHousing.setPhase(HousingfundStatusCodeEnum.HOUSING_LOGIN_ERROR.getPhase());
					taskHousing.setPhase_status("FAIL");
					taskHousing.setDescription(HousingfundStatusCodeEnum.HOUSING_LOGIN_ERROR.getDescription());
					taskHousing.setError_code(HousingfundStatusCodeEnum.HOUSING_LOGIN_ERROR.getError_code());
					taskHousing.setPassword(messageLoginForHousing.getPassword().trim());
					save(taskHousing);
				}
			}
			else
			{
				tracer.addTag("action.login.ERROR2", taskHousing.getTaskid());
				taskHousing.setPhase(HousingfundStatusCodeEnum.HOUSING_LOGIN_ERROR.getPhase());
				taskHousing.setPhase_status("FAIL");
				taskHousing.setDescription(HousingfundStatusCodeEnum.HOUSING_LOGIN_ERROR.getDescription());
				taskHousing.setError_code(HousingfundStatusCodeEnum.HOUSING_LOGIN_ERROR.getError_code());
				taskHousing.setPassword(messageLoginForHousing.getPassword().trim());
				save(taskHousing);
			}
		} catch (Exception e) {
			tracer.addTag("action.login.ERROR.TIMEOUT2", taskHousing.getTaskid());
			taskHousing.setPhase(HousingfundStatusCodeEnum.HOUSING_LOGIN_ERROR.getPhase());
			taskHousing.setPhase_status("FAIL");
			taskHousing.setDescription("登陆超时，请核对信息及其网络");
			taskHousing.setPassword(messageLoginForHousing.getPassword().trim());
			save(taskHousing);
			e.printStackTrace();
		}		
	}
	
	
	//个人信息
	public void crawlerUser(MessageLoginForHousing messageLoginForHousing, TaskHousing taskHousing) {
		try {
			WebParam<HousingFundZhuZhouUserInfo> webParam = housingFundZhuZhouParser.crawlerUser(messageLoginForHousing,taskHousing);
			if(null != webParam)
			{
				if(null != webParam.getHousingFundZhuZhouUserInfo())
				{
					tracer.addTag("action.crawlerUserInfo.SUCCESS", taskHousing.getTaskid());
					HousingFundZhuZhouHtml h = new HousingFundZhuZhouHtml();
					h.setHtml(webParam.getHtml());
					h.setTaskid(taskHousing.getTaskid());
					h.setType("crawlerUserInfo");
					h.setUrl(webParam.getUrl());
					housingFundZhuZhouRepositoryHtml.save(h);
					housingFundZhuZhouRepositoryUserInfo.save(webParam.getHousingFundZhuZhouUserInfo());
					taskHousing.setPhase(HousingfundStatusCodeEnum.HOUSING_CRAWLER_USER_MSG_SUCCESS.getPhase());
					taskHousing.setPhase_status(HousingfundStatusCodeEnum.HOUSING_CRAWLER_USER_MSG_SUCCESS.getPhasestatus());
					taskHousing.setDescription(HousingfundStatusCodeEnum.HOUSING_CRAWLER_USER_MSG_SUCCESS.getDescription());
					taskHousing.setUserinfoStatus(200);
					taskHousing.setWebdriverHandle(webParam.getHousingFundZhuZhouUserInfo().getPersonalNum());
					taskHousingRepository.save(taskHousing);
					updateTaskHousing(taskHousing.getTaskid());
					
				}
				else
				{
					tracer.addTag("action.crawlerUserInfo.ERROR1", taskHousing.getTaskid());
					taskHousing.setPhase(HousingfundStatusCodeEnum.HOUSING_CRAWLER_USER_MSG_SUCCESS.getPhase());
					taskHousing.setPhase_status(HousingfundStatusCodeEnum.HOUSING_CRAWLER_USER_MSG_SUCCESS.getPhasestatus());
					taskHousing.setDescription(HousingfundStatusCodeEnum.HOUSING_CRAWLER_USER_MSG_SUCCESS.getDescription());
					taskHousing.setUserinfoStatus(201);
					taskHousingRepository.save(taskHousing);
					updateTaskHousing(taskHousing.getTaskid());
				}
			}
			else
			{
				tracer.addTag("action.crawlerUserInfo.ERROR2", taskHousing.getTaskid());
				taskHousing.setPhase(HousingfundStatusCodeEnum.HOUSING_CRAWLER_USER_MSG_SUCCESS.getPhase());
				taskHousing.setPhase_status(HousingfundStatusCodeEnum.HOUSING_CRAWLER_USER_MSG_SUCCESS.getPhasestatus());
				taskHousing.setDescription(HousingfundStatusCodeEnum.HOUSING_CRAWLER_USER_MSG_SUCCESS.getDescription());
				taskHousing.setUserinfoStatus(201);
				taskHousingRepository.save(taskHousing);
				updateTaskHousing(taskHousing.getTaskid());
			}
		} catch (Exception e) {
			tracer.addTag("action.crawlerUserInfo.TIMEOUT", taskHousing.getTaskid());
			taskHousing.setPhase(HousingfundStatusCodeEnum.HOUSING_CRAWLER_USER_MSG_SUCCESS.getPhase());
			taskHousing.setPhase_status(HousingfundStatusCodeEnum.HOUSING_CRAWLER_USER_MSG_SUCCESS.getPhasestatus());
			taskHousing.setDescription(HousingfundStatusCodeEnum.HOUSING_CRAWLER_USER_MSG_SUCCESS.getDescription());
			taskHousing.setUserinfoStatus(201);
			taskHousingRepository.save(taskHousing);
			updateTaskHousing(taskHousing.getTaskid());
			e.printStackTrace();
		}
		
	}
	
	
	//流水 
	public void crawlerAccount(MessageLoginForHousing messageLoginForHousing, TaskHousing taskHousing) {
		try {
				WebParam<HousingFundZhuZhouAccount> webParam = housingFundZhuZhouParser.crawlerAccount(messageLoginForHousing,taskHousing);
				if(null != webParam)
				{
					if(null != webParam.getList())
					{
						tracer.addTag("action.crawlerAccount.SUCCESS", taskHousing.getTaskid());
						HousingFundZhuZhouHtml h = new HousingFundZhuZhouHtml();
						h.setHtml(webParam.getHtml());
						h.setTaskid(taskHousing.getTaskid());
						h.setType("crawlerAccount");
						housingFundZhuZhouRepositoryHtml.save(h);
						housingFundZhuZhouRepositoryAccount.saveAll(webParam.getList());
						taskHousing.setPhase(HousingfundStatusCodeEnum.HOUSING_CRAWLER_STATEMENT_MSG_SUCCESS.getPhase());
						taskHousing.setPhase_status(HousingfundStatusCodeEnum.HOUSING_CRAWLER_STATEMENT_MSG_SUCCESS.getPhasestatus());
						taskHousing.setDescription(HousingfundStatusCodeEnum.HOUSING_CRAWLER_STATEMENT_MSG_SUCCESS.getDescription());
						taskHousing.setPaymentStatus(200);
						taskHousingRepository.save(taskHousing);
						updateTaskHousing(taskHousing.getTaskid());
					}
					else
					{
						tracer.addTag("action.crawlerAccount.ERROR1", taskHousing.getTaskid());
						taskHousing.setPhase(HousingfundStatusCodeEnum.HOUSING_CRAWLER_STATEMENT_MSG_SUCCESS.getPhase());
						taskHousing.setPhase_status(HousingfundStatusCodeEnum.HOUSING_CRAWLER_STATEMENT_MSG_SUCCESS.getPhasestatus());
						taskHousing.setDescription(HousingfundStatusCodeEnum.HOUSING_CRAWLER_STATEMENT_MSG_SUCCESS.getDescription());
						taskHousing.setPaymentStatus(201);
						taskHousingRepository.save(taskHousing);
						updateTaskHousing(taskHousing.getTaskid());
					}
				}
				else
				{
					tracer.addTag("action.crawlerAccount.ERROR2", taskHousing.getTaskid());
					taskHousing.setPhase(HousingfundStatusCodeEnum.HOUSING_CRAWLER_STATEMENT_MSG_SUCCESS.getPhase());
					taskHousing.setPhase_status(HousingfundStatusCodeEnum.HOUSING_CRAWLER_STATEMENT_MSG_SUCCESS.getPhasestatus());
					taskHousing.setDescription(HousingfundStatusCodeEnum.HOUSING_CRAWLER_STATEMENT_MSG_SUCCESS.getDescription());
					taskHousing.setPaymentStatus(201);
					taskHousingRepository.save(taskHousing);
					updateTaskHousing(taskHousing.getTaskid());
				}
		} catch (Exception e) {
			tracer.addTag("action.crawlerUserInfo.TIMEOUT", taskHousing.getTaskid());
			taskHousing.setPhase(HousingfundStatusCodeEnum.HOUSING_CRAWLER_STATEMENT_MSG_SUCCESS.getPhase());
			taskHousing.setPhase_status(HousingfundStatusCodeEnum.HOUSING_CRAWLER_STATEMENT_MSG_SUCCESS.getPhasestatus());
			taskHousing.setDescription(HousingfundStatusCodeEnum.HOUSING_CRAWLER_STATEMENT_MSG_SUCCESS.getDescription());
			taskHousing.setPaymentStatus(201);
			taskHousingRepository.save(taskHousing);
			updateTaskHousing(taskHousing.getTaskid());
			e.printStackTrace();
		}
		
	}

}
