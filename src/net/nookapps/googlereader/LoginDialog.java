/*******************************************************************************
 * 	Filename:	LoginDialog.java
 * 	Author:		Mackenzie Zastrow
 * 	Use:		nookReader (1.0b5)
 * 	Date:		Aug 8, 2010
 ********************************************************************************/
package net.nookapps.googlereader;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * @author zastrowm
 *
 */
public class LoginDialog extends Dialog{
	
	public String username, password, label;
	
	int currentTextBox = 0;
	
	public LoginDialog(Context parent){
		super(parent,android.R.style.Theme_Panel);		
		this.setContentView(R.layout.logindata);

		android.view.View.OnKeyListener okl = new View.OnKeyListener() {
			
			private final static int cancel = -3;
			private final static int submit = -8;
			private final static int clear = -13;
			private final static int enter = 66;

			public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
				if (keyEvent.getAction() == KeyEvent.ACTION_UP && view instanceof EditText) {
				
					EditText editTxt = (EditText) view;
					int selStart = editTxt.getSelectionStart();
					String textString = editTxt.getText().toString();
					
					switch (keyCode){
					case clear:
						editTxt.setText("");
						break;
					case cancel:
						LoginDialog.this.cancel();
						break;
					
					case KeyEvent.KEYCODE_ENTER:
					case submit:
						
						username = ((EditText) findViewById(R.id.EditText01)).getText().toString();
						password = ((EditText) findViewById(R.id.EditText02)).getText().toString();
						label = ((EditText) findViewById(R.id.EditText03)).getText().toString();
						
						LoginDialog.this.dismiss();
						break;
					case KeyEvent.KEYCODE_DEL:
						if (selStart > 0 && textString.length() > 0){
							editTxt.setText(textString.substring(0, selStart - 1)
									+ textString.substring(selStart,textString.length()));
							editTxt.setSelection(selStart - 1);
						}
						break;								
					case KeyEvent.KEYCODE_DPAD_LEFT:
						if (selStart > 0)
							editTxt.setSelection(selStart - 1);
						break;								
					case KeyEvent.KEYCODE_DPAD_RIGHT:
						if (selStart < textString.length())
							editTxt.setSelection(selStart + 1);
						break;
					case KeyEvent.KEYCODE_DPAD_UP:
						currentTextBox = (currentTextBox + 2) %3;
						changeFocus();
						break;
					case KeyEvent.KEYCODE_DPAD_DOWN:
						currentTextBox = (currentTextBox + 1) %3;
						changeFocus();
						break;
					default:
						if (keyCode >= 7 && keyCode <= 16){
							
							char letter = (char)('0' - 7 + keyCode);
							
							editTxt.setText(textString.substring(0, selStart)
									+ letter
									+ textString.substring(selStart,textString.length()));
							editTxt.setSelection(selStart + 1);
						} else 
							editTxt.setText(""+keyCode);
							break;
					}					
				}
				
				return true;
			}

		};

		((EditText) this.findViewById(R.id.EditText01)).setOnKeyListener(okl);
		((EditText) this.findViewById(R.id.EditText02)).setOnKeyListener(okl);
		((EditText) this.findViewById(R.id.EditText03)).setOnKeyListener(okl);
		
		InputMethodManager imm = (InputMethodManager) parent.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
	}
	
	@Override
	protected void onStart(){
		this.currentTextBox = 0;
	}
	
	public void changeFocus(){
		
		EditText next;
		
		switch(currentTextBox){
		default:
		case 0:
			next = ((EditText) findViewById(R.id.EditText01));
			break;
		case 1:
			next = ((EditText) findViewById(R.id.EditText02));
			break;
		case 2:
			next = ((EditText) findViewById(R.id.EditText03));
			break;
		}
		
		next.requestFocus();
		next.selectAll();
	}
	
}
