package docgenerate;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 9/1/12
 * Time: 10:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class SourceScanner
{
    public  static Collection<String> getAllFilesWithExtension(String srcRoot, String extension)
    {
        Collection<String> files = new ArrayList<String>();
        getAllFilesWithExtension(files, srcRoot,extension);
        return files;
    }

    private  static void getAllFilesWithExtension(Collection<String> foundList,String srcRoot,String extension)
    {
        File file = new File(srcRoot);
        if (file.isFile())
        {
            String absolutePath = file.getAbsolutePath();
            if (absolutePath.toLowerCase().endsWith(extension))
            {
                foundList.add(absolutePath);
            }
        }
        else if (file.isDirectory())
        {
            String[] children = file.list();
            for (String child : children)
            {
                getAllFilesWithExtension(foundList, srcRoot + File.separator + child,extension);
            }
        }
    }
}
