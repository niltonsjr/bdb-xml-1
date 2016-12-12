package com.bdbxml;

import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.Environment;
import com.sleepycat.db.EnvironmentConfig;
import com.sleepycat.dbxml.*;

import java.io.File;

class Ejemplo1 {
    public static void main(String args[]) throws Throwable{
        Environment myEnv;
        File envHome = new File("myEnv/");
        XmlManager myManager = null;
        XmlContainer myContainer = null;
        XmlContainer nuevoContainer = null;


        try {

            /* Crear Entorno */
            EnvironmentConfig envConf = new EnvironmentConfig();
            envConf.setAllowCreate(true); // If the environment does not exits, create it.
            envConf.setInitializeCache(true); // Turn on the shared memory region.
            envConf.setInitializeLocking(true); // Turn on the locking subsystem.
            envConf.setInitializeLogging(true); // Turn on the logging subsystem.
            envConf.setTransactional(true); // Turn on the transactional subsystem.
            myEnv = new Environment(envHome, envConf);

            //Crear manejador para Container asignando Entorno
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
            if(myManager.existsContainer("db/myContainer.bdbxml") == 0){ //Container no existe, lo creamos y añade los documentos
                System.out.println("El container \"myContainer\" no existe.\nCreando container.\n");
                myContainer = myManager.createContainer("db/myContainer.bdbxml", myContainerConfig);

                //Añadir documentos al container
                String[] fileNames = leerFicheros();
                if (fileNames == null) //si el fichero está vacío
                    System.out.println("No hay ficheros en el directorio especificado");
                else { // si hay archivos
                    for (String fileName1 : fileNames) {
                        String fileName = "data\\simpleData\\".concat(fileName1); //nombre y localización del archivo
                        String docName = fileName1.substring(0, fileName1.indexOf(".")); //nombre del archivo sin extención

                        XmlInputStream theStream = myManager.createLocalFileInputStream(fileName);
                        //Añadir documento
                        myContainer.putDocument(docName, theStream); // The document's name, The document.
                        System.out.println("Añadiendo documento: " + docName + " desde fichero " + fileName + " a container: " + myContainer.getName() + ".");
                    }
                }
            }else{ // Container existe, lo abrimos
                System.out.println("El container \"myContainer\" ya existe.\nAbriendo container.\n");
                myContainer = myManager.openContainer("db/myContainer.bdbxml", myContainerConfig); //db está en dentro de la carpeta del entorno
            }

            // Do BDB XML work here.

            // Obtener el contexto de la consulta
            XmlQueryContext context = myManager.createQueryContext();

            // Declarar un namespace
            //context.setNamespace("fruits", "http://groceryItem.dbxml/fruits");

            // Declarar la consulta en un string
            String myQuery = "collection('db/myContainer.bdbxml')/product[item=\"Yellow Sapote\"]";

            XmlQueryExpression qe = myManager.prepare(myQuery, context);
            // Realizar la consulta
            XmlResults results = qe.execute(context);

            // Mostrar el tamaño del resultado de la consulta
            String message = "Encontrados ";
            message += results.size() + " documentos para la consulta: '";
            message += myQuery + "'\n";
            System.out.println(message);

            // Mostrar el resultado de la consulta
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
            qe.delete();
            results.delete();

            //-------------------------------//

            //Crear nuevo container y añadir documento a container.
            if(myManager.existsContainer("db/nuevoContainer.bdbxml") == 0){ //Container no existe, lo creamos y añade los documentos
                System.out.println("El container \"nuevoContainer\" no existe.\nCreando container.\n");
                nuevoContainer = myManager.createContainer("db/nuevoContainer.bdbxml", myContainerConfig);

                //Añadir documento al container
                String contenido = "<?xml version=\"1.0\"?><lista_productos>\n</lista_productos>";
                // Añadir documento
                nuevoContainer.putDocument("lista_productos.xml", contenido); // The document's name, The document.
                System.out.println("Añadiendo documento: lista_productos.xml a container: " + nuevoContainer.getName() + ".");

            }else{ // Container existe, lo abrimos
                System.out.println("El container \"nuevoContainer\" ya existe.\nAbriendo container.\n");
                nuevoContainer = myManager.openContainer("db/nuevoContainer.bdbxml", myContainerConfig); //db está en dentro de la carpeta del entorno
            }

            // Obtener el contexto de la consulta
            XmlQueryContext context2 = myManager.createQueryContext();
/*
            //Obtener todos los nodos de los documentos del container myContainer y añadirlos al documento del container nuevoContainer
            String insertar = "copy $c := doc(\"dbxml:db/myContainer.bdbxml/ZapoteBlanco.xml\")\n" +
                    "modify (insert nodes $c/product,\n" +
                    "replace value of node $c/b2 with \"replacement value\")\n" +
                    "return $c"


                    "for $i in doc(\"dbxml:db/myContainer.bdbxml/ZapoteBlanco.xml\")/product return " +
                    "insert $i into doc(\"dbxml:/db/nuevoContainer.bdbxml/lista_productos.xml\")/lista_productos";
                    //"insert nodes <b4>inserted child</b4> into doc(\"dbxml:/db/nuevoContainer.bdbxml/lista_productos.xml\")/lista_productos";

            // Realizar la insercción
            XmlResults res_inserc = myManager.query(insertar, context2);
*/
            // Verificar contenido del container nuevoContainer
            String consulta = "collection('db/nuevoContainer.bdbxml')/*";
            XmlResults resultado = myManager.query(consulta, context2);
            System.out.println("Consultando contenido de container nuevoContainer.\n");

            // Mostrar el tamaño del resultado de la consulta
            String message2 = "Encontrados ";
            message2 += resultado.size() + " documentos para la consulta: '";
            message2 += consulta + "'\n";
            System.out.println(message2);

            // Mostrar el resultado de la consulta
            XmlValue res_consulta = resultado.next();
            while (res_consulta != null) {
                XmlDocument theDoc = res_consulta.asDocument();
                String docName = theDoc.getName();
                String docString = res_consulta.asString();
                message2 = "Documento ";
                message2 += theDoc.getName() + ":\n";
                message2 += res_consulta.asString();
                message2 += "\n===============================\n";
                System.out.println(message2);
                res_consulta = resultado.next();
            }
            resultado.delete();

            } catch (DatabaseException de) {
            System.out.println("Database error: " +de.getMessage());

        }
        finally {
            try {
                if(myContainer != null){
                    myContainer.close();
                }
                if(nuevoContainer != null){
                    nuevoContainer.close();
                }
                if (myManager != null) {
                    myManager.close();
                }
            } catch (XmlException ce) {
                System.out.println("Database error: " +ce.getMessage());
            }
        }

    }

    /**
     * Método para leer los ficheros en un directorio     *
     * @return un array de strings con los nombres de los ficheros incluyendo la extención.
     */
    private static String[] leerFicheros(){
        // Aquí la carpeta donde queremos buscar
        File dir = new File("data\\simpleData\\");
        String[] ficheros = dir.list();

        if (ficheros == null)
            System.out.println("No hay ficheros en el directorio especificado");

        return ficheros;
    }
}