import org.junit.Assert.*;


/**

*/
public class SoundexEncoderTest{
	
	@org.junit.Test public void constructorTest00(){
		SoundexEncoder encoder = new SoundexEncoder();
	}
	
	@org.junit.Test public void getSoundexCodeTest00(){
		String groupcode = "1AB/2C";
		String word = "ABCB";
		String expectedResult = "A121";
		
		SoundexEncoder encoder = new SoundexEncoder(groupcode);
		encoder.setCollapseDoublings(false);
		
		String result = encoder.getSoundexCode(word);
		
		org.junit.Assert.assertEquals(expectedResult, result);
	}
	
	@org.junit.Test public void getSoundexCodeTest01(){
		String groupcode = "1AB/2C";
		String word = "AABCCCB";
		String expectedResult = "A121";
		
		SoundexEncoder encoder = new SoundexEncoder(groupcode);
		
		String result = encoder.getSoundexCode(word);
		
		org.junit.Assert.assertEquals(expectedResult, result);
	}
	
}
