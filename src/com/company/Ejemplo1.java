package com.company;

import com.sleepycat.dbxml.*;
import com.sleepycat.db.*;
import java.io.*;

class Ejemplo1 {
    public static void main(String args[]) throws Throwable{
        Environment myEnv = null;
        File envHome = new File("myEnv/");
        XmlManager myManager = null;
        XmlContainer myContainer = null;

        try {
            //Crear enviroment
            EnvironmentConfig envConf = new EnvironmentConfig();
            envConf.setAllowCreate(true); // If the environment does not exits, create it.
            envConf.setInitializeCache(true); // Turn on the shared memory region.
            envConf.setInitializeLocking(true); // Turn on the locking subsystem.
            envConf.setInitializeLogging(true); // Turn on the logging subsystem.
            envConf.setTransactional(true); // Turn on the transactional subsystem.
            myEnv = new Environment(envHome, envConf);

            //Crear manejador para Container asignando enviroment
            XmlManagerConfig managerConfig = new XmlManagerConfig();
            managerConfig.setAdoptEnvironment(true);
            managerConfig.setAllowAutoOpen(true);
            myManager = new XmlManager(myEnv, managerConfig);

            //Crear configuración de Container habilitando crear al abrir por primera vez
            XmlContainerConfig myContainerConfig= new XmlContainerConfig();
            myContainerConfig.setAllowCreate(true);
            myContainerConfig.setTransactional(true);
            myContainerConfig.setContainerType(XmlContainer.NodeContainer);

            //Crear container
            //myContainer = myManager.createContainer("db/Container.bdbxml", myContainerConfig);
            myContainer = myManager.openContainer("db/myContainer.bdbxml", myContainerConfig); //db está en dentro de la carpeta del enviroment
/*
            //Añadir ficheros al container
            String[] fileNames = leerFicheros();

            if (fileNames == null) //si el fichero está vacío
                System.out.println("No hay ficheros en el directorio especificado");
            else { // si hay archivos
                for (int i = 0, fileNamesLength = fileNames.length; i < fileNamesLength; i++) {
                    String fileName1 = fileNames[i];
                    String fileName = "data\\simpleData\\".concat(fileName1); //nombre y localización del archivo
                    String docName = fileName1.substring(0, fileName1.indexOf(".")); //nombre del archivo sin extención

                    XmlInputStream theStream = myManager.createLocalFileInputStream(fileName);
                    // Do the actual put
                    myContainer.putDocument(docName, theStream); // The document's name, The document.
                    System.out.println("Añadiendo documento: " + docName + " desde fichero " + fileName + " a container: " + myContainer.getName() + ".");
                }
            }
*/
            // Do BDB XML work here.

            // Get a query context
            XmlQueryContext context = myManager.createQueryContext();
            // Declare a namespace
                       // context.setNamespace("fruits", "http://groceryItem.dbxml/fruits");
            // Declare the query string. Find all the product documents
            // in the fruits namespace.
                        String myQuery = "collection('db/myContainer.bdbxml')/product/category";
            // Perform the query.
                        XmlResults results = myManager.query(myQuery, context);


            // Show the size of the result set
            String message = "Found ";
            message += results.size() + " documents for query: '";
            message += myQuery + "'\n";
            System.out.println(message);

            // Display the result set
            XmlValue value = results.next();
            while (value != null) {
                XmlDocument theDoc = value.asDocument();
                String docName = theDoc.getName();
                String docString = value.asString();
                message = "Document ";
                message += theDoc.getName() + ":\n";
                message += value.asString();
                message += "\n===============================\n";
                System.out.println(message);
                value = results.next();
            }
            results.delete();

            } catch (DatabaseException de) {
            // Exception handling goes here
            System.out.println("Database Error: " +de.getMessage());

        }
        finally {
            try {
                if(myContainer != null){
                    myContainer.close();
                }
                if (myManager != null) {
                    myManager.close();
                }
            } catch (XmlException ce) {
                // Exception handling goes here
                System.out.println("Database Error: " +ce.getMessage());
            }
        }

    }

    /**
     * Método para leer los ficheros en un directorio     *
     * @return un array de strings con los nombres de los ficheros incluyendo la extención.
     */
    public static String[] leerFicheros(){
        // Aquí la carpeta donde queremos buscar
        File dir = new File("data\\simpleData\\");
        String[] ficheros = dir.list();

        if (ficheros == null)
            System.out.println("No hay ficheros en el directorio especificado");

        return ficheros;
    }
}