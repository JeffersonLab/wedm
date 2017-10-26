var jlab = jlab || {};
jlab.wedm = jlab.wedm || {};
jlab.wedm.monitoredPvs = [];
jlab.wedm.pvWidgetMap = {};
jlab.wedm.idWidgetMap = {};
jlab.wedm.localPvMap = {};
jlab.wedm.localPvs = [];

/*Polyfill for IE and Opera to add String.endsWith function*/
if (!String.prototype.endsWith) {
    String.prototype.endsWith = function (searchString, position) {
        var subjectString = this.toString();
        if (typeof position !== 'number' || !isFinite(position) || Math.floor(position) !== position || position > subjectString.length) {
            position = subjectString.length;
        }
        position -= searchString.length;
        var lastIndex = subjectString.lastIndexOf(searchString, position);
        return lastIndex !== -1 && lastIndex === position;
    };
}

/*https://stackoverflow.com/questions/400212/how-do-i-copy-to-the-clipboard-in-javascript*/
function copyTextToClipboard(text) {
    var textArea = document.createElement("textarea");

    // Place in top-left corner of screen regardless of scroll position.
    textArea.style.position = 'fixed';
    textArea.style.top = 0;
    textArea.style.left = 0;

    // Ensure it has a small width and height. Setting to 1px / 1em
    // doesn't work as this gives a negative w/h on some browsers.
    textArea.style.width = '2em';
    textArea.style.height = '2em';

    // We don't need padding, reducing the size if it does flash render.
    textArea.style.padding = 0;

    // Clean up any borders.
    textArea.style.border = 'none';
    textArea.style.outline = 'none';
    textArea.style.boxShadow = 'none';

    // Avoid flash of white box if rendered for any reason.
    textArea.style.background = 'transparent';


    textArea.value = text;

    document.body.appendChild(textArea);

    textArea.select();

    try {
        var successful = document.execCommand('copy');
        if (!successful) {
            console.log('unable to copy');
        }
        /*var msg = successful ? 'successful' : 'unsuccessful';*/
        /*console.log('Copying text command was ' + msg);*/
    } catch (err) {
        console.log('Oops, unable to copy');
    }

    document.body.removeChild(textArea);
}

jlab.wedm.stringToFunction = function (str, errorCheck) {
    var arr = str.split(".");

    var fn = (window || this);
    for (var i = 0, len = arr.length; i < len; i++) {
        fn = fn[arr[i]];
    }

    if (errorCheck && typeof fn !== "function") {
        throw new Error("function not found");
    }

    return  fn;
};

jlab.wedm.hasTouch = function () {
    try {
        document.createEvent("TouchEvent");
        return true;
    } catch (e) {
        return false;
    }
};

