package application_for_paper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;

import com.camunda.demo.application_for_paper.DigitalSignature;
import com.camunda.demo.application_for_paper.SignatureStep;

public class TestDigitalSignature {
	
	DigitalSignature ds;
	
	@Before
	public void setup() {
		this.ds = new DigitalSignature();
	}
	
	@Test
	public void testSerialisation() {
		assertEquals(
				"{\"document\": {\"documentID\": \"\", \"documentLink\": \"\"}, "
				+ "\"signees\": {}, "
				+ "\"callback\": {\"link\": \"\", \"body\": \"\"}}", ds.toJSON());
		
	}
	
	@Test
	public void testDeserialisation() {
		String json = "{\"document\": {\"documentID\": \"\", \"documentLink\": \"\"}, "
				+ "\"signees\": {}, "
				+ "\"callback\": {\"link\": \"\", \"body\": \"\"}}";
		ds = DigitalSignature.fromJSON(json);
		
		
		assert ds.equals(new DigitalSignature());
		
		
	}
	
	@Test
	public void testDocument() {
		ds.setDocument("testID", "www.mock.com/document/test.pdf");
		
		String jsonExpected = "{\"document\": {\"documentID\": \"testID\", \"documentLink\": \"www.mock.com/document/test.pdf\"}, "
				+ "\"signees\": {}, "
				+ "\"callback\": {\"link\": \"\", \"body\": \"\"}}";
		String jsonActual = ds.toJSON();
		
		assertEquals(jsonExpected, jsonActual);
		
		DigitalSignature testSign = DigitalSignature.fromJSON(jsonExpected);
		
		assert ds.equals(testSign);
	}
	
	@Test
	public void testCallback() {
		ds.setCallback("www.test.com/callback/todo", "{json:{message: \"test\"}}");
		
		String jsonExpected =  "{\"document\": {\"documentID\": \"\", \"documentLink\": \"\"}, "
				+ "\"signees\": {}, "
				+ "\"callback\": {\"link\": \"www.test.com/callback/todo\", \"body\": \"{json:{message: \\\"test\\\"}}\"}}";
		
		assertEquals(jsonExpected, ds.toJSON());
		
		DigitalSignature testSign = DigitalSignature.fromJSON(jsonExpected);
		
		assertEquals(jsonExpected, testSign.toJSON());
	}
	
	@Test
	public void testNewStep() {
		ds.addStep();
		
		String jsonExpected = "{\"document\": {\"documentID\": \"\", \"documentLink\": \"\"}, "
				+ "\"signees\": {\"1\": []}, "
				+ "\"callback\": {\"link\": \"\", \"body\": \"\"}}";
		
		assertEquals(jsonExpected, ds.toJSON());
		
		ds = DigitalSignature.fromJSON(jsonExpected);
		
		assertEquals(jsonExpected, ds.toJSON());
	}
	
	@Test
	public void testMultipleSteps() {
		ds.addStep();
		ds.addStep();
		
		String jsonExpected = "{\"document\": {\"documentID\": \"\", \"documentLink\": \"\"}, "
				+ "\"signees\": {\"1\": [], \"2\": []}, "
				+ "\"callback\": {\"link\": \"\", \"body\": \"\"}}";
		
		assertEquals(jsonExpected, ds.toJSON());
		
		ds = DigitalSignature.fromJSON(jsonExpected);
		
		assertEquals(jsonExpected, ds.toJSON());
	}
	
	@Test
	public void testAddSignee() {
		ds.addStep();
		ds.addSigneeToStep("1", "signee", "test", "test@nowhere.com");
		
		String jsonExpected = "{\"document\": {\"documentID\": \"\", \"documentLink\": \"\"}, "
				+ "\"signees\": {\"1\": [{\"name\": \"signee\", \"surname\": \"test\", \"email\": \"test@nowhere.com\"}]}, "
				+ "\"callback\": {\"link\": \"\", \"body\": \"\"}}";
		
		assertEquals(jsonExpected, ds.toJSON());
		
		ds = DigitalSignature.fromJSON(jsonExpected);
		
		assertEquals(jsonExpected, ds.toJSON());
	}
	
	@Test
	public void testMultipleSignees() {
		ds.addStep();
		ds.addSigneeToStep("1", "signee", "test", "test@nowhere.com");
		ds.addSigneeToStep("1", "signee2", "test2", "test2@nowhere.com");
		
		String jsonExpected = "{\"document\": {\"documentID\": \"\", \"documentLink\": \"\"}, "
				+ "\"signees\": {\"1\": [{\"name\": \"signee\", \"surname\": \"test\", \"email\": \"test@nowhere.com\"}, "
				+ "{\"name\": \"signee2\", \"surname\": \"test2\", \"email\": \"test2@nowhere.com\"}]}, "
				+ "\"callback\": {\"link\": \"\", \"body\": \"\"}}";
		
		assertEquals(jsonExpected, ds.toJSON());
		
		ds = DigitalSignature.fromJSON(jsonExpected);
		
		assertEquals(jsonExpected, ds.toJSON());
	}

}
