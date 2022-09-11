import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

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
		m1.setp1Points(5);
		m1.setp2Points(10);

		m2.setp1guess(3);
		m2.setp2guess(6);

		m3.setIsDraw(true);
		
		m4.setIsVictor(true);

		m5.setWinner(false);

		assertEquals(5, m1.getp1Points());
		assertEquals(10, m1.getp2Points());

		assertEquals(3, m2.getp1guess());
		assertEquals(6, m2.getp2guess());

		assertEquals(m3.isDraw(), true);

		assertEquals(m4.isVictor(), true);

		assertEquals(m5.isWinnner(), false);
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
