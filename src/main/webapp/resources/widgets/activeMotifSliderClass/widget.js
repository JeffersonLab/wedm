jlab = jlab || {};
jlab.wedm = jlab.wedm || {};
jlab.wedm.classToObserverMap = jlab.wedm.classToObserverMap || {};
jlab.wedm.classToObserverMap['ActiveMotifSlider'] = 'jlab.wedm.MotifSliderPvObserver';

jlab.wedm.MotifSliderPvObserverInit = function() {
jlab.wedm.MotifSliderPvObserver = function (id, pvSet) {
    jlab.wedm.PvObserver.call(this, id, pvSet);
};

jlab.wedm.MotifSliderPvObserver.prototype = Object.create(jlab.wedm.PvObserver.prototype);
jlab.wedm.MotifSliderPvObserver.prototype.constructor = jlab.wedm.MotifSliderPvObserver;

jlab.wedm.MotifSliderPvObserver.prototype.refresh = function () {
    var $obj = $("#" + this.id),
            pv = $obj.attr("data-pv");

    if (pv) {
        this.handleControlUpdate({pv: pv, value: this.pvNameToValueMap[pv]});
    }
};

jlab.wedm.MotifSliderPvObserver.prototype.handleControlUpdate = function (update) {
    var $obj = $("#" + this.id),
            value = update.value,
            min = $obj.attr("data-min"),
            max = $obj.attr("data-max"),
            range = Math.abs(max - min),
            adjValue = Math.abs(value - min),
            ratio = Math.abs(adjValue / range),
            horizontal = $obj.attr("data-orientation") === "horizontal",
            $track = $obj.find(".slider-track"),
            $knob = $track.find(".knob");

    if(ratio > 1) {
        console.log('WARNING: motif slider value over max; using max');
        ratio = 1;
    } else if(ratio < 0) {
        console.log('WARNING: motif slider value under min; using min');
        ratio = 0;
    }

    if ($.isNumeric(max) && $.isNumeric(min)) {
        if (horizontal) {
            var trackWidth = $track.width(),
                    offset = ((trackWidth * ratio));
            //offset = Math.max(0, offset),
            //offset = Math.min(range, offset);
            $knob.css("left", offset + "px");
        } else { /*Vertical*/
            var trackHeight = $track.height(),
                    offset = (trackHeight - (trackHeight * ratio));
            //offset = Math.max(0, offset),
            //offset = Math.min(range, offset);
            $knob.css("top", offset + "px");
        }
    }
};
};

jlab.wedm.initPvObserver('jlab.wedm.MotifSliderPvObserver', 'jlab.wedm.PvObserver');