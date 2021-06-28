jlab = jlab || {};
jlab.wedm = jlab.wedm || {};
jlab.wedm.classToObserverMap = jlab.wedm.classToObserverMap || {};
jlab.wedm.classToObserverMap['ActiveByte'] = 'jlab.wedm.BytePvObserver';

jlab.wedm.BytePvObserverInit = function () {

    jlab.wedm.BytePvObserver = function (id, pvSet) {
        jlab.wedm.PvObserver.call(this, id, pvSet);
    };

    jlab.wedm.BytePvObserver.prototype = Object.create(jlab.wedm.PvObserver.prototype);
    jlab.wedm.BytePvObserver.prototype.constructor = jlab.wedm.BytePvObserver;

    jlab.wedm.BytePvObserver.prototype.handleInfo = function (info) {
        /*console.log('Datatype: ' + info.datatype + ": " + info.count);*/

        var $obj = $("#" + this.id);

        if (!info.connected && $obj.length > 0) {
            /*Can't use $obj.addClass on SVG with jquery 2*/
            $obj[0].classList.add("disconnected-pv");
            $obj[0].classList.remove("waiting-for-state");
            $obj.find(".bit").css("fill", "black");
        }
    };

    jlab.wedm.BytePvObserver.prototype.handleControlUpdate = function (update) {
        var $obj = $("#" + this.id);

        var value = update.value,
                onColor = $obj.attr("data-on-color"),
                offColor = $obj.attr("data-off-color"),
                shift = $obj.attr("data-shift"),
                littleEndian = $obj.attr("data-little-endian") === "true",
                $bits = $obj.find(".bit"),
                index;

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
};

jlab.wedm.initPvObserver('jlab.wedm.BytePvObserver', 'jlab.wedm.PvObserver');