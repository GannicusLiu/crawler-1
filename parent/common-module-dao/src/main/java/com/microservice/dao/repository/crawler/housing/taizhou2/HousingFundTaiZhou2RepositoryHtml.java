package com.microservice.dao.repository.crawler.housing.taizhou2;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.microservice.dao.entity.crawler.housing.taizhou2.HousingFundTaiZhou2Account;
import com.microservice.dao.entity.crawler.housing.taizhou2.HousingFundTaiZhou2Html;

@Repository
public interface HousingFundTaiZhou2RepositoryHtml extends JpaRepository<HousingFundTaiZhou2Html,Long>{

}
