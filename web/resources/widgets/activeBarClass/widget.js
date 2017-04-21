jlab = jlab || {};
jlab.wedm = jlab.wedm || {};
jlab.wedm.classToObserverMap = jlab.wedm.classToObserverMap || {};
jlab.wedm.classToObserverMap['ActiveBarMonitor'] = 'jlab.wedm.BarMeterPvObserver';

jlab.wedm.BarMeterPvObserverInit = function () {

    jlab.wedm.BarMeterPvObserver = function (id, pvSet) {
        jlab.wedm.PvObserver.call(this, id, pvSet);
    };

    jlab.wedm.BarMeterPvObserver.prototype = Object.create(jlab.wedm.PvObserver.prototype);
    jlab.wedm.BarMeterPvObserver.prototype.constructor = jlab.wedm.BarMeterPvObserver;

    jlab.wedm.BarMeterPvObserver.prototype.handleIndicatorUpdate = function (update) {
        var value = update.value,
                $obj = $("#" + this.id),
                horizontal = $obj.attr("data-orientation") === "horizontal",
                $holder = $obj.find(".bar-holder"),
                $bar = $obj.find(".bar"),
                $baseline = $obj.find(".base-line"),
                max = $obj.attr("data-max"),
                min = $obj.attr("data-min"),
                origin = parseFloat($obj.attr("data-origin") || "0.0"),
                magnitude = Math.abs(max - origin) + Math.abs(min - origin);

        if ($.isNumeric(max) && $.isNumeric(min)) {
            var height = $bar.attr("height"),
                    width = $bar.attr("width"),
                    $barHolder = $obj.find(".bar-holder"),
                    holderHeight = $barHolder.attr("height") * 1,
                    verticalPadding = $barHolder.attr("data-vertical-padding") * 1; // constant padding offset


            if (horizontal) {
                /*$.attr will force lowercase, not camel case so we use native JavaScript*/
                $holder[0].setAttribute("viewBox", "0 0 " + magnitude + " " + height);

                $bar.attr("width", value);

            } else { /*Vertical*/

                var maxMag = Math.abs(max - origin),
                        proportion = maxMag / magnitude,
                        baselineOffset = verticalPadding + (holderHeight * proportion),
                        upBarHolderOffset = verticalPadding - (holderHeight * (1 - proportion)),
                        downBarHolderOffset = verticalPadding + (holderHeight * proportion);

                var y1 = baselineOffset;
                var y2 = y1;

                $baseline.attr("y1", y1);
                $baseline.attr("y2", y2);

                if (value > origin) {
                    /*$.attr will force lowercase, not camel case so we use native JavaScript*/
                    /*Use -magnitude for x since we are using scale(1,-1) to flip coordintes and have x values go up instead of down*/
                    $holder[0].setAttribute("viewBox", "0 " + (-magnitude) + " " + width + " " + magnitude);

                    $barHolder.attr("y", upBarHolderOffset);

                    $bar.attr("transform", "scale(1,-1)");

                    $bar.attr("height", Math.min(value, max));
                } else { /*Bar grows downward since less than origin*/

                    /*$.attr will force lowercase, not camel case so we use native JavaScript*/
                    $holder[0].setAttribute("viewBox", "0 " + (0) + " " + width + " " + magnitude);

                    $barHolder.attr("y", downBarHolderOffset);

                    $bar.removeAttr("transform");

                    var height = Math.max(value, min);

                    $bar.attr("height", Math.abs(height));
                }
            }
        }
    };

    jlab.wedm.BarMeterPvObserver.prototype.handleAlarmUpdate = function (update) {
        var $obj = $("#" + this.id),
                sevr = update.value,
                $bar = $obj.find(".bar"),
                $box = $obj.find("> rect"),
                $baseline = $obj.find(".base-line"),
                invalid = false;

        $obj.attr("data-sevr", sevr);
        $obj[0].classList.remove("waiting-for-state");

        if (typeof sevr !== 'undefined') {
            if (sevr === 0) { // NO_ALARM
                $bar.attr("fill", jlab.wedm.noAlarmColor);
                $box.attr("stroke", jlab.wedm.noAlarmColor);
                $baseline.attr("stroke", jlab.wedm.noAlarmColor);
            } else if (sevr === 1) { // MINOR
                $bar.attr("fill", jlab.wedm.minorAlarmColor);
                $box.attr("stroke", jlab.wedm.minorAlarmColor);
                $baseline.attr("stroke", jlab.wedm.minorAlarmColor);
            } else if (sevr === 2) { // MAJOR
                $bar.attr("fill", jlab.wedm.majorAlarmColor);
                $box.attr("stroke", jlab.wedm.majorAlarmColor);
                $baseline.attr("stroke", jlab.wedm.majorAlarmColor);
            } else if (sevr === 3) { // INVALID
                invalid = true;
            }
        } else {
            invalid = true;
        }

        if (invalid) {
            $bar.attr("fill", jlab.wedm.invalidAlarmColor);
            $box.attr("stroke", jlab.wedm.invalidAlarmColor);
            $baseline.attr("stroke", jlab.wedm.invalidAlarmColor);
        }
    };
};

jlab.wedm.initPvObserver('jlab.wedm.BarMeterPvObserver', 'jlab.wedm.PvObserver');