jlab = jlab || {};
jlab.wedm = jlab.wedm || {};
jlab.wedm.classToObserverMap = jlab.wedm.classToObserverMap || {};
jlab.wedm.classToObserverMap['ActiveControlText'] = 'jlab.wedm.ControlTextPvObserver';


jlab.wedm.StaticTextPvObserver = jlab.wedm.StaticTextPvObserver || function () {};

jlab.wedm.ControlTextPvObserver = function (id, pvSet) {
    jlab.wedm.StaticTextPvObserver.call(this, id, pvSet);
};

jlab.wedm.ControlTextPvObserver.prototype = Object.create(jlab.wedm.StaticTextPvObserver.prototype);
jlab.wedm.ControlTextPvObserver.prototype.constructor = jlab.wedm.ControlTextPvObserver;

jlab.wedm.ControlTextPvObserver.prototype.handleInfo = function (info) {
    jlab.wedm.StaticTextPvObserver.prototype.handleInfo.call(this, info);

    this.enumValuesArray = info['enum-labels'] || [];
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

    if (typeof enumVal !== 'undefined') {
        value = enumVal;
    } else { /*Not an enum*/
        if (jlab.wedm.isCalcExpr(this.pvSet.ctrlPvExpr)) {
            var pvs = [];
            for (var i = 0; i < this.pvSet.ctrlPvs.length; i++) {
                var name = this.pvSet.ctrlPvs[i],
                        val;

                val = this.pvNameToValueMap[name];

                if (typeof val === 'undefined') {
                    /*Still more PVs we need values from*/
                    return;
                }
                pvs.push(val);
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

            value = value.toFixed(precision);
        }
    }

    if (typeof units !== 'undefined') {
        value = value + " " + units;
    }

    $("#" + this.id + " .screen-text").text(value);
};