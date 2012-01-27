var win = Ti.UI.createWindow({
    backgroundColor:'white',
    exitOnClose:true,
    navBarHidden:true
});
win.add(Ti.UI.createLabel({
    text:'Starting up TTS...'
}));
win.open();

var TTS = require('ti.tts');
TTS.start({
    language:TTS.LOCALE_US,
    ready:function () {

        TTS.speak({
            text:'This speech interrupts all other speeches.',
            queue:TTS.QUEUE_FLUSH
        });
        TTS.speak({
            text:'But this speech is queued.',
            queue:TTS.QUEUE_ADD
        });
        TTS.speak({
            text:'Queuing is the default behavior.'
        });
        TTS.speak({
            text:'We can speak to different streams as well.'
        });
        TTS.speak({
            text:'Such as the alarm stream.',
            stream:TTS.STREAM_ALARM
        });
        TTS.speak({
            text:'Or the music stream.',
            stream:TTS.STREAM_MUSIC
        });
        TTS.speak({
            text:'Speeches can be differentiated with string identifiers.',
            id:'wow'
        });
        TTS.pause({
            milliseconds:1000
        });
        TTS.speak({
            text:'When you are done, make sure you call T T S dot shutdown.',
            id:'done'
        });
        Ti.API.info('Note that calls to speak and pause are asynchronous.');
    },
    doneSpeaking:function (args) {
        if (args.id == 'wow') {
            Ti.API.info('The speech with id "wow" just finished!');
        }
        else if (args.id == 'done') {
            TTS.shutdown();
        }
    },
    failed:function () {
        Ti.UI.createAlertDialog({
            title:'TTS Not Installed',
            message:'Relaunch this app after installing TTS, please!'
        }).show();
    }
});