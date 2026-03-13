import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import java.io.File;

/**
 * DTD验证器 - 验证XML文档是否符合DTD约束
 */
public class XmlDtdValidator implements ErrorHandler {

    public static void main(String[] args) {
        // 获取当前工作目录
        String basePath = System.getProperty("user.dir") + File.separator;
        String xmlFile = basePath + "bookstoreWithDtdVal.xml";
        
        System.out.println("========== DTD验证 ==========");
        System.out.println("XML文件: " + xmlFile);
        System.out.println();
        
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(true);  // 启用DTD验证
            DocumentBuilder builder = factory.newDocumentBuilder();
            
            XmlDtdValidator validator = new XmlDtdValidator();
            builder.setErrorHandler(validator);
            
            // 解析并验证XML文件
            builder.parse(new File(xmlFile));
            
            System.out.println("✅ DTD验证成功！XML文档符合DTD约束。");
            
        } catch (SAXException e) {
            System.out.println("❌ DTD验证失败: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void warning(SAXParseException exception) throws SAXException {
        System.out.println("⚠️ 警告 [行 " + exception.getLineNumber() + "]: " + exception.getMessage());
    }

    @Override
    public void error(SAXParseException exception) throws SAXException {
        System.out.println("❌ 错误 [行 " + exception.getLineNumber() + "]: " + exception.getMessage());
        throw exception;
    }

    @Override
    public void fatalError(SAXParseException exception) throws SAXException {
        System.out.println("🔴 严重错误 [行 " + exception.getLineNumber() + "]: " + exception.getMessage());
        throw exception;
    }
}