# Instructions to set up CoCoME Pickup Shop Eclipse environment

## Prerequisits

- Eclipse with Java EE support
- Glassfish 4.0 or higher (or Glassfish 3.1)

Quick Facts:
- ${GLASSFISH} - path to the Glassfish installation
- ${HOME}      - home folder of your user account

## Preparations

### 1. Install Glassfish

This will usually create a domain named 'domain1'. We assume that you
installed Glassfish in a directory represented by the variable
${GLASSFISH}, which we use in the remainder of this document to refer 
to the installation directory.

**Note:** in Unix/Linux path names use / and in Windows \. This may require
escaping in certain places, i.e., \\ for a \.

Glassfish is usually installed with the following layout:

- ${GLASSFISH}/bin
- ${GLASSFISH}/glassfish
- ${GLASSFISH}/javadb
- ${GLASSFISH}/mq
- ${GLASSFISH}/pkg
- ${GLASSFISH}/README.txt

### 2. Configure Glassfish Domains

We must create one Glassfish domain for the pickup shop website.

The domain name is:
- cocome-pickup

#### 2.1 Create domain using asadmin

Each domain requires their own port numbers for the service and
administration. The defaults are 8080 for the service and 4848 for the
administration interface. However, to avoid conflict with running
systems we define:
- cocome-pickup: admin 8548, service 8580

You may choose other port numbers, especially when on of these ports is
already in use. You may not be able to select port numbers below 32000,
as this can be prohibited by firewalls.

1. Change to ${GLASSFISH}/glassfish 
2. Execute bin/asadmin as follows. The tool will prompt for a password.
   You may leave that empty.

```
bin/asadmin create-domain --portbase 8500 cocome-pickup
```
    

**Note:** It is best to start each domain before creating the next when using 
this method. Otherwise both domains may end up using the same ports, for 
example for the jmx service, which will then cause a conflict if both 
domains are started on the same host.

#### 2.2 Create domain using Eclipse

If you installed the Glassfish Tools from the Oracle Enterprise Pack 
for Eclipse you can create a new domain directly from within Eclipse. 
To do this, open the Servers view in Eclipse and add a new server with 
Right-Click -> New -> Server. In the wizard, select Glassfish in the 
version you use and enter cocome-logic or cocome-web as the server name.

In the next step of this wizard click on the "+" signs to the right of 
the domain path field. This opens a new dialogue. In the name field 
insert cocome-logic or cocome-web. The domain directory should already point 
to ${GLASSFISH}/domains and should not be modified. The portbase 
is used to compute the ports for this domain. Portbase + 80 is the 
port for accessing the application and portbase + 48 is the port where 
the admin console will be available. For this method we therefore define:

- cocome-pickup: portbase 8500, admin 8548, service 8580

In the remaining steps we will use the port numbers from Section 2.1.

### 3. Install the CoCoME cloud-jee-platform-migration version

The pickup shop requires the basic CoCoME version to be running already. 
The required projects can be found in the following repository:

https://github.com/cocome-community-case-study/cocome-cloud-jee-platform-migration

The installation instructions can be found in the repository under 
cocome-maven-project/doc/

**IMPORTANT:** If you installed CoCoME before, make sure it is running without any troubles.
To verify: do ```mvn clean post-clean``` in cocome-maven project, then do mvn install with the cocome settings.
Don't forget to start each Glassfish server.

**Troubleshooting:**  
- **(un-)deployment failed:** STOP the glassfish server that's causing trouble. 
  Go to the domain folder (glassfish4/glassfish/domains/...) in your glassfish 
  folder (e.g.: enterprise) and delete the CONTENT of the osgi-cache, apllications and generated folder.
  Restart the glassfish server.
  Then access the admin surface of the server ( e.g.: localhost:8348   to access the enterprise server via browser).
  Go to "list deployment applications" under deployment and delete the application (e.g. enterprise-logic-ear on 
  enterprise server). Repeat this for other domains that are causing trouble when executing mvn install on 
  cocome-maven-project.  ONLY proceed if BUILD SUCCES when doing mvn install.
       
       
        
## Importing and Configuring of the CoCoME Pickup Shop Build

1. Check out git repository from:  https://github.com/cocome-community-case-study/cocome-cloud-jee-web-shop.git
   This can be done via 'git clone https://github.com/cocome-community-case-study/cocome-cloud-jee-web-shop.git'
   or with eclipse 'clone repository' (use above URL)
  1. Import the existing maven project in Eclipse with 
     Import -> Maven -> Existing Maven Projects
2. Select the cocome-shop-project folder in
   the GIT repository to import. There should appear two 
   sub-projects, cloud-auth-provider and cloud-pickup-shop.
   These projects depend on an existing installation of the 
   basic CoCoME version to be available at least in the local
   maven repository.
