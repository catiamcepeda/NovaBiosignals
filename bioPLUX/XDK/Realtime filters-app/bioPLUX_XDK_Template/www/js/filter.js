/*jslint browser:true, devel:true, white:true, vars:true */
/*global $:false, intel:false app:false, dev:false, cordova:false */


/* ECG FILTER 
GET BPM IN REAL TIME */ 

/* RESP FILTER 
GET BREATHS/MIN IN REAL TIME 
DIFFERS CHEST AND ABDOMINAL RESPIRATION */

/* EMG FILTER
GET TENSE LEVEL BY TRAPEZIUS MUSCLE (RIGHT AND LEFT) */

/* EDA FILTER
GET THE VALUE OF EVENTS */ 


var signal = function(){
    this.input0 = 0;
    this.inputhp0 = 0;
    this.outputlp = 0;
    this.output = 0;
    this.peaksMax = [];
    this.pond = [];
    this.peakRange = [];
    this.timeRange = [];
    this.tempBeats = [];
    this.beats = [];
    this.tbeat = 0;
    this.beat = 0;
    this.flag = 0;
    this.ioutlier = 0; 
    this.calInput = [];
    this.flagCal = 0;
    this.RV = 0;
    this.sumRV = 0;
    this.averageRV = 0;
    this.k = 0;
    this.th = -10000;
    this.mean = -10000; 
    this.min = 0;
    this.events = [];
};

signal.prototype.bandpass = function(signal, fc_hp, fc_lp, fs){
    var alpha_lp = 2*Math.PI*fc_lp/(2*Math.PI*fc_lp+fs);
    var alpha_hp = 1/(1+(2*Math.PI*fc_hp/fs));
    var outputlp = alpha_lp*signal+(1-alpha_lp)*this.input0;
    this.output = alpha_hp*this.output+alpha_hp*(outputlp-this.input0);
    this.input0 = outputlp;
};

signal.prototype.lowpass = function(signal, fc, fs){
    var alpha = 2*Math.PI*fc/(2*Math.PI*fc+fs);
    this.outputlp = alpha*signal+(1-alpha)*this.outputlp;
};

signal.prototype.highpass = function(signal, fc, fs){
    var alpha = 1/(1+(2*Math.PI*fc/fs));
    this.output = alpha * this.output + alpha * (signal-this.inputhp0);
    this.inputhp0 = signal;
};

signal.prototype.calibration = function(signal, fs){
    if (this.flagCal === 0) {
        this.calInput.push(signal); 
        if (this.calInput.length == fs*7){
            var max = Math.max.apply(null, this.calInput);
            var min = Math.min.apply(null, this.calInput);
            this.mean = (max+min)/2; 
            this.th = max*0.4;  
            this.flagCal = 1;
            this.calInput = [];
        }
    }    
};

signal.prototype.calibrationEMG = function(signal, fs){
    var out = 0;
    if (this.flagCal === 0) {
        this.calInput.push(signal); 
        if (this.calInput.length == fs*7){
            for (var i=1; i < fs*7; i++){
                if ((Math.abs(this.calInput[i] - this.calInput[i-1]) > 1) || (Math.abs(this.calInput[fs*7-1] - this.calInput[0]) > 1)){
                    out = out + 1; 
                }
            }
            if (out === 0){
                var max = Math.max.apply(null, this.calInput);
                var min = Math.min.apply(null, this.calInput);
                this.mean = (max+min)/2;
                this.calInput = [];
                this.flagCal = 1;
            }
            else{
                this.calInput.shift();
            }
        }   
    }
};

signal.prototype.getPeaks = function(signal, delta, time) {
    if (delta != -10000){
        if (signal > delta){
            this.peakRange.push(signal);
            this.timeRange.push(time); 
            this.flag = 1;
        }

        if ((signal < delta) && (this.flag == 1)){
            this.flag = 0;
            this.beat = Math.max.apply(null, this.peakRange);
            this.tbeat = this.timeRange[this.peakRange.indexOf(this.beat)];
            this.beats.push(this.beat);
            this.tempBeats.push(this.tbeat);
            this.peaksMax.push(this.tbeat);
            this.peakRange = [];
            this.timeRange = [];
        }  
    }
};

signal.prototype.IHR = function(peaks, min, max){
    if (peaks.length === 2){
        var tachogram = peaks[1] - peaks[0];
        peaks.shift();
        if (tachogram > min && tachogram < max){
            this.RV = 60/tachogram;
            this.k++;
            this.sumRV += this.RV;
            this.averageRV = this.sumRV/this.k;   
        }
        else{
            this.ioutlier++;
        }
    }  
};

