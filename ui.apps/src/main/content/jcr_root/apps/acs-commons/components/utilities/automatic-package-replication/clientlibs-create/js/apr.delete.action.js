;(function(document, $, Granite, XSS, undefined) {
    "use strict";

    var title,
        single,
        multiple,
        progress;


    var deleteLaunchersDialogId = "#cq-workflow-admin-launchers-action-delete-modal";
    var rel = ".cq-workflow-admin-launchers-action-delete";
    var ui = $(window).adaptTo("foundation-ui");
    var MAX_DISPLAY_COUNT = 3;

    function execute(form, paths) {
        var $dialog = form.closest("coral-dialog");
        var coralDialog;
        if ($dialog && $dialog.length==1) {
            coralDialog = $dialog[0];
        }
        var data = form.serializeArray();

        var total = paths.length;

        ui.wait();

        var promises = [];
        for (var i = 0; i < paths.length; i++) {
            var path = paths[i];
            form.find("input:hidden[name='delete']").val(path);
            var forData = form.serializeArray();

            promises.push(
                $.ajax({
                    url: Granite.HTTP.externalize(form.attr("action")),
                    type: form.prop("method"),
                    data: $.param(forData)
                })
            );
        }

        $.when.apply($, promises)
            .then( function() {
                // success
                // hide the dialog
                coralDialog.hide();

                ui.clearWait();
                location.reload();
            }, function(val) {
                // failure
                // hide the dialog
                coralDialog.hide();

                // clear out our wait UI
                ui.clearWait();

                var div = document.createElement("div");
                div.innerHTML = val.responseText;

                var message = $("title", div).text();

                ui.alert(Granite.I18n.get("Error"),
                    Granite.I18n.get("Failed to delete configuration.  Error: '{0}'", message),
                    "error");
            });
    }

    $(document).on("submit", rel, function(e) {
        e.preventDefault();

        var paths = $(".foundation-collection .foundation-selections-item").toArray().map(function(v) {
            var item = $(v);
            return item.data("foundationCollectionItemId");
        });

        execute($(this), paths);
    });

    $(document).on("foundation-contentloaded", function() {
        var $deleteLaunchersDialog = $(deleteLaunchersDialogId);
        // check if we actually found the dialog (initial load won't have it yet)
        if ($deleteLaunchersDialog.length) {
            $deleteLaunchersDialog.off("coral-overlay:beforeopen.wfmodel.run")
                .on("coral-overlay:beforeopen.wfmodel.run", function () {
                    var $modal = $(this);

                    var selectedItems = $(".foundation-collection .foundation-selections-item");

                    var intro = $modal.find(".launcher-intro");
                    var introText = intro.data(selectedItems.length === 1 ? "templateSingle" : "templateMultiple");
                    intro.text(Granite.I18n.get(introText, selectedItems.length));

                    var maxCount = Math.min(selectedItems.length, MAX_DISPLAY_COUNT);

                    var list = [];
                    for (var i = 0; i < maxCount; i++) {
                        var selectedItem = $(selectedItems[i]);
                        var eventType = selectedItem.find(".event-type").text();
                        var glob = selectedItem.find(".launcher-glob").text();
                        glob = XSS.getXSSValue(glob);
                        list.push("<b>" + eventType + Granite.I18n.get(":", null, "added after event type, example 'Node Created'") + glob + "</b>");
                    }
                    if (selectedItems.length > maxCount) {
                        list.push("...");
                    }

                    $modal.find(".launchers").html("<br>" + list.join("<br>"));
                });

            var $submitButton = $deleteLaunchersDialog.find(".perform-instance-action");
            $submitButton.off("click"+rel)
                .on("click"+rel, function() {
                    ui.wait();
                    var paths = $(".foundation-collection .foundation-selections-item").toArray().map(function (v) {
                        var item = $(v);
                        return item.data("foundationCollectionItemId") || item.data("path");
                    });

                    var $form = $deleteLaunchersDialog.find("form");
                    execute($form, paths);
                });
        }
    });

})(document, Granite.$, Granite, _g.XSS);

