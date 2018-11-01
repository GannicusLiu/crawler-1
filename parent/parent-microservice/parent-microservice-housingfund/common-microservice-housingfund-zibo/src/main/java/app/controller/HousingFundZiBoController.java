package app.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.crawler.housingfund.json.MessageLoginForHousing;
import com.crawler.mobile.json.ResultData;
import com.crawler.mobile.json.StatusCodeEnum;
import com.microservice.dao.entity.crawler.housing.basic.TaskHousing;

import app.service.HousingZiBoService;
//import app.service.HousingHaiNanFutureService;

@RestController
@Configuration
@RequestMapping("/housing/zibo") 
public class HousingFundZiBoController extends HousingBasicController {

	@Autowired
	private HousingZiBoService housingZiBoService;
	
	
	@RequestMapping(value = "/crawler", method = RequestMethod.POST)
	public ResultData<TaskHousing> crawler(@RequestBody MessageLoginForHousing messageLoginForHousing) {
		TaskHousing taskHousing = findTaskHousing(messageLoginForHousing.getTask_id());

		ResultData<TaskHousing> result = new ResultData<TaskHousing>();

		
		taskHousing = housingZiBoService.getAllData(messageLoginForHousing);
		
		result.setData(taskHousing);
		return result;
	}

}
