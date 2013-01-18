import org.junit.Assert.*;

public class SoundexEncoderTest{
	//####################
	//### constructors ###
	//####################
	@org.junit.Test public void constructorTest00(){
		SoundexEncoder encoder = new SoundexEncoder();
	}
	
	
	
	//###############
	//### methods ###
	//###############
	@org.junit.Test public void getSoundexCodeTest00(){
		String groupcode = "1AB/2C";
		String word = "ABBCB";
		String expectedResult = "A1121";
		
		SoundexEncoder encoder = new SoundexEncoder(groupcode);
		encoder.collapseDoublings	= false;
		encoder.enforceLength		= false;
		
		String result = encoder.getSoundexCode(word);
		
		org.junit.Assert.assertEquals(expectedResult, result);
	}
	
	/**
	collapse doublings test
	*/
	@org.junit.Test public void getSoundexCodeTest01(){
		String groupcode = "1AB/2C";
		String word = "AABCCCBC";
		String expectedResult = "A1212";
		
		SoundexEncoder encoder = new SoundexEncoder(groupcode);
		encoder.enforceLength		= false;
		
		String result = encoder.getSoundexCode(word);
		
		org.junit.Assert.assertEquals(expectedResult, result);
	}
	
	/**
	standard encoding test
	*/
	@org.junit.Test public void getSoundexCodeTest02(){
		//TODO
	}
}
