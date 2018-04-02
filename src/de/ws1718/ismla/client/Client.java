// ISMLA
// WiSe 2017/2018
// Classifier Project

package de.ws1718.ismla.client;


import java.util.Vector;


import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Client implements EntryPoint {
	
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	//private static final String SERVER_ERROR = "An error occurred while "
			//+ "attempting to contact the server. Please check your network " + "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side.
	 */
	private final UseServerFunctionsAsync serverFunctions = GWT.create(UseServerFunctions.class);

	private String welcome = "Hello! Welcome to our page. Please check the levels you " + 
	"would like to practice for and the language you are learning. Simply paste a text you'd like to work with " +
	"and start your exercise! <br> Note: After the exercise is finished, you can move the cursor to the cloze for feedback, " +
	"in case there are incorrect answers.";
	private TextArea textarea;
	private FlowPanel resultPanel;
	private HTMLPanel htmlPanel;
	private Vector<TextBox> boxVec 		= new Vector<TextBox>();
	private Vector<String> answerVec 	= new Vector<String>();
	private Button btnFinished;
	private String lang = "";
	private VerticalPanel vp 			= new VerticalPanel();
	private HorizontalPanel checkp 		= new HorizontalPanel();
	
	private Button btnJp 				= new Button("Japanese");
	private Button btnVn 				= new Button("Vietnamese");
	private Button btnCh 				= new Button("Chinese");
	private Button btnStart 			= new Button("Start Exercise");
	private Vector<Vector<String>> propsVec	= new Vector<Vector<String>>();

	private CheckBox cb1 = new CheckBox("A");
	private CheckBox cb2 = new CheckBox("B1");
	private CheckBox cb3 = new CheckBox("B2");
	private CheckBox cb4 = new CheckBox("C1+");

	MaskedText masked;


// ----------------------------------
// Entry point - will be loaded, when the client starts
// ----------------------------------
	public void onModuleLoad() {
		
		serverFunctions.read(new ReadCallback());	// read in the dictionary

		HTML instr = new HTML("Paste some text for practice here:");
		vp.add(instr);
		HTML note = new HTML(welcome);
		vp.add(note);
		
		textarea = new TextArea();
		
		vp.add(textarea);
		
		//---------------------------------------------
		// Chinese Button
		vp.add(btnCh);
		btnCh.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				lang = "ch";
				btnCh.addStyleName("red");
				btnJp.removeStyleName("red");
				btnVn.removeStyleName("red");
			}
		});
		
		//---------------------------------------------
		// Japanese Button
		vp.add(btnJp);
		btnJp.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				lang = "jp";
				btnJp.addStyleName("red");
				btnCh.removeStyleName("red");
				btnVn.removeStyleName("red");
			}
		});
		//---------------------------------------------
		// Vietnamese Button
		vp.add(btnVn);
		btnVn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				lang = "vn";
				btnVn.addStyleName("red");
				btnCh.removeStyleName("red");
				btnJp.removeStyleName("red");
			}
		});
		//---------------------------------------------	
		// Checkbox
	      checkp.add(cb1);
	      checkp.add(cb2);
	      checkp.add(cb3);
	      checkp.add(cb4);
	      vp.add(checkp);

		//---------------------------------------------
		// Start Button
		vp.add(btnStart);
		btnStart.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				boolean a = cb1.getValue();
				boolean b1 = cb2.getValue();
				boolean b2 = cb3.getValue();
				boolean c1 = cb4.getValue();
				
				if(textarea.getText().equals("")){
					btnStart.setText("No Text pasted");
				}
				else if(a == false && b1 == false && b2 == false && c1 == false){
					btnStart.setText("No level selected");
				}
				else if(lang.equals("")){
					btnStart.setText("No language selected");
				}
				else{
					String text = textarea.getText();
					serverFunctions.covering(text, lang, a, b1, b2, c1, new CoveringCallback());
					vp.setVisible(false);
				}
				//RootPanel.get().clear();	
			}
		});

		
		RootPanel.get().add(vp);
		note.setStyleName("note");
		instr.setStyleName("instr");
		btnStart.setStyleName("button");
		btnCh.setStyleName("buttonCh");
		btnJp.setStyleName("buttonJp");
		btnVn.setStyleName("buttonVn");
		textarea.setStyleName("textarea");	
		checkp.setStyleName("checkpanel");
	}
	
	
