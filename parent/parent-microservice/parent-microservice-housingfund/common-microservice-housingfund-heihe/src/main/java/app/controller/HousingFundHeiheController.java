package app.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.crawler.housingfund.json.HousingfundStatusCodeEnum;
import com.crawler.housingfund.json.MessageLoginForHousing;
import com.crawler.mobile.json.ResultData;
import com.crawler.mobile.json.StatusCodeEnum;
import com.microservice.dao.entity.crawler.housing.basic.TaskHousing;

import app.service.HousingHeiheCrawlerService;

@RestController
@Configuration
@RequestMapping("/housing/heihe") 
public class HousingFundHeiheController extends HousingBasicController {

	public static final Logger log = LoggerFactory.getLogger(HousingFundHeiheController.class);
	@Autowired
	private HousingHeiheCrawlerService housingHeiheCrawlerService;
	
	@RequestMapping(value = "/crawler", method = RequestMethod.POST)
	public ResultData<TaskHousing> crawler(@RequestBody MessageLoginForHousing messageLoginForHousing) {
		
		ResultData<TaskHousing> result = new ResultData<TaskHousing>();
		TaskHousing taskHousing=housingHeiheCrawlerService.login(messageLoginForHousing);
		if(taskHousing.getPhase().indexOf(HousingfundStatusCodeEnum.HOUSING_LOGIN_SUCCESS.getPhase())!=-1&&
				taskHousing.getPhase_status().indexOf(HousingfundStatusCodeEnum.HOUSING_LOGIN_SUCCESS.getPhasestatus())!=-1){
			taskHousing=housingHeiheCrawlerService.getAllData(messageLoginForHousing);
		}
		result.setData(taskHousing);
		return result;
	}

}