jlab.wedm.PvObserver = function (id, pvSet) {
    this.id = id;
    this.pvSet = pvSet;
    this.pvNameToValueMap = {};
    this.enumValuesArray = [];

    jlab.wedm.PvObserver.prototype.handleUpdate = function (update) {
        this.pvNameToValueMap[update.pv] = update.value;

        if (this.pvSet.visPvs.indexOf(update.pv) > -1) {
            this.handleVisibilityUpdate.call(this, update);
        }

        if (this.pvSet.ctrlPvs.indexOf(update.pv) > -1) {
            this.handleControlUpdate.call(this, update);
        }

        if (this.pvSet.alarmPvs.indexOf(update.pv) > -1) {
            this.handleAlarmUpdate.call(this, update);
        }

        if (this.pvSet.colorPvs.indexOf(update.pv) > -1) {
            this.handleColorUpdate.call(this, update);
        }

        if (this.pvSet.indicatorPvs.indexOf(update.pv) > -1) {
            this.handleIndicatorUpdate.call(this, update);
        }

        if (this.pvSet.limitPvs.indexOf(update.pv) > -1) {
            this.handleLimitUpdate.call(this, update);
        }

        /*console.log('Update: ' + pv + ': ' + value);*/
    };

    jlab.wedm.PvObserver.prototype.refresh = function () {
        /*Do nothing by default*/
    };

    jlab.wedm.PvObserver.prototype.handleInfo = function (info) {
        /*console.log('Datatype: ' + info.datatype + ": " + info.count);*/

        var $obj = $("#" + this.id);

        if (!info.connected && $obj.length > 0) {
            /*Can't use $obj.addClass on SVG with jquery 2*/
            $obj[0].classList.add("disconnected-pv");
            $obj[0].classList.remove("waiting-for-state");
            $obj.css("border", "1px solid " + jlab.wedm.disconnectedAlarmColor);
            $obj[0].classList.remove("interactable");
        }
    };

    jlab.wedm.PvObserver.prototype.handleControlUpdate = function () {
        console.log('control update called - this should be overridden; id: ' + this.id);
    };

    jlab.wedm.PvObserver.prototype.handleAlarmUpdate = function () {
        console.log('alarm update called - this should be overridden; id: ' + this.id);
    };

    jlab.wedm.PvObserver.prototype.handleColorUpdate = function () {
        console.log('color update called - this should be overridden; id: ' + this.id);
    };

    jlab.wedm.PvObserver.prototype.handleIndicatorUpdate = function () {
        console.log('indicator update called - this should be overridden; id: ' + this.id);
    };

    jlab.wedm.PvObserver.prototype.handleVisibilityUpdate = function (update) {
        var pv = update.pv;
        var value = update.value;
        var $obj = $("#" + this.id);

        if (jlab.wedm.isCalcExpr(this.pvSet.visPvExpr)) {
            var pvs = [];
            for (var i = 0; i < this.pvSet.visPvs.length; i++) {
                var name = this.pvSet.visPvs[i],
                        val = this.pvNameToValueMap[name];

                if (typeof val === 'undefined') {
                    /*Still more PVs we need values from*/
                    return;
                }
                pvs.push(val);
            }

            value = jlab.wedm.evalCalcExpr(this.pvSet.visPvExpr, pvs);
        }

        /*From a visibility perspective true = 1 and false = 0*/
        if (value === true) {
            value = 1;
        } else if (value === false) {
            value = 0;
        }

        /*console.log('val: ' + value);
         $obj.attr("data-value", value);*/

        var invert = $obj.attr("data-vis-invert") === "true";

        var min = $obj.attr("data-vis-min");
        var max = $obj.attr("data-vis-max");

        /*Must ensure we are dealing with numbers; floating point too*/
        min = parseFloat(min);
        max = parseFloat(max);
        //console.log(this.id + ' value: ' + value);
        value = parseFloat(value);

        if (isNaN(value)) {
            value = 0.0;
        }

        //console.log(this.id + " - vis value: " + value + "; min: " + min + "; max: " + max + "; value >= min: " + (value >= min) + "; value < max: " + (value < max));

        var result = ((value >= min) && value < max);

        if (invert) {
            result = !result;
        }

        if (result) {
            $obj.show();
        } else {
            $obj.hide();
        }
    };

    jlab.wedm.PvObserver.prototype.handleLimitUpdate = function (update) {
        /*console.log('limit update ' + update.pv + ": " + update.value);*/

        var $obj = $("#" + this.id);
        if (update.pv.endsWith(".HOPR")) {
            $obj.attr("data-max", update.value);
            var pv = $obj.attr("data-pv");
            if (pv) {
                this.handleControlUpdate.call(this, {pv: pv, value: this.pvNameToValueMap[pv]});
            }
        } else if (update.pv.endsWith(".LOPR")) {
            $obj.attr("data-min", update.value);
            var pv = $obj.attr("data-pv");
            if (pv) {
                this.handleControlUpdate.call(this, {pv: pv, value: this.pvNameToValueMap[pv]});
            }
        } else if (update.pv.endsWith(".PREC")) {
            $obj.attr("data-precision", update.value);
            var pv = $obj.attr("data-pv");
            this.handleControlUpdate.call(this, {pv: pv, value: this.pvNameToValueMap[pv]});
        } else if (update.pv.endsWith(".EGU")) {
            $obj.attr("data-units", update.value);
            var pv = $obj.attr("data-pv");
            this.handleControlUpdate.call(this, {pv: pv, value: this.pvNameToValueMap[pv]});
        } else {
            console.log('Unknown limit PV: ' + update.pv);
        }
    };
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
    expr = expr.replace(new RegExp('and', 'gi'), "&");
    expr = expr.replace(new RegExp('or', 'gi'), "|");
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
         console.log(D);
         console.log(E);*/

        var stmt = expr.substring(7, expr.indexOf("}"));

        //console.log("before: " + stmt);

        stmt = jlab.wedm.convertEDMExpressionToJavaScript(stmt);

        //console.log("Expression: " + stmt);

        var result;

        try {
            //console.time("eval");
            result = eval(stmt);
            //console.timeEnd("eval");
        } catch (e) {
            result = 0;
            console.log("Unable to eval: " + e.message + "; stmt: " + stmt);
        }

        //console.log("Result: " + result);

        return result;

    } else { // Just return value of first pv
        return pvs[0];
    }
};

