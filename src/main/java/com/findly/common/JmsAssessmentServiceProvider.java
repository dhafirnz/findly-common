package com.findly.common;

import java.util.ArrayList;
import java.util.List;

import com.findly.common.assessment.AssessmentServiceProvider;
import com.findly.common.jms.JmsConstants;
import com.findly.common.jms.JmsHelper;

public class JmsAssessmentServiceProvider implements AssessmentServiceProvider {

	@Override
	public Response<List<String>> startAssessment(String candidateId)
			throws Exception {

		JmsHelper.INSATNCE.send(JmsConstants.ASSESSMENT_QUEUE, candidateId);
		List<String> payLoad = new ArrayList<String>();
		payLoad.add("Message Sent");
		Response<List<String>> response = new Response<List<String>>();
		response.setData(payLoad);
		return response;
	}

	private void log(String msg) {
		System.out.println(msg);
	}
}