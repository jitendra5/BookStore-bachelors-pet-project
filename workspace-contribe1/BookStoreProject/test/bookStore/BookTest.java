package bookStore;
import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.InputMismatchException;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class BookTest {
	private Book book;
	@Before
	public void setup(){
		 book=new Book();
	}

@Test(expected=IllegalArgumentException.class)
public void listShouldThrowAnException_If_SearchString_Is_NotString(){
	
	book.list(null);
	
}
	    
@After
public void teardown(){
	book=null;
	}
}
