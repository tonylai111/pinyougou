import com.pinyougou.common.util.FastDFSClient;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.junit.Test;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package PACKAGE_NAME *
 * @since 1.0
 */
public class fastdfsTest {

    @Test
    public void uploadFile() throws Exception{
        //1.创建一个配置文件
        //2.加载配置文件
        ClientGlobal.init("C:\\Users\\Administrator\\IdeaProjects\\53\\pinyougou-parent\\pinyougou-shop-web\\src\\main\\resources\\config\\fastdfs_client.conf");
        //3.创建一个trackerClient 对象
        TrackerClient trackerClient = new TrackerClient();
        //4.创建一个trackerServer对象 （根据trackerClient）获取
        TrackerServer trackerServer = trackerClient.getConnection();
        //5.创建storageClient
        StorageClient storageClient= new StorageClient(trackerServer,null);

        //参数1：要上传的文件的路径
        //参数2：要上传的文件的扩展名  不能带"."
        //参数3：元数据
        String[] jpgs = storageClient.upload_file("C:\\Users\\Administrator\\Pictures\\timg.jpg", "jpg", null);
        for (String jpg : jpgs) {
            System.out.println(jpg);
        }



    }

    @Test
    public void testFastdfsclient() throws Exception{
        FastDFSClient fastDFSClient = new FastDFSClient("C:\\Users\\Administrator\\IdeaProjects\\53\\pinyougou-parent\\pinyougou-shop-web\\src\\main\\resources\\config\\fastdfs_client.conf");
        String jpg = fastDFSClient.uploadFile("C:\\Users\\Administrator\\Pictures\\5b13cd6cN8e12d4aa.jpg", "jpg");
        System.out.println(jpg);
    }
}
