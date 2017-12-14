jlab = jlab || {};

/*No PvObserver used at this time*/

$(document).on("click contextmenu", ".RelatedDisplay", function (e) {

    var expected = 1;

    if ($(this).hasClass("swapped-buttons")) {
        expected = 3;
    }

    if (e.which === expected) {
        var files = [],
                labels = [],
                macros = [],
                $obj = $(this);

        for (var i = 0; i < 64; i++) {
            var file = $obj.attr("data-linked-file-" + i),
                    label = $obj.attr("data-linked-label-" + i),
                    macro = $obj.attr("data-symbols-" + i),
                    doNotPropagate = $obj.attr("data-propagate-" + i) === "false";

            if (file === undefined) {
                break;
            } else {
                files.push(file);

                if (label === undefined || label === '') {
                    labels.push("");
                } else {
                    labels.push(label);
                }

                if (macro === undefined || macro === '') {
                    macros.push("");
                } else {
                    macros.push(macro);
                }

                if (!doNotPropagate) {
                    if (macros[i].length > 0) {
                        macros[i] = macros[i] + "," + jlab.wedm.macroString;
                    } else {
                        macros[i] = jlab.wedm.macroString;
                    }
                }
            }
        }

        var path = jlab.contextPrefix + '/wedm/screen?edl=',
                //left = $obj.css("left"),
                //right = $obj.css("top");
                left = e.pageX + "px",
                top = e.pageY + "px";

        if (files.length === 0) {
            /*Do nothing*/
        } else if (files.length === 1) {
            window.open(path + files[0] + jlab.wedm.macroQueryString(macros[0]), '_blank');
        } else {
            var $html = $('<div class="related-display-menu" style="left: ' + left + '; top: ' + top + ';" ><ul></ul></div>');

            for (var i = 0; i < files.length; i++) {
                $html.find("ul").append('<li class="anchor-li"><a href="' + path + files[i] + jlab.wedm.macroQueryString(macros[i]) + '" target="_blank">' + labels[i] + '</a></li>');
            }

            $(document.body).append($html);
        }
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
    $(this).closest(".related-display-menu").remove();
    return false; // Don't let anchor open another
});