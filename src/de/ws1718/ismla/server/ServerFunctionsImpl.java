// ISMLA
// WiSe 2017/2018
// Classifier Project

package de.ws1718.ismla.server;

import de.ws1718.ismla.client.MTentry;
import de.ws1718.ismla.client.MaskedText;
import de.ws1718.ismla.client.UseServerFunctions;

import tagger.RDRPOSTagger;
import tagger.Utils;

import edu.fudan.nlp.cn.tag.CWSTagger;
import edu.fudan.util.exception.LoadModelException;

import tokenizerVN.TokenizerVN;

import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.Vector;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server-side implementation of the RPC service.
 */
public class ServerFunctionsImpl extends RemoteServiceServlet implements UseServerFunctions {
    private static final long serialVersionUID = 1L;

	TreeMap<String,Vector<String>> mapCh = new TreeMap<String,Vector<String>>();
	TreeMap<String,Vector<String>> mapJp = new TreeMap<String,Vector<String>>();
	TreeMap<String,Vector<String>> mapVn = new TreeMap<String,Vector<String>>();
	CWSTagger tag;

	
	String level = "";


/**
 *	Function to add a classifier to the Chinese vector
 **/
	private boolean addToCh(String line, int rank){
	    String[] parts 	= line.split("\t");

		Vector<String> props = new Vector<String>();
		props.add(Integer.toString(rank));	//rank
		props.add(parts[3]);				//gloss
		props.add(parts[2]);				//pinyin
		props.add(parts[4]);				//examples
		props.add("");						//type
		props.add("");						//pron
		props.add("");						//vnChar
		props.add(parts[7]);				//level
		
		mapCh.put(parts[0], props);			// simple char
		mapCh.put(parts[1], props);		    // trad char
		return true;	
	}
/**
 *	Function to add a classifier to the Japanese vector
 **/	
	private boolean addToJp(String line, int rank){
	    String[] parts 	= line.split("\t");

		Vector<String> props = new Vector<String>();
		props.add(Integer.toString(rank));	//rank
		props.add(parts[2]);				//gloss
		props.add("");						//pinyin
		props.add("");						//examples
		props.add(parts[3]);				//type
		props.add(parts[1]);				//pron
		props.add("");						//vnChar
		props.add(parts[5]);				//level

		mapJp.put(parts[0], props);			// kanji
		return true;	
	}

/**
 *	Function to add a classifier to the Vietnamese vector
 **/
	private boolean addToVn(String line, int rank){
	    String[] parts 	= line.split("\t");

		Vector<String> props = new Vector<String>();
		props.add(Integer.toString(rank));	//rank
		props.add(parts[2]);				//gloss
		props.add("");						//pinyin
		props.add(parts[3]);				//examples
		props.add("");						//type
		props.add("");						//pron
		props.add(parts[1]);				//vnChar
		props.add(parts[5]);				//level
		
		mapVn.put(parts[0], props);			// word
		return true;	
	}
	
	
/**
 *	Function to read in the classifiers from csv files and save it to a HashMap
 **/
	@Override
	public boolean read() throws IOException {
		// Chinese tokenizer
		try {
			tag = new CWSTagger("../resources/seg.m");		// needs to be loaded at program start (too big)
		} catch (LoadModelException e) {
			e.printStackTrace();
		}

		// Initialization
		Vector<String> files = new Vector<String>();
		files.add("crank");				// chinese csv file
		files.add("jrank");				// japanese csv file
		files.add("vrank");				// vietnamese csv file
		
		int rank = 1;		// ranking calculator (the classifiers are ordered by rank)
		
		// for each file:
		for(String language : files){
			InputStream inputStream 		= getServletContext().getResourceAsStream("/WEB-INF/" + language + ".csv");		
			BufferedReader buffer 			= new BufferedReader(new InputStreamReader(inputStream));
			String line;
			
			// Get each line of the .csv file
			while((line=buffer.readLine()) != null) {
			    if(language.equals("crank")){
			    	addToCh(line,rank);		// add to mapCh
			    	rank++;
			    }
			    if(language.equals("jrank")){
			    	addToJp(line,rank);		// add to mapJp
			    	rank++;
			    }
			    if(language.equals("vrank")){
			    	addToVn(line,rank);		// add to mapVn
			    	rank++;
			    }
			}
			rank = 1;
			buffer.close();
		} // End for loop
		return true;
	}
	
	
	private MaskedText classCh(String text) throws IOException, LoadModelException{
		MaskedText m = new MaskedText();
		MTentry entry = null;
		// Checking each character (because tokenizer might be unreliable and the classifiers are always only one character)
		// If a character was found in the classifier file, get the previous 5 characters, tokenize and pos tag them
		// get the word directly before the classifier and do pattern matching
		for(int ii = 0; ii < text.length(); ii++){
			String c = text.charAt(ii) +"";
			if(mapCh.containsKey(c)){
				
				// NOTE: the first four characters have less than 5 previous characters
				String previous = "";				
				int jj = ii-5;
				if (jj < 0){
					jj = 0;
				}
				while(jj < ii) {
					previous = previous + text.charAt(jj);
					jj++;
				}
					
				String s = tag.tag(previous);			// tokenize the previous 5 chars
				String[] tokenized = s.split(" ");
				String lastWord = "";					// get the last token
				if(tokenized!=null){
					lastWord = tokenized[tokenized.length-1];
				}
				
				// pos tag the last token
				RDRPOSTagger tree = new RDRPOSTagger ();
				tree.constructTreeFromRulesFile("../resources/models/zh-upos.RDR");
				HashMap<String, String> FREQDICT = Utils.getDictionary("../resources/models/zh-upos.DICT");
				String pos = tree.tagSentence(FREQDICT, lastWord);
				System.out.println(lastWord + " " +tree.tagSentence(FREQDICT, lastWord));

				// pattern matching
				if(pos.equals("NUM") || pos.equals("DET") || lastWord.matches("是|这|這|那|每")){
					//level
					if(level.contains(mapCh.get(c).get(7))){
						entry = new MTentry(c, mapCh.get(c));	// new entry(class, props)
						m.vec.add(entry);
					}
					else{
						entry = new MTentry(c);					// new entry(non class)
						m.vec.add(entry);							
					}
				}
				else{
					entry = new MTentry(c);					// new entry(non class)
					m.vec.add(entry);							
				}
			}
			else{
				entry = new MTentry(c);						// new entry(non class)
				m.vec.add(entry);							
			}
		}
		return m;
	}
	
