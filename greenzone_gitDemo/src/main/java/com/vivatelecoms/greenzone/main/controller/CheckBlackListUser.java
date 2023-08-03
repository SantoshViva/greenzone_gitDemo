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
public class CheckBlackListUser {
	
private static final Logger logger = LogManager.getLogger(CheckBlackListUser.class);
	
	@Autowired
	Environment env;
	
	@Autowired
	JdbcTemplate jdbcTemplate;	
	@RequestMapping(value = "/checkBlackListUser", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> checkBlackListUser(@RequestParam("msisdn") String aparty,@RequestParam("serviceId") String serviceId,@RequestParam("operation") String operation,
			@RequestParam(required =false) String userId,HttpServletRequest req,HttpServletResponse res) {
		
		if(userId == null ||userId.isEmpty())
		{
			userId="GUI";
		}	
		logger.info("checkBlackListUser|aparty="+aparty+"|serviceId="+serviceId+"|operation="+operation+"|userId="+userId);
		
		String blackQuery = ChatUtils.getQuery(env.getProperty("SQL48_SELECT_BACKLIST_NUMBER"), aparty);
		//logger.info("blackQuery="+blackQuery);
		String numberBlackList="N";
		String dbUserId="SYSTEM";
		ResponseDTO<List<ChatDashboardDetailsModel>> response = new ResponseDTO<>();
		ResponseHeader addHeader = new ResponseHeader();
		if(operation.equalsIgnoreCase("select"))
		{
			logger.trace("blackQuery="+blackQuery);
		try {
			List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(blackQuery);
			
			if(queryForList.isEmpty())
			{
				logger.info("No Record Found in SQL|aparty="+aparty);
				addHeader.setCode(0);
				addHeader.setMessage("Number is not in Blacklist");
				numberBlackList="N";
				
			}else 
			{
				addHeader.setCode(1);
				addHeader.setMessage("Success");
				for (Map<String, Object> row : queryForList) {				
				
					if(row.get("subscriber_id")==null ||row.get("subscriber_id").toString().isEmpty())
					{
						
					}else
					{	
						numberBlackList="Y";
						addHeader.setMessage("Number is Blacklist");
						
					}
					if(row.get("user_id")==null ||row.get("user_id").toString().isEmpty())
					{
						dbUserId="SYSTEM";
					}else
					{
						dbUserId=row.get("user_id").toString();
						
					}
					
				}
				
			}
		} catch (Exception e) {
			logger.error("SQL Exception" + e +"Query="+blackQuery);
			logger.error("No Row Found");
			addHeader.setCode(2);
			addHeader.setMessage("DB Error");
			numberBlackList="N";
			e.printStackTrace();
		}
		}
		if(operation.equalsIgnoreCase("delete"))
		{
			String deleteBlackListQuery = ChatUtils.getQuery(env.getProperty("SQL48_DELETE_BACKLIST_NUMBER"), aparty);
			logger.info("deleteBlackList="+deleteBlackListQuery);
			try {
				int updateResult=jdbcTemplate.update(deleteBlackListQuery);
				if(updateResult<=0)
				{
					logger.info("Fail to delete blacklist |result="+updateResult);
					addHeader.setCode(1);
					addHeader.setMessage("No record in DB");
				}else {
					logger.info("Successful delete blacklist number|result="+updateResult);
					addHeader.setCode(0);
					addHeader.setMessage("Successful Deleted");
					
				}
				
			}catch(Exception e)
			{
				logger.error("Exception occurred="+e);
				addHeader.setCode(2);
				addHeader.setMessage("DB Error");
				
			}
		}else if(operation.equalsIgnoreCase("add")||operation.equalsIgnoreCase("insert"))
		{
			String insertBlackListNumberQuery = ChatUtils.getBlackListChatQuery(env.getProperty("SQL48_INSERT_BACKLIST_NUMBER"), aparty,userId);
			
			//logger.info("insertBlackListNumberQuery="+insertBlackListNumberQuery);
			String insertFlag="N";
			try {
				int insertQueryResult= jdbcTemplate.update(insertBlackListNumberQuery);
				if(insertQueryResult <= 0)
				{
					addHeader.setCode(1);
					addHeader.setMessage("Fail To Insert");
					logger.error("Failed to insert|query="+insertQueryResult);
				}else {
					addHeader.setCode(0);
					addHeader.setMessage("Successful Inserted");
					insertFlag="Y";
					logger.info("Successfully to data insert in  backlist|resultChangesRow="+insertQueryResult);
					
				}
				if(insertFlag.equalsIgnoreCase("Y"))
				{
					String updateChatProfile = ChatUtils.getBlackListChatQuery(env.getProperty("SQL48_CHAT_PROFILE_UPDATE_BLACKLIST"), aparty,userId);
					int updateResult= jdbcTemplate.update(updateChatProfile);
					if(updateResult<=0)
					{
						logger.info("Fail to update chat profile for blacklist |result="+updateResult);
					}else
					{
						logger.info("Successful update chat profile for blacklist|result="+updateResult);
					}
				}
				
			}catch (DuplicateKeyException dke ) {
				logger.error("DB Error="+dke.getRootCause());
				addHeader.setCode(2);
				addHeader.setMessage("Already Insert");
			}catch(Exception e) {
				logger.error("Exception occurred|Query="+insertBlackListNumberQuery+"|SQL exception="+e);
				logger.error("Exception occurred|Error="+e.getCause());
				//e.printStackTrace();
				addHeader.setCode(3);
				addHeader.setMessage("DB Error");
			}
		}
		response.setHeader(addHeader);
		
		logger.info("Response="+response.getHeader());
		return new ResponseEntity<>(response, HttpStatus.OK);
		
	}
	
	
	@RequestMapping(value = "/fetchBlackListUserDetails", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> operation(@RequestParam("serviceId") String serviceId,@RequestParam("startIndex") String startIndex,@RequestParam("endIndex") String endIndex,
			HttpServletRequest req,HttpServletResponse res) {
		
		if(startIndex==null ||startIndex.toString().isEmpty())
		{
			startIndex="0";
		}
		if(endIndex==null || endIndex.toString().isEmpty())
		{
			endIndex="10";
		}
		logger.info("fetchBlackListUserDetails|serviceId="+serviceId+"|startIndex="+startIndex+"|endIndex="+endIndex);
		String selectBacklistQuery=ChatUtils.getMwomanDashboardQuery(env.getProperty("SQL48_SELECT_ALL_BACKLIST_NUMBER"),startIndex,endIndex);
		List<BlackListDetailsModel> blackListDetails = new ArrayList<BlackListDetailsModel>();
		ResponseDTO<List<BlackListDetailsModel>> response = new ResponseDTO<>();
		logger.info("query="+selectBacklistQuery);
		ResponseHeader addHeader = new ResponseHeader();
		try {
			List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(selectBacklistQuery);
			if(queryForList.isEmpty()) {
				addHeader.setCode(1);
				addHeader.setMessage("No record found");
			}else {
				addHeader.setCode(0);
				addHeader.setMessage("Success");
				for(Map<String, Object> row : queryForList)
				{
		
					BlackListDetailsModel blackListDetail = new BlackListDetailsModel();
					if(row.get("subscriber_id")!=null)
					{
						blackListDetail.setMsisdn(row.get("subscriber_id").toString());
					}
					if(row.get("user_id")!=null)
					{
						blackListDetail.setUserId(row.get("user_id").toString());
					}else {
						blackListDetail.setUserId("SYSTEM");
					}
					if(row.get("MODIFY_DATE")!=null)
					{
						blackListDetail.setDate(row.get("MODIFY_DATE").toString());
					}
					blackListDetails.add(blackListDetail);
				}
				
			}
			
		}catch(Exception e) {
			addHeader.setCode(2);
			addHeader.setMessage("DB Error");
			e.printStackTrace();
			logger.error("Exception="+e);			
		}
				
		response.setHeader(addHeader);
		response.setBody(blackListDetails);
		
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
