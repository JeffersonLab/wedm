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
