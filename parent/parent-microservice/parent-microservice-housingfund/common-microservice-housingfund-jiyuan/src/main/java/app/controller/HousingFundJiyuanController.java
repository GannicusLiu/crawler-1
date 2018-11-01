package app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crawler.housingfund.json.MessageLoginForHousing;
import com.crawler.mobile.json.ResultData;
import com.microservice.dao.entity.crawler.housing.basic.TaskHousing;

import app.commontracerlog.TracerLog;
import app.service.HousingFundJiyuanService;


@RestController
@Configuration
@RequestMapping("/housing/jiyuan") 
public class HousingFundJiyuanController extends HousingBasicController{

	@Autowired
	private HousingFundJiyuanService housingFundJiyuanService;
	
	@Autowired
	private TracerLog tracer;
	 
	/**
	 * 登录
	 * @param insuranceRequestParameters
	 * @return
	 */
	@PostMapping(value="/login")
	public  ResultData<TaskHousing> login(@RequestBody MessageLoginForHousing messageLoginForHousing){
		
		tracer.qryKeyValue("parser.crawler.taskid",messageLoginForHousing.getTask_id());
		tracer.addTag("parser.crawler.auth",messageLoginForHousing.getNum());
		try {
			housingFundJiyuanService.getAllData(messageLoginForHousing);
		} catch (Exception e) {
			tracer.addTag("HousingFundJiyuanController.login:" , messageLoginForHousing.getTask_id()+"---ERROR:"+e.toString());
			e.printStackTrace();
		}
		return null;
		
		
	}
	
}
