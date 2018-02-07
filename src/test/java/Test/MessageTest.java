package Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.inter.MessageDAO;
import com.inter.ThirdDAO;
import com.service.ThirdAuthorService;

@RunWith(SpringRunner.class)
@ContextConfiguration({ "/spring-config.xml" })
public class MessageTest {
	
	 @Autowired
	   private MessageDAO dao;
	 
	 @Autowired
		private ThirdDAO third;

	  
		@Test
		public void testSet() {
			assertNotNull(third.getAuthor("oE0sDwpKgCb2yK160qdbma7bjd-4"));
		}
}
