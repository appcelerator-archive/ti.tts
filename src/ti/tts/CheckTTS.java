/**
 * Ti.TTS Module
 * Copyright (c) 2010-2011 by Appcelerator, Inc. All Rights Reserved.
 * Please see the LICENSE included with this distribution for details.
 */
package ti.tts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;

public class CheckTTS extends Activity {

	private static TtsModule parent;

	public static void setParent(TtsModule parent) {
		CheckTTS.parent = parent;
	}

	int requestCode = 9998;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent checkIntent = new Intent();
		checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(checkIntent, requestCode);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == this.requestCode) {
			parent.handleTTSResultCode(resultCode);
		}
		finish();
	}
}