jlab.wedm.parseLocalVar = function (expr) {

    var name, /*Includes LOC prefix to avoid collision with EPICS PVs which are stored without prefix */
            declaration = false,
            end = expr.indexOf("=");

    if (end === -1) {
        name = expr;
    } else {
        name = expr.substring(0, end);
        declaration = true;
    }

    var local = jlab.wedm.localPvMap[name];

    if (typeof local === 'undefined' || (local.type === 'unresolved' && declaration)) {

        if (declaration) {
            var type = expr.substring(expr.indexOf("=") + 1, expr.indexOf(":")),
                    value = expr.substring(expr.indexOf(":") + 1);

            local = {};

            local.name = name;
            local.type = type;
            local.value = value;

            if (type === "e") {
                local.enumLabels = value.split(",");
                if (typeof local.enumLabels !== 'undefined' && local.enumLabels.length > 0) {
                    local.value = local.enumLabels[0];
                    local.enumLabels.shift();
                } else {
                    local.value = 0;
                }
            } else if (type !== "s") { // if not enum or string must be integer or double
                if (local.value === '') { // Default is zero for numbers
                    local.value = 0;
                }
            }

            jlab.wedm.localPvMap[name] = local;
        } else { /*Reference to an undeclared local variable encountered*/
            /*console.log("Reference to undeclared local variable encountered: " + name);*/
            local = {};
            local.name = name;
            local.type = "unresolved";
            local.value = 0;
            jlab.wedm.localPvMap[name] = local;
        }
    }

    return local;
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
            } else if (expr.indexOf(")", expr.lastIndexOf("(")) !== -1) {
                /*EDM allows multiple parenthesis at the end too*/
                end = expr.indexOf(")", expr.lastIndexOf("("));
            }

            expr.substring(expr.indexOf("}") + 2, end).split(",").forEach(function (pv) {
                /*We assume LOC declaration/assignments are not possible inside CALC otherwise we would need to parse out assignment*/
                pvs.push($.trim(pv));
            });
        } else if (expr.indexOf("EPICS\\") === 0) {
            pvs.push(expr.substring(6));
        } else if (expr.indexOf("LOC\\") === 0) {
            var local = jlab.wedm.parseLocalVar(expr);
            pvs.push(local.name);
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


        /* If EDM object is hidden due to an ancestor hidden Group then resize 
         * won't work.  Therefore it is imperitive that display: none isn't set
         * on anything initially and only hide starting in the init methods.  An
         * exception is made for "waiting-for-state" objects and we work around 
         * them */

        /*We temporarily remove waiting-for-state class so we can resize (can't resize invisible items)*/
        $parent[0].classList.remove("waiting-for-state");

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
            if (i > 10) {
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
                alarmSensitive = $obj.attr("data-indicator-alarm") === "true",
                showUnits = $obj.attr("data-show-units") === "true";

        if (limitsFromDb) {
            if (indicatorPvs.length === 1 && !jlab.wedm.isLocalExpr(indicatorPvs[0])) {
                basename = jlab.wedm.basename(indicatorPvs[0]);
                limitPvs.push(basename + ".HOPR");
                limitPvs.push(basename + ".LOPR");
            }

            if (ctrlPvs.length === 1 && !jlab.wedm.isLocalExpr(ctrlPvs[0])) {

                if ($obj.hasClass("ActiveMotifSlider")) {
                    basename = jlab.wedm.basename(ctrlPvs[0]);
                    limitPvs.push(basename + ".HOPR");
                    limitPvs.push(basename + ".LOPR");
                }

                if (typeof $obj.find(".screen-text") !== 'undefined') {
                    basename = jlab.wedm.basename(ctrlPvs[0]);
                    limitPvs.push(basename + ".PREC");
                }
            }
        }

        if (alarmSensitive && indicatorPvs.length === 1 && !jlab.wedm.isLocalExpr(indicatorPvs[0])) {
            basename = jlab.wedm.basename(indicatorPvs[0]);
            alarmPvs.push(basename + ".SEVR");
        } else if (alarmPvs.length === 1) {
            basename = jlab.wedm.basename(alarmPvs[0]);
            alarmPvs[0] = basename + ".SEVR";
        }

        if (showUnits && ctrlPvs.length === 1 && !jlab.wedm.isLocalExpr(ctrlPvs[0]) && typeof $obj.find(".screen-text") !== 'undefined') {
            basename = jlab.wedm.basename(ctrlPvs[0]);
            limitPvs.push(basename + ".EGU");
        }

        var allPvs = jlab.wedm.uniqueArray(ctrlPvs.concat(visPvs).concat(alarmPvs).concat(colorPvs).concat(indicatorPvs).concat(limitPvs)),
                widget = null,
                pvSet = {ctrlPvExpr: ctrlPvExpr, visPvExpr: visPvExpr, alarmPvExpr: alarmPvExpr, indicatorPvExpr: indicatorPvExpr, alarmPvs: alarmPvs, colorPvs: colorPvs, ctrlPvs: ctrlPvs, visPvs: visPvs, indicatorPvs: indicatorPvs, limitPvs: limitPvs};

        if (ctrlPvExpr !== undefined || visPvExpr !== undefined || alarmPvExpr !== undefined || colorPvExpr !== undefined || indicatorPvExpr !== undefined) {
            /*console.log($obj[0].className);*/

            var classes = $obj.attr("class").split(" "),
                    found = false;
            for (var i = 0; i < classes.length; i++) {
                var s = jlab.wedm.classToObserverMap[classes[i]];

                if (typeof s !== 'undefined') {
                    var f = jlab.wedm.stringToFunction(s);
                    widget = new f(id, pvSet);
                    found = true;
                    break;
                }
            }

            if (!found) {
                widget = new jlab.wedm.PvObserver(id, pvSet);
            }

            jlab.wedm.idWidgetMap[id] = widget;

            allPvs.forEach(function (pv) {
                jlab.wedm.addPvWithWidget(pv, widget, false);
            });
        }
    });
};

