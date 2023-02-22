package com.tmp.tmpsurvey.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import com.google.api.services.forms.v1.model.Form;
import com.google.api.services.forms.v1.model.ListFormResponsesResponse;
import com.sun.istack.internal.logging.Logger;
import com.tmp.tmpsurvey.dto.GoogleFormDto;
import com.tmp.tmpsurvey.services.TmpGoogleFormService;

@Controller
@RequestMapping("tmp/google-forms")
public class TmpSurveyController {
	
	Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	TmpGoogleFormService tmpGFormService;
	
	@PostMapping("/surveys")
	public ResponseEntity<?> createSurvey(@RequestBody GoogleFormDto gForm) {
		try {
			HttpStatus status = HttpStatus.OK;
			GoogleFormDto createdGForm = null;
			if(gForm.getName()!=null && !gForm.getName().isEmpty()) {
				createdGForm = this.tmpGFormService.createNewForm(gForm.getName());
				status = HttpStatus.CREATED;
			}
			return new ResponseEntity<GoogleFormDto>(createdGForm, status);
		}catch(Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.OK);
		}		
	}

	@GetMapping("/surveys/{formId}")
	public ResponseEntity<?> getForm(@PathVariable("formId") String formId){
		try {
			log.info("getting metadata form: " + formId);
			Form form = this.tmpGFormService.getForm(formId);
			return new ResponseEntity<Form>(form, HttpStatus.OK);
		}catch(Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.OK);
		}		
	}
	
	@GetMapping("/surveys/{formId}/results")
	public ResponseEntity<?> getResults(@PathVariable("formId") String formId) {
		try {
						
			log.info("getting form responses: " + formId);			
			ListFormResponsesResponse response = this.tmpGFormService.getResults(formId);			
			log.info(response.toPrettyString());
			return new ResponseEntity<ListFormResponsesResponse>(response, HttpStatus.OK);

		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.OK);
		}
		

	}

}
