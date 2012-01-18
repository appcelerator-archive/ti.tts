/**
 * Ti.TTS Module
 * Copyright (c) 2010-2011 by Appcelerator, Inc. All Rights Reserved.
 * Please see the LICENSE included with this distribution for details.
 */
package ti.tts;

import java.util.HashMap;
import java.util.Locale;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;

import android.app.Activity;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;

@Kroll.module(name = "Tts", id = "ti.tts")
public class TtsModule extends KrollModule implements OnUtteranceCompletedListener {

	// Private Variables

	private static final String LCAT = "TtsModule";

	private int optionIncrement = 0;
	private boolean started;
	private Locale locale;
	private TextToSpeech engine;
	private KrollFunction ready;
	private KrollFunction failed;
	private KrollFunction doneSpeaking;

	public TtsModule() {
		super();
		started = false;
	}

	// Callbacks

	@Override
	public void onUtteranceCompleted(String id) {
		Log.i(LCAT, "Completed utterance " + id + "!");
		if (doneSpeaking != null) {
			HashMap<String, String> args = new HashMap<String, String>();
			args.put("id", id);
			doneSpeaking.callAsync(getKrollObject(), args);
		}
	}

	public void handleTTSResultCode(int resultCode) {
		if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
			instantiateEngine();
		} else {
			tryInstall();
		}
	}

	// Utility Methods

	private Locale convertStringToLocale(String locale) {
		Locale[] available = Locale.getAvailableLocales();
		for (Locale current : available) {
			if (current.toString().equals(locale)) {
				return current;
			}
		}
		Log.e(LCAT, "Provided locale is not available on this device: " + locale);
		return Locale.US;
	}

	private HashMap<String, String> getOptions(KrollDict dict) {
		HashMap<String, String> retVal = new HashMap<String, String>();
		if (dict == null) {
			return retVal;
		}
		retVal.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, dict.optString("id", "id" + optionIncrement++));
		retVal.put(TextToSpeech.Engine.KEY_PARAM_STREAM, dict.optString("stream", STREAM_SYSTEM));
		return retVal;
	}

	private void instantiateEngine() {
		Activity activity = TiApplication.getAppCurrentActivity();
		final TtsModule self = this;
		engine = new TextToSpeech(activity, new OnInitListener() {

			@Override
			public void onInit(int arg0) {
				int lang = engine.isLanguageAvailable(locale);
				if (lang == TextToSpeech.LANG_MISSING_DATA || lang == TextToSpeech.LANG_NOT_SUPPORTED) {
					if (failed != null) {
						HashMap<String, String> error = new HashMap<String, String>();
						error.put("error", lang == TextToSpeech.LANG_MISSING_DATA ? "LANG_MISSING_DATA" : "LANG_NOT_SUPPORTED");
						failed.callAsync(getKrollObject(), error);
					}
					Log.e(LCAT, locale.toString() + " is not available!");
					return;
				}
				engine.setLanguage(locale);
				engine.setOnUtteranceCompletedListener(self);
				started = true;
				if (ready != null) {
					ready.callAsync(getKrollObject(), new HashMap<String, String>());
				}
			}

		});
	}

	private void tryInstall() {
		Activity activity = TiApplication.getAppCurrentActivity();
		Intent installIntent = new Intent();
		installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
		activity.startActivity(installIntent);
		// TODO: listen for result to see if we can restart the engine after installation?
		failNotInstalled();
	}

	private void failNotInstalled() {
		if (failed != null) {
			HashMap<String, String> error = new HashMap<String, String>();
			error.put("error", "TTS_NOT_INSTALLED");
			failed.callAsync(getKrollObject(), error);
		}
		Log.e(LCAT, "TTS is not yet installed!");
	}

	// Public Methods

	@Kroll.method
	public void start(@SuppressWarnings("rawtypes") HashMap map) {
		if (map == null) {
			Log.e(LCAT, "No arguments provided to the start method!");
			return;
		}

		if (started) {
			Log.e(LCAT, "TTS has already been started! Please call the 'shutdown' method before trying to start it again!");
			return;
		}

		started = false;

		try {
			@SuppressWarnings("unchecked")
			KrollDict dict = new KrollDict(map);
			String language = dict.optString("language", Locale.US.toString());
			locale = convertStringToLocale(language);
			ready = dict.containsKey("ready") ? (KrollFunction) dict.get("ready") : null;
			failed = dict.containsKey("failed") ? (KrollFunction) dict.get("failed") : null;
			doneSpeaking = dict.containsKey("doneSpeaking") ? (KrollFunction) dict.get("doneSpeaking") : null;

			Activity activity = TiApplication.getAppCurrentActivity();
			CheckTTS.setParent(this);
			activity.startActivity(new Intent(activity, CheckTTS.class));
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(LCAT, "Hit exception when starting!");
		}
	}

	@Kroll.method
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void speak(HashMap args) {
		try {
			if (!started) {
				Log.e(LCAT, "TTS has not been started yet! Please call the 'start' method and wait for it to finish.");
				return;
			}
			if (args == null) {
				Log.e(LCAT, "No options provided to speak!");
				return;
			}
			KrollDict dict = new KrollDict(args);
			String text = dict.getString("text");
			int queue = dict.optInt("queue", QUEUE_ADD);
			engine.speak(text, queue == QUEUE_ADD ? TextToSpeech.QUEUE_ADD : TextToSpeech.QUEUE_FLUSH, getOptions(dict));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Kroll.method
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void pause(HashMap args) {
		try {
			if (!started) {
				Log.e(LCAT, "TTS has not been started yet! Please call the 'start' method and wait for it to finish.");
				return;
			}
			if (args == null) {
				Log.e(LCAT, "No options provided to speak!");
				return;
			}
			KrollDict dict = new KrollDict(args);
			int milliseconds = dict.getInt("milliseconds");
			int queue = dict.optInt("queue", QUEUE_ADD);
			engine.playSilence(milliseconds, queue == QUEUE_ADD ? TextToSpeech.QUEUE_ADD : TextToSpeech.QUEUE_FLUSH, getOptions(dict));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Kroll.method
	public void shutdown() {
		if (!started) {
			Log.e(LCAT, "TTS has not been started yet! Please call the 'start' method and wait for it to finish.");
			return;
		}
		started = false;
		engine.shutdown();
		engine = null;

		optionIncrement = 0;
		locale = null;
		ready = null;
		failed = null;
		doneSpeaking = null;
	}

	// Public Constants

	// Queue Modes
	@Kroll.constant
	public static final int QUEUE_FLUSH = 1;
	@Kroll.constant
	public static final int QUEUE_ADD = 0;

	// Locales
	@Kroll.constant
	public static final String LOCALE_CANADA = "en_CA";
	@Kroll.constant
	public static final String LOCALE_CANADA_FRENCH = "fr_CA";
	@Kroll.constant
	public static final String LOCALE_CHINA = "zh_CN";
	@Kroll.constant
	public static final String LOCALE_CHINESE = "zh";
	@Kroll.constant
	public static final String LOCALE_ENGLISH = "en";
	@Kroll.constant
	public static final String LOCALE_FRANCE = "fr_FR";
	@Kroll.constant
	public static final String LOCALE_FRENCH = "fr";
	@Kroll.constant
	public static final String LOCALE_GERMAN = "de";
	@Kroll.constant
	public static final String LOCALE_GERMANY = "de_DE";
	@Kroll.constant
	public static final String LOCALE_ITALIAN = "it";
	@Kroll.constant
	public static final String LOCALE_ITALY = "it_IT";
	@Kroll.constant
	public static final String LOCALE_JAPAN = "ja_JP";
	@Kroll.constant
	public static final String LOCALE_JAPANESE = "ja";
	@Kroll.constant
	public static final String LOCALE_KOREA = "ko_KR";
	@Kroll.constant
	public static final String LOCALE_KOREAN = "ko";
	@Kroll.constant
	public static final String LOCALE_PRC = "zh_CN";
	@Kroll.constant
	public static final String LOCALE_SIMPLIFIED_CHINESE = "zh_CN";
	@Kroll.constant
	public static final String LOCALE_TAIWAN = "zh_TW";
	@Kroll.constant
	public static final String LOCALE_TRADITIONAL_CHINESE = "zh_TW";
	@Kroll.constant
	public static final String LOCALE_UK = "en_GB";
	@Kroll.constant
	public static final String LOCALE_US = "en_US";

	// Stream Modes
	@Kroll.constant
	public static final String STREAM_ALARM = "4";
	@Kroll.constant
	public static final String STREAM_MUSIC = "3";
	@Kroll.constant
	public static final String STREAM_DTMF = "8";
	@Kroll.constant
	public static final String STREAM_NOTIFICATION = "5";
	@Kroll.constant
	public static final String STREAM_RING = "2";
	@Kroll.constant
	public static final String STREAM_SYSTEM = "1";
	@Kroll.constant
	public static final String STREAM_VOICE_CALL = "0";

}
