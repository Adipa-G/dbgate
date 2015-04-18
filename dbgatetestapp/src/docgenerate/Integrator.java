package docgenerate;

import java.io.*;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 9/1/12
 * Time: 11:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class Integrator
{
    public static final String WIKI_TEMPLATE_EXTENSION = "wiki.template";
    public static final String WIKI_EXTENSION = "md";
    public static final String JAVA_SOURCE_EXTENSION = "java";
    
    private static void doProcess(String srcRoot,String srcOut)
    {
        Collection<String> allWikiTemplateFiles = SourceScanner.getAllFilesWithExtension(srcRoot, WIKI_TEMPLATE_EXTENSION);
        Collection<String> allJavaFiles = SourceScanner.getAllFilesWithExtension(srcRoot, JAVA_SOURCE_EXTENSION);
        Collection<WikiCodeBlockInfo> allCodeBlocks = CodeReader.readAndExtractWikiBlocks(allJavaFiles);

        for (String wikiTemplateFile : allWikiTemplateFiles)
        {
            String wikiText = TemplateProcessor.processTemplate(wikiTemplateFile,allCodeBlocks);
            File file = new File(srcOut + File.separator + findWikiFileName(wikiTemplateFile));
            writeFile(file,wikiText);
        }
    }
    
    private static String findWikiFileName(String wikiTemplateFilePath)
    {
        File file = new File(wikiTemplateFilePath);
        return wikiTemplateFilePath.replace(file.getParent(),"").replace(WIKI_TEMPLATE_EXTENSION,WIKI_EXTENSION);
    }
    
    private static void writeFile(File file,String content)
    {
        try {
            Writer w = new FileWriter(file);
            w.write(content);
            w.flush();
            w.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static void main(String[] args)
    {
        doProcess("Y:\\dev\\community\\github\\dbgate\\dbgatetestapp\\src","Y:\\dev\\community\\github\\dbgate\\dbgatetestapp\\wikigen");
    }
}
