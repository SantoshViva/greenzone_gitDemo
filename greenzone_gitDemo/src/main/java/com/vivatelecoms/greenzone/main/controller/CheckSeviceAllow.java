package com.vivatelecoms.greenzone.main.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.vivatelecoms.greenzone.model.BlackListDetailsModel;
import com.vivatelecoms.greenzone.model.ChatDashboardDetailsModel;
import com.vivatelecoms.greenzone.model.ResponseDTO;
import com.vivatelecoms.greenzone.model.ResponseHeader;
import com.vivatelecoms.greenzone.utils.ChatUtils;

@CrossOrigin
@RestController
public class CheckSeviceAllow {
	
private static final Logger logger = LogManager.getLogger(CheckSeviceAllow.class);
	
	@Autowired
	Environment env;
	
	@Autowired
	JdbcTemplate jdbcTemplate;	
	@RequestMapping(value = "/checkServiceAllow", method = RequestMethod.GET)
	@ResponseBody
	public String checkServiceAllow(@RequestParam("aparty") String aparty,@RequestParam("serviceId") String serviceId,@RequestParam("operation") String operation,HttpServletRequest req,
			HttpServletResponse res) {
		logger.info("checkServiceAllow|aparty="+aparty+"|serviceId="+serviceId+"|operation="+operation);
		
		String blackQuery = ChatUtils.getServiceAllowQuery(env.getProperty("SQL48_SELECT_SERVICE_ALLOW"), aparty,serviceId);
		//logger.info("blackQuery="+blackQuery);
		
		String responseString = new String();
		
		String dbError ="N";
		String allow = "N";
		
		if(operation.equalsIgnoreCase("select"))
		{
			logger.trace("blackQuery=" + blackQuery);
			try {
				List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(blackQuery);

				if (queryForList.isEmpty()) 
				{
					logger.info("No Record Found in SQL|aparty=" + aparty);
					
							

				} else {
					
					for (Map<String, Object> row : queryForList) {

						if (row.get("subscriber_id") == null || row.get("subscriber_id").toString().isEmpty()) {
					
						} else {
							allow = "Y";
						}

					}
				}
			} catch (Exception e) 
			{
				logger.error("SQL Exception" + e + "Query=" + blackQuery);
				logger.error("No Row Found");
				dbError ="Y";
			}
		}	
		responseString = responseString.concat("RES.dbError=\'"+dbError+"\';");
		responseString = responseString.concat("RES.allow=\'"+allow+"\';");
		return responseString;
	}

}
