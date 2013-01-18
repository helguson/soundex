/**
implementation of soundex algorithm

@author TH
@since 2013_01_10
*/
public class SoundexEncoder{
	//#################
	//### constants ###
	//#################
	public static final char	STANDARD_GROUPCODE_GROUPDELIMETER	= '/';
	public static final String	STANDARD_SOUNDEX_GROUPCODE		= "1BFPV/2CGJKQSXZ/3DT/4L/5MN/6R";
	public static final boolean	STANDARD_COLLAPSEDOUBLINGS_SETTING	= true;
	public static final int		STANDARD_RESULT_LENGTH			= 4;
	
	private static final int	GROUPTABLE_NOGROUP_ID			= -1;
	//##################
	//### attributes ###
	//##################
	/**
	lookup table for characters with value from <code>groupTableFirstElementCode</code> to <code>groupTableFirstElementCode + groupTable.length</code>
	-1 for no group
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
	private boolean collapseDoublings;
	
	
	
	
	//###############
	//### methods ###
	//###############
	
	//constructors
	/**
	creates SoundexEncoder object with standard SoundexEncoding
	@see #STANDARD_SOUNDEX_GROUPCODE
	@see #setGroups(String,char)
	*/
	public SoundexEncoder(){
		this(STANDARD_SOUNDEX_GROUPCODE);
	}
	
	/**
	creates SoundexEncoder object with custom groupcode 
	and STANDARD_GROUPCODE_GROUPDELIMETER
	@see #STANDARD_GROUPCODE_GROUPDELIMETER
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
		
		this.collapseDoublings = STANDARD_COLLAPSEDOUBLINGS_SETTING;
	}
	
	//getters & setters
	/**
	@return whether sequential doublings in sequence of group symbols are collapsed<br/>
	example: "A112223444" -> "A1234"
	*/
	public boolean getCollapseDoublings(){
		return this.collapseDoublings;
	}
	/**
	sets whether sequential doublings in sequence of group symbols are collapsed<br/>
	example: "A112223444" -> "A1234"
	*/
	public void setCollapseDoublings(boolean value){
		this.collapseDoublings = value;
	}
	
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
	determines sequence of group symbol using the groupcode
	*/
	private String getGroupSymbolSequence(String word){
		String result = "";
		int group;
		int previousWrittenGroup = GROUPTABLE_NOGROUP_ID;
		
		for(int i = 0; i < word.length(); i++){
			
			group = this.getGroup(word.charAt(i));
			
			if((group != GROUPTABLE_NOGROUP_ID) && ((!this.collapseDoublings) || (group != previousWrittenGroup))){
				result += this.groupSymbolTable[group];
				
				previousWrittenGroup = group;
			}
			
		}
		
		return result;
	}
	
	/**
	@return <code>groupSymbolTable</code> index for given character
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
	
	public static void main(String[] args){
		java.util.Scanner sc = new java.util.Scanner(System.in);
		SoundexEncoder encoder = new SoundexEncoder();
		
		String input = "";
		
		while(!input.equals("Q")){
			input = sc.next().toUpperCase();
			
			System.out.println(input + ": " + encoder.getSoundexCode(input));
		}
	}
}
