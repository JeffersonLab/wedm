var jlab = jlab || {};
jlab.wedm = jlab.wedm || {};

jlab.wedm.PvWidget = function (id, pvSet) {
    this.id = id;
    this.ctrlPvExpr = pvSet.ctrlPvExpr;
    this.visPvExpr = pvSet.visPvExpr;
    this.alarmPvExpr = pvSet.alarmPvExpr;
    this.indicatorPvExpr = pvSet.indicatorPvExpr;
    this.ctrlPvs = pvSet.ctrlPvs;
    this.visPvs = pvSet.visPvs;
    this.alarmPvs = pvSet.alarmPvs;
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
            this.handleAlarmUpdate.call(this);
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
            $obj.css("border", "1px solid " + jlab.wedm.disconnectedAlarmColor);
        }
    };

    jlab.wedm.PvWidget.prototype.handleControlUpdate = function () {
        console.log('control update called - this should be overridden; id: ' + this.id);
    };

    jlab.wedm.PvWidget.prototype.handleAlarmUpdate = function () {
        console.log('alarm update called - this should be overridden; id: ' + this.id);
    };

    jlab.wedm.PvWidget.prototype.handleIndicatorUpdate = function () {
        console.log('indicator update called - this should be overridden; id: ' + this.id);
    };

    jlab.wedm.PvWidget.prototype.handleVisibilityUpdate = function () {
        var pv = this.visPvs[0];
        var value = this.pvNameToValueMap[pv];
        var $obj = $("#" + this.id);

        /*console.log('val: ' + value);
         $obj.attr("data-value", value);*/

        var min = $obj.attr("data-vis-min");
        var max = $obj.attr("data-vis-max");
        var invert = $obj.attr("data-vis-invert") === "true";
        var result = (value >= min && value < max);

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
        } else if (update.pv.indexOf(".HIHI") > -1) {
            $obj.attr("data-hihi", update.value);
        } else if (update.pv.indexOf(".HIGH") > -1) {
            $obj.attr("data-high", update.value);
        } else if (update.pv.indexOf(".LOW") > -1) {
            $obj.attr("data-low", update.value);
        } else if (update.pv.indexOf(".LOLO") > -1) {
            $obj.attr("data-lolo", update.value);
        } else {
            console.log('Unknown limit PV: ' + update.pv);
        }
    };
};

jlab.wedm.TextPvWidget = function (id, pvSet) {
    jlab.wedm.PvWidget.call(this, id, pvSet);
};

jlab.wedm.TextPvWidget.prototype = Object.create(jlab.wedm.PvWidget.prototype);
jlab.wedm.TextPvWidget.prototype.constructor = jlab.wedm.TextPvWidget;

jlab.wedm.TextPvWidget.prototype.handleControlUpdate = function () {
    var pv = this.ctrlPvs[0];
    var value = this.pvNameToValueMap[pv];

    if ($.isNumeric(value)) {
        value = value * 1; // could use parseFloat too; just need to ensure is numeric
        value = value.toFixed(2);
    }

    $("#" + this.id + " .screen-text").text(value);
};

jlab.wedm.SymbolPvWidget = function (id, pvSet) {
    jlab.wedm.PvWidget.call(this, id, pvSet);
};

jlab.wedm.SymbolPvWidget.prototype = Object.create(jlab.wedm.PvWidget.prototype);
jlab.wedm.SymbolPvWidget.prototype.constructor = jlab.wedm.SymbolPvWidget;

