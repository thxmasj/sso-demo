package it.thomasjohansen.hello.config;

import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

public class WebApplicationContext implements WebApplicationInitializer {

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
        AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
		servletContext.addListener(new ContextLoaderListener(applicationContext));
		applicationContext.register(SpringConfig.class);
		ServletRegistration.Dynamic dispatcher = servletContext.addServlet("cxf", new CXFServlet());
		dispatcher.setLoadOnStartup(0);
		dispatcher.addMapping("/*");
	}

}
