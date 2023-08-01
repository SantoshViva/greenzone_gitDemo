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

import com.santosh.greenzone.model.MwomanDashboardDetailsModel;
import com.santosh.greenzone.model.ResponseDTO;
import com.santosh.greenzone.model.ResponseHeader;
import com.santosh.greenzone.utils.ChatUtils;

@CrossOrigin
@RestController
public class MwomanDashboardDetails {
	
private static final Logger logger = LogManager.getLogger(IvrPromptDetailsController.class);
	
	@Autowired
	Environment env;
	
	@Autowired
	JdbcTemplate jdbcTemplate;	
	@RequestMapping(value = "/mWomanDashboardDetails", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> getMwomanDashboardDetails(@RequestParam("fromDate") String fromDate,@RequestParam("toDate") String toDate, HttpServletRequest req,
			HttpServletResponse res) {
		logger.info("getMwomanDashboardDetails|fromDate="+fromDate+"|toDate="+toDate);
		
		String mwomanDashboardQuery=ChatUtils.getMwomanDashboardQuery(env.getProperty("SQL46_SELECT_MWOMAN_SUMMARY"),fromDate,toDate);
		List<MwomanDashboardDetailsModel> mWomanDashboardDetails = new ArrayList<MwomanDashboardDetailsModel>();
		ResponseDTO<List<MwomanDashboardDetailsModel>> response = new ResponseDTO<>();
		ResponseHeader addHeader = new ResponseHeader();
		try {
			List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(mwomanDashboardQuery);
			if(queryForList.isEmpty()) {
				addHeader.setCode(1);
				addHeader.setMessage("No record found");
			}else {
				addHeader.setCode(0);
				addHeader.setMessage("Success");
				for(Map<String, Object> row : queryForList)
				{
					 
					MwomanDashboardDetailsModel mWomanDashboardDetail = new MwomanDashboardDetailsModel();
					if(row.get("REPORT_DATE")!=null)
					{
						mWomanDashboardDetail.setDate(row.get("REPORT_DATE").toString());
					}
					if(row.get("TOTAL_TRAFFIC")!=null)
					{
						mWomanDashboardDetail.setTotalTraffic(row.get("TOTAL_TRAFFIC").toString());
					}
					if(row.get("TOTAL_UNIQUE_TRAFFIC")!=null)
					{
						mWomanDashboardDetail.setTotalUniqueTraffic(row.get("TOTAL_UNIQUE_TRAFFIC").toString());
					}
					if(row.get("MOST_LISTEN_CONTENT_NAME")!=null)
					{
						mWomanDashboardDetail.setMostListenContentName(row.get("MOST_LISTEN_CONTENT_NAME").toString());
					}
					if(row.get("MOST_LISTEN_CONTENT_CNT")!=null)
					{
						mWomanDashboardDetail.setMostListenContentCount(row.get("MOST_LISTEN_CONTENT_CNT").toString());
					}
					if(row.get("LEAST_LISTEN_CONTENT_NAME")!=null)
					{
						mWomanDashboardDetail.setLeastListenContentName(row.get("LEAST_LISTEN_CONTENT_NAME").toString());
					}
					if(row.get("LEAST_LISTEN_CONTENT_CNT")!=null)
					{
						mWomanDashboardDetail.setLeastListenContentCount(row.get("LEAST_LISTEN_CONTENT_CNT").toString());
					}
					if(row.get("SUB_COUNT")!=null)
					{
						mWomanDashboardDetail.setSubscriptionCount(row.get("SUB_COUNT").toString());
					}
					mWomanDashboardDetails.add(mWomanDashboardDetail);
				}
				
			}
			
		}catch(Exception e) {
			addHeader.setCode(2);
			addHeader.setMessage("DB Error");
			e.printStackTrace();
			logger.error("Exception="+e);			
		}
		response.setHeader(addHeader);
		response.setBody(mWomanDashboardDetails);
		logger.info("Response="+response.getHeader()+"|body="+response.getBody());
		return new ResponseEntity<>(response, HttpStatus.OK);
		
	}

}
