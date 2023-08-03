package com.vivatelecoms.greenzone.main.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

import com.vivatelecoms.greenzone.model.IvrPromptDetailsModel;
import com.vivatelecoms.greenzone.model.ResponseDTO;
import com.vivatelecoms.greenzone.model.ResponseHeader;
import com.vivatelecoms.greenzone.utils.ChatUtils;



@CrossOrigin
@RestController
public class IvrPromptDetailsController {
	
	private static final Logger logger = LogManager.getLogger(IvrPromptDetailsController.class);
	
	@Autowired
	Environment env;
	
	@Autowired
	JdbcTemplate jdbcTemplate;	
	@RequestMapping(value = "/ivrPromptDetails", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> getIvrPromptDetails(@RequestParam("serviceName") String serviceName, HttpServletRequest req,
			HttpServletResponse res)
	{
		logger.info("serviceName="+serviceName);
		String selectPromptDetailsQuery= ChatUtils.getIvrPromptDetailsQuery(env.getProperty("SQL45_SELECT_IVR_PROMPT"),serviceName);
		
		logger.trace("final Query="+selectPromptDetailsQuery);
		
		List<IvrPromptDetailsModel> ivrPromptDetails = new ArrayList<IvrPromptDetailsModel>();
		ResponseDTO<List<IvrPromptDetailsModel>> response = new ResponseDTO<>();
		ResponseHeader addHeader = new ResponseHeader();
		try {
			List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(selectPromptDetailsQuery);
			if(queryForList.isEmpty()) {
				addHeader.setCode(1);
				addHeader.setMessage("No record found");
			}else {
				addHeader.setCode(0);
				addHeader.setMessage("Success");
				for(Map<String, Object> row : queryForList)
				{
					IvrPromptDetailsModel ivrPromptDetail= new IvrPromptDetailsModel();
					if(row.get("id")!=null)
					{
						ivrPromptDetail.setPromptId(row.get("id").toString());
					}
					if(row.get("prompt_name")!=null)
					{
						ivrPromptDetail.setPromptName(row.get("prompt_name").toString());
					}
					if(row.get("prompt_desc")!=null)
					{
						ivrPromptDetail.setPromptDesc(row.get("prompt_desc").toString());
					}
					if(row.get("prompt_path")!=null)
					{
						ivrPromptDetail.setPromptPath(row.get("prompt_path").toString());
					}
					ivrPromptDetails.add(ivrPromptDetail);
				}
				
			}
			
			
		}catch(Exception e) {
			addHeader.setCode(2);
			addHeader.setMessage("DB Error");
			e.printStackTrace();
			logger.error("Exception="+e);			
		}
		
		response.setHeader(addHeader);
		response.setBody(ivrPromptDetails);
		logger.info("Response="+response.getHeader()+"|body="+response.getBody());
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
