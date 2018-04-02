// ISMLA
// WiSe 2017/2018
// Classifier Project

package de.ws1718.ismla.client;

import java.io.Serializable;
import java.util.Vector;

public class MTentry implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String token 				= "";
    private boolean classifier 			= false;
    
    // props contains properties off classifiers (pinyin,...)
    private Vector<String> props 		= new Vector<String>();
    
    // Empty Constructor
    MTentry(){}
    
    // 1 Argument Constructor - Case: non classifier
    public MTentry(String token){
    	this.token 		= token;
    	this.classifier = false;
    }
    
    // 2 Argument Constructor - Case: classifier
    public MTentry(String token, Vector<String> props){
    	this.token 		= token;
    	this.classifier	= true;
    	this.props 		= props;
    }
    
    public String getToken(){
    	return token;
    }
    
    public boolean isClass(){
    	return classifier;
    }
    
    public String getGloss(){
    	return props.get(1);
    }
    
    public Vector<String> getProps(){
    	return props;
    }
    
    
}
