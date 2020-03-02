;(function(Granite, $, undefined) {
    "use strict";

    var rel = ".cq-workflow-admin-launchers-addlauncher";
    var ui = $(window).adaptTo("foundation-ui");

    $(document).on("foundation-contentloaded", function() {
        var $form = $(rel);
        if ($form) {
            $form.off("foundation-form-submitted"+rel)
                .on("foundation-form-submitted"+rel, function(event, success, xhr) {
                    if (success) {
                        ui.clearWait();
                        redirect($form);
                    } else {
                        var message = $(xhr.responseText).find("#Message").text();

                        ui.clearWait();
                        $form.trigger("reset");
                        ui.alert(Granite.I18n.get("Error"), message, "error");
                    }
                });
        }
    });

    function redirect(wizard) {
        var redirectTo = wizard.find("[name=':redirect']").val();
        if (redirectTo) {
            window.location = Granite.HTTP.externalize(redirectTo);
        }
    }
})(Granite, Granite.$);