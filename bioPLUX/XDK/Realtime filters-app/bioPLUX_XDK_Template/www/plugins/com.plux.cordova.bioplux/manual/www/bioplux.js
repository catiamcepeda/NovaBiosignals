var exec = require('cordova/exec');

/**
 * Create a new instance of BioPlux(Plugin).
 *
 * @class       BioPlix
 * @classdesc  BioPluxPlugin for cordova 3.0.0+
 */
var BioPlux = function()
{
    this.platforms = [ "android" ];
};




/**
 * Begin Bioplux acquisition
 */
BioPlux.prototype.beginAcq = function(onSuccess, onError, macAddress, fs, channels, nbits, nframes)
{
    exec(   function(result){ alert("beginAcq " + result); },
            function(error){ alert("beginAcq " + error); },
            "BioPlux",
            "beginAcq",
            [macAddress, fs, channels, nbits, nframes]);
}


/**
 * Check if the API is supported on this platform. Requires the
 * cordova-plugin-device to function on PhoneGap 3.0.0 onwards.
 *
 * @memberOf Bluetooth
 *
 * @returns {boolean}
 */
BioPlux.prototype.isSupported = function()
{
    if(window.device)
    {
        var platform = window.device.platform;
        if(platform !== undefined && platform !== null)
        {
            return (this.platforms.indexOf(platform.toLowerCase()) >= 0);
        }
    }
    return false;
}


/**
 * Stop Bioplux acquisition
 */
BioPlux.prototype.endAcq = function(onSuccess, onError)
{
    exec(onSuccess, onError, "BioPlux", "endAcq", []);
}


/**
 * Transmits the frames from de bioPLUX API to the app
 * */
BioPlux.prototype.getData = function(onSuccess, onError)
{
    exec(onSuccess, onError, "BioPlux", "getData", []);
}


var bioplux		= new BioPlux();
module.exports  = bioplux;