jlab.wedm.addPvWithWidget = function (pv, widget, immediate) {

    if (jlab.wedm.isLocalExpr(pv)) {
        var local = jlab.wedm.parseLocalVar(pv);

        jlab.wedm.pvWidgetMap[local.name] = jlab.wedm.pvWidgetMap[local.name] || [];
        jlab.wedm.pvWidgetMap[local.name].push(widget);

        if (jlab.wedm.localPvs.indexOf(local.name) === -1) {
            jlab.wedm.localPvs.push(local.name);
        }
    } else { /*EPICS PV*/

        jlab.wedm.pvWidgetMap[pv] = jlab.wedm.pvWidgetMap[pv] || [];
        jlab.wedm.pvWidgetMap[pv].push(widget);

        if (jlab.wedm.monitoredPvs.indexOf(pv) === -1) {
            jlab.wedm.monitoredPvs.push(pv);

            if (immediate) {
                jlab.wedm.con.monitorPvs([pv]);
            }
        }
    }
};

jlab.wedm.updatePv = function (detail) {
    //console.time("onupdate");
    $(jlab.wedm.pvWidgetMap[detail.pv]).each(function () {
        this.handleUpdate(detail);
    });
    //console.timeEnd("onupdate");    
};

jlab.wedm.infoPv = function (detail) {
    $(jlab.wedm.pvWidgetMap[detail.pv]).each(function () {
        this.handleInfo(detail);
    });
};

jlab.wedm.initWebsocket = function () {
    var options = {reconnectWaitMillis: 1000};

    jlab.wedm.con = new jlab.epics2web.ClientConnection(options);

    jlab.wedm.con.onopen = function (e) {
        /*This is for re-connect - and for initial batch of PVs*/
        if (jlab.wedm.monitoredPvs.length > 0) {
            jlab.wedm.con.monitorPvs(jlab.wedm.monitoredPvs);
        }
    };

    jlab.wedm.con.onupdate = function (e) {
        jlab.wedm.updatePv(e.detail);
    };

    jlab.wedm.con.oninfo = function (e) {
        jlab.wedm.infoPv(e.detail);
    };
};

