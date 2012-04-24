# Ti.TTS Module

## Description

Text to Speech for Android.

## Getting Started

View the [Using Titanium Modules](http://docs.appcelerator.com/titanium/2.0/#!/guide/Using_Titanium_Modules) document for instructions on getting
started with using this module in your application.

Note that there may be two versions of this module available to you, one for use with pre-1.8.0.1 SDKs and one for use with 1.8.0.1 or newer.
In your tiapp.xml file, make sure that you specify the version of the module that corresponds to the version of Titanium Mobile SDK that you are targeting.
For Appcelerator modules, specify the 1.X version of the module if building for versions of Titanium Mobile SDK prior to 1.8.0.1 and specify the 2.X version of the module if
building for versions of Titanium Mobile SDK 1.8.0.1 or newer.

## Accessing the Ti.TTS Module

To access this module from JavaScript, you would do the following:

	var TTS = require('ti.tts');


## Methods

### void start(dictionary args)

Prepares the TTS module to speak. If TTS is not installed on the device, the market will be opened. (After installing
TTS, you must prompt users to restart the app.)

Takes a dictionary with the following keys:

- function ready: A function to call once TTS has been successfully enabled.
- function failed [optional]: A function to call if TTS fails to start, most likely because it is not installed. Prompt the user to restart after installing TTS.
- function doneSpeaking [optional]: A function to call each time a speech finishes. Because calls to the speak and pause methods are asynchronous, this allows you to detect when a speech is finished.
- constant language [optional, defaults to TTS.LOCALE_US]: The language to speak in.

### void speak(dictionary args)

Asynchronously queue a speech.

Takes a dictionary with the following keys:

- string text: The text to speak.
- constant queue [optional, defaults to TTS.QUEUE_ADD]: Whether or not to interrupt other speeches before speaking.
- constant stream [optional, defaults to TTS.STREAM_SYSTEM]: Which audio stream to speak in to.
- string id [optional, defaults to id#]: The unique identifier for the speech, which will be passed to the doneSpeaking function.

### void pause(dictionary args)

Asynchronously pauses speaking.

Takes a dictionary with the following keys:

- int milliseconds: The amount of time to pause.
- constant queue [optional, defaults to TTS.QUEUE_ADD]: Whether or not to interrupt other speeches before pausing.
- constant stream [optional, defaults to TTS.STREAM_SYSTEM]: Which audio stream to pause in to.
- string id [optional, defaults to id#]: The unique identifier for the pause, which will be passed to the doneSpeaking function.

### void shutdown()
 
Releases our exclusive lock on TTS so that other apps may use it again. Also cleans up resources. You may call "start" again after calling this method.


## Constants

### Queue Modes

- int QUEUE_FLUSH: Empties the queue, and then begins this speech right away.
- int QUEUE_ADD: Places self in to the queue, waiting until previously queued speeches finish before speaking.

### Locales

- String LOCALE_CANADA
- String LOCALE_CANADA_FRENCH 
- String LOCALE_CHINA
- String LOCALE_CHINESE
- String LOCALE_ENGLISH
- String LOCALE_FRANCE
- String LOCALE_FRENCH
- String LOCALE_GERMAN
- String LOCALE_GERMANY
- String LOCALE_ITALIAN
- String LOCALE_ITALY
- String LOCALE_JAPAN
- String LOCALE_JAPANESE
- String LOCALE_KOREA
- String LOCALE_KOREAN
- String LOCALE_PRC
- String LOCALE_SIMPLIFIED_CHINESE
- String LOCALE_TAIWAN
- String LOCALE_TRADITIONAL_CHINESE
- String LOCALE_UK
- String LOCALE_US

### Stream Modes

- String STREAM_ALARM
- String STREAM_MUSIC
- String STREAM_DTMF
- String STREAM_NOTIFICATION
- String STREAM_RING
- String STREAM_SYSTEM
- String STREAM_VOICE_CALL


## Usage
See example.


## Author
Dawson Toth

## Module History

View the [change log](changelog.html) for this module.

## Feedback and Support
Please direct all questions, feedback, and concerns to [info@appcelerator.com](mailto:info@appcelerator.com?subject=Android%20TTS%20Module).

## License
Copyright(c) 2010-2011 by Appcelerator, Inc. All Rights Reserved. Please see the LICENSE file included in the distribution for further details.