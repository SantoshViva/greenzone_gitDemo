package com.santosh.greenzone.main.controller;

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

import com.santosh.greenzone.model.BlackListDetailsModel;
import com.santosh.greenzone.model.ChatDashboardDetailsModel;
import com.santosh.greenzone.model.ChatMessagesDetails;
import com.santosh.greenzone.model.ResponseDTO;
import com.santosh.greenzone.model.ResponseHeader;
import com.santosh.greenzone.utils.ChatUtils;

@CrossOrigin
@RestController
public class ApprovedChatMessage {
	
private static final Logger logger = LogManager.getLogger(ApprovedChatMessage.class);
	
	@Autowired
	Environment env;
	
	@Autowired
	JdbcTemplate jdbcTemplate;	
	@RequestMapping(value = "/approvedChatMessage", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> approveChatMessage(@RequestParam("messageId") String messageId,@RequestParam(required =false) String status,
			@RequestParam(required =false) String userId,HttpServletRequest req,HttpServletResponse res) {
		
		if(status == null || status.isEmpty())
		{
			status="A";
		}
		if(userId == null || userId.isEmpty())
		{
			userId="GUI";
		}
		logger.info("approvedChatMessage|messageId="+messageId+"|status="+status+"|userId="+userId);
		ResponseHeader addHeader = new ResponseHeader();		
		ResponseDTO<List<ChatDashboardDetailsModel>> response = new ResponseDTO<>();
		String approvedChatMessageQuery = ChatUtils.getApprovalChatQuery(env.getProperty("SQL48_APPROVED_CHAT_MESSAGE"), messageId,status,userId);
		//logger.info("deleteChatMessageQuery="+deleteChatMessageQuery);
		try {
			int updateResult=jdbcTemplate.update(approvedChatMessageQuery);
			if(updateResult<=0)
			{
				logger.info("Fail to update chat message |result="+updateResult);
				addHeader.setCode(1);
				addHeader.setMessage("No record in DB");
			}else {
				logger.info("Successful update chat message|result="+updateResult);
				addHeader.setCode(0);
				addHeader.setMessage("Successful updated");
				
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
