jlab = jlab || {};
jlab.wedm = jlab.wedm || {};
jlab.wedm.classToObserverMap = jlab.wedm.classToObserverMap || {};
jlab.wedm.classToObserverMap['ActiveButton'] = 'jlab.wedm.ButtonPvObserver';

jlab.wedm.StaticTextPvObserver = jlab.wedm.StaticTextPvObserver || function () {};

jlab.wedm.ButtonPvObserver = function (id, pvSet) {
    jlab.wedm.StaticTextPvObserver.call(this, id, pvSet);
};

jlab.wedm.ButtonPvObserver.prototype = Object.create(jlab.wedm.StaticTextPvObserver.prototype);
jlab.wedm.ButtonPvObserver.prototype.constructor = jlab.wedm.ButtonPvObserver;

jlab.wedm.ButtonPvObserver.prototype.handleControlUpdate = function (update) {
    var $obj = $("#" + this.id),
            value = update.value,
            pressValue = $obj.attr("data-press-value"),
            releaseValue = $obj.attr("data-release-value");

    /*Only ActiveButton.toggle-buttons actually have visible state to update*/
    if ($obj.hasClass("ActiveButton") && $obj.hasClass("toggle-button")) {

        /*if press and release have same value we only do press*/
        if (typeof pressValue !== undefined && pressValue === value) {
            /*console.log("press state");*/

            $obj.removeClass("toggle-button-off");
            $obj.addClass("toggle-button-on");


            jlab.wedm.doButtonDown($obj);
        } else if (typeof releaseValue !== undefined && releaseValue === value) {
            /*console.log("release state");*/

            $obj.removeClass("toggle-button-on");
            $obj.addClass("toggle-button-off");

            jlab.wedm.doButtonUp($obj);
        }
    }
};

$(document).on("mousedown", ".interactable.push-button", function () {
    jlab.wedm.doButtonDown($(this));
});

$(document).on("mouseup mouseout", ".interactable.push-button", function () {
    if ($(this).hasClass("button-down")) {
        jlab.wedm.doButtonUp($(this));
    }
});

$(document).on("click", ".interactable.toggle-button", function () {
    var $obj = $(this);

    if ($obj.hasClass("toggle-button-off")) {
        $obj.removeClass("toggle-button-off");
        $obj.addClass("toggle-button-on");
        jlab.wedm.doButtonDown($obj);
    } else if ($obj.hasClass("toggle-button-on")) {
        $obj.removeClass("toggle-button-on");
        $obj.addClass("toggle-button-off");
        jlab.wedm.doButtonUp($obj);
    }
});