signal.prototype.getEvents = function(peaks, tempBeats, beats, min){
    if (tempBeats.length === 2){
        var tachogram = tempBeats[1]-tempBeats[0];
        tempBeats.shift();
        if (tachogram > min){
            var mean = 0;
            var maxEvent = Math.max.apply(null, beats);
            var k = beats.indexOf(maxEvent); 
            var maxP = 2*(Math.max.apply(null, peaks) - Math.min.apply(null, peaks));
            for (var j=0; j < beats.length-1; ++j){
                var dif = Math.abs(peaks[k]-peaks[j]); 
                this.pond.push(beats[j]*(1-dif/maxP));
            }
            for(var p=0; p < this.pond.length-1; ++p){
                mean += this.pond[p]/this.pond.length;
            }
            this.events.push(mean.toFixed(1));
            beats = [];
            peaks = [];         
            this.pond = [];
        }   
    }
};

var ecg = new signal();
var respC = new signal();
var respA = new signal();
var emgL = new signal();
var emgR = new signal();
var eda = new signal();

function processECG(inputECG, fs, time){    
    inputECG = inputECG - 2048;
    
    //ecg.bandpass(inputECG, 5, 8, fs);
    ecg.bandpass(inputECG, 5, 15, fs);
    ecg.calibration(ecg.output, fs);
    ecg.getPeaks(ecg.output, ecg.th, time);
    ecg.IHR(ecg.peaksMax, 0.4, 2);   
    document.getElementById("outlierECG").innerHTML = inputECG + " " + ecg.output;
    if (ecg.th != -10000){
        document.getElementById("outlierECG").innerHTML = ecg.ioutlier+ecg.calInput;
        document.getElementById("maxECG").innerHTML = "Max ECG: " + ecg.th.toFixed(1);
        document.getElementById("RV_ECG").innerHTML = "Beats per minute: " + ecg.RV.toFixed(1);
        document.getElementById("MRV_ECG").innerHTML = "Beats per minute (mean): " + ecg.averageRV.toFixed(1);
    }
    
    return ecg.output;
}

function processRESP(inputB_C, inputB_A, fs, time){
    var mainResp; 
    
    inputB_C = (inputB_C - 2048)*-1;
    inputB_A = (inputB_A - 2048)*-1;
    
    respC.bandpass(inputB_C, 0.1, 0.3, fs);
    respA.bandpass(inputB_A, 0.1, 0.3, fs);
    respC.calibration(respC.output, fs);
    respA.calibration(respA.output, fs);
    respC.getPeaks(respC.output, respC.mean, time);
    respA.getPeaks(respA.output, respA.mean, time);
    respC.IHR(respC.peaksMax, 2, 10);   
    respA.IHR(respA.peaksMax, 2, 10);   
    
    if (respA.mean != -10000){
        if (Math.abs(respA.beat - respA.mean) > Math.abs(respC.beat-respC.mean)){
            mainResp = "Abdominal";
        }
        else{
            mainResp = "Chest";
        }

        document.getElementById("maxB").innerHTML = "MeanB: " + respC.mean.toFixed(1) + "; " + respA.mean.toFixed(1);
        document.getElementById("outlierB").innerHTML = mainResp + " " + respC.ioutlier;
        document.getElementById("RV_B").innerHTML = "Breaths per minute: " + respC.RV.toFixed(1);
        document.getElementById("MRV_B").innerHTML = "Breaths per minute (mean): " + respC.averageRV.toFixed(1);
    }
    
    return [respC.output, respA.output];
}

function processEMG(inputEMGR, inputEMGL, fs, time){
    inputEMGR = inputEMGR - 2048;
    inputEMGR = Math.abs(inputEMGR);
    inputEMGL = inputEMGL - 2048;
    inputEMGL = Math.abs(inputEMGL);
    
    emgL.lowpass(inputEMGL, 0.3, fs); 
    emgR.lowpass(inputEMGR, 0.3, fs);
    emgL.calibrationEMG(emgL.outputlp, fs);
    emgR.calibrationEMG(emgR.outputlp, fs);
    
    var minR = emgR.mean + 0.1;
    var minL = emgL.mean + 0.1;
    
    if (minR != 0.1 || minL != 0.1){
        document.getElementById("min").innerHTML = "Max EMG: " + minR.toFixed(1) + "; " + minL.toFixed(1);
        if (emgR.outputlp > minR){
            document.getElementById("emgR").innerHTML = "R relax";
        }
        else{
            document.getElementById("emgR").innerHTML = "R well done";   
        }
        if (emgL.outputlp > minL){
            document.getElementById("emgL").innerHTML = "L relax";
        }
        else{
            document.getElementById("emgL").innerHTML = "L well done";   
        }
    }
    
    return [emgR.outputlp, emgL.outputlp];
}

function processEDA(inputEDA, fs, time){
    var thEDA = 20;  
    inputEDA = inputEDA - 2048;
    
    eda.bandpass(inputEDA, 0.2, 0.5, fs);
    eda.getPeaks(eda.output, thEDA, time);
    eda.getEvents(eda.peaksMax, eda.tempBeats, eda.beats, 10);
    
    if (eda.events.length !== 0){
        document.getElementById("eda").innerHTML = "EDA stress: " + eda.events;
    }
    
    return eda.output;
}