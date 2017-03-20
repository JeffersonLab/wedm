var jlab = jlab || {};
jlab.wedm = jlab.wedm || {};
jlab.wedm.monitoredPvs = [];
jlab.wedm.pvWidgetMap = {};

jlab.wedm.PvWidget = function (id, pvSet) {
    this.id = id;
    this.ctrlPvExpr = pvSet.ctrlPvExpr;
    this.visPvExpr = pvSet.visPvExpr;
    this.alarmPvExpr = pvSet.alarmPvExpr;
    this.indicatorPvExpr = pvSet.indicatorPvExpr;
    this.ctrlPvs = pvSet.ctrlPvs;
    this.visPvs = pvSet.visPvs;
    this.alarmPvs = pvSet.alarmPvs;
    this.colorPvs = pvSet.colorPvs;
    this.indicatorPvs = pvSet.indicatorPvs;
    this.limitPvs = pvSet.limitPvs;
    this.pvNameToValueMap = {};
    this.enumValuesArray = [];

    jlab.wedm.PvWidget.prototype.handleUpdate = function (update) {
        this.pvNameToValueMap[update.pv] = update.value;

        if (this.visPvs.indexOf(update.pv) > -1) {
            /*console.log('updating visibility');*/
            this.handleVisibilityUpdate.call(this);
        }

        if (this.ctrlPvs.indexOf(update.pv) > -1) {
            /*console.log('updating control');*/
            this.handleControlUpdate.call(this);
        }

        if (this.alarmPvs.indexOf(update.pv) > -1) {
            /*console.log('updating alarm');*/
            this.handleAlarmUpdate.call(this, update);
        }

        if (this.colorPvs.indexOf(update.pv) > -1) {
            this.handleColorUpdate.call(this, update);
        }

        if (this.indicatorPvs.indexOf(update.pv) > -1) {
            /*console.log('updating indicator');*/
            this.handleIndicatorUpdate.call(this);
        }

        if (this.limitPvs.indexOf(update.pv) > -1) {
            /*console.log('updating limit');*/
            this.handleLimitUpdate.call(this, update);
        }

        /*console.log('Update: ' + pv + ': ' + value);*/
    };

    jlab.wedm.PvWidget.prototype.handleInfo = function (info) {
        /*console.log('Datatype: ' + info.datatype + ": " + info.count);*/

        var $obj = $("#" + this.id);

        if (!info.connected && $obj.length > 0) {
            /*Can't use $obj.addClass on SVG with jquery 2*/
            $obj[0].classList.add("disconnected-pv");
            $obj[0].classList.remove("waiting-for-state");
            $obj.css("border", "1px solid " + jlab.wedm.disconnectedAlarmColor);
        }
    };

    jlab.wedm.PvWidget.prototype.handleControlUpdate = function () {
        console.log('control update called - this should be overridden; id: ' + this.id);
    };

    jlab.wedm.PvWidget.prototype.handleAlarmUpdate = function () {
        console.log('alarm update called - this should be overridden; id: ' + this.id);
    };

    jlab.wedm.PvWidget.prototype.handleColorUpdate = function () {
        console.log('color update called - this should be overridden; id: ' + this.id);
    };

    jlab.wedm.PvWidget.prototype.handleIndicatorUpdate = function () {
        console.log('indicator update called - this should be overridden; id: ' + this.id);
    };

    jlab.wedm.PvWidget.prototype.handleVisibilityUpdate = function () {
        var pv = this.visPvs[0];
        var value = this.pvNameToValueMap[pv];
        var $obj = $("#" + this.id);

        if (jlab.wedm.isCalcExpr(this.visPvExpr)) {
            var pvs = [];
            for (var i = 0; i < this.visPvs.length; i++) {
                var name = this.visPvs[i],
                        val;

                if (jlab.wedm.isLocalExpr(name)) {
                    console.log(this.id + ' - LOC expressions inside CALC expressions are not supported');
                } else {
                    val = this.pvNameToValueMap[name];
                }

                if (typeof val === 'undefined') {
                    /*Still more PVs we need values from*/
                    return;
                }
                pvs.push(val);
            }

            value = jlab.wedm.evalCalcExpr(this.visPvExpr, pvs);
        } else if (jlab.wedm.isLocalExpr(this.visPvExpr)) {
            console.log(this.id + ' - LOC expressions are not supported');
        }

        /*console.log('val: ' + value);
         $obj.attr("data-value", value);*/

        var invert = $obj.attr("data-vis-invert") === "true";

        //if (typeof value === 'boolean') {
        //    result = value;
        //} else {
        var min = $obj.attr("data-vis-min");
        var max = $obj.attr("data-vis-max");
        var result = (value >= min && value < max); /*boolean values automatically convert to 0 or 1*/
        //}

        if (invert) {
            result = !result;
        }

        if (result) {
            $obj.show();
        } else {
            $obj.hide();
        }
    };

    jlab.wedm.PvWidget.prototype.handleLimitUpdate = function (update) {
        /*console.log('limit update ' + update.pv + ": " + update.value);*/
        var $obj = $("#" + this.id);
        if (update.pv.indexOf(".HOPR") > -1) {
            $obj.attr("data-max", update.value);
        } else if (update.pv.indexOf(".LOPR") > -1) {
            $obj.attr("data-min", update.value);
        } else if (update.pv.indexOf(".PREC") > -1) {
            $obj.attr("data-precision", update.value);
            var pv = $obj.attr("data-pv");
            this.handleControlUpdate.call(this, {pv: pv, value: this.pvNameToValueMap[pv]});
        } else {
            console.log('Unknown limit PV: ' + update.pv);
        }
    };
};

