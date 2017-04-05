$(document).on("click", ".edl-file", function () {
    var url = $(this).attr("href"),
            macros = $("#macros").val(),
            tokens = macros.split(",");

    for (var i = 0; i < tokens.length; i++) {
        var kvPair = tokens[i],
                pieces = kvPair.split("=");
        if (pieces.length === 2) {
            url = url + "&%24(" + encodeURIComponent(pieces[0]) + ")=" + encodeURIComponent(pieces[1]);
        }
    }

    window.location.href = url;

    return false; /* Don't actually do default anchor click */
});
