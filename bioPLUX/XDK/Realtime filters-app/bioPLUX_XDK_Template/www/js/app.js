/*jslint browser:true, devel:true, white:true, vars:true */
/*global $:false, intel:false app:false, dev:false, cordova:false */

var macAddress = "00:07:80:79:6F:97";
document.getElementById("idMacAddress").innerHTML = macAddress;

$("#idSetMacAddress").val(macAddress).change(function () {
    macAddress = document.getElementById("idSetMacAddress").value;
    document.getElementById("idMacAddress").innerHTML = macAddress;
});

var fs = 1000;
$("#idSamplingFrequency").val(fs).change(function () {
    var v = $(this).val();
	if (v && !isNaN(+v)) {
        fs = +v;
	}
});

var nChannels = 8;
var bCh1 = 1, bCh2 = 0, bCh3 = 0, bCh4 = 0, bCh5 = 0, bCh6 = 0, bCh7 = 0, bCh8 = 0;
$("#idCh1").change(function () {
    if(this.checked) { bCh1 = 1; }
    else{ bCh1 = 0; }
    
    setChannels();
});
$("#idCh2").change(function () {
    if(this.checked) { bCh2 = 1; }
    else{ bCh2 = 0; }
    
    setChannels();
});
$("#idCh3").change(function () {
    if(this.checked) { bCh3 = 1; }
    else{ bCh3 = 0; }
         
    setChannels();
});
$("#idCh4").change(function () {
    if(this.checked) { bCh4 = 1; }
    else{ bCh4 = 0; }
    
    setChannels();
});
$("#idCh5").change(function () {
    if(this.checked) { bCh5 = 1; }
    else{ bCh5 = 0; }
    
    setChannels();
});
$("#idCh6").change(function () {
    if(this.checked) { bCh6 = 1; }
    else{ bCh6 = 0; }
    
    setChannels();
});
$("#idCh7").change(function () {
    if(this.checked) { bCh7 = 1; }
    else{ bCh7 = 0; }
    
    setChannels();
});
$("#idCh8").change(function () {
    if(this.checked) { bCh8 = 1; }
    else{ bCh8 = 0; }
    
    setChannels();
});

var binaryChannels = "00000001"; //bitMask
var channels = parseInt(binaryChannels, 2);

function setChannels (){
    binaryChannels = bCh8+ "" + bCh7+ "" + bCh6+ "" + bCh5 + "" + bCh4 + "" + bCh3 + "" + bCh2+ "" + bCh1;
    channels = parseInt(binaryChannels, 2);
}  

var nbits = 12;
$("#idBits").val(nbits).change(function () {
	var v = $(this).val();
	if (v && !isNaN(+v)) {
        nbits = +v;
    }
});

var nFrames = 5;
$("#idNFrames").val(nFrames).change(function (){
    var v = $(this).val();
    if(v && !isNaN(+v)){
        nFrames = +v;
    }
});

var flag_connection = false;
document.getElementById("idPower").style.color = "#E80000";

function idConnection(){
    "use strict";
    var fName = "idConnection()";
    console.log(fName,"function entry");
    
    if (flag_connection === false) {
        window.bioplux.beginAcq(onSuccess(fName),onError(fName),macAddress, fs, channels, nbits, nFrames);
        window.bioplux.getData(getFrames,onError); //Channel for the information transmition 

        count = 0;
        
        document.getElementById("idPower").style.color = "#00CC00";
        flag_connection = true;
    }
    else {
        window.bioplux.endAcq(onSuccess(fName), onError(fName));        
        
        document.getElementById("idPower").style.color = "#E80000";
        flag_connection = false;  
    }   
    
}

//Receiving data and draw canvas
var len = 0, count = 0, shift = 10, deltaX = 0.5, posY, pResult = 0, time = 0;

var canvasHeight = 0.50 * window.screen.height;
var canvasWidth = window.screen.width - shift*2;

var canvas = document.getElementById('idCanvas');

canvas.height = canvasHeight;
canvas.width = canvasWidth;

var context = canvas.getContext('2d');


function getFrames(result){
    for(var f = 0; f < result.length; f += nChannels){
        var outputECG = processECG(result[f], fs, time);
        /*var outputRESP = processRESP(result[f+1], result[f+2], fs, time);
        var outputECG = processECG(result[f+3], fs, time);
        var outputEMG = processEMG(result[f+4], result[f+6], fs);
        var outputEDA = processEDA(result[f+6], fs, time);*/
        time = time + 1/fs;
        if(count===0){
            context.clearRect(0, 0, canvas.width, canvas.height);
        }
    
        ++count;
        var n = shift + count * deltaX;

        //Ch1
        posY  = canvas.height - ((result[f] * canvas.height) * 1/4096);

        if(count === 1){
            pResult = posY;
        }
        else{
            context.beginPath();
            context.moveTo(n - deltaX, pResult);
            context.lineTo(n, posY );
            context.strokeStyle = '#1f64ff';
            context.stroke();
            pResult = posY;
        }

        
        if(count*deltaX === canvas.width){
            count = 0;
        }

    }
}


function onSuccess(fName){
    console.log(fName,"runned with success");
}

function onError(fName){
    console.log(fName,"someting went wrong");
}


