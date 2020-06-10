jlab = jlab || {};
jlab.wedm = jlab.wedm || {};
jlab.wedm.classToObserverMap = jlab.wedm.classToObserverMap || {};
jlab.wedm.classToObserverMap['ActiveMenuButton'] = 'jlab.wedm.MenuButtonPvObserver';

jlab.wedm.MenuButtonPvObserverInit = function () {
    jlab.wedm.MenuButtonPvObserver = function (id, pvSet) {
        jlab.wedm.ControlTextPvObserver.call(this, id, pvSet);
    };

    jlab.wedm.MenuButtonPvObserver.prototype = Object.create(jlab.wedm.ControlTextPvObserver.prototype);
    jlab.wedm.MenuButtonPvObserver.prototype.constructor = jlab.wedm.MenuButtonPvObserver;

    jlab.wedm.MenuButtonPvObserver.prototype.handleIndicatorUpdate = function () {

    };
};

jlab.wedm.initPvObserver('jlab.wedm.MenuButtonPvObserver', 'jlab.wedm.ControlTextPvObserver');