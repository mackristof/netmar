/**
 * SEXTANT OVERRIDE: previously uses getUTC.. functions
 */
Date.prototype.getISOString = function(showTimes) {
    var ISOString = this.getFullYear() + '-' + prepareDate(this.getMonth(), this.getDate());
    
    if (showTimes) {
        var hours = this.getHours();
        var minutes = this.getMinutes();
        var seconds = this.getSeconds();
        ISOString += 'T' + (hours >= 10 ? "" : "0") + hours + ':'
                + (minutes >= 10 ? "" : "0") + minutes + ':'
                + (seconds >= 10 ? "" : "0") + seconds + 'Z'; 
    }   
    
    return ISOString;
}
