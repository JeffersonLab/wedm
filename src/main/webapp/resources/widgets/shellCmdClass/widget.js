jlab = jlab || {};
jlab.wedm = jlab.wedm || {};

$(document).on("click", ".ShellCommand", function (e) {
    var url = $(this).data("url");

    if(url) {
        window.open(url);
    }
});
