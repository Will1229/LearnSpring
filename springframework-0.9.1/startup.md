# Application startup

#### web.xml
A spring web project contains a web.xml file which configures the root application context.

```xml
<listener>
  <listener-class>com.interface21.web.context.ContextLoaderListener</listener-class>
</listener>
```

#### ContextLoaderListener

```java
public class ContextLoaderListener implements ServletContextListener {

	public void contextInitialized(ServletContextEvent event) {
		ContextLoader.initContext(event.getServletContext());
	}

	public void contextDestroyed(ServletContextEvent event) {
		ContextLoader.closeContext(event.getServletContext());
	}
}
```
ContextLoaderListener implements the interface in ServletContextListener. It calls ContextLoader to init and close context. Usually when a serlet container (like Tomcat) starts, the `contextInitialized` will be triggered. In later version of springframework ContextLoaderListener extends ContextLoader.

#### ContextLoader
ContextLoader first get the context class name from the given servletContext.
```java
String contextClass = servletContext.getInitParameter(CONTEXT_CLASS_PARAM);
````

Then a new instance of the given application context is created by calling `newInstance()`. The context class is supposed to implement WebApplicationContext. The given servlet context is referenced by the newly created context instance so that it can be retrieved any time from application context.

```java
Class clazz = (contextClass != null ? Class.forName(contextClass) : DEFAULT_CONTEXT_CLASS);
logger.info("Loading root WebApplicationContext: using context class '" + clazz.getName() + "'");

if (!WebApplicationContext.class.isAssignableFrom(clazz)) {
  throw new ApplicationContextException("Context class is no WebApplicationContext: " + contextClass);
}

WebApplicationContext webApplicationContext = (WebApplicationContext) clazz.newInstance();
webApplicationContext.setServletContext(servletContext);
return webApplicationContext;
```

#### XmlWebApplicationContext
The default context class is XmlWebApplicationContext. If the context class is initialized as root application, then the display name will be set to "Root WebApplicationContext". Otherwise it's set to "WebApplicationContext for namespace XXX". 

The `setServletContext` method does more things than expected. It also initialize the configuration location. If the location and config file name are not specified from servlet context, it will use the default config file: `/WEB-INF/applicationContext.xml`

```java
public void setServletContext(ServletContext servletContext) throws ApplicationContextException {
  this.servletContext = servletContext;
  this.configLocation = initConfigLocation();
  logger.info("Using config location '" + this.configLocation + "'");
  refresh();

  if (this.namespace == null) {
    // We're the root context
    WebApplicationContextUtils.publishConfigObjects(this);
    // Expose as a ServletContext object
    WebApplicationContextUtils.publishWebApplicationContext(this);
  }	
}
```

The most important method `refresh()` is also called here. It does a series of things to load/reload configurations:

```java
public final void refresh() throws ApplicationContextException {
  if (this.contextOptions != null && !this.contextOptions.isReloadable())
    throw new ApplicationContextException("Forbidden to reload config");

  this.startupTime = System.currentTimeMillis();

  refreshBeanFactory();

  if (getBeanDefinitionCount() == 0)
    logger.warn("No beans defined in ApplicationContext [" + getDisplayName() + "]");
  else
    logger.info(getBeanDefinitionCount() + " beans defined in ApplicationContext [" + getDisplayName() + "]");

  // invoke configurers that can override values in the bean definitions
  invokeContextConfigurers();

  // load options bean for this context
  loadOptions();

  // initialize message source for this context
  initMessageSource();

  // initialize other special beans in specific context subclasses
  onRefresh();

  // check for listener beans and register them
  refreshListeners();

  // instantiate singletons this late to allow them to access the message source
  preInstantiateSingletons();

  // last step: publish respective event
  publishEvent(new ContextRefreshedEvent(this));
}

```


