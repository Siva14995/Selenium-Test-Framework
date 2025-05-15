package com.orangehrm.test;

import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import com.orangehrm.utilities.ApiUtility;
import com.orangehrm.utilities.ExtentManager;


import io.restassured.response.Response;

public class ApiTest {

	@Test
	public void verifyGetUserAPI() {

		SoftAssert softAssert = new SoftAssert();

		// Step 1: Define an Endpoint
		String endPoint = "https://jsonplaceholder.typicode.com/users/1";
		ExtentManager.logStep("API Endpoint: " + endPoint);

		// Step 2: Send GET Request
		ExtentManager.logStep("Sending GET Request to the API");
		Response response = ApiUtility.sendGetRequest(endPoint);

		// Step3 : Validate Status Code
		ExtentManager.logStep("Validating API Response Status Code");
		boolean isStatusCodeValid = ApiUtility.validateStatusCode(response, 200);
		softAssert.assertTrue(isStatusCodeValid, "Status Code is not as Expected");
		if (isStatusCodeValid) {
			ExtentManager.logStepValidationForAPI("Status Code Validation Passed!");
		} else {
			ExtentManager.logFailureAPI("Status Code Validation Failed!");
		}

		// Step4: Validate user name
		ExtentManager.logStep("Validating response body for username");
		String userName = ApiUtility.getJSONValue(response, "username");
		boolean isUserNameValid = "Bret".equals(userName);
		softAssert.assertTrue(isUserNameValid, "Username is not valid");
		if (isUserNameValid) {
			ExtentManager.logStepValidationForAPI("Username Validation Passed!");
		} else {
			ExtentManager.logFailureAPI("Username Validation Failed!");
		}

		// Step 5: Validate email
		ExtentManager.logStep("Validating response body for email");
		String userEmail = ApiUtility.getJSONValue(response, "email");
		boolean isEmailValid = "Sincere@april.biz".equals(userEmail);
		softAssert.assertTrue(isEmailValid, "Email is not valid");
		if (isEmailValid) {
			ExtentManager.logStepValidationForAPI("Username Validation Passed!");
		} else {
			ExtentManager.logFailureAPI("Username Validation Failed!");
		}
		softAssert.assertAll();

	}

}
