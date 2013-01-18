/**
implementation of Soundex algorithm<br/>
using a lookup table to acces group for a given character

@author TH
@since 2013_01_10
*/
public class SoundexEncoder{
	//#################
	//### constants ###
	//#################
	private static final int	GROUPTABLE_NOGROUP_ID			= -1;
	
	//standard values
	public static final char	STANDARD_GROUPCODE_GROUPDELIMETER	= '/';
	public static final String	STANDARD_SOUNDEX_GROUPCODE		= "1BFPV/2CGJKQSXZ/3DT/4L/5MN/6R";
	
	public static final boolean	STANDARD_COLLAPSEDOUBLINGS_SETTING	= true;

	public static final boolean	STANDARD_ENFORCELENGTH_SETTING		= true;
	public static final int		STANDARD_RESULT_LENGTH			= 4;
	public static final char	STANDARD_NOGROUP_SYMBOL			= '0';
	
	

	//##################
	//### attributes ###
	//##################
	/**
	lookup table for characters with value from <code>groupTableFirstElementCode</code> to <code>groupTableFirstElementCode + groupTable.length</code><br/>
	-1 for no group<br/>
	n for group n
	*/
	private int[] groupTable;
	/**
	value for first char in <code>groupTable</code>
	*/
	private int groupTableFirstElementCode;
	/**
	lookup table for groups to compose soundex code
	*/
	private String[] groupSymbolTable;
	
	/**
	whether sequential doublings in sequence of group symbols are collapsed<br/>
	example: "A112223444" -> "A1234"
	*/
	public boolean collapseDoublings;
	
	/**
	whether result of {@link #getSoundexCode getSoundexCode} is lengtened by appending {@link #nogroupSymbol} or shortened until it has a length of {@link #resultLength}
	*/
	public boolean enforceLength;
	/**
	will be appended to lengthen the result of {@link #getSoundexCode getSoundexCode} if {@link #enforceLength} is set to <code>true</code>
	*/
	public char nogroupSymbol;
	/**
	desired length for result of {@link #getSoundexCode getSoundexCode}
	*/
	public int resultLength;
	
	
	
	//###############
	//### methods ###
	//###############
	
	//constructors
	/**
	creates SoundexEncoder object with standard Soundex encoding
	@see #STANDARD_SOUNDEX_GROUPCODE
	@see #setGroups(String,char)
	*/
	public SoundexEncoder(){
		this(STANDARD_SOUNDEX_GROUPCODE);
	}
	
	/**
	creates SoundexEncoder object with custom groupcode 
	and {@link #STANDARD_GROUPCODE_GROUPDELIMETER}
	@see #setGroups(String,char)
	*/
	public SoundexEncoder(String groupcode){
		this(groupcode, STANDARD_GROUPCODE_GROUPDELIMETER);
	}
	
	/**
	creates SoundexEncoder object
	with custom groupcode and group delimiter
	@see #setGroups(String,char)
	*/
	public SoundexEncoder(String groupcode, char groupDelimiter){
		this.setGroups(groupcode, groupDelimiter);
		
		this.collapseDoublings	= STANDARD_COLLAPSEDOUBLINGS_SETTING;
		
		this.enforceLength 	= STANDARD_ENFORCELENGTH_SETTING;
		this.nogroupSymbol	= STANDARD_NOGROUP_SYMBOL;
		this.resultLength	= STANDARD_RESULT_LENGTH;
	}
	
