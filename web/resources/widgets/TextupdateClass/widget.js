jlab = jlab || {};
jlab.wedm = jlab.wedm || {};
jlab.wedm.classToObserverMap = jlab.wedm.classToObserverMap || {};
jlab.wedm.classToObserverMap['ActiveUpdateText'] = 'jlab.wedm.UpdateTextPvObserver';

jlab.wedm.UpdateTextPvObserverInit = function () {
    jlab.wedm.UpdateTextPvObserver = function (id, pvSet) {
        jlab.wedm.ControlTextPvObserver.call(this, id, pvSet);
        
        var $obj = $("#" + this.id);        
        
        this.noAlarmColor = $obj.attr('data-no-alarm-color') || jlab.wedm.noAlarmColor;
    };

    jlab.wedm.UpdateTextPvObserver.prototype = Object.create(jlab.wedm.ControlTextPvObserver.prototype);
    jlab.wedm.UpdateTextPvObserver.prototype.constructor = jlab.wedm.UpdateTextPvObserver;
};

jlab.wedm.initPvObserver('jlab.wedm.UpdateTextPvObserver', 'jlab.wedm.ControlTextPvObserver');