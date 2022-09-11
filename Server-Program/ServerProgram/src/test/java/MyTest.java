import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class MyTest {

	static MorraInfo m1, m2, m3, m4, m5;

	@BeforeAll
	static void testMorraInfoConstructor() {
		 m1 = new MorraInfo(); 
		 m2 = new MorraInfo(); 
		 m3 = new MorraInfo(); 
		 m4 = new MorraInfo(); 
		 m5 = new MorraInfo();

		assertNotNull(m1);
		assertNotNull(m2);
		assertNotNull(m3);
		assertNotNull(m4);
		assertNotNull(m5);
	}

	@Test 
	void testGettterSetter() {
		m1.setp1Points(8);
		m1.setp2Points(2);

		m2.setp1guess(4);
		m2.setp2guess(7);

		m3.setIsDraw(false);
		m4.setHave2Players(true);
		m5.setIsPlayingAgain(true);
		
		assertEquals(8, m1.getp1Points());
		assertEquals(2, m1.getp2Points());

		assertEquals(4, m2.getp1guess());
		assertEquals(7, m2.getp2guess());

		assertEquals(m3.isDraw(), false);

		assertEquals(m4.have2players(), true);

		assertEquals(m5.isPlayingAgain(), true);
	}

	@Test
	void testMorraInfoPrint() {
		m1.print();
		m2.print();
		m3.print();
		m4.print();
		m5.print();
	} 
}