	private MaskedText classJp(String text) throws IOException{
		MaskedText m = new MaskedText();
		MTentry entry = null;
		
		Tokenizer tokenizer = new Tokenizer() ;				// Tokenizer for Japanese
        List<Token> tokens = tokenizer.tokenize(text);		// tokenize
        
		RDRPOSTagger tree = new RDRPOSTagger();				// pos tagger for Japanese
		tree.constructTreeFromRulesFile("../resources/models/ja-upos.RDR");
		HashMap<String, String> FREQDICT = Utils.getDictionary("../resources/models/ja-upos.DICT");
		
        
        // for Japanese we rely on the tokenizer (in contrast to chinese)
        // check each token, if it is in the classifier list, than pos tag the previous token
        // and do pattern matching
        for (int jj = 0; jj < tokens.size(); jj++) {
        	String token= tokens.get(jj).getSurface();
        	String previous = "";
        	if(jj >= 1){
        		previous = tokens.get(jj-1).getSurface();
        	}
        	String pos = tree.tagSentence(FREQDICT, previous);
          
			if(mapJp.containsKey(token)){
				if(pos.equals("NUM")){
					//level
					if(level.contains(mapJp.get(token).get(7))){
						entry = new MTentry(token, mapJp.get(token));	// new entry(class, props)
						m.vec.add(entry);
					}
					else{
						entry = new MTentry(token);					// new entry(non class)
						m.vec.add(entry);							
					}
				}
				else{
					entry = new MTentry(token);					// new entry(non class)
					m.vec.add(entry);							
				}
			}
			else{
				entry = new MTentry(token);						// new entry(non class)
				m.vec.add(entry);	
			}
			System.out.println(token + " " +tree.tagSentence(FREQDICT, token));
        }
		return m;
	}
	
	private MaskedText classVn(String text) throws IOException{
		MaskedText m = new MaskedText();
		MTentry entry = null;
		
		List<String> tokens = TokenizerVN.tokenize(text);	// tokenize Vietnamese
		
        // for Vietnamese we rely on the tokenizer (in contrast to chinese)
        // check each token, if it is in the classifier list, than pos tag the previous token
        // and the token afterwords, then do pattern matching
		RDRPOSTagger tree = new RDRPOSTagger();
		tree.constructTreeFromRulesFile("../resources/models/vi-upos.RDR");
		HashMap<String, String> FREQDICT = Utils.getDictionary("../resources/models/vi-upos.DICT");
		
		for(int jj = 0; jj < tokens.size(); jj++){
			String token	= tokens.get(jj);
        	String previous = "";
        	String next 	= "";
        	if(jj >= 1){
        		previous = tokens.get(jj-1);
        	}
        	if(jj < tokens.size()-1){
        		next = tokens.get(jj+1);
        	}
        	String posPre = tree.tagSentence(FREQDICT, previous);
        	String posPost = tree.tagSentence(FREQDICT, next);

			if(mapVn.containsKey(token)){
				System.out.println(token);
				if(posPre.equals("NUM") || posPost.equals("NOUN") || posPost.equals("PROPN")){
					//level
					if(level.contains(mapVn.get(token).get(7))){
						entry = new MTentry(token, mapVn.get(token));	// new entry(class, props)
						m.vec.add(entry);
					}
					else{
						entry = new MTentry(token);					// new entry(non class)
						m.vec.add(entry);							
					}
				}
				else{
					entry = new MTentry(token);						// new entry(non class)
					m.vec.add(entry);							
				}
			}
			else{
				entry = new MTentry(token);							// new entry(non class)
				m.vec.add(entry);							
			}
		}
		return m;
	}
	

// ----------------------------------
// Split the input text into tokens
// Generate MTentrys (verb or non verb)
// save the MTentrys in the MaskedText.vec
// ----------------------------------
	
	public MaskedText covering(String text, String lang, boolean a, boolean b1, boolean b2, boolean c1) throws IOException{
		MaskedText m = new MaskedText();
		if(a == true)
			level = level + " A ";
		if(b1 == true)
			level = level + " B1 ";
		if(b2 == true)
			level = level + " B2 ";
		if(c1 == true)
			level = level + " C ";
		
		if(lang.equals("ch")){
			try {
				m = classCh(text);
			} catch (LoadModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(lang.equals("jp")){
			m = classJp(text);
		}
		if(lang.equals("vn")){
			m = classVn(text);
		}
		
		level = "";				//reset level
		return m;
	}

}