jlab.wedm.StaticTextPvWidget = function (id, pvSet) {
    jlab.wedm.PvWidget.call(this, id, pvSet);
};

jlab.wedm.StaticTextPvWidget.prototype = Object.create(jlab.wedm.PvWidget.prototype);
jlab.wedm.StaticTextPvWidget.prototype.constructor = jlab.wedm.StaticTextPvWidget;

jlab.wedm.StaticTextPvWidget.prototype.handleInfo = function (info) {

    var $obj = $("#" + this.id);

    if (!info.connected) {
        $obj.css("color", jlab.wedm.disconnectedAlarmColor);
        $obj.attr("background-color", "transparent");
        $obj[0].classList.add("disconnected-pv");
        $obj[0].classList.remove("waiting-for-state");
    }
};

jlab.wedm.StaticTextPvWidget.prototype.handleAlarmUpdate = function (update) {
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
                $obj.css("color", jlab.wedm.noAlarmColor);
            }
            if (bgAlarm) {
                $obj.css("background-color", jlab.wedm.noAlarmColor);
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

jlab.wedm.StaticTextPvWidget.prototype.handleColorUpdate = function (update) {
    var $obj = $("#" + this.id),
            color,
            fgRuleIndex = $obj.attr("data-fg-color-rule"),
            bgRuleIndex = $obj.attr("data-bg-color-rule"),
            stmt;

    $obj[0].classList.remove("waiting-for-state");

    if (typeof fgRuleIndex !== 'undefined') {
        stmt = jlab.wedm.colorRules[fgRuleIndex];
        color = jlab.wedm.evalColorExpr.call(this, stmt, update.value);
        $obj.css("color", color);
    }

    if (typeof bgRuleIndex !== 'undefined') {
        stmt = jlab.wedm.colorRules[bgRuleIndex];
        color = jlab.wedm.evalColorExpr.call(this, stmt, update.value);
        $obj.css("background-color", color);
    }
};

jlab.wedm.ControlTextPvWidget = function (id, pvSet) {
    jlab.wedm.StaticTextPvWidget.call(this, id, pvSet);
};

jlab.wedm.ControlTextPvWidget.prototype = Object.create(jlab.wedm.StaticTextPvWidget.prototype);
jlab.wedm.ControlTextPvWidget.prototype.constructor = jlab.wedm.ControlTextPvWidget;

jlab.wedm.ControlTextPvWidget.prototype.handleInfo = function (info) {
    jlab.wedm.StaticTextPvWidget.prototype.handleInfo.call(this, info);

    this.enumValuesArray = info['enum-labels'] || [];
};

jlab.wedm.ControlTextPvWidget.prototype.handleControlUpdate = function () {
    var $obj = $("#" + this.id),
            pv = this.ctrlPvs[0],
            value = this.pvNameToValueMap[pv],
            enumVal = this.enumValuesArray[value],
            format = $obj.attr("data-format"),
            precision = $obj.attr("data-precision"),
            hexPrefix = $obj.attr("data-hex-prefix") === "true";

    if (typeof enumVal !== 'undefined') {
        value = enumVal;
    } else { /*Not an enum*/
        if (jlab.wedm.isCalcExpr(this.ctrlPvExpr)) {
            var pvs = [];
            for (var i = 0; i < this.ctrlPvs.length; i++) {
                var name = this.ctrlPvs[i],
                        val;

                if (jlab.wedm.isLocalExpr(name)) {
                    console.log(this.id + ' - LOC expressions inside CALC expressions are not supported');
                } else {
                    val = this.pvNameToValueMap[name];
                }

                if (typeof val === 'undefined') {
                    /*Still more PVs we need values from*/
                    return;
                }
                pvs.push(val);
            }

            value = jlab.wedm.evalCalcExpr(this.ctrlPvExpr, pvs);
        } else if (jlab.wedm.isLocalExpr(this.ctrlPvExpr)) {
            console.log(this.id + ' - LOC expressions are not supported');
        }

        if ("hex" === format) {
            value = (value >>> 0).toString(16).toUpperCase();

            if (hexPrefix) {
                value = "0x" + value;
            }
        } else if ($.isNumeric(value)) { /*Could still be a string at this point*/
            value = value * 1; // could use parseFloat too; just need to ensure is numeric

            if (typeof precision === 'undefined') {
                precision = 2;

                $obj.attr("data-precision", precision);

                if ($obj.attr("data-db-limits") !== "true" && this.ctrlPvs.length === 1 && typeof $obj.find(".screen-text") !== 'undefined') {
                    $obj.attr("data-db-limits", "true");
                    var basename = jlab.wedm.basename(pv),
                            precPv = basename + ".PREC";
                    this.limitPvs.push(precPv);
                    jlab.wedm.addPvWithWidget(precPv, this, true);
                }
            }

            value = value.toFixed(precision);
        }
    }

    $("#" + this.id + " .screen-text").text(value);
};

jlab.wedm.SymbolPvWidget = function (id, pvSet) {
    jlab.wedm.PvWidget.call(this, id, pvSet);
};

jlab.wedm.SymbolPvWidget.prototype = Object.create(jlab.wedm.PvWidget.prototype);
jlab.wedm.SymbolPvWidget.prototype.constructor = jlab.wedm.SymbolPvWidget;

jlab.wedm.SymbolPvWidget.prototype.handleControlUpdate = function () {
    var $obj = $("#" + this.id),
            pv = this.ctrlPvs[0],
            value = this.pvNameToValueMap[pv],
            minVals = $obj.attr("data-min-values").split(" "),
            maxVals = $obj.attr("data-max-values").split(" "),
            state = 1;

    /*console.log("comparing value: " + value);*/

    for (var i = 0; i < minVals.length; i++) {
        if ((value * 1) >= (minVals[i] * 1) && ((value * 1) <= (maxVals[i] * 1))) {
            state = i;
            break;
        }
    }

    state = state + 2;

    /*console.log('state: ' + state);*/

    $obj.find(".ActiveGroup").hide();
    $obj.find(".ActiveGroup:nth-child(" + state + ")").show();
};

jlab.wedm.ChoicePvWidget = function (id, pvSet) {
    jlab.wedm.PvWidget.call(this, id, pvSet);
};

jlab.wedm.ChoicePvWidget.prototype = Object.create(jlab.wedm.PvWidget.prototype);
jlab.wedm.ChoicePvWidget.prototype.constructor = jlab.wedm.ChoicePvWidget;

jlab.wedm.ChoicePvWidget.prototype.handleControlUpdate = function () {
    var $obj = $("#" + this.id);

    var pv = this.ctrlPvs[0];
    var value = this.pvNameToValueMap[pv];
    var enumVal = this.enumValuesArray[value];

    //$(".ActiveChoiceButton[data-pv='" + this.pv + "']").text(value);
    $obj.find(".ScreenObject").each(function () {
        if ($(this).text() === enumVal) {
            $(this).css("border-top", "1px solid rgb(0, 0, 0)");
            $(this).css("border-left", "1px solid rgb(0, 0, 0)");
            $(this).css("border-right", "1px solid rgb(255, 255, 255)");
            $(this).css("border-bottom", "1px solid rgb(255, 255, 255)");
        } else {
            $(this).css("border-top", "1px solid rgb(255, 255, 255)");
            $(this).css("border-left", "1px solid rgb(255, 255, 255)");
            $(this).css("border-right", "1px solid rgb(0, 0, 0)");
            $(this).css("border-bottom", "1px solid rgb(0, 0, 0)");
        }
    });
};

jlab.wedm.ChoicePvWidget.prototype.handleInfo = function (info) {
    /*console.log('Datatype: ' + info.datatype + ": " + info.count + ": " + info['enum-labels']);*/

    var $obj = $("#" + this.id);

    this.enumValuesArray = info['enum-labels'];

    if (typeof this.enumValuesArray !== 'undefined') {
        var states = this.enumValuesArray.length;

        var horizontal = $obj.attr("data-orientation") === 'horizontal',
                width = $obj.width(),
                height = $obj.height(),
                btnWidth = (width / states) - ((states - 1) * 2),
                btnHeight = height,
                html = "",
                left = 0,
                top = 0;

        if (!horizontal) { // vertical
            btnWidth = width;
            btnHeight = (height / states) - ((states - 1) * 2);
        }

        for (var i = 0; i < this.enumValuesArray.length; i++) {
            html = html + '<div class="ScreenObject" style="display: table; overflow: hidden; top: ' + top + 'px; left: ' + left + 'px; width: ' + btnWidth + 'px; height: ' + btnHeight + 'px; text-align: center; border-top: 1px solid rgb(255, 255, 255); border-left: 1px solid rgb(255, 255, 255); border-bottom: 1px solid rgb(0, 0, 0); border-right: 1px solid rgb(0, 0, 0);"><span style="display: table-cell; vertical-align: middle; width: ' + btnWidth + 'px; max-width: ' + btnWidth + 'px;">' + this.enumValuesArray[i] + '</span></div>';
            if (horizontal) {
                left = left + btnWidth + 2;
            } else {
                top = top + btnHeight + 2;
            }
        }

        $obj.html(html);
    } else {
        console.log(this.id + " does not have enum labels");
    }
};

jlab.wedm.BytePvWidget = function (id, pvSet) {
    jlab.wedm.PvWidget.call(this, id, pvSet);
};

jlab.wedm.BytePvWidget.prototype = Object.create(jlab.wedm.PvWidget.prototype);
jlab.wedm.BytePvWidget.prototype.constructor = jlab.wedm.BytePvWidget;

jlab.wedm.BytePvWidget.prototype.handleControlUpdate = function () {
    var $obj = $("#" + this.id);

    var pv = this.ctrlPvs[0],
            value = this.pvNameToValueMap[pv],
            onColor = $obj.attr("data-on-color"),
            offColor = $obj.attr("data-off-color"),
            shift = $obj.attr("data-shift"),
            littleEndian = $obj.attr("data-little-endian") === "true",
            $bits = $obj.find(".bit"),
            index;

    /*console.log("value: " + value);*/

    //$(".ActiveByte[data-pv='" + this.pv + "']").text(value);

    if (littleEndian) {
        index = 0;
    } else {
        index = $bits.length - 1;
    }

    $bits.each(function () {
        var mask = 1 << shift << index,
                bit = mask & value;
        /*console.log('mask: ' + mask);
         console.log('bit: ' + bit);*/
        if (bit > 0) {
            $(this).css("fill", onColor);
        } else {
            $(this).css("fill", offColor);
        }

        if (littleEndian) {
            index = index + 1;
        } else {
            index = index - 1;
        }
    });
};

jlab.wedm.BarMeterPvWidget = function (id, pvSet) {
    jlab.wedm.PvWidget.call(this, id, pvSet);
};

jlab.wedm.BarMeterPvWidget.prototype = Object.create(jlab.wedm.PvWidget.prototype);
jlab.wedm.BarMeterPvWidget.prototype.constructor = jlab.wedm.BarMeterPvWidget;

jlab.wedm.BarMeterPvWidget.prototype.handleIndicatorUpdate = function () {
    var pv = this.indicatorPvs[0],
            value = this.pvNameToValueMap[pv],
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

jlab.wedm.BarMeterPvWidget.prototype.handleAlarmUpdate = function (update) {
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

jlab.wedm.ShapePvWidget = function (id, pvSet) {
    jlab.wedm.PvWidget.call(this, id, pvSet);
};

jlab.wedm.ShapePvWidget.prototype = Object.create(jlab.wedm.PvWidget.prototype);
jlab.wedm.ShapePvWidget.prototype.constructor = jlab.wedm.ShapePvWidget;

jlab.wedm.ShapePvWidget.prototype.handleInfo = function (info) {

    jlab.wedm.PvWidget.prototype.handleInfo.call(this, info);

    var $obj = $("#" + this.id),
            $shape = $obj.find("rect, ellipse, path");

    /*Disconnected Shape always has disconnectedAlarmColor border and transparent fill regardless of fillAlarm or lineAlarm*/
    if (!info.connected) {
        $shape.attr("fill", "transparent");
        $shape.attr("stroke", "transparent");
    }
};

jlab.wedm.ShapePvWidget.prototype.handleAlarmUpdate = function (update) {
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

jlab.wedm.ShapePvWidget.prototype.handleColorUpdate = function (update) {
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

jlab.wedm.isLocalExpr = function (expr) {
    return expr.indexOf("LOC\\") === 0;
};

jlab.wedm.isCalcExpr = function (expr) {
    return expr.indexOf("CALC\\") === 0;
};

/**
 * TODO: Should this be done on the server?  Or at least only do it once and cache the result.
 * 
 * TODO: Watch out for LOC = as that should be = and not ==
 * 
 * NOTE: We are already doing this same thing for color expr on server...
 */
jlab.wedm.convertEDMExpressionToJavaScript = function (expr) {
    /*Convert EPICS Operators to JavaScript Operators*/
    expr = expr.replace(new RegExp('([^<>\!])=', 'g'), "$1=="); /*Match =, but not >= or <= or !=*/
    expr = expr.replace(new RegExp('#', 'g'), "!=");
    expr = expr.replace(new RegExp('and', 'gi'), "&&");
    expr = expr.replace(new RegExp('or', 'gi'), "||");
    expr = expr.replace(new RegExp('abs', 'gi'), "Math.abs");
    expr = expr.replace(new RegExp('min', 'gi'), "Math.min");
    expr = expr.replace(new RegExp('max', 'gi'), "Math.max");

    return expr;
};

jlab.wedm.evalColorExpr = function (stmt, A) {
    var B, color;

    if (typeof stmt === 'undefined') {
        console.log(this.id + " - undefined color expression");
        return "black";
    }

    /*console.log("stmt: " + stmt);*/

    try {
        //console.time("color eval");
        eval(stmt);
        //console.timeEnd("color eval");

        color = jlab.wedm.colors[B];
    } catch (e) {
        color = "black";
        console.log("Unable to color eval: " + e.message + "; expr: " + stmt);
    }

    return color;
};

jlab.wedm.evalCalcExpr = function (expr, pvs) {

    if (expr.indexOf("CALC\\") === 0) {

        if (pvs.length > 10) {
            console.log('Expression has more than 10 PVs, which is not supported: ' + expr);
            return 0;
        }

        /*console.log(pvs);*/

        // Define vars
        var A = pvs[0],
                B = pvs[1],
                C = pvs[2],
                D = pvs[3],
                E = pvs[4],
                F = pvs[5],
                G = pvs[6],
                H = pvs[7],
                I = pvs[8],
                J = pvs[9];
        /*for (var i = 1; i < pvs.length; i++) {
         eval('var ' + String.fromCharCode("A".charCodeAt(0) + i) + ' = ' + pvs[i] + ';');
         }*/

        /*console.log(A);
         console.log(B);
         console.log(C);
         console.log(D);*/

        var stmt = expr.substring(8, expr.indexOf("}") - 1);

        //console.log("before: " + stmt);

        stmt = jlab.wedm.convertEDMExpressionToJavaScript(stmt);

        //console.log("after: " + stmt);

        var result;

        try {
            //console.time("eval");
            result = eval(stmt);
            //console.timeEnd("eval");
        } catch (e) {
            result = 0;
            console.log("Unable to eval: " + e.message + "; stmt: " + stmt);
        }

        /*console.log(result);*/

        return result;

    } else { // Just return value of first pv
        return pvs[0];
    }
};

jlab.wedm.pvsFromExpr = function (expr) {
    var pvs = [];

    if (expr !== undefined) {

        /*console.log("parsing expr: " + expr);*/

        if (expr.indexOf("CALC\\") === 0) {

            var end = expr.length - 1;

            /*EDM allows end parenthesis to be optional*/
            if (expr.lastIndexOf(")") !== end) {
                end = expr.length;
            }

            expr.substring(expr.indexOf("}") + 2, end).split(",").forEach(function (pv) {
                pvs.push($.trim(pv));
            });
        } else if (expr.indexOf("EPICS\\") === 0) {
            pvs.push(expr.substring(6));
        } else {
            pvs.push(expr);
        }
    }

    return pvs;
};

jlab.wedm.basename = function (name) {
    var basename = name,
            fieldIndex = basename.lastIndexOf(".");

    if (fieldIndex !== -1) {
        basename = basename.substring(0, fieldIndex);
    }

    return basename;
};

jlab.wedm.uniqueArray = function (array) {
    var a = array.concat();
    for (var i = 0; i < a.length; ++i) {
        for (var j = i + 1; j < a.length; ++j) {
            if (a[i] === a[j])
                a.splice(j--, 1);
        }
    }

    return a;
};

jlab.wedm.resizeText = function () {
    $(".screen-text").each(function () {
        var $obj = $(this),
                $parent = $obj.closest(".ScreenObject"),
                $wrap = $parent.find(".text-wrap"),
                wrapHeight = 0,
                wrapWidth = 0,
                waitingForState = ($parent.attr("class").indexOf("waiting-for-state") > -1);

        /*We temporarily remove waiting-for-state class so we can resize (can't resize invisible items)*/
        $parent[0].classList.remove("waiting-for-state");

        /*TODO: if EDM object is hidden due to an ancestor hidden Group then we need to unhide temporarily?*/

        /*$parent[0].classList.contains() has limited support*/
        if (($parent.attr("class").indexOf("invisible") > -1)) {
            /*console.log("permanently invisible text can't/won't be resized");*/
            return true; /*This is like a loop continue statement*/
        }

        /*Some text widgets are wrapped in a "3d border div" which adds 2px border all around */
        if ($wrap.length > 0) {
            wrapHeight = $wrap.outerHeight() - $wrap.innerHeight(); // get border width
            wrapWidth = $wrap.outerWidth() - $wrap.innerWidth();
            /*console.log("wrapHeight: " + wrapHeight);*/
        }

        /*See TextScreenObject class for explanation*/
        if ($parent.attr("data-border-alarm") === "true") {
            wrapHeight = wrapHeight - 2;
        }

        /**
         * Height = no padding; no border; no margin
         * InnerHeight = padding included 
         * OuterHeight = padding and border included
         * OuterHeight(true) = padding, border, and margin included
         */

        /*console.log($parent.attr("id") + " - Screen Object Height: " + $parent.outerHeight());
         console.log($parent.attr("id") + " - Text OuterHeight(true) + wrapHeight: " + ($obj.outerHeight(true) + wrapHeight));*/

        var i = 0;

        while (($obj.outerHeight(true) + wrapHeight) > $parent.height() || ($obj.outerWidth(true) + wrapWidth) > $parent.width()) {
            if (i > 6) {
                console.log($parent.attr("id") + ' - font size difference too big; aborting resize');
                break;
            }

            /*console.log($parent.attr("id") + ' - Shrinkng font size for text obj');*/
            var smallerSize = parseFloat($parent.css("font-size")) - 1;
            $parent.css("font-size", smallerSize);
            i++;

            /*console.log($parent.attr("id") + " - Modified Text OuterHeight(true) + wrapHeight: " + ($obj.outerHeight(true) + wrapHeight));*/
        }


        if (waitingForState === true) {
            /*console.log("Adding waiting for state class back");*/
            /*Add class back if it was there to start with*/
            $parent[0].classList.add("waiting-for-state");
        }
    });
};

jlab.wedm.createWidgets = function () {

    $(".ScreenObject").each(function () {
        /*console.log("Attr: " + $(this).attr("data-pv"));*/
        var $obj = $(this),
                id = $obj.attr("id"),
                ctrlPvExpr = $obj.attr("data-pv"),
                visPvExpr = $obj.attr("data-vis-pv"),
                alarmPvExpr = $obj.attr("data-alarm-pv"),
                colorPvExpr = $obj.attr("data-color-pv"),
                indicatorPvExpr = $obj.attr("data-indicator-pv"),
                alarmPvs = jlab.wedm.pvsFromExpr(alarmPvExpr),
                colorPvs = jlab.wedm.pvsFromExpr(colorPvExpr),
                ctrlPvs = jlab.wedm.pvsFromExpr(ctrlPvExpr),
                visPvs = jlab.wedm.pvsFromExpr(visPvExpr),
                indicatorPvs = jlab.wedm.pvsFromExpr(indicatorPvExpr),
                limitPvs = [],
                basename,
                limitsFromDb = $obj.attr("data-db-limits") === "true",
                alarmSensitive = $obj.attr("data-indicator-alarm") === "true";

        if (limitsFromDb) {
            if (indicatorPvs.length === 1) {
                basename = jlab.wedm.basename(indicatorPvs[0]);
                limitPvs.push(basename + ".HOPR");
                limitPvs.push(basename + ".LOPR");
            }

            if (ctrlPvs.length === 1 && typeof $obj.find(".screen-text") !== 'undefined') {
                basename = jlab.wedm.basename(ctrlPvs[0]);
                limitPvs.push(basename + ".PREC");
            }
        }

        if (alarmSensitive && indicatorPvs.length === 1) {
            basename = jlab.wedm.basename(indicatorPvs[0]);
            alarmPvs.push(basename + ".SEVR");
        } else if (alarmPvs.length === 1) {
            basename = jlab.wedm.basename(alarmPvs[0]);
            alarmPvs[0] = basename + ".SEVR";
        }

        var allPvs = jlab.wedm.uniqueArray(ctrlPvs.concat(visPvs).concat(alarmPvs).concat(colorPvs).concat(indicatorPvs).concat(limitPvs)),
                widget = null,
                pvSet = {ctrlPvExpr: ctrlPvExpr, visPvExpr: visPvExpr, alarmPvExpr: alarmPvExpr, indicatorPvExpr: indicatorPvExpr, alarmPvs: alarmPvs, colorPvs: colorPvs, ctrlPvs: ctrlPvs, visPvs: visPvs, indicatorPvs: indicatorPvs, limitPvs: limitPvs};

        if (ctrlPvExpr !== undefined || visPvExpr !== undefined || alarmPvExpr !== undefined || colorPvExpr !== undefined || indicatorPvExpr !== undefined) {
            /*console.log($obj[0].className);*/
            if ($obj.hasClass("ActiveXTextDsp")) {
                /*console.log("text widget");*/
                widget = new jlab.wedm.ControlTextPvWidget(id, pvSet);
            } else if ($obj.hasClass("ActiveSymbol")) {
                /*console.log("symbol widget");*/
                widget = new jlab.wedm.SymbolPvWidget(id, pvSet);
            } else if ($obj.hasClass("ActiveChoiceButton")) {
                /*console.log("choice widget");*/
                widget = new jlab.wedm.ChoicePvWidget(id, pvSet);
            } else if ($obj.attr("class").indexOf("ActiveByte") > -1) { /*SVG class handling is different*/
                /*console.log("byte widget");*/
                widget = new jlab.wedm.BytePvWidget(id, pvSet);
            } else if ($obj.attr("class").indexOf("ActiveBarMonitor") > -1) {
                widget = new jlab.wedm.BarMeterPvWidget(id, pvSet);
            } else if ($obj.attr("class").indexOf("ActiveRectangle") > -1 ||
                    $obj.attr("class").indexOf("ActiveCircle") > -1 ||
                    $obj.attr("class").indexOf("ActiveLine") > -1 ||
                    $obj.attr("class").indexOf("ActiveArc") > -1) {
                widget = new jlab.wedm.ShapePvWidget(id, pvSet);
            } else if ($obj.attr("class").indexOf("ActiveXText") > -1) {
                widget = new jlab.wedm.StaticTextPvWidget(id, pvSet);
            } else {
                /*console.log("other widget");*/
                widget = new jlab.wedm.PvWidget(id, pvSet);
            }

            allPvs.forEach(function (pv) {
                jlab.wedm.addPvWithWidget(pv, widget, false);
            });
        }
    });
};

jlab.wedm.addPvWithWidget = function (pv, widget, immediate) {
    
    if (!jlab.wedm.isLocalExpr(pv) && jlab.wedm.monitoredPvs.indexOf(pv) === -1) {
        jlab.wedm.monitoredPvs.push(pv);
    }

    jlab.wedm.pvWidgetMap[pv] = jlab.wedm.pvWidgetMap[pv] || [];
    jlab.wedm.pvWidgetMap[pv].push(widget);
    
    if(immediate) {
        jlab.wedm.con.monitorPvs([pv]);
    }
};

jlab.wedm.initializeWebsocket = function () {
    var options = {};

    jlab.wedm.con = new jlab.epics2web.ClientConnection(options);

    jlab.wedm.con.onopen = function (e) {
        /*This is for re-connect - and for initial batch of PVs*/
        if (jlab.wedm.monitoredPvs.length > 0) {
            jlab.wedm.con.monitorPvs(jlab.wedm.monitoredPvs);
        }
    };

    jlab.wedm.con.onupdate = function (e) {
        //console.time("onupdate");
        $(jlab.wedm.pvWidgetMap[e.detail.pv]).each(function () {
            this.handleUpdate(e.detail);
        });
        //console.timeEnd("onupdate");
    };

    jlab.wedm.con.oninfo = function (e) {
        $(jlab.wedm.pvWidgetMap[e.detail.pv]).each(function () {
            this.handleInfo(e.detail);
        });
    };
};

jlab.wedm.initEmbedded = function() {
    $(".ActivePictureInPicture .screen:not(:first-child)").hide();   
};

$(document).on("click", ".RelatedDisplay", function (event) {
    var files = [],
            labels = [],
            $obj = $(this);

    for (var i = 0; i < 64; i++) {
        var file = $obj.attr("data-linked-file-" + i),
                label = $obj.attr("data-linked-label-" + i);

        if (file === undefined) {
            break;
        } else {
            files.push(file);

            if (label === undefined || label === '') {
                labels.push("");
            } else {
                labels.push(label);
            }
        }
    }

    var path = '/wedm/screen?edl=',
            //left = $obj.css("left"),
            //right = $obj.css("top");
            left = event.pageX + "px",
            top = event.pageY + "px";

    if (files.length === 1) {
        window.open(path + files[0], '_blank');
    } else {
        var $html = $('<div class="related-display-menu" style="left: ' + left + '; top: ' + top + ';" ><ul></ul></div>');

        for (var i = 0; i < files.length; i++) {
            $html.find("ul").append('<li class="anchor-li"><a href="' + path + files[i] + '" target="_blank">' + labels[i] + '</a></li>');
        }

        $(document.body).append($html);
    }
});

$(document).mouseup(function (e)
{
    var container = $(".related-display-menu");

    if (!container.is(e.target) // if the target of the click isn't the container...
            && container.has(e.target).length === 0) // ... nor a descendant of the container
    {
        container.remove();
    }
});

$(document).on("click", ".anchor-li", function () {
    var href = $(this).find("a").attr("href");
    window.open(href, '_blank');
    return;
});

$(function () {
    $(".ActiveSymbol .ActiveGroup:nth-child(2)").show();

    jlab.wedm.resizeText();

    jlab.wedm.initEmbedded();

    jlab.wedm.createWidgets();

    jlab.wedm.initializeWebsocket();
});
