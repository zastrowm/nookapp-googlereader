/*******************************************************************************
 * 	Filename:	AbstractDialog.java
 * 	Author:		Mackenzie Zastrow
 * 	Use:		nookReader
 * 	Date:		Aug 30, 2010
 ********************************************************************************/
package net.nookapps;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;

/**
 * @author zastrowm
 *
 */
public abstract class CustomDialog extends Dialog{ 

	OnDismissedDialogListener listener;
	/**
	 * @param context
	 */
	public CustomDialog(Context context,int resourceID,OnDismissedDialogListener theListener) {
			super(context,android.R.style.Theme_Panel);	
			this.setContentView(resourceID);
			this.setOnKeyListener(new KeyListener());
			this.listener = theListener;
		}
	
	class KeyListener implements DialogInterface.OnKeyListener{
		private final static int cancel = -3;
		private final static int submit = -8;
		
		@Override
		public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent keyEvent) {
					
			if (keyEvent.getAction() == KeyEvent.ACTION_UP) {

				switch (keyCode) {
				case cancel:
					onKeyboardClose(CustomDialog.this,true);
					listener.onDismissedDialog(CustomDialog.this,true);
					cancel();
					break;

				case submit:
					onKeyboardClose(CustomDialog.this,false);
					listener.onDismissedDialog(CustomDialog.this,false);
					dismiss();
					break;
				default:
					return false;
				}

			}

			return false;
		}

	};
	
	public static interface OnDismissedDialogListener{
		public void onDismissedDialog(CustomDialog dialog,boolean byCancel);
	}
	
	public abstract void onKeyboardClose(CustomDialog dialog,boolean byCancel);
	
	
}