	//getters & setters
	/**
	sets groups by decoding the parameter <code>groupcode</code>
	@param groupcode see {@link #setGroups(String, char) setGroups} for specification
	@see SoundexEncoder#STANDARD_GROUPCODE_GROUPDELIMETER
	*/
	public void setGroups(String groupcode){
		this.setGroups(groupcode, STANDARD_GROUPCODE_GROUPDELIMETER);
	}
	/**
	sets groups by decoding the parameter <code>groupcode</code><br/>
	@param groupcode rough specification of <code>groupcode</code>:<br/>
	<ul>
	<li>U ~ set of all Unicode characters</li>
	<li>U* ~ Kleene star of U</li>
	<li>let A,B element of U* <br/>
		then AB is concatenation of A and B</li>
	<li>
	let S element of U ~ symbol for group<br/>
	let E1, E2, ... , En for n >= 1 elements of U ~ elements of group<br/>
	then SE1E2..En is a group
	</li>
	<li>
	let G1, G2, ... , Gm for m >= 1 are groups<br/>
	let | element of U ~ group delimeter<br/>
	then G1|G2| ... |Gm is a groupcode<br/>
	(and G1, G2, ... , Gm are groupcodes)
	</li>
	<ul/>
	*/
	public void setGroups(String groupcode, char groupDelimiter){
		
		//delimit groups
		String[] groups = groupcode.split("" + groupDelimiter);
		
		this.groupSymbolTable = new String[groups.length];
		
		
		//search for minimum and maximum character value
		int max = Integer.MIN_VALUE;
		int min = Integer.MAX_VALUE;
		
		for(int i = 0; i < groups.length; i++){
			for(int j = 1; j < groups[i].length(); j++){	// ... first character is group symbol
				max = Math.max(max, (int) groups[i].charAt(j));
				min = Math.min(min, (int) groups[i].charAt(j));
			}
		}
		
		//set groupTable
		this.groupTableFirstElementCode = min;
		this.groupTable = new int[max-min+1];
		
		java.util.Arrays.fill(this.groupTable, GROUPTABLE_NOGROUP_ID);
		
		for(int i = 0; i < groups.length; i++){
			
			//note group symbol
			this.groupSymbolTable[i] = ""+groups[i].charAt(0);
			
			//mark group elements
			for(int j = 1; j < groups[i].length(); j++){
				this.groupTable[groups[i].charAt(j)-this.groupTableFirstElementCode] = i;
			}
			
		}
		
	}
	
	//other methods
	/**
	determines Soundex encoding
	@param word will be encoded <br/> is not empty (assertion)
	@return Soundex encoding of parameter word
	*/
	public String getSoundexCode(String word){
		String result = ""+word.charAt(0);
		
		result += this.getGroupSymbolSequence(word.substring(1));
		
		return result;
	}
	
	/**
	determines sequence of group symbols using the {@link #groupSymbolTable lookup table}
	*/
	private String getGroupSymbolSequence(String word){
		String result = "";
		int group;
		int previousWrittenGroup = GROUPTABLE_NOGROUP_ID;
		
		//encode sequence
		for(int i = 0; (i < word.length()) && (!this.enforceLength || result.length() <= this.resultLength - 1); i++){	//hint 1 for 'resultLength - 1': transfer of the first letter, see #getSoundexCode
			
			group = this.getGroup(word.charAt(i));
			
			if((group != GROUPTABLE_NOGROUP_ID) && ((!this.collapseDoublings) || (group != previousWrittenGroup))){
				result += this.groupSymbolTable[group];
				
				previousWrittenGroup = group;
			}
			
		}
		
		//lengthen if needed
		while (this.enforceLength && result.length() < this.resultLength - 1){						//see hint 1
			result += this.nogroupSymbol;
		}
		
		return result;
	}
	
	/**
	@return {@link groupSymbolTable} index for given character
	*/
	private int getGroup(char c){
		int result = GROUPTABLE_NOGROUP_ID;
		
		int index = ((int) c)- this.groupTableFirstElementCode;
		
		if((index >= 0) && (index < this.groupTable.length)){
			result = this.groupTable[index];
		}
		
		return result;
	}

	
	
	//######################
	//### static methods ###
	//######################
	/**
	interactive test method<br/>
	starts loop:<br/>
	1. asks for input<br/>
	2. prints encoding of input with standard settings<br/>
	<b>quit loop with input "Q"</b>
	*/
	public static void main(String[] args){
		java.util.Scanner sc = new java.util.Scanner(System.in);
		SoundexEncoder encoder = new SoundexEncoder();
		
		String input = "";
		
		System.out.println("starting loop to encode input");
		System.out.println("enter 'Q' to cancel");
		
		while(!input.equals("Q")){
			input = sc.next().toUpperCase();
			
			System.out.println(input + ": " + encoder.getSoundexCode(input));
		}
	}
}