jlab.wedm.initLocalPVs = function () {

    /*It seems button PV widgets must have enum PV with first item being selected index and second item is Pressed label and third item is Released label*/

    $(jlab.wedm.localPvs).each(function () {
        var local = jlab.wedm.localPvMap[this];

        if (typeof local.enumLabels !== 'undefined' && local.enumLabels.length > 0) {
            var info = {pv: local.name, connected: true, datatype: 'DBR_ENUM', 'enum-labels': local.enumLabels};
            jlab.wedm.infoPv(info);
        } else if (local.type === 'unresolved') {
            jlab.wedm.infoPv({pv: local.name, connected: false});
        }

        /*console.log("settting local pv initial value: " + local.name + " = " + local.value);*/
        jlab.wedm.updatePv({pv: local.name, value: local.value});
    });
};

jlab.wedm.doButtonDown = function ($obj) {
    $obj.addClass("button-down");
    if ($obj.attr("data-bg-color-rule")) {
        $obj.attr("data-bg-color-rule", $obj.attr("data-on-color"));
    } else {
        $obj.css("background-color", $obj.attr("data-on-color"));
    }
    $obj.find(".screen-text").text($obj.attr("data-on-label"));
    $obj.find(".text-wrap").css("border-width", "0");
    var local = jlab.wedm.parseLocalVar($obj.attr("data-pv")),
            pressValue = $obj.attr("data-press-value");
    if (typeof pressValue !== 'undefined') {
        if (local.value !== pressValue) {
            local.value = pressValue;
            /*console.log("settting local pv down value: " + local.name + " = " + local.value);*/
            jlab.wedm.updatePv({pv: local.name, value: local.value});
        }
    }
};

jlab.wedm.doButtonUp = function ($obj) {
    $obj.removeClass("button-down");
    if ($obj.attr("data-bg-color-rule")) {
        $obj.attr("data-bg-color-rule", $obj.attr("data-off-color"));
    } else {
        $obj.css("background-color", $obj.attr("data-off-color"));
    }
    $obj.find(".screen-text").text($obj.attr("data-off-label"));
    $obj.find(".text-wrap").css("border-width", "2px");
    var local = jlab.wedm.parseLocalVar($obj.attr("data-pv")),
            releaseValue = $obj.attr("data-release-value");
    if (typeof releaseValue !== 'undefined') {
        if (local.value !== releaseValue) {
            local.value = releaseValue;
            /*console.log("settting local pv up value: " + local.name + " = " + local.value);*/
            jlab.wedm.updatePv({pv: local.name, value: local.value});
        }
    }
};

jlab.wedm.macroQueryString = function (macros) {
    var url = "",
            tokens = macros.split(",");

    for (var i = 0; i < tokens.length; i++) {
        var kvPair = tokens[i],
                pieces = kvPair.split("=");
        if (pieces.length === 2) {
            url = url + "&%24(" + encodeURIComponent(pieces[0]) + ")=" + encodeURIComponent(pieces[1]);
        }
    }

    return url;
};

/*Due to observer prototype inheritance declaration order of functions matters so we provide a mechanism to ensure a dependent functions exists before init*/
jlab.wedm.observerDependencies = [];
jlab.wedm.initPvObserver = function (observer, dependency) {
    /*console.log('init: ' + observer);*/
    if (typeof dependency === 'undefined' || jlab.wedm.stringToFunction(dependency)) { /*No dependency or dependency already defined*/
        var f = jlab.wedm.stringToFunction(observer + 'Init');
        f();
        jlab.wedm.observerDependencies[observer] = jlab.wedm.observerDependencies[observer] || []; /*Invoke dependents*/
        for (var i = 0; i < jlab.wedm.observerDependencies[observer].length; i++) {
            /*console.log('init dep: ' + jlab.wedm.observerDependencies[observer][i]);*/
            f = jlab.wedm.stringToFunction(jlab.wedm.observerDependencies[observer][i] + 'Init');
            f();
        }
    } else { /*Queue init function to be called after dependency initialized*/
        /*console.log('Queuing: ' + observer);*/
        jlab.wedm.observerDependencies[dependency] = jlab.wedm.observerDependencies[dependency] || [];
        jlab.wedm.observerDependencies[dependency].push(observer);
    }
};

$(document).on("contextmenu", ".screen", function (e) {
    e.preventDefault();
});

