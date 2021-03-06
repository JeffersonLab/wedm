jlab = jlab || {};
jlab.wedm = jlab.wedm || {};
jlab.wedm.classToObserverMap = jlab.wedm.classToObserverMap || {};
jlab.wedm.classToObserverMap['ActiveChoiceButton'] = 'jlab.wedm.ChoicePvObserver';

jlab.wedm.ChoicePvObserverInit = function () {
    jlab.wedm.ChoicePvObserver = function (id, pvSet) {
        jlab.wedm.PvObserver.call(this, id, pvSet);
    };

    jlab.wedm.ChoicePvObserver.prototype = Object.create(jlab.wedm.PvObserver.prototype);
    jlab.wedm.ChoicePvObserver.prototype.constructor = jlab.wedm.ChoicePvObserver;

    jlab.wedm.ChoicePvObserver.prototype.handleControlUpdate = function (update) {
        var $obj = $("#" + this.id),
                topShadColor = $obj.attr("data-top-shad-color"),
                botShadColor = $obj.attr("data-bot-shad-color"),
                selectColor = $obj.attr("data-select-color"),
                fgColor = $obj.attr("data-fg-color");

        var value = update.value,
                enumVal = this.enumValuesArray[value];

        //$(".ActiveChoiceButton[data-pv='" + this.pv + "']").text(value);
        $obj.find(".choice").each(function () {
            var $btn = $(this);

            if ($btn.text() === enumVal) {
                $btn.css("border-top", "1px solid " + botShadColor);
                $btn.css("border-left", "1px solid " + botShadColor);
                $btn.css("border-right", "1px solid " + topShadColor);
                $btn.css("border-bottom", "1px solid " + topShadColor);

                $btn.find("span").css("background-color", selectColor);
                //$btn.find("span").css("opacity", "0.75");
                $btn.find("span").css("color", fgColor);

            } else {
                $btn.css("border-top", "2px solid " + topShadColor);
                $btn.css("border-left", "2px solid " + topShadColor);
                $btn.css("border-right", "1px solid " + botShadColor);
                $btn.css("border-bottom", "1px solid " + botShadColor);

                $btn.find("span").css("background-color", "transparent");
                //$btn.find("span").css("opacity", "1.0");
                $btn.find("span").css("color", "inherit");
            }
        });
    };

    jlab.wedm.ChoicePvObserver.prototype.handleInfo = function (info) {
        /*console.log('Datatype: ' + info.datatype + ": " + info.count + ": " + info['enum-labels']);*/

        jlab.wedm.PvObserver.prototype.handleInfo.call(this, info);

        var $obj = $("#" + this.id);

        /*In addition to standard disconnected stuff in super object we do more: */
        if (!info.connected) {
            $obj.css("background-color", "transparent");
        }

        /*Only get enum labels from control PV, ignore color PV for example*/
        if (info.connected && this.pvSet.ctrlPvs.indexOf(info.pv) > -1) {
            this.enumValuesArray = info['enum-labels'];

            if (typeof this.enumValuesArray !== 'undefined') {
                var states = this.enumValuesArray.length;

                var horizontal = $obj.attr("data-orientation") === 'horizontal',
                        width = $obj.width(),
                        height = $obj.height(),
                        btnWidth = (width / states) - (states - 2),
                        btnHeight = height,
                        html = "",
                        left = 0,
                        top = 0;

                if (!horizontal) { // vertical
                    btnWidth = width;
                    btnHeight = (height / states) - (states - 2);
                }

                for (var i = 0; i < this.enumValuesArray.length; i++) {
                    html = html + '<div class="choice" style="display: table; overflow: hidden; top: ' + top + 'px; left: ' + left + 'px; width: ' + btnWidth + 'px; height: ' + btnHeight + 'px; text-align: center; border-top: 1px solid rgb(255, 255, 255); border-left: 1px solid rgb(255, 255, 255); border-bottom: 1px solid rgb(0, 0, 0); border-right: 1px solid rgb(0, 0, 0);"><span style="display: table-cell; vertical-align: middle; width: ' + (btnWidth - 3) + 'px; max-width: ' + (btnWidth - 3) + 'px;">' + this.enumValuesArray[i] + '</span></div>';
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
        }
    };

    jlab.wedm.ChoicePvObserver.prototype.handleAlarmUpdate = function (update) {
        var $obj = $("#" + this.id),
                sevr = update.value * 1,
                fgAlarm = $obj.attr("data-fg-alarm") === "true",
                invalid = false;

        $obj.attr("data-sevr", sevr);
        $obj[0].classList.remove("waiting-for-state");

        if (fgAlarm) {
            $obj.find(".choice").css("color", "inherit");
        }

        if (typeof sevr !== 'undefined') {
            if (sevr === 0) { // NO_ALARM
                if (fgAlarm) {
                    $obj.attr("data-fg-color", jlab.wedm.noAlarmColor);
                }
            } else if (sevr === 1) { // MINOR
                if (fgAlarm) {
                    $obj.attr("data-fg-color", jlab.wedm.minorAlarmColor);
                }
            } else if (sevr === 2) { // MAJOR
                if (fgAlarm) {
                    $obj.attr("data-fg-color", jlab.wedm.majorAlarmColor);
                }
            } else if (sevr === 3) { // INVALID
                invalid = true;
            }
        } else {
            invalid = true;
        }

        if (invalid) {
            if (fgAlarm) {
                $obj.attr("data-fg-color", jlab.wedm.invalidAlarmColor);
            }
        }

        var pv = this.pvSet.ctrlPvs[0],
                value = this.pvNameToValueMap[pv];

        this.handleControlUpdate({pv: pv, value: value});
    };

    jlab.wedm.ChoicePvObserver.prototype.handleColorUpdate = function (update) {
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
            $obj.attr("data-fg-color", color);
            $obj.find(".choice span").css("color", color);
        }

        if (typeof bgRuleIndex !== 'undefined') {
            stmt = jlab.wedm.colorRules[bgRuleIndex];
            color = jlab.wedm.evalColorExpr.call(this, stmt, update.value);
            $obj.css("background-color", color);
            $obj.attr("data-bg-color", color);
            $obj.find(".choice span").css("background-color", color);
        }
    };

    $(document).on("click", ".ActiveChoiceButton.interactable .choice", function () {
        var $choice = $(this),
                $parent = $choice.closest(".ActiveChoiceButton"),
                index = $choice.index(),
                widget = jlab.wedm.idWidgetMap[$parent.attr("id")],
                pv = widget.pvSet.ctrlPvs[0];

        $parent.find(".choice").removeClass("selected-choice");
        $choice.addClass("selected-choice");

        jlab.wedm.updatePv({pv: pv, value: index});
    });
};

jlab.wedm.initPvObserver('jlab.wedm.ChoicePvObserver', 'jlab.wedm.PvObserver');