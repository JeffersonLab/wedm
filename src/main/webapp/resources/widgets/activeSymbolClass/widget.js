jlab = jlab || {};
jlab.wedm = jlab.wedm || {};
jlab.wedm.classToObserverMap = jlab.wedm.classToObserverMap || {};
jlab.wedm.classToObserverMap['ActiveSymbol'] = 'jlab.wedm.SymbolPvObserver';

jlab.wedm.SymbolPvObserverInit = function () {
    jlab.wedm.SymbolPvObserver = function (id, pvSet) {
        jlab.wedm.PvObserver.call(this, id, pvSet);
    };

    jlab.wedm.SymbolPvObserver.prototype = Object.create(jlab.wedm.PvObserver.prototype);
    jlab.wedm.SymbolPvObserver.prototype.constructor = jlab.wedm.SymbolPvObserver;

    jlab.wedm.SymbolPvObserver.prototype.handleControlUpdate = function (update) {
        var $obj = $("#" + this.id),
                value = update.value,
                minVals = $obj.attr("data-min-values").split(" "),
                maxVals = $obj.attr("data-max-values").split(" "),
                state = 0;

        /*console.log("comparing value: " + value);*/

        for (var i = 0; i < minVals.length; i++) {
            if ((value * 1) >= (minVals[i] * 1) && ((value * 1) < (maxVals[i] * 1))) {
                state = i;
                break;
            }
        }

        state = state + 1; /* nth-child starts at 1, not zero */

        /*console.log('state: ' + state);*/

        $obj.find(".ActiveGroup").hide();
        $obj.find(".ActiveGroup:nth-child(" + state + ")").show();
    };
};

jlab.wedm.initPvObserver('jlab.wedm.SymbolPvObserver', 'jlab.wedm.PvObserver');

jlab.wedm.initSymbol = function () {
    $(".ActiveSymbol .ActiveGroup:nth-child(1)").show();
};

jlab.wedm.initFuncs = jlab.wedm.initFuncs || [];
jlab.wedm.initFuncs.push(jlab.wedm.initSymbol);