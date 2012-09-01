package docgenerate;

import java.io.File;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 9/1/12
 * Time: 11:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class TemplateProcessor
{
    private static final String WIKI_BLOCK_TAG = "wiki_code_block";

    public static String processTemplate(String templatePath,Collection<WikiCodeBlockInfo> codeBlocks)
    {
        String templateText = CodeReader.readFile(new File(templatePath));

        Pattern pattern = Pattern.compile("(<" + WIKI_BLOCK_TAG + ">)([^<]*)(</" + WIKI_BLOCK_TAG + ">)");
        Matcher matcher = pattern.matcher(templateText);

        while (matcher.find())
        {
            String blockId = matcher.group(2);
            WikiCodeBlockInfo blockInfo = findByName(codeBlocks,blockId);

            templateText = matcher.replaceFirst(blockInfo.getCode());
            matcher.reset(templateText);
        }

        return templateText;
    }

    private static WikiCodeBlockInfo findByName(Collection<WikiCodeBlockInfo> codeBlocks,String blockId)
    {
        for (WikiCodeBlockInfo codeBlock : codeBlocks)
        {
            if (codeBlock.getId().equals(blockId))
            {
                return codeBlock;
            }
        }
        return null;
    }
}
