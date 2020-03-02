;(function(document, $, Granite, undefined) {
    var REMOVED_EVENT_TYPE = 2;

    var ns = "cq-workflow-launcher";
    var relEventType = ".js-cq-workflow-eventTypeSelect";
    var relNodeTypeSelect = ".js-cq-workflow-Nodetypeselect";

    var comparisonObject = {};

    var nodeTypeSelectionFilter = function(value, display) {
        var isRemoveSelected = $(relEventType).data("select").getValue() == REMOVED_EVENT_TYPE;
        if (isRemoveSelected) {
            return comparisonObject.hasOwnProperty(value);
        } else {
            return "Any Node Type" != value;
        }
    };

    $(document).on("foundation-contentloaded", function() {
        $(relEventType).on("change."+ns, function(e) {
            var selectList = $(relNodeTypeSelect).find(".coral-SelectList").data("selectList");
            selectList.filter(nodeTypeSelectionFilter);

            var firstVisibleOption = $(".js-cq-workflow-Nodetypeselect").find(".coral-SelectList li:not('.is-hidden')").first();
            var selectedValue = firstVisibleOption.data("value");
            var displayValue = firstVisibleOption.text();

            $(relNodeTypeSelect).data("select")._handleSelected(jQuery.Event("selected", {selectedValue: selectedValue, displayedValue: displayValue }));
        });

        if ($(relNodeTypeSelect).length > 0) {
            var $selectList = $(relNodeTypeSelect).find(".coral-SelectList");
            var selectList = $selectList.data("selectList");

            var removeNodeTypes = $(relNodeTypeSelect).data("filterRemovedNodeTypes");
            if (removeNodeTypes) {
                var removeFilterListArray = removeNodeTypes.split(",");
                comparisonObject = {};
                removeFilterListArray.forEach(function(item) {
                    comparisonObject[item] = item;
                });

                selectList.filter(nodeTypeSelectionFilter);
            }
        }
    });



})(document, Granite.$, Granite);


