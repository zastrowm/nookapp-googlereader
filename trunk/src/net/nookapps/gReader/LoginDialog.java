/*******************************************************************************
 * 	Filename:	LoginDialog.java
 * 	Author:		Mackenzie Zastrow
 * 	Use:		nookReader
 * 	Date:		Aug 31, 2010
 ********************************************************************************/
package net.nookapps.gReader;

import android.content.Context;
import android.widget.EditText;
import net.nookapps.CustomDialog;

/**
 * @author zastrowm
 *
 */
public class LoginDialog extends CustomDialog {

	public String username = null, password = null, label = null;
	
	/**
	 * @param context
	 * @param resourceID
	 * @param theListener
	 */
	public LoginDialog(Context context, OnDismissedDialogListener theListener) {
		super(context, R.layout.logindata, theListener);
	}

	/* (non-Javadoc)
	 * @see net.nookapps.CustomDialog#onKeyboardClose(net.nookapps.CustomDialog, boolean)
	 */
	@Override
	public void onKeyboardClose(CustomDialog dialog, boolean byCancel) {
		if (!byCancel){
			username = ((EditText) findViewById(R.id.EditText01)).getText().toString().trim();
			password = ((EditText) findViewById(R.id.EditText02)).getText().toString().trim();
			label = ((EditText) findViewById(R.id.EditText03)).getText().toString().trim();
		}

	}

}
