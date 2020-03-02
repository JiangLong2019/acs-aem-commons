<%--
  ADOBE CONFIDENTIAL

  Copyright 2013 Adobe Systems Incorporated
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
%><%@page session="false"
            import="com.adobe.granite.ui.components.Config,
                    com.adobe.granite.ui.components.ExpressionHelper,
                    com.adobe.granite.ui.components.PagingIterator,
                    com.adobe.granite.ui.components.ds.AbstractDataSource,
                    com.adobe.granite.ui.components.ds.DataSource,
                    com.adobe.granite.ui.components.ds.EmptyDataSource,
                    com.adobe.granite.ui.components.ds.ValueMapResource,
                  	com.day.cq.wcm.api.Page,
                    org.apache.commons.collections.Transformer,
                    org.apache.commons.collections.iterators.TransformIterator,
                    org.apache.sling.api.SlingHttpServletRequest,
                    org.apache.sling.api.resource.Resource,
                    org.apache.sling.api.resource.ValueMap,
                    org.apache.sling.api.wrappers.ValueMapDecorator,
                    java.util.HashMap,
                    java.util.Iterator" %><%
%><%@include file="/libs/granite/ui/global.jsp" %><%
    final Config dsCfg = new Config(resource.getChild(Config.DATASOURCE));
    final String itemRT = dsCfg.get("itemResourceType", String.class);

    ExpressionHelper ex = cmp.getExpressionHelper();
    final Integer offset = ex.get(dsCfg.get("offset", String.class), Integer.class);
    Integer paramLimit = ex.get(dsCfg.get("limit", String.class), Integer.class);
    if (paramLimit != null) {
        // for infinite scroll according to the table definition the size has to
        // return one more than specified to indicate that there are more items
        paramLimit = paramLimit + 1;
    }
	final Integer limit = paramLimit;

    final String dataPath = "/conf/acs-commons/automatic-package-replication";
    
    DataSource ds = EmptyDataSource.instance();

    try {

		Resource rootRes = resourceResolver.getResource(dataPath);
		Iterator<Resource> dataPageIterator = rootRes.getChildren().iterator();

        if (dataPageIterator != null && dataPageIterator.hasNext()) {
            final SlingHttpServletRequest slingHttpRequest = slingRequest;

            ds = new AbstractDataSource() {
                @Override
                public Iterator<Resource> iterator() {
                    Iterator<Resource> pagingIterator = new PagingIterator<Resource>(dataPageIterator, offset, limit);

                    return new TransformIterator(pagingIterator, new Transformer() {
                        public Object transform(Object input) {
                            Resource pageResource = (Resource) input;

                            ValueMap vm = new ValueMapDecorator(new HashMap<String, Object>());
        					Page page = pageResource.adaptTo(Page.class);
                            vm.put("id", pageResource.getPath());                            
                            vm.put("propertiesValueMap", page.getProperties());

                            ValueMapResource vmr = new ValueMapResource(slingHttpRequest.getResourceResolver(), pageResource.getPath(), itemRT, vm);
                            return vmr;
                        }
                    });
                }
            };
        }
    } catch (Exception e) {
        log.error(e.getMessage());
    }

    request.setAttribute(DataSource.class.getName(), ds);
log.info("OK!");
%>