// ISMLA
// WiSe 2017/2018
// Classifier Project

package de.ws1718.ismla.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface UseServerFunctionsAsync {

	void read(AsyncCallback<Boolean> callback);

	void covering(String text, String lang, boolean a, boolean b1, boolean b2, boolean c1, AsyncCallback<MaskedText> callback);

	
}
