package test.utils;

import static org.junit.Assert.*;

import java.io.File;

import main.utils.XMLFile;

import org.junit.Test;

public class XMLFileTest {

	@Test
	public void testAddAndGetString() {
		XMLFile file = new XMLFile("test.xml");
		file.add("a.b.c1.d.e1.f", "test1");
		file.add("a.b.c1.d.e2.f", "test2");
		file.add("a.b.c1.d.e3",   "test3");
		file.add("a.b.c2",        "test4");
		file.add("a.b.c3.d.e",    "test5");
		file.add("a.b.c4.d.e",    1);
		assertEquals("test1", file.getString("a.b.c1.d.e1.f", "failed"));
		assertEquals("test2", file.getString("a.b.c1.d.e2.f", "failed"));
		assertEquals("test3", file.getString("a.b.c1.d.e3",   "failed"));
		assertEquals("test4", file.getString("a.b.c2",        "failed"));
		assertEquals("test5", file.getString("a.b.c3.d.e",    "failed"));
		assertEquals("1",     file.getString("a.b.c4.d.e",    "fail"));
		assertEquals("fail2", file.getString("a.b.c5",        "fail2"));
	}

	@Test
	public void testAddAndGetInt() {
		XMLFile file = new XMLFile("test.xml");
		file.add("a_b.c_d.number1", 1);
		file.add("a_b.c_d.number2", 2);
		file.add("a_b.number3",     3);
		file.add("a_b.number4",    "4");
		assertEquals( 1, file.getInt("a_b.c_d.number1", -1));
		assertEquals( 2, file.getInt("a_b.c_d.number2", -1));
		assertEquals( 3, file.getInt("a_b.number3", -1));
		assertEquals( 4, file.getInt("a_b.number4", -1));
		assertEquals(-1, file.getInt("a.b.number5", -1));
	}

	@Test
	public void testAddAndGetElement() {
		XMLFile.ElementValue<String> element1 = new XMLFile.ElementValue<String>("test1");
		XMLFile.ElementValue<Integer> element2 = new XMLFile.ElementValue<Integer>(2);
		XMLFile.ElementValue<String> element3 = new XMLFile.ElementValue<String>("test3");
		XMLFile.ElementValue<Integer> element4 = new XMLFile.ElementValue<Integer>(4);
		XMLFile.ElementList list = new XMLFile.ElementList();
		list.add("l1", element1);
		list.add("l2", element2);
		
		XMLFile file = new XMLFile("test.xml");
		file.add("a.b.c.d.e.f.g1.h.i.j.k", list);
		file.add("a.b.c.d.e.f.g2", element3);
		file.add("a.b.c.d.e.f.g3.h.i", element4);
		assertEquals("test1", file.getString("a.b.c.d.e.f.g1.h.i.j.k.l1", "failed"));
		assertEquals(2, file.getInt("a.b.c.d.e.f.g1.h.i.j.k.l2", -1));
		assertEquals("test3", file.getString("a.b.c.d.e.f.g2", "failed"));
		assertEquals(4, file.getInt("a.b.c.d.e.f.g3.h.i", -1));
	}

	@Test
	public void testAddAndGetList() {
		XMLFile file = new XMLFile("test.xml");
		file.add("a.b.c.d1.e.f", 1);		
		file.add("a.b.c.d2",    "2");
		file.add("a.b.c.d3.e.f", 3);
		
		XMLFile.ElementList list = file.getElement("a.b.c");
		assertEquals(1, list.getInt("d1.e.f", -1));
		assertEquals("2", list.getString("d2", "failed"));
		assertEquals(3, list.getInt("d3.e.f", -1));
		
		assertEquals(0, file.getElement("a.b.c.d").size());
	}
	
	@Test(expected=RuntimeException.class)
	public void testCreateInvalidFile1() {
		XMLFile file1 = new XMLFile("test.xml");
		file1.add("a.b.c.d1.e.f", 1);
		file1.add("a.b.c.d1", 2);
		file1.add("b", 5);	
	}
	
	@Test
	public void testRInvalidFile() {
		XMLFile file = new XMLFile("test/testfiles/two_equal_tags.xml");
		assertFalse(file.read());
		
		file = new XMLFile("test/testfiles/two_root_nodes.xml");
		assertFalse(file.read());
		
		file = new XMLFile("test/testfiles/wrong_end_tag.xml");
		assertFalse(file.read());
	}
	
	@Test
	public void testReadAndWriteCorrect() {
		XMLFile file1 = new XMLFile("test.xml");
		file1.add("a.b.c.d1.e.f1",  1);
		file1.add("a.b.c.d1.e.f2", "2");
		file1.add("a.b.c.d2",      "3");
		file1.add("a.b.c.d3.e.f",   4);
		System.out.println("reference:");
		System.out.println(file1.toString());
		System.out.println("----------------------------");
		file1.write();
		
		XMLFile file2 = new XMLFile("test.xml");		
		assertTrue(file2.read());
		System.out.println("test:");
		System.out.println(file2.toString());
		assertEquals(1, file2.getInt("a.b.c.d1.e.f1", -1));
		assertEquals(2, file2.getInt("a.b.c.d1.e.f2", -1));
		assertEquals(3, file2.getInt("a.b.c.d2", -1));
		assertEquals(4, file2.getInt("a.b.c.d3.e.f", -1));
			
		File file = new File("test.xml");
		file.delete();
	}
}
