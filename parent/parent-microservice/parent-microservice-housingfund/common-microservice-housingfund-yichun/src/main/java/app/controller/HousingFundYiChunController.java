package app.controller;

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

import app.commontracerlog.TracerLog;
import app.service.HousingFundYiChunService;


@RestController  
@Configuration
@RequestMapping("/housing/yichun")
public class HousingFundYiChunController extends HousingBasicController{
	@Autowired
	private TracerLog tracer;
	@Autowired
	private HousingFundYiChunService housingFundYiChunService;
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResultData<TaskHousing> housingFundLogin(@RequestBody MessageLoginForHousing messageLoginForHousing) throws Exception {
		TaskHousing taskHousing = findTaskHousing(messageLoginForHousing.getTask_id());
		tracer.addTag("parser.housingFund.yichun.login.start",messageLoginForHousing.getTask_id());
		ResultData<TaskHousing> result = new ResultData<TaskHousing>();

		taskHousing.setPhase(HousingfundStatusCodeEnum.HOUSING_LOGIN_LOADING.getPhase());
		taskHousing.setPhase_status(HousingfundStatusCodeEnum.HOUSING_LOGIN_LOADING.getPhasestatus());
		taskHousing.setDescription(HousingfundStatusCodeEnum.HOUSING_LOGIN_LOADING.getDescription());
		taskHousing.setError_code(HousingfundStatusCodeEnum.HOUSING_LOGIN_LOADING.getError_code());
		save(taskHousing);
//		
		housingFundYiChunService.login(messageLoginForHousing, taskHousing);
		
		result.setData(taskHousing);
		return result;
		
		
	}
	
	
}
