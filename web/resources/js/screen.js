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
        
        /*TODO: ensure subclasses call this method too?*/
        
        var $obj = $("#" + this.id);
        
        if(!info.connected) {
           $obj.addClass("disconnected-pv");
           $obj.css("border", "1px solid " + disconnectedAlarmColor);
        }
    };

    jlab.wedm.PvWidget.prototype.handleControlUpdate = function () {
        console.log('control update called - this should be overridden; id: ' + this.id);
    };

    jlab.wedm.PvWidget.prototype.handleAlarmUpdate = function () {
        var pv = this.alarmPvs[0],
                value = this.pvNameToValueMap[pv],
                $obj = $("#" + this.id),
                alarmOn = false;

        if (value != 0) {
            alarmOn = true;
        }

        /*if(alarmOn) {
         $obj.find("path").css({fill: majorAlarmColor});
         } else {
         $obj.find("path").css({fill: noAlarmColor});
         }*/

        if (alarmOn) {
            $obj.find("path").attr('fill', majorAlarmColor);
        } else {
            $obj.find("path").attr('fill', noAlarmColor);
        }
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
        } else {
            $obj.attr("data-min", update.value);
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
        html = html + '<div class="ScreenObject" style="display: table; overflow: hidden; top: ' + top + 'px; left: ' + left + 'px; width: ' + btnWidth + 'px; height: ' + btnHeight + 'px; text-align: center; border-top: 1px solid rgb(255, 255, 255); border-left: 1px solid rgb(255, 255, 255); border-bottom: 1px solid rgb(0, 0, 0); border-right: 1px solid rgb(0, 0, 0);"><span style="display: table-cell; vertical-align: middle;">' + this.enumValuesArray[i] + '</span></div>';
        if (horizontal) {
            left = left + btnWidth + 2;
        } else {
            top = top + btnHeight + 2;
        }
    }

    $obj.html(html);
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
            $holder = $obj.find(".bar-holder"),
            $bar = $obj.find(".bar"),
            max = $obj.attr("data-max"),
            min = $obj.attr("data-min");
    /*console.log(value);*/

    if ($.isNumeric(max) && $.isNumeric(min)) {
        var holderHeight = $holder.attr("height"),
                height = $bar.attr("height"),
                width = $bar.attr("width"),
                y = 0;
        
        /*$.attr will force lowercase, not camel case so we use native JavaScript*/
        $holder[0].setAttribute("viewBox", "0 " + (-max) + " " + width + " " + (max - min));
        
        $bar.attr("height", value);
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

$(document).on("click", ".RelatedDisplay", function(event) {
    var files = [],
    labels = [],
    $obj = $(this);
    
    for(var i = 0; i < 64; i++) {
        var file = $obj.attr("data-linked-file-" + i),
        label = $obj.attr("data-linked-label-" + i);
        
        if(file === undefined) {
            break;
        } else {
            files.push(file);
            
            if(label === undefined || label === '') {
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
    
    if(files.length === 1) {
        window.open(path + files[0], '_blank');
    } else {
        var $html = $('<div class="related-display-menu" style="left: ' + left + '; top: ' + top + ';" ><ul></ul></div>');
        
        for(var i = 0; i < files.length; i++) {
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
                limitsFromDb = $obj.attr("data-limits") === "from-db";

        if (limitsFromDb && indicatorPvs.length === 1) {
            limitPvs.push(indicatorPvs[0] + ".HOPR");
            limitPvs.push(indicatorPvs[0] + ".LOPR");
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
