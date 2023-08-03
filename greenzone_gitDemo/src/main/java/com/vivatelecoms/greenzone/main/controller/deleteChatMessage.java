package com.vivatelecoms.greenzone.main.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
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
import com.vivatelecoms.greenzone.model.ChatMessagesDetails;
import com.vivatelecoms.greenzone.model.ResponseDTO;
import com.vivatelecoms.greenzone.model.ResponseHeader;
import com.vivatelecoms.greenzone.utils.ChatUtils;

@CrossOrigin
@RestController
public class deleteChatMessage {
	
private static final Logger logger = LogManager.getLogger(deleteChatMessage.class);
	
	@Autowired
	Environment env;
	
	@Autowired
	JdbcTemplate jdbcTemplate;	
	@RequestMapping(value = "/deleteChatMessage", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> deleteChatMessage(@RequestParam("messageId") String messageId,
			HttpServletRequest req,HttpServletResponse res) {
		
		logger.info("deleteChatMessage|messageId="+messageId);
		ResponseHeader addHeader = new ResponseHeader();		
		ResponseDTO<List<ChatDashboardDetailsModel>> response = new ResponseDTO<>();
		String deleteChatMessageQuery = ChatUtils.getQuery(env.getProperty("SQL48_DELETE_CHAT_MESSAGE"), messageId);
		//logger.info("deleteChatMessageQuery="+deleteChatMessageQuery);
		try {
			int updateResult=jdbcTemplate.update(deleteChatMessageQuery);
			if(updateResult<=0)
			{
				logger.info("Fail to delete chat message |result="+updateResult);
				addHeader.setCode(1);
				addHeader.setMessage("No record in DB");
			}else {
				logger.info("Successful delete chat message|result="+updateResult);
				addHeader.setCode(0);
				addHeader.setMessage("Successful Deleted");
				
			}
			
		}catch(Exception e)
		{
			logger.error("Exception occurred="+e);
			addHeader.setCode(2);
			addHeader.setMessage("DB Error");
			
		}
		response.setHeader(addHeader);
		
		logger.info("Response="+response.getHeader());
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
