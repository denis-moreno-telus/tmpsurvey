package com.tmp.tmpsurvey.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.Permission;
import com.google.api.services.drive.model.PermissionList;
import com.google.api.services.forms.v1.Forms;
import com.google.api.services.forms.v1.FormsScopes;
import com.google.api.services.forms.v1.model.Form;
import com.google.api.services.forms.v1.model.Info;
import com.google.api.services.forms.v1.model.ListFormResponsesResponse;
import com.google.auth.oauth2.GoogleCredentials;
import com.sun.istack.internal.logging.Logger;
import com.tmp.tmpsurvey.dto.GoogleFormDto;
import com.tmp.tmpsurvey.services.TmpGoogleFormService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.Objects;

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
			//15O5AdvxAEAG0vLbARg4f-cQPZajLX0Gi3xOvIS3Qk4w
			//1oF-L0oDWJq88kzoSdS39JfzxBCW9_i8BPLMBb7pcnTI				
			log.info("getting form responses: " + formId);			
			ListFormResponsesResponse response = this.tmpGFormService.getResults(formId);			
			log.info(response.toPrettyString());
			return new ResponseEntity<ListFormResponsesResponse>(response, HttpStatus.OK);

		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.OK);
		}
		

	}

}
