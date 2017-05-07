import org.junit.*;
import static org.junit.Assert.*;

public class ContactTest {
    ContactImpl testContact;

    @Before
    public void testSetup() {
	testContact = new ContactImpl(7,"robert paulson","hisname is not");
    }

    @Test
    public void testContactId() {
	assertEquals(7,testContact.getId());
    }

    @Test
    public void testContactName() {
	assertEquals("robert paulson",testContact.getName());
    }

    @Test
    public void testContactNotes() {
	assertEquals("hisname is not",testContact.getNotes());
    }
}
