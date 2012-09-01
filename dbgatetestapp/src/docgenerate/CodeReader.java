package docgenerate;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 9/1/12
 * Time: 8:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class CodeReader
{
    private static String blockAttributeName = WikiCodeBlock.class.getSimpleName();

    public static Collection<WikiCodeBlockInfo> readAndExtractWikiBlocks(Collection<String> sourceFileList)
    {
        Collection<WikiCodeBlockInfo> allBlocks = new ArrayList<WikiCodeBlockInfo>();
        for (String srcFile : sourceFileList)
        {
            String fileContent = readFile(new File(srcFile));
            allBlocks.addAll(findBlocks(fileContent));
        }
        return allBlocks;
    }

    private static Collection<WikiCodeBlockInfo> findBlocks(String source)
    {
        int curPos = 0;
        Collection<WikiCodeBlockInfo> infoList = new ArrayList<WikiCodeBlockInfo>();

        Pattern pattern = Pattern.compile("(@" + blockAttributeName + "\\()([^\"]*)(\")([^\"]*)(\"*\\))");
        Matcher matcher = pattern.matcher(source);
        while (matcher.find(curPos))
        {
            curPos = matcher.end();
            WikiCodeBlockInfo info = new WikiCodeBlockInfo();
            info.setId(matcher.group(4));
            info.setCode(readBlock(source, curPos));
            infoList.add(info);
        }
        return infoList;
    }

    private static String readBlock(String source, int position)
    {
        StringBuilder block = new StringBuilder();
        boolean blockStart = false;
        int bracketCount = 0;
        char[] chars = source.substring(position).toCharArray();

        for (char aChar : chars){
            switch (aChar)
            {
                case '{':
                    blockStart = true;
                    bracketCount++;
                    break;
                case '}':
                    bracketCount--;
                    break;
            }

            block.append(aChar);
            if (blockStart && bracketCount == 0)
            {
                break;
            }
        }
        return block.toString();
    }
    
    protected static String readFile(File source)
    {
        StringBuilder sb = new StringBuilder((int) source.length());
        try {
            Reader r = new FileReader(source);
            int c = 0;
            while (c != -1) {
                c = r.read();
                sb.append((char) c);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }
}
