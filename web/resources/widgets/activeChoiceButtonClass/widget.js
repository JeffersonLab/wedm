jlab = jlab || {};
jlab.wedm = jlab.wedm || {};
jlab.wedm.classToObserverMap = jlab.wedm.classToObserverMap || {};
jlab.wedm.classToObserverMap['ActiveChoiceButton'] = 'jlab.wedm.ChoicePvObserver';

jlab.wedm.ChoicePvObserver = function (id, pvSet) {
    jlab.wedm.PvObserver.call(this, id, pvSet);
};

jlab.wedm.ChoicePvObserver.prototype = Object.create(jlab.wedm.PvObserver.prototype);
jlab.wedm.ChoicePvObserver.prototype.constructor = jlab.wedm.ChoicePvObserver;

jlab.wedm.ChoicePvObserver.prototype.handleControlUpdate = function (update) {
    var $obj = $("#" + this.id);

    var value = update.value,
            enumVal = this.enumValuesArray[value];

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

jlab.wedm.ChoicePvObserver.prototype.handleInfo = function (info) {
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