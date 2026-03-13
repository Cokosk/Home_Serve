import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;

/**
 * Schema验证器 - 验证XML文档是否符合Schema约束
 */
public class XMLSchemaValidator {

    public static void main(String[] args) {
        // 获取当前工作目录
        String basePath = System.getProperty("user.dir") + File.separator;
        String xmlFile = basePath + "bookstoreWithSchemaVal.xml";
        String xsdFile = basePath + "bookstore.xsd";
        
        System.out.println("========== Schema验证 ==========");
        System.out.println("XML文件: " + xmlFile);
        System.out.println("XSD文件: " + xsdFile);
        System.out.println();
        
        try {
            // 创建Schema工厂
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            
            // 加载Schema文件
            Schema schema = factory.newSchema(new File(xsdFile));
            
            // 创建验证器
            Validator validator = schema.newValidator();
            
            // 验证XML文件
            validator.validate(new StreamSource(new File(xmlFile)));
            
            System.out.println("✅ Schema验证成功！XML文档符合Schema约束。");
            
        } catch (org.xml.sax.SAXException e) {
            System.out.println("❌ XML Schema验证失败: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
}