package application_for_paper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineServices;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.runtime.Incident;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.value.TypedValue;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.FlowElement;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.camunda.demo.application_for_paper.FillDocument;
import com.camunda.demo.application_for_paper.ReceiveApllication;


public class TestcasePAI {
	static final Logger logger = LoggerFactory.getLogger(TestcasePAI.class);
	DelegateExecution execution = null;
	
	HashMap<String, String> app_info = new HashMap<String, String>(){{
		put("stid", "1");
		put("unternehmenName", "");
		put("unternehmenAbteilung", "");
		put("kursInfo", "");
		put("ProjektarbeitThema", "");
		put("ProjektarbeitThemenbereich", "");
		put("ProjektarbeitThemenschwerpunkt", "");
		put("unternehmensbetreuerNachname", "");
		put("unternehmensbetreuerVorname", "");
		put("unternehmensbetreuerEmail", "");
		put("unternehmensbetreuerTelefon", "");
		put("unternehmensbetreuerFunktion", "");
		put("ProjektarbeitInhalt", "");
		put("unternehmensvertreterNachname", "");
		put("unternehmensvertreterVorname", "");
		put("unternehmensvertreterFunktion", "");
		put("applicationType", "PA I");
	}};
	
	Map<String, String> values = new HashMap<String, String>(){{
		put("type", "PAI");
		put("stid", "1");
		put("stcourse", "WWI18/SCB");
		put("company", "testCompany");
		put("department", "testDepartment");
		put("csupName", "testCName");
		put("csupSurname", "testCSurname");
		put("csupMail", "testCMail");
		put("csupPhone", "testCPhone");
		put("csupFunction", "testCFunction");
		put("content", "This is the content description of the application. This text is longer than the others.");
		put("title", "This is the title of the application");
		put("area", "Informatik");
		put("focus", "Softwareentwicklung");
	}};
	
	
	@Rule
	public ProcessEngineRule processEngineRule = new ProcessEngineRule();	
	
