/**
 * 
 */
package com.findly.common.assessment;

import java.util.List;

import com.findly.common.Response;

/**
 * Interface describes the contract to provide assessment service
 * @author Dhafir Moussa
 *
 */
public interface AssessmentServiceProvider {

	/**
	 * Request to start assessment.
	 * This is a synch operation
	 * @param candidateId
	 * @return
	 */
	Response<List<String>> startAssessment(String candidateId) throws Exception;
}
