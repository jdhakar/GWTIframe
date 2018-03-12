package com.iframe.gwt.client;

import com.iframe.gwt.shared.FieldVerifier;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import java.util.logging.Logger;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GWTIframe implements EntryPoint {
	
	private static final Logger logger = Logger.getLogger(EntryPoint.class.getName());
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network " + "connection and try again.";

	public static native void console(String text)
	/*-{
	    console.log(text);
	}-*/;
	
	
//	@Override
//	public void createPresenter() {
//	    injectEventListener(this);
//	}
//	 
//	public void eventListener(String message) {
//	    Log.debug("Received a message from child: " + message);
//	}
	
//	public static native void addEventListener() 
//	/*-{
//		var eventMethod = window.addEventListener ? "addEventListener" : "attachEvent";
//		var eventer = window[eventMethod];
//		var messageEvent = eventMethod == "attachEvent" ? "onmessage" : "message";
//		
//		// Listen to message from child window
//		eventer(messageEvent,function(e) {
//		  console.log('parent received message!:  ',e.data);
//		},false);
//	}-*/;

	private native void injectEventListener(GWTIframe p) 
	/*-{
	    function postMessageListener(e) {
//	        var curUrl = $wnd.location.protocol + "//" + $wnd.location.hostname;
//	        if (e.origin !== curUrl) return; // security check to verify that we receive event from trusted source
//	        p.@com.bear-z.demo.presenter.ChildContainerPresenter::eventListener(Ljava/lang/String;)(e.data); 
			p.@com.iframe.gwt.client.GWTIframe::updateName(Ljava/lang/String;)(e.data);
	        // call function with the name
	        console.log("Message recieved is: " + e.data);
	    }
	    // Listen to message from child window
//	    if ($wnd.BrowserDetect.browser == "Explorer") {
	        // fucking IE
//	        $wnd.attachEvent("onmessage", postMessageListener, false);
//	    } else {
//	        // "Normal" browsers
	        $wnd.addEventListener("message", postMessageListener, false);
//	    }
	}-*/;
	
//	/*-{
//			function displayMessage (evt) {
//			var message;
//			if (evt.origin !== "https://robertnyman.com") {
//			message = "You are not worthy";
//			}
//			else {
//			message = "I got " + evt.data + " from " + evt.origin;
//			}
//			document.getElementById("received-message").innerHTML = message;
//			}
//			
//			if (window.addEventListener) {
//			// For standards-compliant web browsers
//			window.addEventListener("message", displayMessage, false);
//			}
//			else {
//			window.attachEvent("onmessage", displayMessage);
//			}
//	 }-*/
	
	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);
	
	Button sendButton;
	TextBox nameField;
	
	public void updateName(String name) {
		this.nameField.setText(name);
		this.sendButton.click();
	}

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		sendButton = new Button("Send");
		nameField = new TextBox();
		nameField.setText("GWT User");
		final Label errorLabel = new Label();

		// We can add style names to widgets
		sendButton.addStyleName("sendButton");

		// Add the nameField and sendButton to the RootPanel
		// Use RootPanel.get() to get the entire body element
		RootPanel.get("nameFieldContainer").add(nameField);
		RootPanel.get("sendButtonContainer").add(sendButton);
		RootPanel.get("errorLabelContainer").add(errorLabel);

		// Focus the cursor on the name field when the app loads
		nameField.setFocus(true);
		nameField.selectAll();

		// Create the popup dialog box
		final DialogBox dialogBox = new DialogBox();
		dialogBox.setText("Remote Procedure Call");
		dialogBox.setAnimationEnabled(true);
		final Button closeButton = new Button("Close");
		// We can set the id of a widget by accessing its Element
		closeButton.getElement().setId("closeButton");
		final Label textToServerLabel = new Label();
		final HTML serverResponseLabel = new HTML();
		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.addStyleName("dialogVPanel");
		dialogVPanel.add(new HTML("<b>Sending name to the server:</b>"));
		dialogVPanel.add(textToServerLabel);
		dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
		dialogVPanel.add(serverResponseLabel);
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		dialogVPanel.add(closeButton);
		dialogBox.setWidget(dialogVPanel);

		// Add a handler to close the DialogBox
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.hide();
				sendButton.setEnabled(true);
				sendButton.setFocus(true);
			}
		});

		// Create a handler for the sendButton and nameField
		class MyHandler implements ClickHandler, KeyUpHandler {
			/**
			 * Fired when the user clicks on the sendButton.
			 */
			public void onClick(ClickEvent event) {
				logger.info(" Sending name to server logger ");
				System.out.println(" Sending name to server  sysout");
				console(" Sending name to server  console ");
				sendNameToServer();
			}

			/**
			 * Fired when the user types in the nameField.
			 */
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					logger.info(" Sending name to server logger ");
					System.out.println(" Sending name to server  sysout");
					console(" Sending name to server  console ");
					sendNameToServer();
				}
			}

			/**
			 * Send the name from the nameField to the server and wait for a response.
			 */
			private void sendNameToServer() {
				// First, we validate the input.
				errorLabel.setText("");
				String textToServer = nameField.getText();
				if (!FieldVerifier.isValidName(textToServer)) {
					errorLabel.setText("Please enter at least four characters");
					return;
				}

				// Then, we send the input to the server.
				sendButton.setEnabled(false);
				textToServerLabel.setText(textToServer);
				serverResponseLabel.setText("");
				greetingService.greetServer(textToServer, new AsyncCallback<String>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						dialogBox.setText("Remote Procedure Call - Failure");
						serverResponseLabel.addStyleName("serverResponseLabelError");
						serverResponseLabel.setHTML(SERVER_ERROR);
						dialogBox.center();
						closeButton.setFocus(true);
					}

					public void onSuccess(String result) {
						dialogBox.setText("Remote Procedure Call");
						serverResponseLabel.removeStyleName("serverResponseLabelError");
						serverResponseLabel.setHTML(result);
						dialogBox.center();
						closeButton.setFocus(true);
					}
				});
			}
		}

		// Add a handler to send the name to the server
		MyHandler handler = new MyHandler();
		sendButton.addClickHandler(handler);
		nameField.addKeyUpHandler(handler);
		
		injectEventListener(this);
	}
}
