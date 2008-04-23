package org.apache.axiom.attachments.lifecycle;

import java.io.IOException;

public interface DataHandlerExt {
	
	/**
	 * This method will give users an option to trigger a purge
	 * on temporary attachment files. Temp files are created for
	 * attachment data that is greater than a threshold limit. 
	 * On client side These temp attachment files are not deleted 
	 * untilthe virtual machine exits as user can choose to read 
	 * this dataHandler. So if user is not going to use the data 
	 * handlers provided on this temproray files they can choose 
	 * to purge the file. 
	 */
	public void purgeDataSource() throws IOException;
	
	/**
	 * This method will give users an option to trigger a delete on 
	 * temporary attachment file when DataHandler associated with the 
	 * attachment is read once. Temp files are created for
	 * attachment data that is greater than a threshold limit. 
	 * On client side These temp attachment files are not deleted untill
	 * the virtual machine exits. This method gives options to user to 
	 * trigger a delete on attachment files when they read the dataHandler
	 * once.
	 */
	
	public void deleteWhenReadOnce() throws IOException;
}
