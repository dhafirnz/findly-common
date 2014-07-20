/**
 * 
 */
package com.findly.common.jms;

/**
 * JMS related constants.
 * 
 * @author Dhafir Moussa
 * 
 */
public class JmsConstants {
	
	/**
	 * JMS local (in VM) Connection factory
	 */
	public static final String CONNECTION_FACTORY = "java:/ConnectionFactory";
	
	/**
	 * Queue used to exchange candidate request for assessment.	 
	 */
	public static final String ASSESSMENT_QUEUE = "queue/assessmentQueue";
	
	
	/**
	 * Queue used to place events.
	 */
	public static final String EVENT_QUEUE = "queue/eventQueue";

}
