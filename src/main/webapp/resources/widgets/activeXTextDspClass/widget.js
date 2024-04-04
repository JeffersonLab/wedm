jlab = jlab || {};
jlab.wedm = jlab.wedm || {};
jlab.wedm.classToObserverMap = jlab.wedm.classToObserverMap || {};
jlab.wedm.classToObserverMap['ActiveControlText'] = 'jlab.wedm.ControlTextPvObserver';

jlab.wedm.ControlTextPvObserverInit = function () {
    jlab.wedm.ControlTextPvObserver = function (id, pvSet) {
        jlab.wedm.StaticTextPvObserver.call(this, id, pvSet);
    };

    jlab.wedm.ControlTextPvObserver.prototype = Object.create(jlab.wedm.StaticTextPvObserver.prototype);
    jlab.wedm.ControlTextPvObserver.prototype.constructor = jlab.wedm.ControlTextPvObserver;

    jlab.wedm.ControlTextPvObserver.prototype.handleInfo = function (info) {
        jlab.wedm.StaticTextPvObserver.prototype.handleInfo.call(this, info);

        /* Ignore enum labels from .SEVR, .PREC, etc. */
        if (!info.pv.endsWith('.SEVR') &&
                !info.pv.endsWith('.PREC') &&
                !info.pv.endsWith('.EGU') &&
                !info.pv.endsWith('.HOPR') && 
                !info.pv.endsWith('.LOPR')) {
            this.enumValuesArray = info['enum-labels'] || [];
        }
    };

    jlab.wedm.ControlTextPvObserver.prototype.handleControlUpdate = function (update) {
        var $obj = $("#" + this.id),
                pv = update.pv,
                value = update.value,
                enumVal = this.enumValuesArray[value],
                format = $obj.attr("data-format"),
                precision = $obj.attr("data-precision"),
                hexPrefix = $obj.attr("data-hex-prefix") === "true",
                units = $obj.attr("data-units");

        if (typeof enumVal !== 'undefined' && this.pvSet.ctrlPvs.length === 1) {
            value = enumVal;
        } else { /*Not an enum*/
            if (jlab.wedm.isCalcExpr(this.pvSet.ctrlPvExpr)) {
                var pvs = this.toOrderedExpressionValues(this.pvSet.ctrlPvs);

                if(pvs == null) {
                    return; // We don't have complete set of variables yet!
                }

                value = jlab.wedm.evalCalcExpr(this.pvSet.ctrlPvExpr, pvs);
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

                    if ($obj.attr("data-db-limits") !== "true" && this.pvSet.ctrlPvs.length === 1 && !jlab.wedm.isLocalExpr(this.pvSet.ctrlPvs[0]) && typeof $obj.find(".screen-text") !== 'undefined') {
                        $obj.attr("data-db-limits", "true");
                        var basename = jlab.wedm.basename(pv),
                                precPv = basename + ".PREC";
                        this.pvSet.limitPvs.push(precPv);
                        jlab.wedm.addPvWithWidget(precPv, this, true);
                    }
                }

                if ("exponential" === format) {
                    value = value.toExponential(precision);
                } else {
                    value = value.toFixed(precision);
                }
            }
        }

        if (typeof units !== 'undefined') {
            value = value + " " + units;
        }

        $("#" + this.id + " .screen-text").text(value);
    };
};

jlab.wedm.initPvObserver('jlab.wedm.ControlTextPvObserver', 'jlab.wedm.StaticTextPvObserver');
