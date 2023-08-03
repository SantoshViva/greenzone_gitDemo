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

import com.vivatelecoms.greenzone.model.ChatDashboardDetailsModel;
import com.vivatelecoms.greenzone.model.ResponseDTO;
import com.vivatelecoms.greenzone.model.ResponseHeader;
import com.vivatelecoms.greenzone.utils.ChatUtils;

@CrossOrigin
@RestController
public class ChatDashboardDetails {
	
private static final Logger logger = LogManager.getLogger(IvrPromptDetailsController.class);
	
	@Autowired
	Environment env;
	
	@Autowired
	JdbcTemplate jdbcTemplate;	
	@RequestMapping(value = "/chatDashboardDetails", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> getchatDashboardDetails(@RequestParam("fromDate") String fromDate,@RequestParam("toDate") String toDate, @RequestParam("serviceId") String serviceId,HttpServletRequest req,
			HttpServletResponse res) {
		logger.info("chatDashboardDetails|fromDate="+fromDate+"|toDate="+toDate+"|serviceId="+serviceId);
		
		String chatDashboardQuery=ChatUtils.getMwomanDashboardQuery(env.getProperty("SQL48_SELECT_CHAT_SUMMARY"),fromDate,toDate);
		List<ChatDashboardDetailsModel> chatDashboardDetails = new ArrayList<ChatDashboardDetailsModel>();
		ResponseDTO<List<ChatDashboardDetailsModel>> response = new ResponseDTO<>();
		logger.info("query="+chatDashboardQuery);
		ResponseHeader addHeader = new ResponseHeader();
		try {
			List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(chatDashboardQuery);
			if(queryForList.isEmpty()) {
				addHeader.setCode(1);
				addHeader.setMessage("No record found");
			}else {
				addHeader.setCode(0);
				addHeader.setMessage("Success");
				for(Map<String, Object> row : queryForList)
				{
					 
					ChatDashboardDetailsModel chatDashboardDetail = new ChatDashboardDetailsModel();
					if(row.get("REPORT_DATE")!=null)
					{
						chatDashboardDetail.setDate(row.get("REPORT_DATE").toString());
					}
					if(row.get("TOTAL_TRAFFIC")!=null)
					{
						chatDashboardDetail.setTotalTraffic(row.get("TOTAL_TRAFFIC").toString());
					}
					if(row.get("TOTAL_UNIQUE_TRAFFIC")!=null)
					{
						chatDashboardDetail.setTotalUniqueTraffic(row.get("TOTAL_UNIQUE_TRAFFIC").toString());
					}
					if(row.get("TOTAL_BLACKLIST")!=null)
					{
						chatDashboardDetail.setTotalBlackListCount(row.get("TOTAL_BLACKLIST").toString());
					}
					if(row.get("TOTAL_SUBS")!=null)
					{
						chatDashboardDetail.setTotalSubs(row.get("TOTAL_SUBS").toString());
					}
					if(row.get("TOTAL_SUBS_SUCCESS")!=null)
					{
						chatDashboardDetail.setTotalSubsSuccess(row.get("TOTAL_SUBS_SUCCESS").toString());
					}
					if(row.get("TOTAL_SUBS_FAIL")!=null)
					{
						chatDashboardDetail.setTotalSubsFail(row.get("TOTAL_SUBS_FAIL").toString());
					}
					if(row.get("MOST_LIKE_MSISDN")!=null)
					{
						chatDashboardDetail.setMostLikeMsisdn(row.get("MOST_LIKE_MSISDN").toString());
					}
					if(row.get("MOST_JOINED_GROUP")!=null)
					{
						chatDashboardDetail.setMostJoinedGroup(row.get("MOST_JOINED_GROUP").toString());
					}
					chatDashboardDetails.add(chatDashboardDetail);
				}
				
			}
			
		}catch(Exception e) {
			addHeader.setCode(2);
			addHeader.setMessage("DB Error");
			e.printStackTrace();
			logger.error("Exception="+e);			
		}
		response.setHeader(addHeader);
		response.setBody(chatDashboardDetails);
		logger.info("Response="+response.getHeader()+"|body="+response.getBody());
		return new ResponseEntity<>(response, HttpStatus.OK);
		
	}

}
