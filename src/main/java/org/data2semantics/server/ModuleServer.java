package org.data2semantics.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.data2semantics.modules.AbstractModule;
import org.data2semantics.modules.ModuleWrapper;
import org.data2semantics.util.RepositoryWriter;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModuleServer extends HttpServlet {
	
	
	private static Logger log = LoggerFactory.getLogger(ModuleWrapper.class);
	
	
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	
    	String moduleName = request.getParameter("module");
    	String fileName = request.getParameter("file");
    	String graph = request.getParameter("graph");
    	String resource = request.getParameter("resource");
    	
    	
	    try {
	    	AbstractModule module = ModuleWrapper.constructModule(moduleName, fileName,
					graph, resource);
	        
	        log.info("Starting module");
	        Repository outputRepository = module.start();
	        log.info("Module run completed");
	        
	        // TODO Add provenance information about ModuleWrapper run
	        
	        log.info("Starting RepositoryWriter (writing to output.n3)");
	        RepositoryWriter rw = new RepositoryWriter(outputRepository, "output.n3");
	        
	        rw.write();
	        log.info("Done");
	        
	        FileInputStream inputStream = new FileInputStream("output.n3");
	        String output = "NOTHING";
	        try {
	            output = IOUtils.toString(inputStream);
	        } finally {
	            inputStream.close();
	        }
	        
	        
	        response.setContentType("text/html");
	        response.setStatus(HttpServletResponse.SC_OK);
	        response.getWriter().println("<pre>");
	        response.getWriter().print(StringEscapeUtils.escapeHtml(output));
	        response.getWriter().println("</pre>");
	        response.getWriter().println("session=" + request.getSession(true).getId());
	        
	        
	    } catch (ClassNotFoundException e) {
	    	// TODO Auto-generated catch block
	        e.printStackTrace();
	    } catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RDFParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    
}