jlab.wedm.propogatingMouseEvent = false;

jlab.wedm.propogateMouseEventToStackedElements = function (e, type) {

    if (jlab.wedm.propogatingMouseEvent === true) {
        return true;
    }

    jlab.wedm.propogatingMouseEvent = true;

    var depth = 0,
            stack = [],
            /*viewport coordinates are what this function wants (to work even if viewport changes due to resize/scrolling)*/
            element = document.elementFromPoint(e.clientX, e.clientY),
            $obj = $(element).closest(".ScreenObject");
    stack.push($obj);
    $obj.hide();
    // No event since top element already got it

    var element = document.elementFromPoint(e.clientX, e.clientY),
            $obj = $(element).closest(".ScreenObject"),
            previous = null;

    while (element !== previous) {
        if (depth > 5) {
            break;
        }
        depth++;
        switch (type) {
            case "mousedown":
                $obj.trigger($.Event("mousedown", {which: e.which, pageX: e.pageX, pageY: e.pageY}));
                break;
            case "mouseup":
                $obj.mouseup();
                break;
            case "click":
                $obj.trigger($.Event("click", {which: 1, pageX: e.pageX, pageY: e.pageY}));
                break;
            case "contextmenu":
                $obj.trigger($.Event("contextmenu", {which: 3, pageX: e.pageX, pageY: e.pageY}));
                break;
            default:
                console.log("unknown event type: " + type);
                break;
        }

        stack.push($obj);
        $obj.hide();
        previous = element;

        var element = document.elementFromPoint(e.clientX, e.clientY),
                $obj = $(element).closest(".ScreenObject");
    }

    for (var i in stack) {
        $obj = stack[i];
        $obj.show();
    }

    jlab.wedm.propogatingMouseEvent = false;
};

$(document).on("click", ".MouseSensitive", function (e) {
    jlab.wedm.propogateMouseEventToStackedElements(e, "click");
});

$(document).on("contextmenu", ".MouseSensitive", function (e) {
    jlab.wedm.propogateMouseEventToStackedElements(e, "contextmenu");
});

$(document).on("mousedown", ".MouseSensitive", function (e) {
    if (e.which === 2) {
        /*console.log('middle mousedown');
        console.log($(this));*/

        var msg = $(this).attr("data-pv");
        if (!msg) {
            msg = $(this).attr("data-alarm-pv");
        }
        if (!msg) {
            msg = $(this).attr("data-indicator-pv");
        }
        if (!msg) {
            msg = $(this).attr("data-color-pv");
        }
        if(!msg) {
            msg = $(this).attr("data-vis-pv");
        }
        if (msg) {
            $("#tooltip").text(msg);
            $(this).parent().prepend($("#tooltip")); /*We move tooltip to be under same parent element to ensure offsets are relative to same parent*/
            $("#tooltip").css("top", $(this).css("top"));
            $("#tooltip").css("left", parseInt($(this).css("left")) + parseInt($(this).css("width")) + "px");
            $("#tooltip").show();
            copyTextToClipboard(msg);
        } else {
            jlab.wedm.propogateMouseEventToStackedElements(e, "mousedown");            
        }
    } else {
        jlab.wedm.propogateMouseEventToStackedElements(e, "mousedown");
    }
    
    return false; /*don't use standard event propogation - we're doing our own*/
});

$(document).on("mouseout", ".MouseSensitive", function (e) {
    $("#tooltip").hide();
});

$(document).on("mouseup", ".MouseSensitive", function (e) {
    jlab.wedm.propogateMouseEventToStackedElements(e, "mouseup");
});

$(function () {
    /*By convention nothing should be display: none initially, otherwise resize text won't work*/
    jlab.wedm.resizeText();

    /*Now we call init callback for widgets*/
    jlab.wedm.initFuncs = jlab.wedm.initFuncs || [];
    for (var i = 0; i < jlab.wedm.initFuncs.length; i++) {
        jlab.wedm.initFuncs[i]();
    }

    jlab.wedm.createWidgets();

    jlab.wedm.initWebsocket();

    jlab.wedm.initLocalPVs();

    /*If a touch screen device assume no mouse and assume iOS which must have cusor: pointer css rule on clickable stuff or events won't propagate*/
    if (jlab.wedm.hasTouch()) {
        $(".MouseSensitive").addClass("iosClickable");
    }
});
