<%--
  ADOBE CONFIDENTIAL

  Copyright 2014 Adobe Systems Incorporated
  All Rights Reserved.

  NOTICE:  All information contained herein is, and remains
  the property of Adobe Systems Incorporated and its suppliers,
  if any.  The intellectual and technical concepts contained
  herein are proprietary to Adobe Systems Incorporated and its
  suppliers and may be covered by U.S. and Foreign Patents,
  patents in process, and are protected by trade secret or copyright law.
  Dissemination of this information or reproduction of this material
  is strictly forbidden unless prior written permission is obtained
  from Adobe Systems Incorporated.
--%><%
%><%@ page session="false"
           import="org.apache.sling.api.resource.Resource,
                org.apache.sling.api.resource.ResourceResolver,
                org.apache.sling.api.resource.ValueMap,
                java.util.List,
                com.adobe.granite.ui.components.AttrBuilder"%><%
%><%@include file="/libs/granite/ui/global.jsp"%><%
    ValueMap requestValueMap = resource.adaptTo(ValueMap.class);
	ValueMap valueMap = requestValueMap.get("propertiesValueMap", ValueMap.class);
    if (valueMap == null) {
        return;
    }
    String aprDataPath = resource.getPath() + "/jcr:content";
    String cronTrigger = xssAPI.encodeForHTMLAttr(valueMap.get("cronTrigger", ""));
    String eventFilter = xssAPI.encodeForHTMLAttr(valueMap.get("eventFilter", ""));
    String eventTopic = xssAPI.encodeForHTMLAttr(valueMap.get("eventTopic", ""));
    String description = xssAPI.encodeForHTMLAttr(valueMap.get("jcr:description", ""));
    String title = xssAPI.encodeForHTMLAttr(valueMap.get("jcr:title", ""));
    String packagePath = xssAPI.encodeForHTMLAttr(valueMap.get("packagePath", ""));
    String trigger = xssAPI.encodeForHTMLAttr(valueMap.get("trigger", ""));
    
    AttrBuilder attrs = new AttrBuilder(request, xssAPI);
    attrs.addClass("foundation-collection-item");
    attrs.addOther("foundation-collection-item-id", aprDataPath);
    attrs.add("is", "coral-table-row");
%>

<tr <%= attrs %>>
    <td is="coral-table-cell">
        <coral-checkbox coral-table-rowselect></coral-checkbox>
    </td>
    <td is="coral-table-cell"><%= title %></td>
    <td is="coral-table-cell"><%= description %></td>
    <td is="coral-table-cell"><%= packagePath %></td>
    <td class="event-type" is="coral-table-cell"><%= trigger %></td><%
    if(trigger!=null && trigger.equalsIgnoreCase("cron")) {
    	%><td is="coral-table-cell"><%= cronTrigger %></td>
    <td is="coral-table-cell"></td>
    <td is="coral-table-cell"></td><%
    }else if(trigger!=null && trigger.equalsIgnoreCase("event")){
	%><td is="coral-table-cell"></td>
    	<td is="coral-table-cell"><%= eventFilter %></td>
	    <td is="coral-table-cell"><%= eventTopic %></td><%
	}%>
</tr>