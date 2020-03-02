package com.adobe.acs.commons.replication.packages.automatic.impl;

import org.apache.sling.xss.XSSFilter;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameterMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.servlets.post.HtmlResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ResourceBundle;

import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static javax.servlet.http.HttpServletResponse.SC_METHOD_NOT_ALLOWED;
import static javax.servlet.http.HttpServletResponse.SC_OK;

/**
 * The <code>AutomaticPackageReplicatorServlet</code> Create/Update/Delete Automatic Package Replication settings.
 */
@Component(metatype=false)
@Service(javax.servlet.Servlet.class)
@Properties({
    @Property(name="sling.servlet.paths", value="/bin/acs-commons/aprmanageconfiguration"),
    @Property(name="sling.servlet.methods", value={"POST"} ),
    @Property(name="sling.servlet.extensions", value={"json", ""} ),
    @Property(name="service.description", value="Automatic Package Replication Servlet")
})
public class AutomaticPackageReplicatorServlet extends SlingAllMethodsServlet {
    private static final long serialVersionUID = 1509019996493073156L;

    private static final String PAGE_TEMPLATE = "/apps/acs-commons/templates/utilities/automatic-package-replication";
    private static final String DATA_PATH = "/conf/acs-commons/automatic-package-replication"; 
    
    @Reference(policy = ReferencePolicy.STATIC)
    private XSSFilter xss;
    
    /**
     * @see org.apache.sling.api.servlets.SlingSafeMethodsServlet#doGet(org.apache.sling.api.SlingHttpServletRequest,
     *      org.apache.sling.api.SlingHttpServletResponse)
     */
    @Override
    protected void doGet(SlingHttpServletRequest request,
                         SlingHttpServletResponse response) throws ServletException, IOException {
    	
    }

    @Override
    protected void doPost(SlingHttpServletRequest request,
                          SlingHttpServletResponse response) throws ServletException,
            IOException {
        ResourceBundle resBundle = request.getResourceBundle(null);
        RequestParameterMap params = request.getRequestParameterMap();
        ResourceResolver resolver = request.getResourceResolver();
        Session userSession = resolver.adaptTo(Session.class);
        PageManager pageManager = resolver.adaptTo(PageManager.class); 
        
        if (params.getValue("delete") != null) {
        	String path = params.getValue("delete").getString().replaceAll("/jcr:content$", "");
        	try {
                Resource pageRes = resolver.getResource(path);
                if(pageRes != null) {
                	Page deletePage = pageRes.adaptTo(Page.class);
                	if(deletePage != null) {
                		pageManager.delete(deletePage, false, true);;
                	}
                	sendResponse(response, SC_OK, resBundle.getString("Entry deleted"));
                } else {
                	sendResponse(response, SC_METHOD_NOT_ALLOWED, "This configuration cannot be deleted.");
                }
            } catch (Exception e) {
                sendResponse(response, SC_INTERNAL_SERVER_ERROR, e.getMessage());
            }
        } else if (params.getValue("add") != null) {
        	String pageName = xss.filter(params.getValue("jcr:title").getString());
        	String description = xss.filter(params.getValue("jcr:description").getString());
            String packagePath = xss.filter(params.getValue("packagePath").getString());
            String trigger = xss.filter(params.getValue("trigger").getString());
            String eventFilter = xss.filter(params.getValue("eventFilter").getString());
            String eventTopic = xss.filter(params.getValue("eventTopic").getString());
            String cronTrigger = xss.filter(params.getValue("cronTrigger").getString());
            try {
            	Page newPage = pageManager.create(DATA_PATH, pageName, PAGE_TEMPLATE, pageName);
            	
            	if (newPage != null) {
            		Node newNode = newPage.adaptTo(Node.class);
                    Node pageJCRRes = newNode.getNode("jcr:content");
                    if (pageJCRRes != null) {
                    	pageJCRRes.setProperty("jcr:title", pageName);
                    	pageJCRRes.setProperty("jcr:description", description);
                    	pageJCRRes.setProperty("packagePath", packagePath);
                    	pageJCRRes.setProperty("trigger", trigger);
                    	if(StringUtils.isNotBlank(eventFilter)) {
                    		pageJCRRes.setProperty("eventFilter", eventFilter);
                    	}
                    	if(StringUtils.isNotBlank(eventTopic)) {
                    		pageJCRRes.setProperty("eventTopic", eventTopic);
                    	}
                    	if(StringUtils.isNotBlank(cronTrigger)) {
                    		pageJCRRes.setProperty("cronTrigger", cronTrigger);
                    	}
                    	userSession.save();
                    }
            	}else {
            		sendResponse(response, SC_INTERNAL_SERVER_ERROR, "Configuration couldn't be created.");
            	}
            	sendResponse(response, SC_OK, resBundle
                        .getString("Entry added"));
            	
            } catch (Exception e) {
                sendResponse(response, SC_INTERNAL_SERVER_ERROR, e.getMessage());
            }
        } else if (params.getValue("edit") != null) {
        	String pageJCRPath = params.getValue("edit").getString();
        	String description = xss.filter(params.getValue("jcr:description").getString());
            String packagePath = xss.filter(params.getValue("packagePath").getString());
            String trigger = xss.filter(params.getValue("trigger").getString());
            String eventFilter = xss.filter(params.getValue("eventFilter").getString());
            String eventTopic = xss.filter(params.getValue("eventTopic").getString());
            String cronTrigger = xss.filter(params.getValue("cronTrigger").getString());
            try {
            	Resource pageJCRRes = resolver.getResource(pageJCRPath);
            	if (pageJCRRes != null) {
            		Node pageJCRNode = pageJCRRes.adaptTo(Node.class);
                    if (pageJCRNode != null) {
                    	pageJCRNode.setProperty("jcr:description", description);
                    	pageJCRNode.setProperty("packagePath", packagePath);
                    	pageJCRNode.setProperty("trigger", trigger);
                    	if(StringUtils.isNotBlank(eventFilter)) {
                    		pageJCRNode.setProperty("eventFilter", eventFilter);
                    	}
                    	if(StringUtils.isNotBlank(eventTopic)) {
                    		pageJCRNode.setProperty("eventTopic", eventTopic);
                    	}
                    	if(StringUtils.isNotBlank(cronTrigger)) {
                    		pageJCRNode.setProperty("cronTrigger", cronTrigger);
                    	}

                    	userSession.save();
                    } else {
                		sendResponse(response, SC_INTERNAL_SERVER_ERROR, "Configuration couldn't be edited.");
                	}
            	} else {
            		sendResponse(response, SC_INTERNAL_SERVER_ERROR, "Configuration couldn't be edited.");
            	}
            	sendResponse(response, SC_OK, resBundle.getString("Entry edited"));
            } catch (Exception e) {
                sendResponse(response, SC_INTERNAL_SERVER_ERROR, e.getMessage());
            }
        } else {
            sendResponse(response, SC_METHOD_NOT_ALLOWED, "unsupported edit operation");
        }
    }

    /**
     * Method to send response
     *
     * @param response	The response
     * @param status	Status code
     * @param message   Message
     */
    private void sendResponse(HttpServletResponse response, int status,
                              String message) {
        HtmlResponse htmlResponse = new HtmlResponse();
        htmlResponse.setStatus(status, message);
        htmlResponse.setTitle(message);

        try {
            htmlResponse.send(response, true);
        } catch (IOException e) {
            log("Error while writing response", e);
        }
    }
}