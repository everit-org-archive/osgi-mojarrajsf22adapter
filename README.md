osgi-mojarrajsf22adapter
========================

JSF 2.2.0 adapter to be able to use it it inside an OSGi environment (based
on Mojarra implementation)

Usage
-----

In web.xml add the following listener:

  <listener>
    <listener-class>org.everit.util.core.servlet.ServiceLoaderListener</listener-class>
  </listener>

In web.xml add the following servlet instead of original FacesServlet:

  <servlet>
    <servlet-name>Faces Servlet</servlet-name>
    <servlet-class>org.everit.osgi.mojarrajsf22adapter.OSGiFacesServlet</servlet-class>
    <load-on-startup>1</load-on-startup>
  </servlet>

In web.xml add a dummy FacesServlet without servlet mapping (this is
necessary as otherwise Mojarra will not configure itself well. It looks for
javax.faces.webapp.FacesServlet in web.xml and if it does not find it there
will be lots of exceptions):

  <servlet>
    <servlet-name>Dummy Faces Servlet</servlet-name>
    <servlet-class>javax.faces.webapp.FacesServlet</servlet-class>
  </servlet>

Be sure that your web bundle has the following two packages at the import
section of MANIFEST.MF:

 - javax.faces.model,
 - javax.faces.webapp


Tested on
---------

Tested on Jetty 8.4 and Equinox. Not all features of JSF 2.2 is tested with
the adapter but hopefully they work. When we find a bug and a solution it
will be in a new version of the adapter.


Tricks that are implemented
---------------------------

Mojarra uses almost always the thread context classloader. Therefore this
listener and servlet above wraps the original classes coming from Mojarra
and for each method call they replace the thread context classloader with
a custom developed one. The custom class loader mixes the classes of
jsf-impl, jsf-api, the adapter and the web application.

When the listener runs it creates the delegating classloader and puts it
into the servletcontext attributes. After that the servlet uses the same
classloader by getting itt from te servletcontext attributes. Mojarra uses
the classloader as keys for caches so it is very important to use exactly
the same classloader at method calls of the listener and the servlet.

To be able to find the factories the necessary files are placed into the
META-INF/services inside the adapter.


Using from maven
----------------

This library is distributed in [Everit Maven Repository][1]. There is an
example of including this repository in the pom.xml of this project.

[1]: http://repository.everit.biz/nexus/content/groups/public


Maven generated site
--------------------

You can find the maven generated site at
[http://everit.org/maven-sites/osgi-mojarrajsf22adapter].