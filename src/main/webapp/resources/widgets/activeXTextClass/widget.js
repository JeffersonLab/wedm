jlab = jlab || {};
jlab.wedm = jlab.wedm || {};
jlab.wedm.classToObserverMap = jlab.wedm.classToObserverMap || {};
jlab.wedm.classToObserverMap['ActiveStaticText'] = 'jlab.wedm.StaticTextPvObserver';

jlab.wedm.StaticTextPvObserverInit = function () {

    jlab.wedm.StaticTextPvObserver = function (id, pvSet) {
        jlab.wedm.PvObserver.call(this, id, pvSet);

        /*We set noalarm color here because subclasses (TextupdateClass, RegTextupdateClass, TextentryClass) might want to change it*/
        this.noAlarmColor = jlab.wedm.noAlarmColor;
    };

    jlab.wedm.StaticTextPvObserver.prototype = Object.create(jlab.wedm.PvObserver.prototype);
    jlab.wedm.StaticTextPvObserver.prototype.constructor = jlab.wedm.StaticTextPvObserver;

    jlab.wedm.StaticTextPvObserver.prototype.handleInfo = function (info) {

        var $obj = $("#" + this.id);

        if (!info.connected) {
            $obj.css("color", jlab.wedm.disconnectedAlarmColor);
            $obj.attr("background-color", "transparent");
            $obj[0].classList.add("disconnected-pv");
            $obj[0].classList.remove("waiting-for-state");
        }
    };

    jlab.wedm.StaticTextPvObserver.prototype.handleAlarmUpdate = function (update) {
        var $obj = $("#" + this.id),
                sevr = update.value,
                fgAlarm = $obj.attr("data-fg-alarm") === "true",
                bgAlarm = $obj.attr("data-bg-alarm") === "true",
                borderAlarm = $obj.attr("data-border-alarm") === "true",
                invalid = false;

        $obj.attr("data-sevr", sevr);
        $obj[0].classList.remove("waiting-for-state");

        if (typeof sevr !== 'undefined') {
            if (sevr === 0) { // NO_ALARM
                if (fgAlarm) {
                    $obj.css("color", this.noAlarmColor);
                }
                if (bgAlarm) {
                    $obj.css("background-color", this.noAlarmColor);
                }
                if (borderAlarm) { /*EDM hides border if no alarm*/
                    $obj.css("border", "2px solid transparent");
                }
            } else if (sevr === 1) { // MINOR
                if (fgAlarm) {
                    $obj.css("color", jlab.wedm.minorAlarmColor);
                }
                if (bgAlarm) {
                    $obj.css("background-color", jlab.wedm.minorAlarmColor);
                }
                if (borderAlarm) {
                    $obj.css("border", "2px solid " + jlab.wedm.minorAlarmColor);
                }
            } else if (sevr === 2) { // MAJOR
                if (fgAlarm) {
                    $obj.css("color", jlab.wedm.majorAlarmColor);
                }
                if (bgAlarm) {
                    $obj.css("background-color", jlab.wedm.majorAlarmColor);
                }
                if (borderAlarm) {
                    $obj.css("border", "2px solid " + jlab.wedm.majorAlarmColor);
                }
            } else if (sevr === 3) { // INVALID
                invalid = true;
            }
        } else {
            invalid = true;
        }

        if (invalid) {
            if (fgAlarm) {
                $obj.css("color", jlab.wedm.invalidAlarmColor);
            }
            if (bgAlarm) {
                $obj.css("background-color", jlab.wedm.invalidAlarmColor);
            }
            if (borderAlarm) {
                $obj.css("border", "2px solid " + jlab.wedm.invalidAlarmColor);
            }
        }
    };

    jlab.wedm.StaticTextPvObserver.prototype.handleCalcExpr = function(value) {
        if (jlab.wedm.isCalcExpr(this.pvSet.colorPvExpr)) {
            var pvs = this.toOrderedExpressionValues(this.pvSet.colorPvs);

            if(pvs == null) {
                return null; // We don't have complete set of variables yet!
            }

            value = jlab.wedm.evalCalcExpr(this.pvSet.colorPvExpr, pvs);
        }

        return value;
    };

    jlab.wedm.StaticTextPvObserver.prototype.handleColorUpdate = function (update) {
        var $obj = $("#" + this.id),
                color,
                fgRuleIndex = $obj.attr("data-fg-color-rule"),
                bgRuleIndex = $obj.attr("data-bg-color-rule"),
                stmt,
                value = update.value;

        $obj[0].classList.remove("waiting-for-state");

        if (typeof fgRuleIndex !== 'undefined') {
            stmt = jlab.wedm.colorRules[fgRuleIndex];

            value = this.handleCalcExpr(value);

            if(value == null) {
                return; // Still waiting for more updates
            }

            color = jlab.wedm.evalColorExpr.call(this, stmt, value);

            $obj.css("color", color);
        }

        if (typeof bgRuleIndex !== 'undefined') {
            stmt = jlab.wedm.colorRules[bgRuleIndex];

            value = this.handleCalcExpr(value);

            if(value == null) {
                return; // Still waiting for more updates
            }

            color = jlab.wedm.evalColorExpr.call(this, stmt, value);
            $obj.css("background-color", color);
        }
    };
};

jlab.wedm.initPvObserver('jlab.wedm.StaticTextPvObserver', 'jlab.wedm.PvObserver');