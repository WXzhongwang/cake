import com.rany.framework.config.Config;
import org.junit.Test;

/**
 * @author zhongshengwang
 * @version 1.0
 * @date 2025/3/31 18:19
 * @slogon 找到银弹
 */
public class ConfigTests {

    @Test
    public void testConfig() {
        Config.init("cake-devops-base");
    }
}
