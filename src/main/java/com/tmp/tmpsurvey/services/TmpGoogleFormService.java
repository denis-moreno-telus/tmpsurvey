package com.tmp.tmpsurvey.services;

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

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Objects;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class TmpGoogleFormService {

	Logger log = Logger.getLogger(this.getClass());
	
	@Value("classpath:keys/credentials.json")
	private Resource resource;

	@Value("${tmpconf.surveys.sharedEmail}")
	private String email;

	private static final String APPLICATION_NAME = "AppTestOnerom";
	private Drive driveService;
	private Forms formsService;
	
	public TmpGoogleFormService() throws GeneralSecurityException, IOException {
		this.setApiServices();
	}

	public GoogleFormDto createNewForm(String surveyName) throws IOException, GeneralSecurityException {
		String token = this.getToken();
		Form form = new Form();
		form.setInfo(new Info());
		form.getInfo().setTitle(surveyName);
		form.getInfo().setDocumentTitle(surveyName);
				
		form = formsService.forms().create(form).setAccessToken(token).execute();
		log.info(form.getFormId());
		this.shareForm(form.getFormId(), token);
		GoogleFormDto gformDto = new GoogleFormDto(form.getFormId(), surveyName);
		return gformDto;
	}

	private void setApiServices() throws GeneralSecurityException, IOException {
		JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

		this.driveService = new Drive.Builder(GoogleNetHttpTransport.newTrustedTransport(), jsonFactory, null)
				.setApplicationName(APPLICATION_NAME).build();

		this.formsService = new Forms.Builder(GoogleNetHttpTransport.newTrustedTransport(), jsonFactory, null)
				.setApplicationName(APPLICATION_NAME).build();
	}

	private String getToken() throws IOException {

		log.info(this.resource.getInputStream().toString());
		GoogleCredentials credential = GoogleCredentials
				.fromStream(Objects.requireNonNull(this.resource.getInputStream())).createScoped(FormsScopes.all());
		String token = credential.getAccessToken() != null ? credential.getAccessToken().getTokenValue()
				: credential.refreshAccessToken().getTokenValue();
		return token;
	}

	public boolean shareForm(String formId, String token) throws GeneralSecurityException, IOException {

		PermissionList list = driveService.permissions().list(formId).setOauthToken(token).execute();
		Stream<Permission> permissions = list.getPermissions().stream().filter((it) -> it.getRole().equals("writer"));
		if (permissions.findAny().empty() != null) {
			Permission body = new Permission();
			body.setRole("writer");
			body.setType("user");
			body.setEmailAddress(this.email);
			driveService.permissions().create(formId, body).setOauthToken(token).execute();
			
			return true;
		}
		return false;
	}
	
	public ListFormResponsesResponse getResults(String formId) throws IOException {
		String token = this.getToken();
		ListFormResponsesResponse response = this.formsService.forms().responses()
				.list(formId).setOauthToken(token).execute();
		log.info(response.toPrettyString());
		return response;
	}
	
	public Form getForm(String formId) throws IOException {
		String token = this.getToken();
		Form form = this.formsService.forms().get(formId).setOauthToken(token).execute();
		return form;
	}

}
