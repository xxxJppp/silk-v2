package com.spark.bitrade.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spark.bitrade.service.CommisionService;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author xiaoxianming
 * @since 2019-12-02
 */
@RestController
@RequestMapping("v2/member")
public class BenefitsDistributeController extends ApiController{
	
	@Autowired
	private CommisionService commisionService;
	
	
	@PostMapping("/benefits/distribute")
	public void distributeMemberBenefits() {
		
		this.commisionService.distribute();
		
	}
	
	
}
