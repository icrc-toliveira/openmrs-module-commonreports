/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.mksreports.definition.data.evaluator;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.VisitService;
import org.openmrs.module.mksreports.definition.data.PersonNameAndAttributesDataDefinition;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonAttributeDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredNameDataDefinition;
import org.openmrs.module.reporting.data.person.service.PersonDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

@org.springframework.test.context.ContextConfiguration(locations = { "classpath:moduleApplicationContext.xml" }, inheritLocations = true)
public class PersonNameAndAttributesDataEvaluatorTest extends BaseModuleContextSensitiveTest {
	
	protected static final String XML_DATASET_PATH = "";
	
	protected static final String XML_REPORT_TEST_DATASET = "reportingTestDataset.xml";
	
	@Autowired
	VisitService visitService;
	
	@Autowired
	PersonService personService;
	
	@Autowired
	EncounterService encounterService;
	
	@Autowired
	PatientService patientService;
	
	@Autowired
	PersonDataService personDataService;
	
	@Autowired
	ConceptService conceptService;
	
	@Autowired
	ObsService obsService;
	
	/**
	 * Run this before each unit test in this class. The "@Before" method in
	 * {@link BaseContextSensitiveTest} is run right before this method.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet(XML_DATASET_PATH + XML_REPORT_TEST_DATASET);
	}
	
	/**
	 * @see PersonNameAndAttributesDataEvaluator#evaluate(PersonDataDefinition,EvaluationContext)
	 * @verifies returns the patient's name followed by the attributes passed
	 */
	@Test
	public void evaluate_shouldCalcuateObservations() throws Exception {
		
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("6"));
		
		PersonNameAndAttributesDataDefinition nameDD = new PersonNameAndAttributesDataDefinition();
		
		// Preferred Name
		PreferredNameDataDefinition preferredNameDD = new PreferredNameDataDefinition();
		Mapped<PreferredNameDataDefinition> mappedPreferredNameDD = new Mapped<PreferredNameDataDefinition>();
		mappedPreferredNameDD.setParameterizable(preferredNameDD);
		
		// Create the list of mapped PersonAttributeDataDefinition to be fed to the PersonNameAndAttributesDD
		// attr1
		PersonAttributeDataDefinition attr1DD = new PersonAttributeDataDefinition();
		attr1DD.setPersonAttributeType(personService.getPersonAttributeType(8));
		Mapped<PersonAttributeDataDefinition> mappedAttr1DD = new Mapped<PersonAttributeDataDefinition>();
		mappedAttr1DD.setParameterizable(attr1DD);
		
		// attr2
		PersonAttributeDataDefinition attr2DD = new PersonAttributeDataDefinition();
		attr2DD.setPersonAttributeType(personService.getPersonAttributeType(2));
		Mapped<PersonAttributeDataDefinition> mappedAttr2DD = new Mapped<PersonAttributeDataDefinition>();
		mappedAttr2DD.setParameterizable(attr2DD);
		
		List<Mapped<? extends PersonAttributeDataDefinition>> attributes = new ArrayList<Mapped<? extends PersonAttributeDataDefinition>>();
		attributes.add(mappedAttr1DD);
		attributes.add(mappedAttr2DD);
		
		// Configure the PersonNameAndAttributesDataDefinition before being evaluated
		
		nameDD.setPreferredNameDefinition(mappedPreferredNameDD);
		nameDD.setPersonAttributeDefinitions(attributes);
		
		EvaluatedPersonData evaluatedPNAADD = personDataService.evaluate(Mapped.mapStraightThrough(nameDD), context);
		
		assertEquals("Johnny Test Doe (SINGLE, NULL)", evaluatedPNAADD.getData().get(6));
	}
	
}