jlab.wedm.SymbolPvWidget.prototype.handleControlUpdate = function () {
    var $obj = $("#" + this.id);
    var pv = this.ctrlPvs[0];
    var value = this.pvNameToValueMap[pv];

    var minVals = $obj.attr("data-min-values").split(" ");
    var maxVals = $obj.attr("data-max-values").split(" ");
    var state = 1;

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
            $bits = $obj.find(".bit");

    /*console.log("value: " + value);*/

    //$(".ActiveByte[data-pv='" + this.pv + "']").text(value);
    var index = $bits.length - 1;
    $bits.each(function () {
        var mask = 1 << index,
                bit = mask & value;
        /*console.log('mask: ' + mask);
         console.log('bit: ' + bit);*/
        if (bit > 0) {
            $(this).css("fill", onColor);
        } else {
            $(this).css("fill", offColor);
        }

        index = index - 1;
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
            $box = $obj.find("> rect"),
            $baseline = $obj.find(".base-line"),
            max = $obj.attr("data-max"),
            min = $obj.attr("data-min"),
            origin = parseFloat($obj.attr("data-origin") || "0.0"),
            magnitude = Math.abs(max - origin) + Math.abs(min - origin),
            alarmSensitive = $obj.attr("data-indicator-alarm") === "true",
            hihi = $obj.attr("data-hihi"),
            high = $obj.attr("data-high"),
            low = $obj.attr("data-low"),
            lolo = $obj.attr("data-lolo");

    if (alarmSensitive) {
        if (typeof hihi !== 'undefined' && value > hihi) {
            $bar.attr("fill", jlab.wedm.majorAlarmColor);
            $box.attr("stroke", jlab.wedm.majorAlarmColor);
            $baseline.attr("stroke", jlab.wedm.majorAlarmColor);
        } else if (typeof high !== 'undefined' && value > high) {
            $bar.attr("fill", jlab.wedm.minorAlarmColor);
            $box.attr("stroke", jlab.wedm.minorAlarmColor);
            $baseline.attr("stroke", jlab.wedm.minorAlarmColor);
        } else if (typeof lolo !== 'undefined' && value < lolo) {
            $bar.attr("fill", jlab.wedm.minorAlarmColor);
            $box.attr("stroke", jlab.wedm.minorAlarmColor);
            $baseline.attr("stroke", jlab.wedm.minorAlarmColor);
        } else if (typeof low !== 'undefined' && value < low) {
            $bar.attr("fill", jlab.wedm.majorAlarmColor);
            $box.attr("stroke", jlab.wedm.majorAlarmColor);
            $baseline.attr("stroke", jlab.wedm.majorAlarmColor);
        } else {
            $bar.attr("fill", jlab.wedm.noAlarmColor);
            $box.attr("stroke", jlab.wedm.noAlarmColor);
            $baseline.attr("stroke", jlab.wedm.noAlarmColor);
        }
    }

    /*console.log(value);*/

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

jlab.wedm.ShapePvWidget.prototype.handleAlarmUpdate = function () {
    var pv = this.alarmPvs[0],
            value = this.pvNameToValueMap[pv],
            $obj = $("#" + this.id),
            hihi = $obj.attr("data-hihi"),
            high = $obj.attr("data-high"),
            low = $obj.attr("data-low"),
            lolo = $obj.attr("data-lolo"),
            $shape = $obj.find("rect, ellipse, path"),
            fillAlarm = $obj.attr("data-fill-alarm") === "true",
            lineAlarm = $obj.attr("data-line-alarm") === "true";

    if (typeof hihi !== 'undefined' && value > hihi) {
        if (fillAlarm) {
            $shape.attr("fill", jlab.wedm.majorAlarmColor);
        }
        if (lineAlarm) {
            $shape.attr("stroke", jlab.wedm.majorAlarmColor);
        }
    } else if (typeof high !== 'undefined' && value > high) {
        if (fillAlarm) {
            $shape.attr("fill", jlab.wedm.minorAlarmColor);
        }
        if (lineAlarm) {
            $shape.attr("stroke", jlab.wedm.minorAlarmColor);
        }
    } else if (typeof lolo !== 'undefined' && value < lolo) {
        if (fillAlarm) {
            $shape.attr("fill", jlab.wedm.minorAlarmColor);
        }
        if (lineAlarm) {
            $shape.attr("stroke", jlab.wedm.minorAlarmColor);
        }
    } else if (typeof low !== 'undefined' && value < low) {
        if (fillAlarm) {
            $shape.attr("fill", jlab.wedm.majorAlarmColor);
        }
        if (lineAlarm) {
            $shape.attr("stroke", jlab.wedm.majorAlarmColor);
        }
    } else {
        if (fillAlarm) {
            $shape.attr("fill", jlab.wedm.noAlarmColor);
        }
        if (lineAlarm) {
            $shape.attr("stroke", jlab.wedm.noAlarmColor);
        }
    }
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
    }
};

jlab.wedm.StaticTextPvWidget.prototype.handleAlarmUpdate = function () {
    var pv = this.alarmPvs[0],
            value = this.pvNameToValueMap[pv],
            $obj = $("#" + this.id),
            hihi = $obj.attr("data-hihi"),
            high = $obj.attr("data-high"),
            low = $obj.attr("data-low"),
            lolo = $obj.attr("data-lolo"),
            fgAlarm = $obj.attr("data-fg-alarm") === "true",
            bgAlarm = $obj.attr("data-bg-alarm") === "true";

    if (typeof hihi !== 'undefined' && value > hihi) {
        if (fgAlarm) {
            $obj.css("color", jlab.wedm.majorAlarmColor);
        }
        if (bgAlarm) {
            $obj.css("background-color", jlab.wedm.majorAlarmColor);
        }
    } else if (typeof high !== 'undefined' && value > high) {
        if (fgAlarm) {
            $obj.css("color", jlab.wedm.minorAlarmColor);
        }
        if (bgAlarm) {
            $obj.css("background-color", jlab.wedm.minorAlarmColor);
        }
    } else if (typeof lolo !== 'undefined' && value < lolo) {
        if (fgAlarm) {
            $obj.css("color", jlab.wedm.minorAlarmColor);
        }
        if (bgAlarm) {
            $obj.css("background-color", jlab.wedm.minorAlarmColor);
        }
    } else if (typeof low !== 'undefined' && value < low) {
        if (fgAlarm) {
            $obj.css("color", jlab.wedm.majorAlarmColor);
        }
        if (bgAlarm) {
            $obj.css("background-color", jlab.wedm.majorAlarmColor);
        }
    } else {
        if (fgAlarm) {
            $obj.css("color", jlab.wedm.noAlarmColor);
        }
        if (bgAlarm) {
            $obj.css("background-color", jlab.wedm.noAlarmColor);
        }
    }
};

var monitoredPvs = null,
        pvWidgetMap = null;

jlab.wedm.pvsFromExpr = function (expr) {
    var pvs = [];

    if (expr !== undefined) {
        if (expr.indexOf("CALC\\") === 0) {

            expr.substring(expr.indexOf("}") + 2, expr.length - 1).split(",").forEach(function (pv) {
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
            left = event.clientX + "px",
            top = event.clientY + "px";

    if (files.length === 1) {
        window.open(path + files[0], '_blank');
    } else {
        var $html = $('<div class="related-display-menu" style="left: ' + left + '; top: ' + top + ';" ><ul></ul></div>');

        for (var i = 0; i < files.length; i++) {
            $html.find("ul").append('<li><a href="' + path + files[i] + '" target="_blank">' + labels[i] + '</a></li>');
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

$(function () {
    $(".ActiveSymbol .ActiveGroup:nth-child(2)").show();

    $(".screen-text").each(function () {
        var $obj = $(this),
                $parent = $obj.closest(".ScreenObject");

        /*console.log("Screen Object Height: " + $parent.height());
         console.log("Text Height: " + $obj.height());*/

        var i = 0;

        while ($obj.outerHeight() > $parent.outerHeight() || $obj.outerWidth() > $parent.outerWidth()) {
            if (i > 6) {
                console.log('font size difference too big; aborting resize');
                break;
            }

            /*console.log('shrinkng font size for text obj' + $parent.attr("id"));*/
            var smallerSize = parseFloat($parent.css("font-size")) - 1;
            $parent.css("font-size", smallerSize);
            i++;
        }

    });

    monitoredPvs = [];
    pvWidgetMap = {};

    $(".ScreenObject").each(function () {
        /*console.log("Attr: " + $(this).attr("data-pv"));*/
        var $obj = $(this),
                id = $obj.attr("id"),
                ctrlPvExpr = $obj.attr("data-pv"),
                visPvExpr = $obj.attr("data-vis-pv"),
                alarmPvExpr = $obj.attr("data-alarm-pv"),
                indicatorPvExpr = $obj.attr("data-indicator-pv"),
                alarmPvs = jlab.wedm.pvsFromExpr(alarmPvExpr),
                ctrlPvs = jlab.wedm.pvsFromExpr(ctrlPvExpr),
                visPvs = jlab.wedm.pvsFromExpr(visPvExpr),
                indicatorPvs = jlab.wedm.pvsFromExpr(indicatorPvExpr),
                limitPvs = [],
                limitsFromDb = $obj.attr("data-limits") === "from-db",
                alarmSensitive = $obj.attr("data-indicator-alarm") === "true";

        if (limitsFromDb && indicatorPvs.length === 1) {
            limitPvs.push(indicatorPvs[0] + ".HOPR");
            limitPvs.push(indicatorPvs[0] + ".LOPR");
        }

        /*TODO: Should we be using .STAT enum instead? */
        if (alarmSensitive && indicatorPvs.length === 1) {
            limitPvs.push(indicatorPvs[0] + ".HIHI");
            limitPvs.push(indicatorPvs[0] + ".HIGH");
            limitPvs.push(indicatorPvs[0] + ".LOW");
            limitPvs.push(indicatorPvs[0] + ".LOLO");
        }

        if (alarmPvs.length === 1) {
            limitPvs.push(alarmPvs[0] + ".HIHI");
            limitPvs.push(alarmPvs[0] + ".HIGH");
            limitPvs.push(alarmPvs[0] + ".LOW");
            limitPvs.push(alarmPvs[0] + ".LOLO");
        }

        var allPvs = jlab.wedm.uniqueArray(ctrlPvs.concat(visPvs).concat(alarmPvs).concat(indicatorPvs).concat(limitPvs)),
                widget = null,
                pvSet = {ctrlPvExpr: ctrlPvExpr, visPvExpr: visPvExpr, alarmPvExpr: alarmPvExpr, indicatorPvExpr: indicatorPvExpr, alarmPvs: alarmPvs, ctrlPvs: ctrlPvs, visPvs: visPvs, indicatorPvs: indicatorPvs, limitPvs: limitPvs};

        if (ctrlPvExpr !== undefined || visPvExpr !== undefined || alarmPvExpr !== undefined || indicatorPvExpr !== undefined) {
            /*console.log($obj[0].className);*/
            if ($obj.hasClass("ActiveXTextDsp")) {
                /*console.log("text widget");*/
                widget = new jlab.wedm.TextPvWidget(id, pvSet);
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
            } else if($obj.attr("class").indexOf("ActiveXText") > -1) {
                widget = new jlab.wedm.StaticTextPvWidget(id, pvSet);
            } else {
                /*console.log("other widget");*/
                widget = new jlab.wedm.PvWidget(id, pvSet);
            }

            allPvs.forEach(function (pv) {
                if (monitoredPvs.indexOf(pv) === -1) {
                    /*console.log('monitoring pv: ' + pv);*/
                    monitoredPvs.push(pv);
                }

                pvWidgetMap[pv] = pvWidgetMap[pv] || [];
                pvWidgetMap[pv].push(widget);
            });
        }
    });


    var options = {};

    jlab.wedm.con = new jlab.epics2web.ClientConnection(options);

    jlab.wedm.con.onopen = function (e) {
        /*This is for re-connect - on inital connect array will be empty*/
        if (monitoredPvs.length > 0) {
            jlab.wedm.con.monitorPvs(monitoredPvs);
        }
    };

    jlab.wedm.con.onupdate = function (e) {
        $(pvWidgetMap[e.detail.pv]).each(function () {
            this.handleUpdate(e.detail);
        });
    };

    jlab.wedm.con.oninfo = function (e) {
        $(pvWidgetMap[e.detail.pv]).each(function () {
            this.handleInfo(e.detail);
        });
    };
});
