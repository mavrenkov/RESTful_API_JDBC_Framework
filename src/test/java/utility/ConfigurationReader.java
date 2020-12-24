package utility;


import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigurationReader {
    /**
     * properties instance - immutable, encapsulated and static
     * Properties class is extending Map interface - has key:value format
     * to get a source of key:value pairs we need to open Input Stream and load properties file
     */
    private static final Properties properties = new Properties();
    /**
     * File Input Stream - responsible for getting information from the source
     * After we created new Input Stream - we have to destroy it right away in finally block.
     * We're getting it from Input/Output package(io)
     */
    private static FileInputStream file;
    /**
     * Static block to execute loading key-value pairs from configuration properties file
     * after key-value pairs been loaded - input stream will be closed before method call
     */
    static{
        try {
            file = new FileInputStream("configuration.properties");
            properties.load(file);
        } catch (IOException e) {
            System.out.println("Properties File not found");
        }finally {
            if(file!=null)try{
                file.close();
            }catch (IOException e){
                System.out.println("Problem with closing Input Stream!! " +e.getMessage());
            }
        }
    }
    /**
     * Getter
     * @param keyWord key - to get disired value
     * @return returns the value from configuration.properties
     */
    public static String getProperty(String keyWord){
        return properties.getProperty(keyWord);
    }


}
