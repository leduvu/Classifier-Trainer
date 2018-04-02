// ISMLA
// WiSe 2017/2018
// Classifier Project

package de.ws1718.ismla.client;

import java.io.IOException;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("trainer")
public interface UseServerFunctions extends RemoteService {

// Synchronous Function to read file (implemented in GreetingServiceImpl)
	boolean read() throws IOException;
	MaskedText covering(String text, String lang, boolean a, boolean b1, boolean b2, boolean c1)  throws IOException;;
}