// ----------------------------------
// Does nothing currently
// ----------------------------------
	private final class ReadCallback implements AsyncCallback<Boolean> {
		@Override
		public void onFailure(Throwable caught) {
			resultPanel.clear();
			resultPanel.add(new HTML("<h3>Error:</h3> something went wrong on the server."));
			RootPanel.get().add(resultPanel);
		}

		@Override
		public void onSuccess(Boolean result) {
		}
	}

// ----------------------------------
// Creates the exercise using the masked.vec
// it contains information about each token
// classifier or not - if classifier it also contains a vector of possible properties
// ----------------------------------
	private final class CoveringCallback implements AsyncCallback<MaskedText> {
		@Override
		public void onFailure(Throwable caught) {
			resultPanel.clear();
			resultPanel.add(new HTML("<h3>Error:</h3> something went wrong on the server."));
			RootPanel.get().add(resultPanel);
		}

		@Override
		public void onSuccess(MaskedText result) {	
			masked = result; // Get the data from covering(input Text)
			String html = "";
			int counter = 0;
			for(MTentry entry : masked.vec){
				// If not classifier, then the html text stays the same
				if(!entry.isClass()){
					html = html +  " " + entry.getToken();
					
					if(entry.getToken().equals("。") || entry.getToken().equals(".")){
						html = html + "<pre></pre>";
					}
				}
				// If verb, the classifier will be changed to one of it's form (currently 1.)
				// and it will be embedded into <em (emphasize) + id = counter html
				// The id will be used to connect the TextBoxes to the correct place
				else{
					html = html + "<em id='" + counter + "' >" + " </em>" +  "<i>(" + entry.getGloss() + ") </i>";
					answerVec.add(entry.getToken());
					propsVec.add(entry.getProps());
					counter++;
				}
			}	
			
			html = "<div align=\"left\">" + html + "</div>";
			html = html + "<br><div align=\"center\">" + counter + " classifier(s) found.</div><br><br>";
			htmlPanel = new HTMLPanel(html);
			htmlPanel.setStyleName("results");
			
			// Generating the TextBoxes
			for(int ii = 0; ii < counter; ii++){
				TextBox box = new TextBox();
				box.getElement().setId(String.valueOf(ii));	// set an id
				htmlPanel.add(box, String.valueOf(ii));		// find the id place in the html
				box.setStyleName("box");
				boxVec.add(box);							// add the box to a vec, to get the input later
			}

			btnFinished = new Button("Finished!");		// New Button
			htmlPanel.add(btnFinished);					// add Button to panel
			
			// CLICKING FINISHED BUTTON
			btnFinished.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					// For all boxes and answers
					// if the input in the boxes is the same as the answer, then color it green, 
					// otherwise red
					// It will show input + answer in the box
					// Boxes can't be rewritten, Finished Button disappears
					for(int jj = 0; jj < boxVec.size(); jj++){
						String input = boxVec.get(jj).getText();
						String h = propsVec.get(jj).get(2) + " " + propsVec.get(jj).get(3) + " " +
	            		propsVec.get(jj).get(5) + " " + propsVec.get(jj).get(6);
						if(!input.equals(answerVec.get(jj))){
							boxVec.get(jj).setText(input + " → " + answerVec.get(jj));
							boxVec.get(jj).setReadOnly(true);
							boxVec.get(jj).setStyleName("boxWrong");	// style in css file
							boxVec.get(jj).setTitle(h);					// hovering text
						}
						else{
							boxVec.get(jj).setText(input);
							boxVec.get(jj).setReadOnly(true);
							boxVec.get(jj).setStyleName("boxRight");
						}
					}
					//---------------------------------------------
					// Back Button
					Button btnBack = new Button("Back");
					vp.add(btnBack);
					btnBack.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							vp.setVisible(true);
							htmlPanel.removeFromParent();
							btnStart.setText("Start Exercise");
						}
					});
					htmlPanel.add(btnBack);
					htmlPanel.remove(btnFinished);
				}
			});
			
			RootPanel.get().add(htmlPanel);
			
		}
	}
	
}
