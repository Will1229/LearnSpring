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

We go through all the methods one by one.

##### AbstractApplicationContext.refreshBeanFactory()

```java
protected void refreshBeanFactory() throws ApplicationContextException {
	String identifier = "application context with display name [" + getDisplayName() + "]";
	InputStream is = null;
	try {
		// Supports remote as well as local URLs
		is = getInputStreamForBeanFactory();
		this.xmlBeanFactory = new XmlBeanFactory(getParent());
		this.xmlBeanFactory.setEntityResolver(new ResourceBaseEntityResolver(this));
		this.xmlBeanFactory.loadBeanDefinitions(is);

```

The method gets the configuration file applicationContext.xml as an InputStream. And then it instantiates bean factory and entity resolver. In the end it loads bean definitions.

##### XmlBeanFactory.loadBeanDefinitions(InputStream is)

```java
DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
logger.debug("Using JAXP implementation [" + factory + "]");
factory.setValidating(true);
DocumentBuilder db = factory.newDocumentBuilder();
db.setErrorHandler(new BeansErrorHandler());
db.setEntityResolver(this.entityResolver != null ? this.entityResolver : new BeansDtdResolver());
Document doc = db.parse(is);
loadBeanDefinitions(doc);
```

This method first initializes XML DOM parser and unmarshalls the xml to DOM document. Then loads it.

##### XmlBeanFactory.loadBeanDefinitions(Document doc)
```java
public void loadBeanDefinitions(Document doc) throws BeansException {
	Element root = doc.getDocumentElement();
	logger.debug("Loading bean definitions");
	NodeList nl = root.getElementsByTagName(BEAN_ELEMENT);
	logger.debug("Found " + nl.getLength() + " <" + BEAN_ELEMENT + "> elements defining beans");
	for (int i = 0; i < nl.getLength(); i++) {
		Node n = nl.item(i);
		loadBeanDefinition((Element) n);
	}
}
```

This method traversals the whole xml and finds all elements with tag "bean". Then loads them one by one.

##### XmlBeanFactory.loadBeanDefinitions(Element el)
```java
	AbstractBeanDefinition beanDefinition;

	PropertyValues pvs = getPropertyValueSubElements(el);
	beanDefinition = parseBeanDefinition(el, id, pvs);
	registerBeanDefinition(id, beanDefinition);
```

This method parse out bean difinition and register it into beanDefinitionMap. All the important bean attributes like "singleton", "init-method", etc. are parsed and saved. The actual class of the bean is wrapped in AbstractBeanDefinition.

##### AbstractApplicationContext.invokeContextConfigurers()
This method will modify application context's internal bean factory after initialization to override properties. No bean is instantiated yet.

After this the AbstractApplicationContext will loal a couple of special purpose beans: options bean, messageSource bean, context specific bean and listener beans. The beans which implement interface ApplicationListener are considerred as listener beans.





