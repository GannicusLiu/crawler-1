package com.microservice.dao.repository.crawler.housing.zhoushan;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.microservice.dao.entity.crawler.housing.zhoushan.HousingZhouShanBase;

/**
 * @description:
 * @author: sln 
 * @date: 2017年9月29日 上午10:28:43 
 */
@Repository
public interface HousingZhouShanBaseRepository extends JpaRepository<HousingZhouShanBase, Long> {

}