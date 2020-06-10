jlab = jlab || {};
jlab.wedm = jlab.wedm || {};
jlab.wedm.classToObserverMap = jlab.wedm.classToObserverMap || {};
jlab.wedm.classToObserverMap['ActiveRectangle'] = 'jlab.wedm.ShapePvObserver';
jlab.wedm.classToObserverMap['ActiveCircle'] = 'jlab.wedm.ShapePvObserver';
jlab.wedm.classToObserverMap['ActiveLine'] = 'jlab.wedm.ShapePvObserver';
jlab.wedm.classToObserverMap['ActiveArc'] = 'jlab.wedm.ShapePvObserver';

jlab.wedm.ShapePvObserverInit = function () {
    jlab.wedm.ShapePvObserver = function (id, pvSet) {
        jlab.wedm.PvObserver.call(this, id, pvSet);
    };

    jlab.wedm.ShapePvObserver.prototype = Object.create(jlab.wedm.PvObserver.prototype);
    jlab.wedm.ShapePvObserver.prototype.constructor = jlab.wedm.ShapePvObserver;

    jlab.wedm.ShapePvObserver.prototype.handleInfo = function (info) {

        jlab.wedm.PvObserver.prototype.handleInfo.call(this, info);

        var $obj = $("#" + this.id),
                $shape = $obj.find("rect, ellipse, path");

        /*Disconnected Shape always has disconnectedAlarmColor border and transparent fill regardless of fillAlarm or lineAlarm*/
        if (!info.connected) {
            $shape.attr("fill", "transparent");
            $shape.attr("stroke", "transparent");
        }
    };

    jlab.wedm.ShapePvObserver.prototype.handleAlarmUpdate = function (update) {
        var $obj = $("#" + this.id),
                sevr = update.value,
                $shape = $obj.find("rect, ellipse, path"),
                fillAlarm = $obj.attr("data-fill-alarm") === "true",
                lineAlarm = $obj.attr("data-line-alarm") === "true",
                invalid = false;

        $obj.attr("data-sevr", sevr);
        $obj[0].classList.remove("waiting-for-state");

        if (typeof sevr !== 'undefined') {
            if (sevr === 0) { // NO_ALARM
                if (fillAlarm) {
                    $shape.attr("fill", jlab.wedm.noAlarmColor);
                }
                if (lineAlarm) {
                    $shape.attr("stroke", jlab.wedm.noAlarmColor);
                }
            } else if (sevr === 1) { // MINOR
                if (fillAlarm) {
                    $shape.attr("fill", jlab.wedm.minorAlarmColor);
                }
                if (lineAlarm) {
                    $shape.attr("stroke", jlab.wedm.minorAlarmColor);
                }
            } else if (sevr === 2) { // MAJOR
                if (fillAlarm) {
                    $shape.attr("fill", jlab.wedm.majorAlarmColor);
                }
                if (lineAlarm) {
                    $shape.attr("stroke", jlab.wedm.majorAlarmColor);
                }
            } else if (sevr === 3) { // INVALID
                invalid = true;
            }
        } else {
            invalid = true;
        }

        if (invalid) {
            if (fillAlarm) {
                $shape.attr("fill", jlab.wedm.invalidAlarmColor);
            }
            if (lineAlarm) {
                $shape.attr("stroke", jlab.wedm.invalidAlarmColor);
            }
        }
    };

    jlab.wedm.ShapePvObserver.prototype.handleColorUpdate = function (update) {
        var $obj = $("#" + this.id),
                $shape = $obj.find("rect, ellipse, path"),
                color,
                stmt,
                lineRuleIndex = $obj.attr("data-line-color-rule"),
                fillRuleIndex = $obj.attr("data-fill-color-rule");

        $obj[0].classList.remove("waiting-for-state");

        if (lineRuleIndex !== undefined) {
            stmt = jlab.wedm.colorRules[lineRuleIndex];

            color = jlab.wedm.evalColorExpr.call(this, stmt, update.value);

            $shape.attr("stroke", color);
        }

        if (fillRuleIndex !== undefined) {
            stmt = jlab.wedm.colorRules[fillRuleIndex];

            color = jlab.wedm.evalColorExpr.call(this, stmt, update.value);

            $shape.attr("fill", color);
        }
    };
};

jlab.wedm.initPvObserver('jlab.wedm.ShapePvObserver', 'jlab.wedm.PvObserver');