	@Before
	public void setup() {
		execution = new DelegateExecution() {
			String businessKey = "";
			HashMap<String, Object> vars = new HashMap<String, Object>();
		
		@Override
		public ProcessEngineServices getProcessEngineServices() {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public ProcessEngine getProcessEngine() {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public BpmnModelInstance getBpmnModelInstance() {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public FlowElement getBpmnModelElementInstance() {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public void setVariablesLocal(Map<String, ? extends Object> variables) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void setVariables(Map<String, ? extends Object> variables) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void setVariableLocal(String variableName, Object value) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void setVariable(String variableName, Object value) {
			this.vars.put(variableName, value);
			
		}
		
		@Override
		public void removeVariablesLocal(Collection<String> variableNames) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void removeVariablesLocal() {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void removeVariables(Collection<String> variableNames) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void removeVariables() {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void removeVariableLocal(String variableName) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void removeVariable(String variableName) {
			this.vars.remove(variableName);
			
		}
		
		@Override
		public boolean hasVariablesLocal() {
			// TODO Auto-generated method stub
			return false;
		}
		
		@Override
		public boolean hasVariables() {
			// TODO Auto-generated method stub
			return false;
		}
		
		@Override
		public boolean hasVariableLocal(String variableName) {
			// TODO Auto-generated method stub
			return false;
		}
		
		@Override
		public boolean hasVariable(String variableName) {
			// TODO Auto-generated method stub
			return false;
		}
		
		@Override
		public VariableMap getVariablesTyped(boolean deserializeValues) {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public VariableMap getVariablesTyped() {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public VariableMap getVariablesLocalTyped(boolean deserializeValues) {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public VariableMap getVariablesLocalTyped() {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public Map<String, Object> getVariablesLocal() {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public Map<String, Object> getVariables() {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public <T extends TypedValue> T getVariableTyped(String variableName, boolean deserializeValue) {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public <T extends TypedValue> T getVariableTyped(String variableName) {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public String getVariableScopeKey() {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public Set<String> getVariableNamesLocal() {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public Set<String> getVariableNames() {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public <T extends TypedValue> T getVariableLocalTyped(String variableName, boolean deserializeValue) {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public <T extends TypedValue> T getVariableLocalTyped(String variableName) {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public Object getVariableLocal(String variableName) {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public Object getVariable(String variableName) {
			return this.vars.get(variableName);
		}
		
		@Override
		public String getId() {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public String getEventName() {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public String getBusinessKey() {
			return businessKey;
		}
		
		@Override
		public void setVariable(String variableName, Object value, String activityId) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void setProcessBusinessKey(String businessKey) {
			this.businessKey=businessKey;
			
		}
		
		@Override
		public void resolveIncident(String incidentId) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public boolean isCanceled() {
			// TODO Auto-generated method stub
			return false;
		}
		
		@Override
		public String getTenantId() {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public DelegateExecution getSuperExecution() {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public String getProcessInstanceId() {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public DelegateExecution getProcessInstance() {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public String getProcessDefinitionId() {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public String getProcessBusinessKey() {
			return this.businessKey;
		}
		
		@Override
		public String getParentId() {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public String getParentActivityInstanceId() {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public String getCurrentTransitionId() {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public String getCurrentActivityName() {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public String getCurrentActivityId() {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public String getActivityInstanceId() {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public Incident createIncident(String incidentType, String configuration, String message) {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public Incident createIncident(String incidentType, String configuration) {
			// TODO Auto-generated method stub
			return null;
		}
	};
	  }
	
	
	@Test
	public void testReceivePayload() {
		ReceiveApllication task = new ReceiveApllication();
		
		execution.setVariable("stid", values.get("stid"));
		execution.setVariable("stcourse", values.get("stcourse"));
		execution.setVariable("company", values.get("company"));
		execution.setVariable("department", values.get("department"));
		execution.setVariable("ch_name", values.get("csupName"));
		execution.setVariable("ch_surname", values.get("csupSurname"));
		execution.setVariable("ch_mail", values.get("csupMail"));
		execution.setVariable("ch_phone", values.get("csupPhone"));
		execution.setVariable("ch_function", values.get("csupFunction"));
		execution.setVariable("content", values.get("content"));
		execution.setVariable("topic", values.get("title"));
		execution.setVariable("area", values.get("area"));
		execution.setVariable("focus", values.get("focus"));
		
		
		
		try {
			task.execute(execution);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		assertNotNull(execution.getBusinessKey());
		
		Map<String, String> app_info = (Map<String, String>) execution.getVariable("application_info");
		
		assertNotNull(app_info);
		
		assertEquals(app_info.get("stid"), values.get("stid"));
		assertEquals(values.get("stcourse"),app_info.get("kursInfo"));
		
		//For a first thesis, the student can't choose a scientific supervisor
		assertNull(app_info.get("supid"));
		assertNull(app_info.get("wissenschaftlicherbetreuerArbeitgeber"));
		assertNull(app_info.get("wissenschaftlicherbetreuerAbteilung"));
		assertNull(app_info.get("wissenschaftlicherbetreuerFunktion"));
		
		assertEquals(values.get("company"),app_info.get("unternehmenName"));
		assertEquals(values.get("department"), app_info.get("unternehmenAbteilung"));
		assertEquals(values.get("csupName"), app_info.get("unternehmensbetreuerNachname"));
		assertEquals(values.get("csupSurname"),app_info.get("unternehmensbetreuerVorname"));
		assertEquals(values.get("csupMail"), app_info.get("unternehmensbetreuerEmail"));
		assertEquals(values.get("csupPhone"),app_info.get("unternehmensbetreuerTelefon"));
		assertEquals(values.get("csupFunction"), app_info.get("unternehmensbetreuerFunktion"));
		
		//As the testcase is a thesis 1, all fields for thesis should be filled
		assertEquals(values.get("content"), app_info.get("ProjektarbeitInhalt"));
		assertEquals(values.get("title"), app_info.get("ProjektarbeitThema"));
		assertEquals(values.get("area"), app_info.get("ProjektarbeitThemenbereich"));
		assertEquals(values.get("focus"), app_info.get("ProjektarbeitThemenschwerpunkt"));
		
		assertNull(app_info.get("lock"));
		assertNull(app_info.get("lock_reason"));
		
		boolean keysRight=true;
		for (String key: this.app_info.keySet()) {
			if (!app_info.containsKey(key)) {
				logger.error(key);
				keysRight=false;
			}
		}
		
		if (!keysRight) {
			throw new AssertionError("Not all needed keys were present");
		}
				
	}
	
	//Needs DB-Mock
	@Test
	public void testFillPDF() throws Exception {
		FillDocument task = new FillDocument();
		
		for (String key: this.app_info.keySet()) {
			this.execution.setVariable(key, this.app_info.get(key));
		}
		
		//task.execute(execution);
		
	}
	
	

}