3. Once successfully imported, you need to rename the cocome-shop-project/settings.xml.template 
   file to settings.xml and open it.
  1. Check and change the following settings:
     - pickup.domain to cocome-pickup (  <pickup.domain>....</pickup.domain>  -->   <pickup.domain>cocome-pickup</pickup.domain> )
     - pickup.httpPort to 8580
     - pickup.adminPort to 8548
  2. Change the <logic.registry.httpPort> to the port you selected in your cocome project  --> check up in cocome's pom.xml  
     (should be <logic.registry.httpPort>8480</logic.registry.httpPort> if you followed the cocome installation guide, but can be different)    

## Compile and Deploy CoCoME Pickup Shop

**Note:** If compilation and deployment fail half through the build and
deployment process, there might be parts up and running which can cause
additional trouble after fixing the issue and trying installation again.
In this case run mvn -s settings.xml clean on command line or in Eclipse.

### 1. Deploy complete implementation

Don't forget to start the webshop glassfish server first!

To deploy the pickup shop from within Eclipse you can create a 
new build configuration by right-clicking on the 
cocome-shop-project -> Run As -> Maven build...
In the following dialog under Goals enter 'install' and 
under User settings enter the path to your settings.xml 
in the cocome-shop-project folder. Once you did this, you 
can later start this configuration again by right clicking
on cocome-shop-project -> Run As -> Maven build (without the dots).
A window will appear where you can select which configuration to run.

To deploy the pickup shop from command line you have to do this in a console:

```
cd cocome-shop-project
mvn -s settings.xml install
```

### 2. Register authentication provider

**This step is only necessary once, or, in case you changed something in the 
cloud-auth-provider project you have to repeat it.**

1. First you need to move cloud-auth-provider-1.0.jar from your workspace 
   (workspace/cocome-cloud-jee-web-shop/cocome-shop-project/cloud-auth-provider/target)
   to the lib folder of the cocome-pickup domain in glassfish 
   (${GLASSFISH}/glassfish/domains/cocome-pickup/lib)
2. Open localhost:8548 in your browser to open the admin console of the cocome-pickup domain.
   There go to Configurations -> server-config -> Security -> Realms and add a new security realm.
   - Name: LogicServiceRealm, Class name: org.cocome.cloud.auth.provider.LogicServiceRealm

   Go back to Configurations -> server-config -> Security and change the Default Realm to the 
   new LogicServiceRealm.

   Now you need to open the file ${GLASSFISH}/glassfish/domains/cocome-pickup/config/login.conf and add 
   the following lines at the end:
     
```
cocomeLogicRealm {
    org.cocome.cloud.auth.provider.LogicServiceLoginModule required;
};
```

   Then execute (via console or use eclipse mvn...):

```
cd cocome-shop-project
mvn -s settings.xml clean post-clean install
```   

**Troubleshooting:**
- **Null pointer exception when adding the security realm:** cloud-auth-provider-1.0.jar  is not moved yet or moved 
  to the wrong folder.
- **Wsdl exception when adding the security realm:** Make sure that cocome-maven-project is
  deployed without any problems  (repeat deploying cocome as mentioned above) and redeploy webshop.
  - stop and restart cocome-pickup server 
- If you are using Linux and you get following error: **Failed to read artifact descriptor for org.glassfish.external:asm-all:jar:3.3: Could not transfer artifact org.glassfish.external:asm-all:pom:3.3 from/to central (https://repo.maven.apache.org/maven2)**  then you need to prompt following command:
```
sudo /var/lib/dpkg/info/ca-certificates-java.postinst configure
```
## Undeploy Services

To undeploy and shut down Glassfish use Run As -> Maven clean.
If there were problems deploying a subproject, an error may occur
when trying to undeploy this subproject. The order in which the 
projects are deployed and undeployed is the same, so the error 
can normally be ignored. 

## Usage
**BEFORE USING:** Start the database :) In case you are running a Derby Database, select your glassfish folder -> bin then 
execute in a console:  asadmin start-database

**NOTE:** You have to start the database every time you access the frontend (pickupshop or cocome frontend).
If you open the server.log file in your glassfish domain that's causing the issues and scroll to the end,
you probably find an error:   ... index not valid...
This error is probably caused by a not-running database.

Once you have successfully deployed the projects you can access the 
pickup shop under the following URL:

http://localhost:8580/cloud-pickup-shop/

There you can browse the currently registered products, select a store, 
register a new user or log in with an existing one and purchase products.

**Note:** For the login, you have to use the email address as username (the email you used for registration).
Unless you delete the database, you can use the login details again.


## Additional information

- There may be errors regarding the target runtime. They may 
  be resolved by installing at least the Glassfish Tools from the  
  Oracle Enterprise Pack for Eclipse (OEPE) from the Eclipse 
  Marketplace. Then add a new Glassfish server in the Servers view
  with the Server root directory of your existing Glassfish installation.
  After this, change the Targeted Runtimes of the ear, ejb, webservice 
  and java-utils projects under Properties -> Targeted Runtimes to
  the newly created glassfish server.
