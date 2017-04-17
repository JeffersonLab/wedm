jlab = jlab || {};
jlab.wedm = jlab.wedm || {};
jlab.wedm.classToObserverMap = jlab.wedm.classToObserverMap || {};
jlab.wedm.classToObserverMap['ActivePictureInPicture'] = 'jlab.wedm.PiPPvObserver';

jlab.wedm.PiPPvObserver = function (id, pvSet) {
    jlab.wedm.PvObserver.call(this, id, pvSet);
};

jlab.wedm.PiPPvObserver.prototype = Object.create(jlab.wedm.PvObserver.prototype);
jlab.wedm.SymbolPvObserver.prototype.constructor = jlab.wedm.PiPPvObserver;

jlab.wedm.PiPPvObserver.prototype.handleControlUpdate = function (update) {
    var $obj = $("#" + this.id),
            value = update.value,
            $selected = $obj.find(".screen[data-index=" + value + "]");

    $obj.find(".screen").hide();
    $selected.show();

    $selected.find(".ActiveMotifSlider").each(function () {
        var $slider = $(this),
                pv = $slider.attr("data-pv"),
                widget = jlab.wedm.idWidgetMap[$slider.attr("id")];
        if (pv) {
            widget.handleControlUpdate({pv: pv, value: widget.pvNameToValueMap[pv]});
        }
    });
};

jlab.wedm.initPip = function () {
    /*Only vertically center screens shorter than their parent*/
    $(".ActivePictureInPicture.pip-center .screen").each(function () {
        var $screen = $(this),
                $container = $screen.closest(".ActivePictureInPicture");
        if ($screen.outerHeight(true) < $container.height()) {
            $screen.addClass("vertical-center");
        }
    });

    /*Initially just how first screen in the stack*/
    $(".ActivePictureInPicture .screen:not(:first-child)").hide();
};

jlab.wedm.initFuncs = jlab.wedm.initFuncs || [];
jlab.wedm.initFuncs.push(jlab.wedm.initPip);